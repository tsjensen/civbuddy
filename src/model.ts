import { SituationDao } from './dao';
import { RulesJson, CardJson } from './rules';


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



/**
 * The runtime model of one player's situation.
 */
export class Situation
{
    /** reference to the JSON data, which is the part which we persist */
    public readonly dao: SituationDao;

    /** the runtime card state of each card (Map from cardId to CardData) */
    public readonly states: Map<string, CardData>;

    /** the current score */
    private score: number;

    /** the current number of cards in state OWNED */
    private numOwnedCards: number;


    constructor(pDao: SituationDao, pCardData: Map<string, CardData>) {
        this.dao = pDao;
        this.states = pCardData;
        this.score = this.calculateInitialScore(pCardData);
        this.numOwnedCards = this.countOwnedCards(pCardData);
    }

    private calculateInitialScore(pCardData: Map<string, CardData>): number {
        let result: number = 0;
        for (let cardState of pCardData.values()) {
            if (cardState.isOwned()) {
                result += cardState.props.costNominal;
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


    public buyCardIfPlanned(pCardId: string): void {
        const cardState: CardData = this.states.get(pCardId) as CardData;
        if (cardState.isPlanned()) {
            this.score += cardState.props.costNominal;
            this.numOwnedCards++;
            cardState.buyCardIfPlanned();
            this.applyNewCredits(pCardId, cardState.props.creditGiven);
            if (this.dao.ownedCards.indexOf(pCardId) < 0) {
                this.dao.ownedCards.push(pCardId);
            }
        }
    }


    private applyNewCredits(pSourceCardId: string, pCreditGiven: Object): void {
        for (let cardId of Object.keys(pCreditGiven)) {
            const creditValue: number = pCreditGiven[cardId];
            const targetCard: CardData = this.states.get(cardId) as CardData;
            targetCard.addCredit(pSourceCardId, creditValue);
            targetCard.subtractCreditPlanned(pSourceCardId);
        }
    }


    public getNumOwnedCards(): number {
        return this.numOwnedCards;
    }


    public getScore(): number {
        return this.score;
    }
}



export class CardData
{
    /** reference to the JSON data from the variant description file */
    public readonly props: CardJson;

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
        this.props = pFromRules;
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
        return Math.max(0, this.props.costNominal - this.sumCreditReceived);
    }
}
