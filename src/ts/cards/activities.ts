import { Activity, Page } from '../framework';
import { appOptions, runActivityInternal } from '../main';
import { CardData, State, StateUtil } from '../model';
import { Card, Language } from '../rules/rules';
import * as storage from '../storage/storage';
import { CardController, CardInfoModalController, FundsBarController, NavbarController } from './controllers';
import { CardsPageContext } from './init';



abstract class AbstractCardsActivity
    implements Activity
{
    protected readonly cardCtrl: CardController;

    constructor(protected readonly pageContext: CardsPageContext) {
        this.cardCtrl = new CardController(pageContext.selectedRules.cards, appOptions.language);
    }

    abstract execute(pLanguage: Language): void;

    
    /**
     * Ensure that the displayed card states are what we have in the given situation.
     */
    protected syncCardStates(): void {
        const stateMap: Map<string, CardData> = new Map();
        for (let cardId of this.pageContext.currentSituation.getCardIdIterator()) {
            const cardState: CardData = this.pageContext.currentSituation.getCard(cardId);
            stateMap.set(cardId, cardState);
        }
        this.cardCtrl.syncCardStates(stateMap, this.pageContext.selectedRules.maxCredits);
    }

    protected saveSituation(): void {
        storage.saveSituation(this.pageContext.currentSituation.getDaoForStorage());
    }
}



/** 
 * Dispatcher command which delegates to either plan, unplan, or info commands.
 */
export class ClickOnCardActivity
    implements Activity
{
    constructor(protected readonly pageContext: CardsPageContext, public readonly cardId: string) { }

    public execute(pLanguage: Language): void
    {
        if (this.pageContext.hoversOnCard(this.cardId)) {
            const currentState: State = this.pageContext.currentSituation.getCardState(this.cardId);
            if (currentState === State.ABSENT || currentState === State.DISCOURAGED) {
                runActivityInternal(Page.CARDS, 'plan', this.cardId);
            }
            else if (currentState === State.PLANNED) {
                runActivityInternal(Page.CARDS, 'unplan', this.cardId);
            }
        } else {
            runActivityInternal(Page.CARDS, 'info', this.cardId);
        }
    }
}



/**
 * A plannable card is clicked, so it becomes PLANNED.
 */
export class PlanCardActivity
    extends AbstractCardsActivity
{
    private readonly fundsCtrl: FundsBarController = new FundsBarController();

    constructor(pPageContext: CardsPageContext, public readonly cardId: string) {
        super(pPageContext);
    }


    public execute(pLanguage: Language): void
    {
        const changedCreditBars: string[] = this.pageContext.currentSituation.planCard(this.cardId);

        const cardState: CardData = this.pageContext.currentSituation.getCard(this.cardId);
        this.cardCtrl.changeState(cardState, this.pageContext.selectedRules.maxCredits);
        for (let targetCardId of changedCreditBars) {
            const targetState: CardData = this.pageContext.currentSituation.getCard(targetCardId);
            this.cardCtrl.changeCreditBarPlanned(targetState);
        }
        this.syncCardStates();
    
        this.fundsCtrl.setRemainingFunds(this.pageContext.currentSituation.currentFunds);
    }
}



/**
 * A PLANNED card is clicked again, so it will no longer be PLANNED. A new state is calculated.
 */
export class UnplanCardActivity
    extends AbstractCardsActivity
{
    private readonly fundsCtrl: FundsBarController = new FundsBarController();

    constructor(pPageContext: CardsPageContext, public readonly cardId: string) {
        super(pPageContext);
    }


    public execute(pLanguage: Language): void
    {
        if (this.pageContext.currentSituation.getCardState(this.cardId) === State.PLANNED) {
            const changedCreditBars: string[] = this.pageContext.currentSituation.unplanCard(this.cardId);
    
            const cardState: CardData = this.pageContext.currentSituation.getCard(this.cardId);
            this.cardCtrl.changeState(cardState, this.pageContext.selectedRules.maxCredits);
            for (let targetCardId of changedCreditBars) {
                const targetState: CardData = this.pageContext.currentSituation.getCard(targetCardId);
                this.cardCtrl.changeCreditBarPlanned(targetState);
            }
            this.syncCardStates();
    
            this.fundsCtrl.setRemainingFunds(this.pageContext.currentSituation.currentFunds);
        }
    }
}


