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


export class PlanCardActivity
    extends AbstractCardsActivity
{
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
    
        const fundsCtrl: FundsBarController = new FundsBarController();
        fundsCtrl.setRemainingFunds(this.pageContext.currentSituation.getCurrentFunds());
    }
}


export class UnplanCardActivity
    extends AbstractCardsActivity
{
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
    
            const fundsCtrl: FundsBarController = new FundsBarController();
            fundsCtrl.setRemainingFunds(this.pageContext.currentSituation.getCurrentFunds());
        }
    }
}


export class ShowCardInfoActivity
    extends AbstractCardsActivity
{
    constructor(pPageContext: CardsPageContext, public readonly cardId: string) {
        super(pPageContext);
    }


    public execute(pLanguage: Language): void
    {
        // TODO much of this must go into a CardInfoModalController
        const card: Card = this.pageContext.selectedRules.cards.get(this.cardId) as Card;
        const cardState: CardData = this.pageContext.currentSituation.getCard(this.cardId);
    
        // Border style
        let elem: JQuery<HTMLElement> = $('#cardInfoModal .modal-content');
        this.cardCtrl.changeBorderStyle(elem, cardState.state, function(pClass: string): string {
            return pClass.replace('bg-success', 'border-success');
        });
    
        // Card title
        elem = $('#cardInfoModal .modal-title');
        elem.html(card.dao.names[pLanguage] + ' (' + card.dao.costNominal + ')');
        this.cardCtrl.addGroupIcons(elem, card.dao.groups);
    
        // Current cost
        if (cardState.isOwned()) {
            hideElement($('#cardInfoModal .cardInfoModal-currentCost'));
        } else {
            elem = $('#cardInfoModal .cardInfoModal-currentCost-value');
            elem.html(String(cardState.getCurrentCost()));
            showElement($('#cardInfoModal .cardInfoModal-currentCost'));
        }
    
        // Status text
        elem = $('#cardInfoModal .cardInfoModal-status');
        if (cardState.state === State.ABSENT || cardState.state === State.PLANNED) {
            hideElement(elem);
        } else {
            this.cardCtrl.changeTextStyle(elem, cardState.state);
            this.cardCtrl.changeStateExplanationText(elem, cardState.state, cardState.stateExplanationArg);
            showElement(elem);
        }
    
        // Effects descriptions
        $('#cardInfoModal .cardInfoModal-attributes').html(card.dao.attributes[pLanguage]);
        $('#cardInfoModal .cardInfoModal-calamity-effects').html(card.dao.calamityEffects[pLanguage]);
    
        // Credit Provided
        $('#cardInfoModal .cardInfoModal-credit-provided-heading').attr('data-l10n-args',
            JSON.stringify({'totalProvided': card.maxCreditsProvided}));
            this.showListOfCards(pLanguage, $('#cardInfoModal .cardInfoModal-credit-provided-list'), card.dao.creditGiven, false);
    
        // Credit Received
        elem = $('#cardInfoModal .cardInfoModal-credit-received-list');
        $('#cardInfoModal .cardInfoModal-credit-received-heading').attr('data-l10n-args',
            JSON.stringify({'percent': Math.round((cardState.sumCreditReceived / card.maxCreditsReceived) * 100)}));
        if (cardState.isOwned()) {
            hideElement(elem);
            hideElement($('#cardInfoModal .cardInfoModal-credit-received-heading'));
        } else {
            showElement($('#cardInfoModal .cardInfoModal-credit-received-heading'));
            this.showListOfCards(pLanguage, elem, card.creditsReceived, true);
            showElement(elem);
        }
    
        // 'Discard' button
        elem = $('#cardInfoModal div.modal-footer > button:first-child');
        if (cardState.isOwned()) {
            elem.attr('cardId', card.id);
            elem.attr('data-dismiss', 'modal');
            elem.removeClass('disabled');
        } else {
            elem.removeAttr('data-dismiss');
            elem.addClass('disabled');
        }
    
        $('#cardInfoModal').modal();
    }


    private showListOfCards(pLanguage: Language, pTargetElement: JQuery<HTMLElement>,
        pCreditList: Map<string, number> | Object, pShowStatusByColor: boolean): void
    {
        pTargetElement.children().remove();
        const creditItemHtmlTemplate: string = $('#cardInfoCreditItemTemplate').html();
        const cardIds: string[] | IterableIterator<string> =
                pCreditList instanceof Map ? pCreditList.keys() : Object.keys(pCreditList);
        for (let cardId of cardIds) {
            const card: Card = this.pageContext.selectedRules.cards.get(cardId) as Card;
            const state: State = this.pageContext.currentSituation.getCardState(cardId);
            const renderedItem: string = Mustache.render(creditItemHtmlTemplate, {
                'cardTitle': card.dao.names[pLanguage],
                'creditPoints': '+' + (pCreditList instanceof Map ? pCreditList.get(cardId) : pCreditList[cardId]),
                'textColor': pShowStatusByColor ? this.getCreditItemColor(state) : ''
            });
            pTargetElement.append(renderedItem);
            const iconDiv: JQuery<HTMLElement> = pTargetElement.children().last().children('.card-groups');
            this.cardCtrl.addGroupIcons(iconDiv, card.dao.groups);
        }
    }
    
    private getCreditItemColor(pState: State): string {
        let result: string = '';
        if (pState === State.OWNED) {
            result = 'text-success';
        } else if (pState === State.PLANNED) {
            result = 'text-info';
        } else if (pState === State.PREREQFAILED || pState === State.UNAFFORDABLE) {
            result = 'text-muted';
        }
        if (result.length > 0) {
            result = ' ' + result;
        }
        return result;
    }
}


export class BuyCardsActivity
    extends AbstractCardsActivity
{
    constructor(pPageContext: CardsPageContext) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void
    {
        // perform the 'buy' operation on the model
        const cardIdsBought: string[] = this.pageContext.currentSituation.buyPlannedCards();
        if (cardIdsBought.length === 0) {
            return;  // the button was pressed without any cards planned
        }

        // update the card display accordingly
        this.syncCardStates();
        for (let cardId of cardIdsBought) {
            const supportedCardIds: string[] = Array(...this.pageContext.currentSituation.getCreditGiven(cardId).keys());
            for (let cardId of supportedCardIds) {
                this.cardCtrl.changeCreditBar(this.pageContext.currentSituation.getCard(cardId));
            }
        }

        // update the navbar accordingly
        const navbarCtrl: NavbarController = new NavbarController();
        navbarCtrl.setCardCount(this.pageContext.currentSituation.getNumOwnedCards());
        navbarCtrl.setScore(this.pageContext.currentSituation.getScore());

        // save to local storage
        this.saveSituation();
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
