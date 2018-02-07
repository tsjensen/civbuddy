import { CardJson, Rules, Language, Card } from '../rules/rules';
import { CardData, State, Situation, StateUtil } from '../model';



export class Calculator
{
    /** the choices that were made on options offered by the rules */
    private readonly variantOptions: Map<string, string>;

    /** the rules (a.k.a. game variant) that we are based on */
    private readonly rules: Rules;

    private readonly language: Language;


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


    public recalculate(pSituation: Situation): void {
        let discouragedPossible: boolean = true;
        for (let cardId of this.rules.getCardIdsByNominalValue()) {
            const card: Card = this.rules.cards.get(cardId) as Card;
            const oldState: State = pSituation.getCardState(cardId);
            const currentCost: number = pSituation.getCurrentCost(cardId);
            if (StateUtil.isFixed(oldState)) {
                // leave as-is
            }
            else if (!pSituation.isPrereqMet(cardId)) {
                pSituation.setCardState(cardId, State.PREREQFAILED,
                    (this.rules.cards.get(card.dao.prereq as string) as Card).dao.names[this.language]);
            }
            else if (currentCost > pSituation.getCurrentFunds()) {
                pSituation.setCardState(cardId, State.UNAFFORDABLE);
            }
            else {
                pSituation.setCardState(cardId, State.ABSENT);
                if (discouragedPossible && typeof(this.rules.variant.cardLimit) !== undefined) {
                    const numRemainingCards: number = (this.rules.variant.cardLimit as number)
                        - pSituation.getNumOwnedCards() - pSituation.getNumPlannedCards();
                    if (numRemainingCards > 0) {
                        const highestFinish: number = this.highestValueFinish(pSituation, numRemainingCards, cardId);
                        const missed: number = pSituation.getPointsTarget() - pSituation.getScore()
                            - pSituation.getNominalValueOfPlannedCards() - highestFinish;
                        if (missed > 0) {
                            pSituation.setCardState(cardId, State.DISCOURAGED, missed);
                        } else {
                            // Since all remaining cards are more expensive, they must also be okay to buy.
                            discouragedPossible = false;
                        }
                    }
                }
            }
        }
    }


    /**
     * Compute the maximum number of points left to be gained in the game if always the most valuable cards were
     * bought. This calculation makes sense only when the rules define a limit to the number of civilization cards
     * that a player can own.
     * @param pSituation the current player's situation
     * @param pNumCards the number of cards that can still be bought (including the assumed card)
     * @param pAssumedCardId the card ID of the card which is assumed to be bought next for this calculation
     * @returns the combined maximum value, including the value of the assumed card
     */
    private highestValueFinish(pSituation: Situation, pNumCards: number, pAssumedCardId: string): number {
        let result: number = 0;
        const branches: BranchConfig[] = this.setupBranches(pSituation, pNumCards, pAssumedCardId);
        for (let branch of branches) {
            const branchPoints: number = this.computeBranch(branch);
            if (branchPoints > result) {
                result = branchPoints;
            }
            if (this.isSufficient(pSituation, branchPoints)) {
                break;
            }
        }
        return result;
    }

    private setupBranches(pSituation: Situation, pMaxSteps: number, pAssumedCardId: string): BranchConfig[] {
        const allPrereqCardIds: string[] = this.rules.getPrereqCardIds();
        const variableCardIds: string[] = this.filterVariableCards(allPrereqCardIds, pSituation, pAssumedCardId);
        const variablePowerSet: string[][] = this.powerSet(variableCardIds).reverse();   // longest first
        const fixedCardIds: string[] = allPrereqCardIds.filter(x => variableCardIds.indexOf(x) < 0);
        const result: BranchConfig[] = [];
        for (let subset of variablePowerSet) {
            const boughtCardIds = fixedCardIds.concat(subset);
            result.push(new BranchConfig(this.rules, pSituation, pMaxSteps, pAssumedCardId, boughtCardIds));
        }
        return result;
    }

    private filterVariableCards(pAllPrereqCardIds: string[], pSituation: Situation, pAssumedCardId: string): string[] {
        const result: string[] = [];
        for (let cardId of pAllPrereqCardIds) {
            if (!StateUtil.isFixed(pSituation.getCardState(cardId)) && cardId !== pAssumedCardId) {
                result.push(cardId);
            }
        }
        return result;
    }


    private powerSet<T>(pSet: T[]): T[][] {
        const result: T[][] = [];
        result.push([]);
        for (let i = 1; i < Math.pow(2, pSet.length); i++) {
            const subset: T[] = [];
            for (let j = 0; j < pSet.length; j++) {
                if (i & (1 << j)) {
                    subset.push(pSet[j]);
                }
            }
            result.push(subset);
        }
        return result;
    }


