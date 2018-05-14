import { Card, Rules } from '../rules/rules';
import { GlobalOptions } from '../storage/storage';
import { Situation, State, StateUtil } from './model';


/**
 * Calculates active credits and resulting current cost amounts when the "cardMultiUse" rule option is false.
 * This class is not needed / unused when the "cardMultiUse" rule option is set.
 */
export class CreditsCalculator {

    public constructor(private readonly situation: Situation, private readonly rules: Rules) { }


    public recalculate(pChangedCardId?: string): void {
        if (!this.rules.ruleOptionCardMultiUse) {
            if (typeof (pChangedCardId) === 'undefined') {
                this.resetAll();
            } else {
                this.recalcAfterChange(pChangedCardId);
            }
        }
    }


    /**
     * All credits are now active, because no cards are planned (anymore).
     */
    private resetAll(): void {
        this.situation.initCards(new GlobalOptions().get().language);
    }


    /**
     * A card was planned or unplanned, so credit assignments may shift.
     *
     * **Algorithm**
     * 1. Find all cards which support the given card (the "ownedGivingCards").
     * 2. For each "ownedGivingCard":
     *    1. Find all cards supported by the "ownedGivingCard" which are PLANNED.
     *    2. Of those planned cards, select the one which receives the most credit (the "winner").
     *    3. Find all cards supported by the "ownedGivingCard" which are NOT OWNED (and thus have a credit bar).
     *    4. For each such non-owned card:
     *       - If the card is the "winner" chosen above, give it the full credit.
     *       - If not, subtract the winner's credit from the given credit (without going below zero) and set that.
     *
     * **Example**
     * 1. HAVE Mysticism
     * 2. PLAN Music (for 55 instead of 60);
     *    This must update the cost of Medicine from 120 to 125 (and many other cards, too)
     * 3. PLAN Medicine (for 125 instead of 140)
     * 4. Buy Medicine and Music for 180 total, using the Mysticism bonus for Medicine
     *    (as it's 20, which is better than the 5 for Music)
     *
     * @param pChangedCardId card ID of the card that changed its state
     */
    public recalcAfterChange(pChangedCardId: string): void {
        const ownedGivingCardIds: string[] = this.findOwnedGivingCards(pChangedCardId);
        for (const ogcId of ownedGivingCardIds) {
            const ogc: Card = this.rules.cards.get(ogcId) as Card;
            const planned: Map<string, number> = this.filterPlanned(ogc.dao.creditGiven);
            const winner: string | undefined = this.determineHighestCredit(planned);

            for (const affectedCardId of Object.keys(ogc.dao.creditGiven)) {
                if (!this.situation.isCardState(affectedCardId, State.OWNED)) {
                    let effectiveCredit: number = (ogc.dao.creditGiven as any)[affectedCardId];
                    if (typeof (winner) !== 'undefined' && affectedCardId !== winner) {
                        const hc: number = (ogc.dao.creditGiven as any)[winner];
                        effectiveCredit = Math.max(0, effectiveCredit - hc);
                    }
                    this.situation.changeCredit(affectedCardId, ogcId, effectiveCredit);
                }
            }
        }
    }


    private filterPlanned(pDaoCreditGiven: object): Map<string, number> {
        const result: Map<string, number> = new Map();
        for (const affectedCardId of Object.keys(pDaoCreditGiven)) {
            if (this.situation.isCardState(affectedCardId, State.PLANNED)) {
                result.set(affectedCardId, (pDaoCreditGiven as any)[affectedCardId]);
            }
        }
        return result;
    }


    private determineHighestCredit(pCreditsToPlanned: Map<string, number>): string | undefined {
        let result: string | undefined = undefined;
        let hc: number = -1;
        for (const [cardId, credit] of pCreditsToPlanned.entries()) {
            if (credit > hc) {
                hc = credit;
                result = cardId;
            }
        }
        return result;
    }


    public findOwnedGivingCards(pChangedCardId: string): string[] {
        const card: Card = this.rules.cards.get(pChangedCardId) as Card;
        const result: string[] = [];
        for (const sourceCardId of card.creditsReceived.keys()) {
            if (StateUtil.isOwned(this.situation.getCardState(sourceCardId))) {
                result.push(sourceCardId);
            }
        }
        return result;
    }
}
