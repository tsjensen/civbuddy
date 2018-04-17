import * as Mustache from 'mustache';

import { BaseController, BaseNavbarController } from '../framework/framework';
import { CardData, State } from '../framework/model';
import { Card, CardGroup, Language } from '../rules/rules';
import { CardsPageContext } from './init';



/**
 * Manages the display of a card.
 */
export class CardController
    extends BaseController
{
    constructor(private readonly cards: Map<string, Card>, private readonly language: Language) {
        super();
    }


    public static handleCustomHover(pPageContext: CardsPageContext, pCardId: string): void {
        const cardElem: JQuery<HTMLElement> = $('#card-' + pCardId + ' > div.card-civbuddy');
        if (pPageContext.hoversOnCard(pCardId)) {
            cardElem.addClass('hovered');
        } else {
            cardElem.removeClass('hovered');
        }
    }


    private getCard(pCardId: string): Card {
        return this.cards.get(pCardId) as Card;
    }

    /**
     * Add a card to the display. If it exists already, the existing card is replaced.
     * @param pCardState the runtime card information, representing its current state
     * @param pOverallMaxCredits the highest amount of credit received by any card (from rules)
     */
    public putCard(pCardState: CardData, pHtmlTemplate: string, pOverallMaxCredits: number): void
    {
        const dh: DisplayHelper = new DisplayHelper();
        const card: Card = this.getCard(pCardState.id);
        const state: State = pCardState.state;
        const renderedCard: string = Mustache.render(pHtmlTemplate, {
            borderStyle: dh.getBorderStyle(state),
            cardId: card.id,
            cardTitle: (<any> card.dao.names)[this.language],
            costCurrent: pCardState.getCurrentCost(),
            costNominal: card.dao.costNominal,
            creditBarOwnedPercent: Math.round((pCardState.sumCreditReceived / card.maxCreditsReceived) * 100),
            creditBarOwnedValue: pCardState.sumCreditReceived,
            creditBarPlannedPercent: Math.round((pCardState.sumCreditReceivedPlanned / card.maxCreditsReceived) * 100),
            creditBarPlannedValue: pCardState.sumCreditReceivedPlanned,
            creditBarWidth: Math.round((card.maxCreditsReceived / pOverallMaxCredits) * 100),
            explArgs: dh.getExplanationArgumentJson(pCardState.stateExplanationArg),
            isOwned: state === State.OWNED,
            status: State[state].toString().toLowerCase(),
            textStyle: dh.getTextStyle(state),
            totalCredit: card.maxCreditsReceived
        });

        // Add the new card to the list, or replace an already existing card of the same ID
        const cardElem: JQuery<HTMLElement> = $('#card-' + card.id);
        if (cardElem.length) {
            cardElem.replaceWith(renderedCard);
        } else {
            $('#cardList').append(renderedCard);
        }

        // set credit bar info text
        this.setCreditBarInfoText(pCardState);

        // card group icons
        const iconDiv: JQuery<HTMLElement> = $('#card-' + card.id + ' .card-header');
        dh.addGroupIcons(iconDiv, card.dao.groups);
    }


    public changeState(pCardState: CardData, pOverallMaxCredits: number): void
    {
        const dh: DisplayHelper = new DisplayHelper();
        const oldState: State | undefined = this.getDisplayedStatus(pCardState.id);
        const newState: State = pCardState.state;
        if (newState === State.OWNED) {
            this.putCard(pCardState, $('#cardTemplate').html(), pOverallMaxCredits);
            BaseController.addButtonClickHandlers('#card-' + pCardState.id);
            return;
        }

        // border color
        let elem: JQuery<HTMLElement> = $('#card-' + pCardState.id + ' div.card-civbuddy');
        dh.changeBorderStyle(elem, newState);

        // status class
        elem.removeClass(function(index: number, className: string): string {
            return (className.match(/\b(?:card-status-\S+)/g) || []).join(' ');
        });
        elem.addClass('card-status-' + State[newState].toLowerCase());

        // state explanantion text
        elem = $('#card-' + pCardState.id + ' div.card-body > p.card-status-expl');
        dh.changeTextStyle(elem, newState);
        if (oldState !== State.DISCOURAGED || newState !== State.PLANNED) {
            dh.changeStateExplanationText(elem, newState, pCardState.stateExplanationArg);
            elem.removeClass('d-block');
        } else {
            elem.addClass('d-block');
        }
    }


    /**
     * Change the current cost displayed for a card.
     * @param pCardId the card ID of the card to change
     * @param pNewCurrentCost the new value for the current cost to display
     */
    public changeCurrentCost(pCardId: string, pNewCurrentCost: number): void {
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' div.card-current-cost');
        elem.html(String(pNewCurrentCost));
    }

    /**
     * Change the 'planned' value of a card's credit bar.
     * @param pCardState the runtime state of the card to be reflected by the 'planned' value of the credit bar
     */
    public changeCreditBarPlanned(pCardState: CardData): void {
        this.changeCreditBarFragment(pCardState.id, pCardState.sumCreditReceivedPlanned, false);
        this.setCreditBarInfoText(pCardState);
    }

    /**
     * Update the entire credit bar of a card to the values from the current situation.
     * @param pCardId the card's ID
     * @param pCardState the card's runtime state
     */
    public changeCreditBar(pCardState: CardData): void {
        if (!pCardState.isOwned()) {
            this.changeCreditBarFragment(pCardState.id, pCardState.sumCreditReceived, true);
            this.changeCreditBarFragment(pCardState.id, pCardState.sumCreditReceivedPlanned, false);
            this.setCreditBarInfoText(pCardState);
        }
    }

    private changeCreditBarFragment(pCardId: string, pNewValue: number, pIsOwned: boolean): void {
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' div.progress > div.bar-'
                + (pIsOwned ? 'owned' : 'planned'));
        elem.attr('aria-valuenow', pNewValue);
        if (pIsOwned || pNewValue > 0) {
            const cardMaxCredits: number = this.getCard(pCardId).maxCreditsReceived;
            const percent: number = Math.round((pNewValue / cardMaxCredits) * 100);
            elem.attr('style', 'width: ' + percent + '%');
            this.showElement(elem);
        } else {
            this.hideElement(elem);
        }
    }

    private setCreditBarInfoText(pCardState: CardData): void {
        const creditInfoElem: JQuery<HTMLElement> = $('#card-' + pCardState.id + ' .card-credits-info');
        const l10nArgs: string = this.buildL10nArgs(pCardState);
        creditInfoElem.attr('data-l10n-args', l10nArgs);
        let l10nId: string = 'cards-card-credits';
        if (pCardState.creditReceivedPlanned.size > 0) {
            l10nId += '-plan';
        }
        creditInfoElem.attr('data-l10n-id', l10nId);
    }

    private buildL10nArgs(pCardState: CardData): string {
        const card: Card = this.getCard(pCardState.id);
        const d: object = {
            currentCards: pCardState.creditReceived.size,
            currentCredits: pCardState.sumCreditReceived,
            maxCards: card.creditsReceived.size,
            maxCredits: card.maxCreditsReceived,
            plannedCards: pCardState.creditReceived.size + pCardState.creditReceivedPlanned.size,
            plannedCredits: pCardState.sumCreditReceived + pCardState.sumCreditReceivedPlanned
        };
        return JSON.stringify(d);
    }


    /**
     * Ensure that the displayed card states are what we have in the given situation.
     * @param pStateMap information about each card according to the model
     * @param pOverallMaxCredits the highest amount of credit received by any card (from rules)
     * @param pIncludeCost flag indicating whether current cost and blue credit bar should be synced, too
     */
    public syncCardStates(pStateMap: Map<string, CardData>, pOverallMaxCredits: number, pIncludeCost: boolean): void {
        const dh: DisplayHelper = new DisplayHelper();
        for (const cardId of pStateMap.keys()) {
            const cardState: CardData = pStateMap.get(cardId) as CardData;
            const currentState: State | undefined = this.getDisplayedStatus(cardId);
            if (cardState.state === currentState) {
                const stateArg: string | number | undefined = cardState.stateExplanationArg;
                if (typeof(stateArg) === 'number') {
                    const elem: JQuery<HTMLElement> = $('#card-' + cardId + ' p.card-status-expl');
                    elem.attr('data-l10n-args', dh.getExplanationArgumentJson(stateArg) as string);
                }
            } else {
                this.changeState(cardState, pOverallMaxCredits);
            }
            if (pIncludeCost) {
                this.changeCreditBarFragment(cardId, cardState.sumCreditReceived, true);
                this.setCreditBarInfoText(cardState);
                this.changeCurrentCost(cardId, cardState.getCurrentCost());
            }
        }
    }


    /**
     * Return the state of the given card which is currently displayed, determined by looking at the DOM.
     * @param pCardId the card ID
     * @returns the current state, or `undefined` if the state could not be determined
     */
    private getDisplayedStatus(pCardId: string): State | undefined {
        let result: State | undefined = undefined;
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' > div:first-child');
        const classValue: string | undefined = elem.attr('class');
        if (typeof(classValue) === 'string') {
            const match = classValue.match(/\bcard-status-(\w+)\b/);
            if (match !== null && match.length >= 2) {
                result = State[match[1].toUpperCase() as keyof typeof State];
            }
        }
        return result;
    }


    public updateFilterIcon(pFilterActive: boolean): void {
        const openEye: JQuery<HTMLElement> = $('#eye-open');
        const closedEye: JQuery<HTMLElement> = $('#eye-closed');
        if (pFilterActive) {
            this.hideElement(openEye);
            this.showElement(closedEye);
        } else {
            this.hideElement(closedEye);
            this.showElement(openEye);
        }
    }

    public applyFilterToCard(pCardId: string, pFilterActive: boolean, pCardVisible: boolean): void {
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId);
        if (pFilterActive && !pCardVisible) {
            this.hideElement(elem);
        } else {
            this.showElement(elem);
        }
    }

    public showFilterHint(pIsHintShown: boolean): void {
        if (pIsHintShown) {
            this.showElement($('#filterHint'));
        } else {
            this.hideElement($('#filterHint'));
        }
    }

    public detachFilterHint(): JQuery<HTMLElement> {
        return $('#filterHint').detach();
    }

    public reattachFilterHint(pElement: JQuery<HTMLElement>): void {
        $('#cardList').append(pElement);
    }

    public clearCardList(): void {
        $('#cardList > div').remove();
    }


    public addGameIdToLinks(pGameId: string): void {
        $('a.add-game-id').attr('href', 'players.html?ctx=' + pGameId);
    }

    public addSituationIdToLinks(pSituationId: string): void {
        $('a.add-situation-id').attr('href', 'funds.html?ctx=' + pSituationId);
    }
}



