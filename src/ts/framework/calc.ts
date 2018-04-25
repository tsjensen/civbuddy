import { appOptions } from '../main';
import { Card, Rules } from '../rules/rules';
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
        this.situation.initCards(appOptions.language);
    }


    /**
     * A card was planned or unplanned, so credit assignments may shift.
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


    private findOwnedGivingCards(pChangedCardId: string): string[] {
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
