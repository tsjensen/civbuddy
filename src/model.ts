import { SituationDao, FundsDao } from './dao';
import { RulesJson, CardJson, Rules, CommodityJson, Language } from './rules';
import { FundsCalculator } from './funds';
import { buildMap } from './dom';


/**
 * States that a card can be in.
 * 
 * The states 'OWNED' and 'PLANNED' are the major states that can be explicitly set by the user.
 * Only the 'OWNED' state is persisted. All other states are calculated at run time.
 */
export enum State {
    /** The player currently owns this card. */
    OWNED,

    /** The player plans to buy this card. */
    PLANNED,

    /** This card would be a perfectly viable choice to buy, but the player has made no such plans as of yet. */
    ABSENT,

    /** Buying this card is discouraged, because it would make it impossible to reach the total winning points by
     *  civilization cards. The original game variant includes a limit on the number of civilization cards a player
     *  can buy. */
    DISCOURAGED,

    /** Buying this card is impossible because its prerequisites aren't met. */
    PREREQFAILED,

    /** Buying this card should not be possible due to insufficient resources. */
    UNAFFORDABLE
}

export class StateUtil
{
    public static hasExplanationText(pState: State): boolean {
        return pState !== State.ABSENT && pState !== State.PLANNED;
    }

    public static requiresExplanationArgument(pState: State): boolean {
        return pState === State.PREREQFAILED || pState === State.DISCOURAGED;
    }

    public static isPlannable(pState: State): boolean {
        return pState === State.ABSENT || pState === State.DISCOURAGED;
    }
}



/**
 * The runtime model of one player's situation.
 */
export class Situation
{
    /** reference to the JSON data, which is the part which we persist */
    private readonly dao: SituationDao;

    /** the rules of the game to which this situation belongs */
    private readonly rules: Rules;

    /** the runtime card state of each card (Map from cardId to CardData) */
    private readonly states: Map<string, CardData>;

    /** the current score */
    private score: number;

    /** the current number of cards in state OWNED */
    private numOwnedCards: number;

    /** total funds available */
    private totalFundsAvailable: number;

    /** what funds remain if you subtract the planned cards from the total funds */
    private currentFunds: number;


    constructor(pDao: SituationDao, pCardData: Map<string, CardData>, pRules: Rules) {
        this.dao = pDao;
        this.rules = pRules;
        this.states = pCardData;
        this.score = this.calculateInitialScore(pCardData);
        this.numOwnedCards = this.countOwnedCards(pCardData);
        this.totalFundsAvailable =  new FundsCalculator().recalcTotalFunds(this.dao.funds, pRules.variant);
        this.currentFunds = this.totalFundsAvailable;
    }

    private calculateInitialScore(pCardData: Map<string, CardData>): number {
        let result: number = 0;
        for (let cardState of pCardData.values()) {
            if (cardState.isOwned()) {
                result += cardState.dao.costNominal;
            }
        }
        return result;
    }

    private countOwnedCards(pCardData: Map<string, CardData>): number {
        let result: number = 0;
        for (let cardState of pCardData.values()) {
            if (cardState.isOwned()) {
                result++;
            }
        }
        return result;
    }


    public buyPlannedCards(): string[] {
        const cardIdsBought: string[] = [];
        for (let [cardId, cardState] of this.states) {
            if (cardState.isPlanned()) {
                this.buyCard(cardId);
                cardIdsBought.push(cardId);
            }
        }
        // TODO recalculate states of other cards
        return cardIdsBought;
    }

    private buyCard(pCardId: string): void {
        const cardState: CardData = this.states.get(pCardId) as CardData;
        this.score += cardState.dao.costNominal;
        this.numOwnedCards++;
        cardState.buyCardIfPlanned();
        this.applyNewCredits(pCardId, cardState.dao.creditGiven);
        if (this.dao.ownedCards.indexOf(pCardId) < 0) {
            this.dao.ownedCards.push(pCardId);
        }
    }


    private applyNewCredits(pSourceCardId: string, pCreditGiven: Object): void {
        for (let cardId of Object.keys(pCreditGiven)) {
            const creditValue: number = pCreditGiven[cardId];
            const targetCard: CardData = this.states.get(cardId) as CardData;
            if (!targetCard.isOwned()) {
                targetCard.addCredit(pSourceCardId, creditValue);
                targetCard.subtractCreditPlanned(pSourceCardId);
            }
        }
    }


    public planCard(pCardId: string): string[] {
        const changedCreditBars: string[] = [];
        const cardState: CardData = this.states.get(pCardId) as CardData;
        if (StateUtil.isPlannable(cardState.state)) {
            cardState.state = State.PLANNED;
            this.currentFunds -= cardState.getCurrentCost();
            for (let targetCardId of Object.keys(cardState.dao.creditGiven)) {
                const targetCardData: CardData = this.states.get(targetCardId) as CardData;
                const credit: number = cardState.dao.creditGiven[targetCardId] as number;
                if (!targetCardData.isOwned()) {
                    targetCardData.addCreditPlanned(pCardId, credit);
                    changedCreditBars.push(targetCardId);
                }
            }
            // TODO recalculate state of other cards (e.g. no longer affordable; prereq NOT affected, adv rule 31.62)
        }
        return changedCreditBars;
    }