export class ShowCardInfoActivity
    extends AbstractCardsActivity
{
    private readonly modalCtrl: CardInfoModalController = new CardInfoModalController();

    constructor(pPageContext: CardsPageContext, public readonly cardId: string) {
        super(pPageContext);
    }


    public execute(pLanguage: Language): void
    {
        const card: Card = this.pageContext.selectedRules.cards.get(this.cardId) as Card;
        const cardState: CardData = this.pageContext.currentSituation.getCard(this.cardId);
        const received: Map<string, [Card, State, number]> = this.getAffectedCardInfo(card.creditsReceived);
        const given: Map<string, [Card, State, number]> = this.getAffectedCardInfo(card.dao.creditGiven);
        this.modalCtrl.initModal(card, cardState, pLanguage, given, received);
        this.modalCtrl.showModal();
    }

    private getAffectedCardInfo(pAffect: Map<string, number> | Object): Map<string, [Card, State, number]> {
        const result: Map<string, [Card, State, number]> = new Map();
        const affectedCardIds: string[] | IterableIterator<string> =
                pAffect instanceof Map ? pAffect.keys() : Object.keys(pAffect);
        for (let affectedCardId of affectedCardIds) {
            const card: Card = this.pageContext.selectedRules.cards.get(affectedCardId) as Card;
            const state: State = this.pageContext.currentSituation.getCardState(affectedCardId);
            const amount: number = pAffect instanceof Map ? (pAffect.get(affectedCardId) as number) : pAffect[affectedCardId];
            result.set(affectedCardId, [card, state, amount]);
        }
        return result;
    }
}



/**
 * Buy the cards currently marked as PLANNED. Do nothing if no cards are marked.
 */
export class BuyCardsActivity
    extends AbstractCardsActivity
{
    private readonly navbarCtrl: NavbarController = new NavbarController();

    constructor(pPageContext: CardsPageContext) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void
    {
        // FIXME Advanced, no civ cards, 109 funds, buy metalworking, credits not applied to mining
        const cardIdsBought: string[] = this.pageContext.currentSituation.buyPlannedCards();
        if (cardIdsBought.length === 0) {
            return;  // the button was pressed without any cards planned
        }
        this.updateCardDisplay(cardIdsBought);
        this.updateNavbar();
        this.saveSituation();
    }

    private updateCardDisplay(pCardIdsBought: string[]): void {
        this.syncCardStates();
        for (let cardId of pCardIdsBought) {
            const targetCardIds: string[] = Array(...this.pageContext.currentSituation.getCreditGiven(cardId).keys());
            for (let targetCardId of targetCardIds) {
                this.cardCtrl.changeCreditBar(this.pageContext.currentSituation.getCard(targetCardId));
            }
        }
    }

    private updateNavbar(): void {
        this.navbarCtrl.setCardCount(this.pageContext.currentSituation.getNumOwnedCards());
        this.navbarCtrl.setScore(this.pageContext.currentSituation.getScore());
    }
}



export class ToggleCardsFilterActivity
    extends AbstractCardsActivity
{
    constructor(pPageContext: CardsPageContext) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void
    {
        const filtered: boolean = !this.pageContext.currentSituation.isCardFilterActive();
        this.pageContext.currentSituation.setCardFilterActive(filtered);
        this.applyCardsFilter();
        window.setTimeout(this.saveSituation.bind(this), 100);
    }
    
    public applyCardsFilter() {
        const isFilterActive: boolean = this.pageContext.currentSituation.isCardFilterActive();
        for (let cardId of this.pageContext.currentSituation.getCardIdIterator()) {
            const isCardVisible: boolean = !StateUtil.isHiddenByFilter(this.pageContext.currentSituation.getCardState(cardId));
            this.cardCtrl.applyFilterToCard(cardId, isFilterActive, isCardVisible);
        }
        this.cardCtrl.updateFilterIcon(isFilterActive);
        this.cardCtrl.showFilterHint(isFilterActive);
    }
}



/** 
 * Discard a civilization card using the red button on the card info modal.
 */
export class DiscardCardActivity
    extends AbstractCardsActivity
{
    private readonly modalCtrl: CardInfoModalController = new CardInfoModalController();

    constructor(pPageContext: CardsPageContext) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void
    {
        if (!this.modalCtrl.isDiscardButtonDisabled()) {
            const cardId: string = this.modalCtrl.getCardIdFromDiscardButton();
            this.pageContext.currentSituation.discard(cardId);
            this.saveSituation();
            window.location.reload();
        }
    }
}