/**
 * Manages the display of the navigation bar.
 */
export class NavbarController
    extends BaseNavbarController
{
    public constructor() {
        super();
    }

    public setCardCount(pNumCards: number): void {
        $('#navbarCards .navbarCurrentNumCards').html(String(pNumCards));
    }

    public setCardsLimit(pMaxCards: number | undefined | null): void {
        const elem: JQuery<HTMLElement> = $('#navbarCards .navbarMaxCards');
        if (typeof(pMaxCards) === 'number') {
            elem.html('/' + pMaxCards);
            this.showElement(elem);
        } else {
            this.hideElement(elem);
        }
    }

    public setScore(pScore: number): void {
        $('.navbar .navbarCurrentPoints').html(String(pScore));
    }

    public setPointsTarget(pPointsTarget: number): void {
        $('.navbar .navbarPointsTarget').html('/' + pPointsTarget);
    }
}



/**
 * Manages the display of the funds bar (footer).
 */
export class FundsBarController
    extends BaseController
{
    public constructor() {
        super();
    }

    /**
     * The available funds have changed. This happens only when the page loads, usually upon returning from the 'funds'
     * page.
     * @param pMax the new total funds
     */
    public setTotalAvailableFunds(pMax: number): void {
        const elem: JQuery<HTMLElement> = $('.footer div.progress-bar');
        const max: number = Math.max(pMax, 0);
        elem.attr('aria-valuenow', String(max));
        elem.attr('aria-valuemax', String(max));
        elem.attr('style', 'width: 100%');
        this.setInfoText(max, max);
    }

    /**
     * The remaining funds have changed. This happens when a card is planned or unplanned.
     * @param pRemaining the new value of remaining funds
     */
    public setRemainingFunds(pRemaining: number): void {
        const elem: JQuery<HTMLElement> = $('.footer div.progress-bar');
        const max: number = Number(elem.attr('aria-valuemax'));
        const remaining: number = Math.min(Math.max(pRemaining, 0), max);
        const percent: number = Math.round((remaining / max) * 100);
        elem.attr('aria-valuenow', String(remaining));
        elem.attr('style', `width: ${percent}%`);
        this.setInfoText(remaining, max);
    }

    private setInfoText(pRemaining: number, pMax: number): void {
        const elem: JQuery<HTMLElement> = $('.footer p.funds-info-text');
        elem.attr('data-l10n-args', JSON.stringify({
            fundsAvailable: pMax,
            fundsCurrent: pRemaining
        }));
    }
}