    public unplanCard(pCardId: string): string[] {
        const changedCreditBars: string[] = [];
        const cardState: CardData = this.states.get(pCardId) as CardData;
        if (cardState.isPlanned()) {
            cardState.state = State.ABSENT;
            this.currentFunds += cardState.getCurrentCost();
            for (let targetCardId of Object.keys(cardState.dao.creditGiven)) {
                const targetCardData: CardData = this.states.get(targetCardId) as CardData;
                if (!targetCardData.isOwned()) {
                    targetCardData.subtractCreditPlanned(pCardId);
                    changedCreditBars.push(targetCardId);
                }
            }
            // TODO recalculate state of this and other cards
        }
        return changedCreditBars;
    }


    public updateTotalFunds(pNewFunds: FundsDao): void {
        // TODO
    }


    /**
     * Update all text fields in the model with the new language text.
     * @param pNewLanguage the new language to use
     */
    public changeLanguage(pNewLanguage: Language): void {
        for (let cardState of this.states.values()) {
            if (cardState.state === State.PREREQFAILED) {
                const prereqCardId: string = cardState.dao.prereq as string;
                cardState.stateExplanationArg = this.rules.variant.cards[prereqCardId].names[pNewLanguage];
            }
        }
    }


    public getDaoForStorage(): SituationDao {
        return this.dao;
    }

    /**
     * Constructs a deep copy of the runtime card state object.
     * @param pCardId the ID if the card in question
     * @returns the copy of the card state
     */
    public getCard(pCardId): CardData {
        return (this.states.get(pCardId) as CardData).clone();
    }

    public getCardState(pCardId: string): State {
        return (this.states.get(pCardId) as CardData).state;
    }

    public getSumCreditReceivedPlanned(pCardId: string): number {
        return (this.states.get(pCardId) as CardData).sumCreditReceivedPlanned;
    }

    public getNumOwnedCards(): number {
        return this.numOwnedCards;
    }


    public getScore(): number {
        return this.score;
    }


    public getTotalFunds(): number {
        return this.totalFundsAvailable;
    }

    public getCurrentFunds(): number {
        return this.currentFunds;
    }

    public getCreditGiven(pCardId: string): Map<string, number> {
        return buildMap((this.states.get(pCardId) as CardData).dao.creditGiven);
    }

    public getPlayerName(): string {
        return this.dao.player.name;
    }

    public getPointsTarget(): number {
        return this.dao.player.winningTotal;
    }
}



export class CardData
{
    /** reference to the JSON data from the variant description file */
    public readonly dao: CardJson;

    /** current credit received from other owned cards (map from source card ID to credit points) */
    public creditReceived: Map<string, number> = new Map();

    /** current sum of credit received from owned cards */
    public sumCreditReceived: number = 0;

    /** current potential additional credit received from other planned cards (map from source card ID to credit points) */
    public creditReceivedPlanned: Map<string, number> = new Map();

    /** current potential additional sum of credit received from planned cards */
    public sumCreditReceivedPlanned: number = 0;

    /** current card state */
    public state: State = State.ABSENT;

    /** card state explanation argument (e.g. name of prereq card, points missing from target) */
    public stateExplanationArg: string | number | undefined = undefined;


    constructor(pFromRules: CardJson) {
        this.dao = pFromRules;
    }


    /**
     * Add active credit from the given owned card.
     * @param pSourceCardId providing card ID
     * @param pCreditGiven active credit points
     */
    public addCredit(pSourceCardId: string, pCreditGiven: number): void {
        if (!this.creditReceived.has(pSourceCardId)) {
            this.creditReceived.set(pSourceCardId, pCreditGiven);
            this.sumCreditReceived += pCreditGiven;
        }
    }

    /**
     * Add planned credit from the given card.
     * @param pSourceCardId providing card ID
     * @param pCreditGiven planned credit points
     */
    public addCreditPlanned(pSourceCardId: string, pCreditGiven: number): void {
        if (!this.creditReceivedPlanned.has(pSourceCardId)) {
            this.creditReceivedPlanned.set(pSourceCardId, pCreditGiven);
            this.sumCreditReceivedPlanned += pCreditGiven;
        }
    }

    public subtractCreditPlanned(pSourceCardId: string): void {
        if (this.creditReceivedPlanned.has(pSourceCardId)) {
            const credit: number = this.creditReceivedPlanned.get(pSourceCardId) as number;
            this.creditReceivedPlanned.delete(pSourceCardId);
            this.sumCreditReceivedPlanned -= credit;
        }
    }

    public buyCardIfPlanned(): void {
        if (this.state === State.PLANNED) {
            this.state = State.OWNED;
            this.stateExplanationArg = undefined;
        }
    }

    public isPlanned(): boolean {
        return this.state === State.PLANNED;
    }

    public isOwned(): boolean {
        return this.state === State.OWNED;
    }

    public getCurrentCost(): number {
        return Math.max(0, this.dao.costNominal - this.sumCreditReceived);
    }


    /**
     * Clone this object.
     * @returns a deep clone of this object
     */
    public clone(): CardData {
        const result = new CardData(this.dao);
        result.creditReceived = new Map(this.creditReceived);
        result.sumCreditReceived = this.sumCreditReceived;
        result.creditReceivedPlanned = new Map(this.creditReceivedPlanned);
        result.sumCreditReceivedPlanned = this.sumCreditReceivedPlanned;
        result.state = this.state;
        result.stateExplanationArg = this.stateExplanationArg;
        return result;
    }
}
