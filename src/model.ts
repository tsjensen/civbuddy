import { SituationDao, FundsDao } from './storage/dao';
import { RulesJson, CardJson, Rules, CommodityJson, Language } from './rules/rules';
import { Calculator } from './cards/calc';
import { FundsCalculator } from './funds/calc';
import { appOptions } from './main';
import { Util } from './util';


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
    public static isOwned(pState: State): boolean {
        return pState === State.OWNED;
    }

    /**
     * PLANNED or OWNED, so a fixed card cannot be acquired in a future turn (because they are either already OWNED,
     * or they are assumed to be acquired in this turn, which means they are PLANNED).
     * @param pState the state to check
     * @returns true if PLANNED or OWNED
     */
    public static isFixed(pState: State): boolean {
        return pState === State.OWNED || pState === State.PLANNED;
    }

    public static hasExplanationText(pState: State): boolean {
        return pState !== State.ABSENT && pState !== State.PLANNED;
    }

    public static requiresExplanationArgument(pState: State): boolean {
        return pState === State.PREREQFAILED || pState === State.DISCOURAGED;
    }

    public static isPlanned(pState: State): boolean {
        return pState === State.PLANNED;
    }

    public static isPlannable(pState: State): boolean {
        return pState === State.ABSENT || pState === State.DISCOURAGED;
    }

    public static isHiddenByFilter(pState: State): boolean {
        return pState === State.OWNED || pState === State.UNAFFORDABLE || pState === State.PREREQFAILED;
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

    /** the rule options chosen when the game was created (immutable) */
    private readonly gameOptions: Map<string, string>;

    /** the runtime card state of each card (Map from cardId to CardData) */
    private readonly states: Map<string, CardData>;

    /** the current score */
    private score: number;

    /** the current number of cards in state OWNED */
    private numOwnedCards: number;

    /** the current number of cards in state PLANNED */
    private numPlannedCards: number;

    /** the cumulated nominal value of all PLANNED cards */
    private nominalValueOfPlannedCards: number;

    /** total funds available */
    private totalFundsAvailable: number;

    /** what funds remain if you subtract the planned cards from the total funds */
    private currentFunds: number;


    constructor(pDao: SituationDao, pGameOptions: Map<string, string>, pRules: Rules) {
        this.dao = pDao;
        this.rules = pRules;
        this.states = new Calculator(pRules, pGameOptions, appOptions.language).pageInit(pDao.ownedCards);
        this.score = this.calculateInitialScore(this.states);
        this.numOwnedCards = this.countOwnedCards(this.states);
        this.numPlannedCards = 0;
        this.nominalValueOfPlannedCards = 0;
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
        this.recalculate();
        return cardIdsBought;
    }

    private buyCard(pCardId: string): void {
        const cardState: CardData = this.states.get(pCardId) as CardData;
        this.score += cardState.dao.costNominal;
        this.nominalValueOfPlannedCards -= cardState.dao.costNominal;
        this.numOwnedCards++;
        this.numPlannedCards--;
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


    public discard(pCardId: string): void {
        const cardState: CardData = this.states.get(pCardId) as CardData;
        if (cardState.isOwned()) {
            this.score -= cardState.dao.costNominal;
            this.numOwnedCards--;
            cardState.state = State.ABSENT;
            const idx: number = this.dao.ownedCards.indexOf(pCardId);
            if (idx >= 0) {
                this.dao.ownedCards.splice(idx, 1);
            }
            // the rest does not matter as we perform a page reload right afterwards
        }
    }


    public planCard(pCardId: string): string[] {
        const changedCreditBars: string[] = [];
        const cardState: CardData = this.states.get(pCardId) as CardData;
        if (StateUtil.isPlannable(cardState.state)) {
            cardState.state = State.PLANNED;
            this.numPlannedCards++;
            this.nominalValueOfPlannedCards += cardState.dao.costNominal;
            this.currentFunds -= cardState.getCurrentCost();
            for (let targetCardId of Object.keys(cardState.dao.creditGiven)) {
                const targetCardData: CardData = this.states.get(targetCardId) as CardData;
                const credit: number = cardState.dao.creditGiven[targetCardId] as number;
                if (!targetCardData.isOwned()) {
                    targetCardData.addCreditPlanned(pCardId, credit);
                    changedCreditBars.push(targetCardId);
                }
            }
            this.recalculate();
        }
        return changedCreditBars;
    }


    public unplanCard(pCardId: string): string[] {
        const changedCreditBars: string[] = [];
        const cardState: CardData = this.states.get(pCardId) as CardData;
        if (cardState.isPlanned()) {
            cardState.state = State.ABSENT;
            this.numPlannedCards--;
            this.nominalValueOfPlannedCards -= cardState.dao.costNominal;
            this.currentFunds += cardState.getCurrentCost();
            for (let targetCardId of Object.keys(cardState.dao.creditGiven)) {
                const targetCardData: CardData = this.states.get(targetCardId) as CardData;
                if (!targetCardData.isOwned()) {
                    targetCardData.subtractCreditPlanned(pCardId);
                    changedCreditBars.push(targetCardId);
                }
            }
            this.recalculate();
        }
        return changedCreditBars;
    }


    public updateTotalFunds(pNewFunds: FundsDao): void {
        this.dao.funds = pNewFunds;
        this.recalculate();
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


    public recalculate(): void {
        const calc: Calculator = new Calculator(this.rules, this.gameOptions, appOptions.language);
        calc.recalculate(this);
    }


    public getId(): string {
        return this.dao.key;
    }

    public getDaoForStorage(): SituationDao {
        return this.dao;
    }

    public getCardIdIterator(): IterableIterator<string> {
        return this.states.keys();
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

    public isCardState(pCardId: string, pExpectedState: State | undefined): boolean {
        return this.getCardState(pCardId) === pExpectedState;
    }

    public setCardState(pCardId: string, pNewState: State, pStateExplArg?: string | number): void {
        const cardState: CardData = this.states.get(pCardId) as CardData;
        cardState.state = pNewState;
        cardState.stateExplanationArg = pStateExplArg;
    }

    public getStateExplanationArg(pCardId: string): string | number | undefined {
        return (this.states.get(pCardId) as CardData).stateExplanationArg;
    }

    public getCurrentCost(pCardId: string): number {
        return (this.states.get(pCardId) as CardData).getCurrentCost();
    }

    public isPrereqMet(pCardId: string): boolean {
        const prereq: string | undefined = this.rules.getPrereq(pCardId);
        let result: boolean = true;
        if (typeof(prereq) === 'string') {
            result = (this.states.get(prereq) as CardData).isOwned();
        }
        return result;
    }

    public getSumCreditReceivedPlanned(pCardId: string): number {
        return (this.states.get(pCardId) as CardData).sumCreditReceivedPlanned;
    }

    public getNumOwnedCards(): number {
        return this.numOwnedCards;
    }

    public getNumPlannedCards(): number {
        return this.numPlannedCards;
    }

    public getNominalValueOfPlannedCards(): number {
        return this.nominalValueOfPlannedCards;
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
        return Util.buildMap((this.states.get(pCardId) as CardData).dao.creditGiven);
    }

    public getPlayerName(): string {
        return this.dao.player.name;
    }

    public getPointsTarget(): number {
        return this.dao.player.winningTotal;
    }


    public setCardFilterActive(pActive: boolean): void {
        this.dao.filtered = pActive;
    }

    public isCardFilterActive(): boolean {
        return this.dao.filtered === true;
    }
}



export class CardData
{
    /** th card ID */
    public readonly id: string;

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


    constructor(pId: string, pFromRules: CardJson) {
        this.id = pId;
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
        const result = new CardData(this.id, this.dao);
        result.creditReceived = new Map(this.creditReceived);
        result.sumCreditReceived = this.sumCreditReceived;
        result.creditReceivedPlanned = new Map(this.creditReceivedPlanned);
        result.sumCreditReceivedPlanned = this.sumCreditReceivedPlanned;
        result.state = this.state;
        result.stateExplanationArg = this.stateExplanationArg;
        return result;
    }
}