/**
 * Common display manipulation operations shared by multiple controllers on this page.
 */
class DisplayHelper
{
    /**
     * Modify the border CSS class of the given element to match a new card state.
     * @param pElement the element whose border to change
     * @param pNewState the new card state
     * @param pFilterFunc an optional filter to modify the new CSS class before it is being activated
     */
    public changeBorderStyle(pElement: JQuery<HTMLElement>, pNewState: State, pFilterFunc?: (s: string) => string):
         void
    {
        pElement.removeClass(function(index: number, className: string): string {
            return (className.match(/\b(?:bg-success|border-\S+)/g) || []).join(' ');
        });
        let newClass: string = this.getBorderStyle(pNewState);
        if (pFilterFunc !== undefined) {
            newClass = pFilterFunc(newClass);
        }
        pElement.addClass(newClass);
    }


    public getBorderStyle(pState: State): string {
        let result: string = '';
        switch (pState) {
            case State.ABSENT:       result = 'border-success'; break;
            case State.DISCOURAGED:  result = 'border-warning'; break;
            case State.OWNED:        result = 'bg-primary'; break;
            case State.PLANNED:      result = 'bg-success'; break;
            case State.PREREQFAILED: result = 'border-danger'; break;
            case State.UNAFFORDABLE: result = 'border-danger'; break;
            default: result = ''; /* empty */ break;
        }
        return result;
    }


