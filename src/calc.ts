import { CardJson, Rules } from './rules';
import { CardData, State } from './model';


export class Calculator
{
    /** the choices that were made on options offered by the rules */
    public readonly variantOptions: Map<string, string>;

    /** the rules (a.k.a. game variant) that we are based on */
    public readonly rules: Rules;

    constructor(pVariant: Rules, pOptions: Map<string, string>) {
        this.rules = pVariant;
        this.variantOptions = pOptions;
    }

    // TODO extend model to hold data

    /**
     * The 'Cards' page has just loaded, so we have only some owned cards, but no planned ones.
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
            } else {
                data.state = State.ABSENT;
            }
            result.set(cardId, data);
        }
        return result;
    }

    // TODO perform state calculations based on various events:
    //      - situation loaded (some are owned)
    //      - 1 card put as planned
    //      - 1 card no longer planned
    //      - revise mode? clear button?
}
