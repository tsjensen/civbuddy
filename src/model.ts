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
    public dao: SituationDao;

    /** the runtime card state of each card */
    public states: Map<string, CardData>;

    /** the current score */
    public score: number = 0;


    constructor(pDao: SituationDao, pCardData: Map<string, CardData>) {
        this.dao = pDao;
        this.states = pCardData;
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
}