    public changeTextStyle(pElement: JQuery<HTMLElement>, pNewState: State): void {
        pElement.removeClass(function(index: number, className: string): string {
            return (className.match(/\btext-\S+/g) || []).join(' ');
        });
        const newStyle: string = this.getTextStyle(pNewState);
        if (newStyle.length > 0) {
            pElement.addClass(newStyle);
        }
    }

    public getTextStyle(pState: State): string {
        let result: string = '';
        switch (pState) {
            case State.ABSENT:       result = ''; /* empty */ break;
            case State.DISCOURAGED:  result = 'text-warning'; break;
            case State.OWNED:        result = ''; /* empty */ break;
            case State.PLANNED:      result = ''; /* empty */ break;
            case State.PREREQFAILED: result = 'text-danger'; break;
            case State.UNAFFORDABLE: result = 'text-danger'; break;
            default: result = ''; /* empty */ break;
        }
        return result;
    }


    public addGroupIcons(pTargetElement: JQuery<HTMLElement>, pGroups: CardGroup[]): void {
        const groupIconHtmlTemplate: string = $('#groupIconTemplate').html();
        for (const group of Array.from(pGroups).reverse()) {
            const lowerCaseName: string = group.toString().toLowerCase();
            const renderedIcon: string = Mustache.render(groupIconHtmlTemplate, {
                iconName: lowerCaseName,
                inline: group === CardGroup.ARTS || group === CardGroup.SCIENCES
            });
            pTargetElement.prepend(renderedIcon);
        }
    }


