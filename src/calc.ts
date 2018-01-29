import { CardJson, Rules, Language, Card } from './rules';
import { CardData, State, Situation, StateUtil } from './model';



export class Calculator
{
    /** the choices that were made on options offered by the rules */
    private readonly variantOptions: Map<string, string>;

    /** the rules (a.k.a. game variant) that we are based on */
    private readonly rules: Rules;

    private readonly language: Language;

    /** The list of buyable cards (not OWNED or PLANNED), sorted by nominal value in descending order.
     *  Each element of the array contains a tuple of cardId and nominal value. */
    private buyableCardsSortedDesc: [string, number][] | undefined = undefined;


    constructor(pRules: Rules, pGameOptions: Map<string, string>, pLanguage: Language) {
        this.rules = pRules;
        this.variantOptions = pGameOptions;
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

    private getOrBuildBuyableSortedList(pSituation: Situation): [string, number][] {
        if (this.buyableCardsSortedDesc !== undefined) {
            return this.buyableCardsSortedDesc;
        }
        const result: [string, number][] = [];
        for (let card of this.rules.cards.values()) {
            if (!StateUtil.isFixed(pSituation.getCardState(card.id))) {
                result.push([card.id, card.dao.costNominal]);
            }
        }
        // TODO Consider prereqs, e.g. 2 cards left, must buy law+philosophy(410), can't buy democracy+philosophy(440).
        //      This is actually a possible case with 'original' rules.
        result.sort(function(a: [string, number], b:[string, number]): number {
            return a[1] > b[1] ? -1 : (a[1] < b[1] ? 1 : 0);
        });
        this.buyableCardsSortedDesc = result;
        return result;
    }


    /**
     * Compute the maximum number of points left to be gained in the game if always the most valuable cards were
     * bought. This calculation makes sense only when the rules define a limit to the number of civilization cards
     * that a player can own.
     * @param pSituation the current player's situation
     * @param pNumCards the number of cards that can still be bought
     * @returns the combined maximum value
     */
    private highestValueFinish(pSituation: Situation, pNumCards: number): number {
        let result: number = 0;
        const buyableCardsSortedDesc: [string, number][] = this.getOrBuildBuyableSortedList(pSituation);
        const cardsToBuy: [string, number][] =
                buyableCardsSortedDesc.slice(0, Math.min(pNumCards, buyableCardsSortedDesc.length));
        for (let tuple of cardsToBuy) {
            result += tuple[1];
        }
        return result;
    }


    public recalculate(pSituation: Situation): void {
        for (let card of this.rules.cards.values()) {
            const oldState: State = pSituation.getCardState(card.id);
            const currentCost: number = pSituation.getCurrentCost(card.id);
            if (StateUtil.isFixed(oldState)) {
                // leave as-is
            }
            else if (!pSituation.isPrereqMet(card.id)) {
                pSituation.setCardState(card.id, State.PREREQFAILED,
                    (this.rules.cards.get(card.dao.prereq as string) as Card).dao.names[this.language]);
            }
            else if (currentCost > pSituation.getCurrentFunds()) {
                pSituation.setCardState(card.id, State.UNAFFORDABLE);
            }
            else {
                pSituation.setCardState(card.id, State.ABSENT);
                if (typeof(this.rules.variant.cardLimit) !== undefined) {
                    const numRemainingCards: number = (this.rules.variant.cardLimit as number)
                        - pSituation.getNumOwnedCards() - pSituation.getNumPlannedCards();
                    // TODO FIXME For this calculation, we must assume that the current card was PLANNED,
                    //      *in addition* to any other planned cards. So it cannot be part of the highestFinish etc.
                    if (numRemainingCards > 0) {
                        const highestFinish: number = this.highestValueFinish(pSituation, numRemainingCards);
                        const missed: number = pSituation.getPointsTarget() - pSituation.getScore()
                            - pSituation.getNominalValueOfPlannedCards() - highestFinish;
                        if (missed > 0) {
                            pSituation.setCardState(card.id, State.DISCOURAGED, missed);
                        }
                    }
                }
            }
        }
    }
}
