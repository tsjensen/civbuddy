import { CardJson, Rules, Language, Card } from './rules';
import { CardData, State, Situation, StateUtil } from './model';


export class BootstrapCalculator
{
    /** the choices that were made on options offered by the rules */
    private readonly variantOptions: Map<string, string>;

    /** the rules (a.k.a. game variant) that we are based on */
    private readonly rules: Rules;

    private readonly language: Language;


    constructor(pVariant: Rules, pOptions: Map<string, string>, pLanguage: Language) {
        this.rules = pVariant;
        this.variantOptions = pOptions;
        this.language = pLanguage;
    }


    /**
     * The 'cards' page has just loaded, so we have only some owned cards, but no planned ones.
     * @param pOwnedCards array of IDs of the owned cards
     */
    public pageInit(pOwnedCards: string[]): Map<string, CardData> {
        const result: Map<string, CardData> = this.buildInitialMap(pOwnedCards);
        for (let sourceCardId of Object.keys(this.rules.variant.cards)) {
            const sourceCard: CardJson = this.rules.variant.cards[sourceCardId];
            if ((result.get(sourceCardId) as CardData).state == State.OWNED) {
                for (let targetCardId of Object.keys(sourceCard.creditGiven)) {
                    const creditGiven: number = sourceCard.creditGiven[targetCardId];
                    const data: CardData = result.get(targetCardId) as CardData;
                    data.addCredit(sourceCardId, creditGiven);
                }
            }
        }
        return result;
    }

    private buildInitialMap(pOwnedCards: string[]): Map<string, CardData> {
        const result: Map<string, CardData> = new Map();
        for (let cardId of Object.keys(this.rules.variant.cards)) {
            const data: CardData = new CardData(this.rules.variant.cards[cardId]);
            if (pOwnedCards.indexOf(cardId) >= 0) {
                data.state = State.OWNED;
                data.stateExplanationArg = undefined;
            } else if (typeof(data.dao.prereq) === 'string' && pOwnedCards.indexOf(data.dao.prereq) < 0) {
                data.state = State.PREREQFAILED;
                data.stateExplanationArg = this.rules.variant.cards[data.dao.prereq].names[this.language];
            } else {
                data.state = State.ABSENT;
                data.stateExplanationArg = undefined;
            }
            result.set(cardId, data);
        }
        return result;
    }
}



export class Calculator
{
    /** the choices that were made on options offered by the rules */
    private readonly variantOptions: Map<string, string>;

    /** the rules (a.k.a. game variant) that we are based on */
    private readonly rules: Rules;

    private readonly situation: Situation;

    private readonly language: Language;

    /** The list of buyable cards (not OWNED or PLANNED), sorted by nominal value in descending order.
     *  Each element of the array contains a tuple of cardId and nominal value. */
    private readonly buyableCardsSortedDesc: [string, number][];


    constructor(pVariant: Rules, pOptions: Map<string, string>, pSituation: Situation, pLanguage: Language) {
        this.rules = pVariant;
        this.variantOptions = pOptions;
        this.situation = pSituation;
        this.language = pLanguage;
        this.buyableCardsSortedDesc = this.buildBuyableSortedList();
    }

    private buildBuyableSortedList(): [string, number][] {
        const result: [string, number][] = [];
        for (let card of this.rules.cards.values()) {
            if (!StateUtil.isFixed(this.situation.getCardState(card.id))) {
                result.push([card.id, card.dao.costNominal]);
            }
        }
        // TODO Consider prereqs, e.g. 2 cards left, must buy law+philosophy(410), can't buy democracy+philisophy(440).
        //      This is actually a possible case with 'original' rules.
        result.sort(function(a: [string, number], b:[string, number]): number {
            return a[1] > b[1] ? -1 : (a[1] < b[1] ? 1 : 0);
        });
        return result;
    }


    /**
     * Compute the maximum number of points left to be gained in the game if always the most valuable cards were
     * bought. This calculation makes sense only when the rules define a limit to the number of civilization cards
     * that a player can own.
     * @param pNumCards the number of cards that can still be bought
     * @returns the combined maximum value
     */
    private highestValueFinish(pNumCards: number): number {
        let result: number = 0;
        const cardsToBuy: [string, number][] =
            this.buyableCardsSortedDesc.slice(0, Math.min(pNumCards, this.buyableCardsSortedDesc.length));
        for (let tuple of cardsToBuy) {
            result += tuple[1];
        }
        return result;
    }


    public recalculate(): void {
        for (let card of this.rules.cards.values()) {
            const oldState: State = this.situation.getCardState(card.id);
            const currentCost: number = this.situation.getCurrentCost(card.id);
            if (StateUtil.isFixed(oldState)) {
                // leave as-is
            }
            else if (!this.situation.isPrereqMet(card.id)) {
                this.situation.setCardState(card.id, State.PREREQFAILED,
                    (this.rules.cards.get(card.dao.prereq as string) as Card).dao.names[this.language]);
            }
            else if (currentCost > this.situation.getCurrentFunds()) {
                this.situation.setCardState(card.id, State.UNAFFORDABLE);
            }
            else {
                this.situation.setCardState(card.id, State.ABSENT);
                if (typeof(this.rules.variant.cardLimit) !== undefined) {
                    const remainingCards: number = (this.rules.variant.cardLimit as number)
                        - this.situation.getNumOwnedCards() - this.situation.getNumPlannedCards();
                    if (remainingCards > 0) {
                        const highestFinish: number = this.highestValueFinish(remainingCards);
                        const missed: number = this.situation.getPointsTarget() - this.situation.getScore()
                            - this.situation.getNominalValueOfPlannedCards() - highestFinish;
                        if (missed > 0) {
                            this.situation.setCardState(card.id, State.DISCOURAGED, missed);
                        }
                    }
                }
            }
        }
    }
}