    /**
     * Modify the text String explaining the current card state.
     * @param pElement the DOM element whose text to modify
     * @param pNewState the new card state
     * @param pStateArg if the card state has an argument, this would be it
     *      (for example, number of points missing, name of required prereq card)
     */
    public changeStateExplanationText(pElement: JQuery<HTMLElement>, pNewState: State, pStateArg?: string | number):
        void
    {
        if (pStateArg !== undefined) {
            pElement.attr('data-l10n-args', this.getExplanationArgumentJson(pStateArg) as string);
        }
        pElement.attr('data-l10n-id', `cards-card-${State[pNewState].toLowerCase()}-expl`);
    }

    public getExplanationArgumentJson(pStateExplanationArg: string | number | undefined): string | undefined {
        let result: string | undefined = undefined;
        if (typeof(pStateExplanationArg) !== 'undefined') {
            result = JSON.stringify({arg: pStateExplanationArg});
        }
        return result;
    }
}



/**
 * Manages the display of the card info modal.
 */
export class CardInfoModalController
    extends BaseController
{
    public constructor() {
        super();
    }


    public initModal(pCard: Card, pCardState: CardData, pLanguage: Language,
        pCreditGiven: Map<string, [Card, State, number]>, pCreditReceived: Map<string, [Card, State, number]>): void
    {
        const dh: DisplayHelper = new DisplayHelper();
        const discouragedPlanned: boolean = typeof(pCardState.stateExplanationArg) === 'number';

        // Border style
        let elem: JQuery<HTMLElement> = $('#cardInfoModal .modal-content');
        const borderState: State = discouragedPlanned ? State.DISCOURAGED : pCardState.state;
        dh.changeBorderStyle(elem, borderState, function(pClass: string): string {
            return pClass.replace('bg-success', 'border-success').replace('bg-primary', 'border-primary');
        });

        // Card title
        this.setHeaderBackground(pCardState.state, discouragedPlanned);
        elem = $('#cardInfoModal .modal-title');
        elem.html((<any> pCard.dao.names)[pLanguage] + ' (' + pCard.dao.costNominal + ')');
        dh.addGroupIcons(elem, pCard.dao.groups);

        // Current cost
        if (pCardState.isOwned()) {
            this.hideElement($('#cardInfoModal .cardInfoModal-currentCost'));
        } else {
            elem = $('#cardInfoModal .cardInfoModal-currentCost-value');
            elem.html(String(pCardState.getCurrentCost()));
            this.showElement($('#cardInfoModal .cardInfoModal-currentCost'));
        }

        // Status text
        elem = $('#cardInfoModal .cardInfoModal-status');
        if (pCardState.state === State.ABSENT || (pCardState.state === State.PLANNED && !discouragedPlanned)) {
            this.hideElement(elem);
        } else {
            dh.changeTextStyle(elem, borderState);
            dh.changeStateExplanationText(elem, borderState, pCardState.stateExplanationArg);
            this.showElement(elem);
        }

        // Effects descriptions
        $('#cardInfoModal .cardInfoModal-attributes').html((<any> pCard.dao.attributes)[pLanguage]);
        $('#cardInfoModal .cardInfoModal-calamity-effects').html((<any> pCard.dao.calamityEffects)[pLanguage]);

        // Credit Provided
        $('#cardInfoModal .cardInfoModal-credit-provided-heading').attr('data-l10n-args',
            JSON.stringify({totalProvided: pCard.maxCreditsProvided}));
            this.showListOfCards(pLanguage, $('#cardInfoModal .cardInfoModal-credit-provided-list'), pCreditGiven,
                    false);

        // Credit Received
        elem = $('#cardInfoModal .cardInfoModal-credit-received-list');
        $('#cardInfoModal .cardInfoModal-credit-received-heading').attr('data-l10n-args',
            JSON.stringify({percent: Math.round((pCardState.sumCreditReceived / pCard.maxCreditsReceived) * 100)}));
        if (pCardState.isOwned()) {
            this.hideElement(elem);
            this.hideElement($('#cardInfoModal .cardInfoModal-credit-received-heading'));
        } else {
            this.showElement($('#cardInfoModal .cardInfoModal-credit-received-heading'));
            this.showListOfCards(pLanguage, elem, pCreditReceived, true);
            this.showElement(elem);
        }

        // 'Discard' button
        elem = $('#cardInfoModal div.modal-footer > button:first-child');
        if (pCardState.isOwned()) {
            this.setCardIdOnDiscardButton(pCard.id);
            elem.attr('data-dismiss', 'modal');
            elem.removeClass('disabled');
        } else {
            elem.removeAttr('data-dismiss');
            elem.addClass('disabled');
        }
    }

    private setHeaderBackground(pState: State, pDiscouragedPlanned: boolean): void {
        const elem: JQuery<HTMLElement> = $('#cardInfoModal .modal-header');
        let headerBG: string | undefined = undefined;
        if (pState === State.DISCOURAGED || pDiscouragedPlanned) {
            headerBG = 'bg-warning-darker';
        } else if (pState === State.PLANNED) {
            headerBG = 'bg-success-darker';
        } else if (pState === State.OWNED) {
            headerBG = 'bg-primary';
        }
        elem.removeClass(function(index: number, className: string): string {
            return (className.match(/\b(?:bg-\S+)/g) || []).join(' ');
        });
        if (headerBG !== undefined) {
            elem.addClass(headerBG);
        }
    }

    private showListOfCards(pLanguage: Language, pTargetElement: JQuery<HTMLElement>,
        pCredit: Map<string, [Card, State, number]>, pShowStatusByColor: boolean): void
    {
        const dh: DisplayHelper = new DisplayHelper();
        pTargetElement.children().remove();
        const creditItemHtmlTemplate: string = $('#cardInfoCreditItemTemplate').html();
        for (const cardId of pCredit.keys()) {
            const card: Card = (pCredit.get(cardId) as [Card, State, number])[0];
            const state: State = (pCredit.get(cardId) as [Card, State, number])[1];
            const amount: number = (pCredit.get(cardId) as [Card, State, number])[2];
            const renderedItem: string = Mustache.render(creditItemHtmlTemplate, {
                cardTitle: (<any> card.dao.names)[pLanguage],
                creditPoints: '+' + amount,
                textColor: pShowStatusByColor ? this.getCreditItemColor(state) : ''
            });
            pTargetElement.append(renderedItem);
            const iconDiv: JQuery<HTMLElement> = pTargetElement.children().last().children('.card-groups');
            dh.addGroupIcons(iconDiv, card.dao.groups);
        }
    }

    private getCreditItemColor(pState: State): string {
        let result: string = '';
        if (pState === State.OWNED) {
            result = 'text-info';
        } else if (pState === State.PLANNED) {
            result = 'text-success';
        } else if (pState === State.PREREQFAILED || pState === State.UNAFFORDABLE) {
            result = 'text-muted';
        }
        if (result.length > 0) {
            result = ' ' + result;
        }
        return result;
    }


    public showModal(): void {
        $('#cardInfoModal').modal();
    }


    public isDiscardButtonDisabled(): boolean {
        const button: JQuery<HTMLElement> = $('#cardInfoModal div.modal-footer > button:first-child');
        return button.hasClass('disabled');
    }

    public getCardIdFromDiscardButton(): string {
        const button: JQuery<HTMLElement> = $('#cardInfoModal div.modal-footer > button:first-child');
        return button.attr('cardId') as string;
    }

    public setCardIdOnDiscardButton(pCardId: string): void {
        const button: JQuery<HTMLElement> = $('#cardInfoModal div.modal-footer > button:first-child');
        button.attr('cardId', pCardId);
    }
}