    private computeBranch(pBranch: BranchConfig): number {
        const situation: Situation = pBranch.situation;
        pBranch.takeCard(pBranch.assumedCardId);
        const cardIds: string[] = this.rules.getCardIdsByNominalValue().reverse();

        let p: number = 0;
        while(p < cardIds.length && pBranch.hasStepsRemain()) {
            const cardId: string = cardIds[p];
            if (pBranch.includesCard(cardId) || StateUtil.isFixed(situation.getCardState(cardId))) {
                p++;
            }
            else {
                if (this.isPrereqMet(pBranch, cardId)) {
                    pBranch.takeCard(cardId);
                }
                else {
                    const multiStep: MultiStep = this.bestMultiStep(pBranch, cardIds, p);
                    for (let msCardId of multiStep.steps.keys()) {
                        pBranch.takeCard(msCardId);
                    }
                }
            }
        }
        return pBranch.getPoints();
    }

    private isPrereqMet(pBranch: BranchConfig, pCardId: string): boolean {
        const prereq: string | undefined = pBranch.rules.getPrereq(pCardId);
        let result: boolean = true;
        if (typeof(prereq) === 'string') {
            result = pBranch.includesCard(prereq) || StateUtil.isFixed(pBranch.situation.getCardState(prereq));
        }
        return result;
    }


    private isSufficient(pSituation: Situation, pBranchPoints: number): boolean {
        const missed: number = pSituation.getPointsTarget() - pSituation.getScore()
                - pSituation.getNominalValueOfPlannedCards() - pBranchPoints;
        return missed <= 0;
    }


    private bestMultiStep(pBranch: BranchConfig, pCardIdsDesc: string[], pOffset: number): MultiStep {
        let candidate: MultiStep = new MultiStep();
        for (let cardId of this.rules.getCardsWithPrereqs()) {
            const ms: MultiStep = new MultiStep();
            for (let step: string | undefined = cardId; typeof(step) === 'string'; step = pBranch.rules.getPrereq(step)) {
                ms.addStep(step, pBranch.rules.getNominalValue(step));
            }
            const numSteps: number = ms.steps.size;
            if (numSteps <= pBranch.maxSteps && ms.valuePerStep > candidate.valuePerStep) {
                candidate = ms;
            }
        }

        const maxLen: number = candidate.steps.size > 0 ? candidate.steps.size : 1;
        const ms: MultiStep = new MultiStep();
        for (let p = pOffset; p < pCardIdsDesc.length; p++) {
            const cardId: string = pCardIdsDesc[p];
            if (!pBranch.rules.hasPrereq(cardId)) {
                ms.addStep(cardId, pBranch.rules.getNominalValue(cardId));
                if (ms.steps.size === maxLen) {
                    break;
                }
            }
        }

        let result: MultiStep = candidate;
        if (ms.valuePerStep > candidate.valuePerStep) {
            result = ms;
        }
        return result;
    }
}


/**
 * Represents one configuration of prereqs, i.e. a complete set of decision on which prereq cards to buy.
 */
class BranchConfig
{
    private points: number = 0;
    private readonly stepsTaken: Map<string, number> = new Map();

    constructor(public readonly rules: Rules, public readonly situation: Situation,
        public readonly maxSteps: number, public readonly assumedCardId: string,
        public readonly prereqsBought: string[]) {
    }

    public validate(): boolean {
        let result: boolean = true;
        for (let cardId of this.prereqsBought) {
            if (!this.validatePrereqBought(cardId)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Check if the given card has a transitive prereq, and that transitive prereq is satisfied because it is
     * assumed to be bought by this branch config.
     * @param pCardId a card which is assumed to be bought in this branch, and which is a prereq to some other card
     */
    private validatePrereqBought(pCardId: string): boolean {
        let result: boolean = true;
        const prereq: string | undefined = this.rules.getPrereq(pCardId);
        if (typeof(prereq) === 'string') {
            result = this.prereqsBought.indexOf(prereq) >= 0;
        }
        return result;
    }


    public takeCard(pCardId: string): void {
        const nominalValue: number = (this.rules.cards.get(pCardId) as Card).dao.costNominal;
        if (!this.stepsTaken.has(pCardId)) {
            this.stepsTaken.set(pCardId, nominalValue);
            this.points += nominalValue;
        }
    }

    public getStepCount(): number {
        return this.stepsTaken.size;
    }

    public getPoints(): number {
        return this.points;
    }

    public hasStepsRemain(): boolean {
        return this.getStepCount() < this.maxSteps;
    }

    public includesCard(pCardId: string): boolean {
        return this.stepsTaken.has(pCardId);
    }
}


class MultiStep
{
    public readonly steps: Map<string, number> = new Map();

    public valuePerStep: number = 0;


    public addStep(pCardId: string, pNominalValue: number): void {
        this.steps.set(pCardId, pNominalValue);
        this.updateValuePerStep();
    }

    private updateValuePerStep(): void {
        const n: number = this.steps.size;
        if (n > 0) {
            const values: number[] = Array.from(this.steps.values());
            const sum = values.reduce((a, b) => a + b, 0);
            this.valuePerStep = sum / n;
        }
    }
}
