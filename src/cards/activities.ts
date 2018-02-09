import * as Mustache from 'mustache';
import * as storage from '../storage/storage';
import { Activity, Page } from '../framework';
import { CardsPageContext } from './init';
import { Language, Card } from '../rules/rules';
import { CardController, FundsBarController, NavbarController, CardInfoModalController } from './controllers';
import { State, CardData, StateUtil, Situation } from '../model';
import { FundsDao, FundsDaoImpl } from '../storage/dao';
import { showElement, hideElement } from '../util';
import { buttonClick, appOptions } from '../main';



abstract class AbstractCardsActivity
    implements Activity<void>
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
    implements Activity<void>
{
    constructor(protected readonly pageContext: CardsPageContext, public readonly cardId: string) { }

    public execute(pLanguage: Language): void
    {
        if (this.pageContext.hoversOnCard(this.cardId)) {
            const currentState: State = this.pageContext.currentSituation.getCardState(this.cardId);
            if (currentState === State.ABSENT || currentState === State.DISCOURAGED) {
                buttonClick($('#card-' + this.cardId).get(0), Page.CARDS, 'plan', this.cardId);
            }
            else if (currentState === State.PLANNED) {
                buttonClick($('#card-' + this.cardId).get(0), Page.CARDS, 'unplan', this.cardId);
            }
        } else {
            buttonClick($('#card-' + this.cardId).get(0), Page.CARDS, 'info', this.cardId);
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
    
        this.fundsCtrl.setRemainingFunds(this.pageContext.currentSituation.getCurrentFunds());
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
    
            this.fundsCtrl.setRemainingFunds(this.pageContext.currentSituation.getCurrentFunds());
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
        const filtered: boolean = this.pageContext.currentSituation.isCardFilterActive();
        for (let cardId of this.pageContext.currentSituation.getCardIdIterator()) {
            this.applyFilterToCard(cardId, this.pageContext.currentSituation.getCardState(cardId), filtered);
        }
        // TODO update icon
        if (filtered) {
            showElement($('#filterHint'));
        } else {
            hideElement($('#filterHint'));
        }
    }
    
    private applyFilterToCard(pCardId: string, pState: State, pFiltered: boolean): void {
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId);
        if (pFiltered && StateUtil.isHiddenByFilter(pState)) {
            hideElement(elem);
        } else {
            showElement(elem);
        }
    }
}



/**
 * Enable the user to enter information on available funds, and process it.
 */
export class EnterFundsActivity
    extends AbstractCardsActivity
{
    constructor(pPageContext: CardsPageContext) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void
    {
        // TODO replace workaround with real 'funds' page invocation
        const s: string | null = window.prompt('Enter total funds (no \'funds\' page yet):');
        if (s !== null && s.trim().length > 0) {
            let totalFunds: number = Number(s);
            if (!isNaN(totalFunds) && totalFunds >= 0) {
                totalFunds = Math.round(totalFunds);
                const newFunds: FundsDao = new FundsDaoImpl(totalFunds, {}, 0, false);
                this.pageContext.currentSituation.updateTotalFunds(newFunds);
                this.saveSituation();
                window.location.reload();
            }
        }
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
