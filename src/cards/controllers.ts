import * as Mustache from 'mustache';
import { AbstractController } from '../framework';
import { Card, CardGroup, Language } from '../rules/rules';
import { CardData, State } from '../model';
import { getLocalizedString } from '../main';



/**
 * Manages the display of a card.
 */
export class CardController
    extends AbstractController
{
    constructor(private readonly cards: Map<string, Card>, private readonly language: Language) {
        super();
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
        const card: Card = this.getCard(pCardState.id);
        const state: State = pCardState.state;
        const renderedCard: string = Mustache.render(pHtmlTemplate, {
            'cardId': card.id,
            'cardTitle': card.dao.names[this.language],
            'status': State[state].toString().toLowerCase(),
            'borderStyle': this.getBorderStyle(state),
            'textStyle': this.getTextStyle(state),
            'isOwned': state === State.OWNED,
            'explArgs': this.getExplanationArgumentJson(pCardState.stateExplanationArg),
            'costNominal': card.dao.costNominal,
            'costCurrent': pCardState.getCurrentCost(),
            'creditBarWidth': Math.round((card.maxCreditsReceived / pOverallMaxCredits) * 100),
            'creditBarOwnedPercent': Math.round((pCardState.sumCreditReceived / card.maxCreditsReceived) * 100),
            'creditBarOwnedValue': pCardState.sumCreditReceived,
            'creditBarPlannedPercent': Math.round((pCardState.sumCreditReceivedPlanned / card.maxCreditsReceived) * 100),
            'creditBarPlannedValue': pCardState.sumCreditReceivedPlanned,
            'totalCredit': card.maxCreditsReceived
        });

        // Add the new card to the list, or replace an already existing card of the same ID
        let cardElem: JQuery<HTMLElement> = $('#card-' + card.id);
        if (cardElem.length) {
            cardElem.replaceWith(renderedCard);
        } else {
            $('#cardList').append(renderedCard);
        }

        // set credit bar info text
        this.setCreditBarInfoText(pCardState);

        // card group icons
        const iconDiv: JQuery<HTMLElement> = $('#card-' + card.id + ' .card-header');
        this.addGroupIcons(iconDiv, card.dao.groups);
    }


    public changeState(pCardState: CardData, pOverallMaxCredits: number): void
    {
        const oldState: State | undefined = this.getDisplayedStatus(pCardState.id);
        const newState: State = pCardState.state;
        if (newState === State.OWNED) {
            this.putCard(pCardState, $('#cardTemplate').html(), pOverallMaxCredits);
            return;
        }

        // border color
        let elem: JQuery<HTMLElement> = $('#card-' + pCardState.id + ' div.card-civbuddy');
        this.changeBorderStyle(elem, newState);

        // status class
        elem.removeClass(function (index: number, className: string): string {
            return (className.match(/\b(?:card-status-\S+)/g) || []).join(' ');
        });
        elem.addClass('card-status-' + State[newState].toLowerCase());

        // state explanantion text
        elem = $('#card-' + pCardState.id + ' div.card-body > p.card-status-expl');
        this.changeTextStyle(elem, newState);
        if (oldState !== State.DISCOURAGED || newState !== State.PLANNED) {
            this.changeStateExplanationText(elem, newState, pCardState.stateExplanationArg);
            elem.removeClass('d-block');
        } else {
            elem.addClass('d-block');
        }
    }


    /**
     * Modify the text String explaining the current card state.
     * @param pElement the DOM element whose text to modify
     * @param pNewState the new card state
     * @param pStateArg if the card state has an argument, this would be it
     *      (for example, number of points missing, name of required prereq card)
     */
    public changeStateExplanationText(pElement: JQuery<HTMLElement>, pNewState: State, pStateArg?: string | number): void {
        if (pStateArg !== undefined) {
            pElement.attr('data-l10n-args', this.getExplanationArgumentJson(pStateArg) as string);
        }
        pElement.attr('data-l10n-id', `cards-card-${State[pNewState].toLowerCase()}-expl`);
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
                + (pIsOwned? 'owned' : 'planned'));
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
        const d: Object = {
            'currentCards': pCardState.creditReceived.size,
            'plannedCards': pCardState.creditReceived.size + pCardState.creditReceivedPlanned.size,
            'maxCards': card.creditsReceived.size,
            'currentCredits': pCardState.sumCreditReceived,
            'plannedCredits': pCardState.sumCreditReceived + pCardState.sumCreditReceivedPlanned,
            'maxCredits': card.maxCreditsReceived
        };
        return JSON.stringify(d);
    }
    
    
    /**
     * Modify the border CSS class of the given element to match a new card state.
     * @param pElement the element whose border to change
     * @param pNewState the new card state
     * @param pFilterFunc an optional filter to modify the new CSS class before it is being activated
     */
    public changeBorderStyle(pElement: JQuery<HTMLElement>, pNewState: State, pFilterFunc?: (string) => string): void {
        pElement.removeClass(function (index: number, className: string): string {
            return (className.match(/\b(?:bg-success|border-\S+)/g) || []).join(' ');
        });
        let newClass: string = this.getBorderStyle(pNewState);
        if (pFilterFunc !== undefined) {
            newClass = pFilterFunc(newClass);
        }
        pElement.addClass(newClass);
    }

    private getBorderStyle(pState: State): string {
        let result: string = '';
        switch (pState) {
            case State.ABSENT:       result = 'border-info'; break;
            case State.DISCOURAGED:  result = 'border-warning'; break;
            case State.OWNED:        result = 'border-success'; break;
            case State.PLANNED:      result = 'bg-success'; break;
            case State.PREREQFAILED: result = 'border-danger'; break;
            case State.UNAFFORDABLE: result = 'border-danger'; break;
            default: result = ''; /* empty */ break;
        }
        return result;
    }


    public changeTextStyle(pElement: JQuery<HTMLElement>, pNewState: State): void {
        pElement.removeClass(function (index: number, className: string): string {
            return (className.match(/\btext-\S+/g) || []).join(' ');
        });
        const newStyle: string = this.getTextStyle(pNewState);
        if (newStyle.length > 0) {
            pElement.addClass(newStyle);
        }
    }

    private getTextStyle(pState: State): string {
        let result: string = '';
        switch (pState) {
            case State.ABSENT:       result = ''; /* empty */ break;
            case State.DISCOURAGED:  result = 'text-warning'; break;
            case State.OWNED:        result = 'text-muted'; break;
            case State.PLANNED:      result = ''; /* empty */ break;
            case State.PREREQFAILED: result = 'text-danger'; break;
            case State.UNAFFORDABLE: result = 'text-danger'; break;
            default: result = ''; /* empty */ break;
        }
        return result;
    }


    private getExplanationArgumentJson(pStateExplanationArg: string | number | undefined): string | undefined {
        let result: string | undefined = undefined;
        if (typeof(pStateExplanationArg) !== undefined) {
            result = JSON.stringify({'arg': pStateExplanationArg});
        }
        return result;
    }


    public addGroupIcons(pTargetElement: JQuery<HTMLElement>, pGroups: CardGroup[]): void {
        const groupIconHtmlTemplate: string = $('#groupIconTemplate').html();
        for (let group of Array.from(pGroups).reverse()) {
            const lowerCaseName: string = group.toString().toLowerCase();
            getLocalizedString('cards-group-' + lowerCaseName, function(localizedGroupName: string[]): void {
                let renderedIcon: string = Mustache.render(groupIconHtmlTemplate, {
                    'iconName': lowerCaseName,
                    'groupName': localizedGroupName[0].trim()
                });
                pTargetElement.prepend(renderedIcon);
            });
        }
    }


    /**
     * Ensure that the displayed card states are what we have in the given situation.
     * @param pStateMap information about each card according to the model
     * @param pOverallMaxCredits the highest amount of credit received by any card (from rules)
     */
    public syncCardStates(pStateMap: Map<string, CardData>, pOverallMaxCredits: number): void {
        for (let cardId of pStateMap.keys()) {
            const cardState: CardData = pStateMap.get(cardId) as CardData;
            const currentState: State | undefined = this.getDisplayedStatus(cardId);
            if (cardState.state === currentState) {
                const stateArg: string | number | undefined = cardState.stateExplanationArg;
                if (typeof(stateArg) === 'number') {
                    const elem: JQuery<HTMLElement> = $('#card-' + cardId + ' p.card-status-expl');
                    elem.attr('data-l10n-args', this.getExplanationArgumentJson(stateArg) as string);
                }
            } else {
                this.changeState(cardState, pOverallMaxCredits);
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
            var match = classValue.match(/\bcard-status-(\w+)\b/);
            if (match !== null && match.length >= 2) {
                result = State[match[1].toUpperCase()];
            }
        }
        return result;
    }
}



/**
 * Manages the display of the navigation bar.
 */
export class NavbarController
    extends AbstractController
{
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


    /**
     * Update the navbar dropdown which shows the players of this game.
     * @param pCurrentPlayerName name of the current player
     * @param pSituations map from player name to situationKey, including the current player
     */
    public updatePlayersDropdown(pCurrentPlayerName: string, pSituations: Map<string, string>): void
    {
        $('#currentPlayerName').html(pCurrentPlayerName);

        $('#playerDropdown > a.switch-player-link').remove();

        const parent: JQuery<HTMLElement> = $('#playerDropdown');
        const switchPlayerLinkTemplate: string = $('#switchPlayerLinkTemplate').html();
        const dropdownDivider: JQuery<HTMLElement> = $('#playerDropdown > div.dropdown-divider');
        const playerNames: string[] = Array.from(pSituations.keys());
        if (playerNames.length > 1) {
            this.showElement(dropdownDivider);
            for (let playerName of playerNames.sort().reverse()) {
                const situationId: string = pSituations.get(playerName) as string;
                if (playerName !== pCurrentPlayerName) {
                    const renderedLink: string = Mustache.render(switchPlayerLinkTemplate, {
                        'situationId': situationId,
                        'playerName': playerName
                    });
                    parent.prepend(renderedLink);
                }
            }
        }
        else {
            this.hideElement(dropdownDivider);
        }
    }


    public setGameName(pGameName: string): void {
        const elem: JQuery<HTMLElement> = $('#gameName1');
        elem.html(pGameName);
        if (pGameName.length > 17) {
            elem.attr('title', pGameName);
        } else {
            elem.removeAttr('title');
        }
        $('#gameName2').html(pGameName);   // TODO Check if #gameName1 and #gameName2 always exist?
    }


    public setVariantName(pVariantName: string): void {
        $('#variantName').html(pVariantName);
    }
}



/**
 * Manages the display of the funds bar (footer).
 */
export class FundsBarController
    extends AbstractController
{
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
        elem.attr('data-l10n-args', JSON.stringify({'fundsCurrent': pRemaining, 'fundsAvailable': pMax}));
    }
}



/**
 * Manages the display of the card info modal.
 */
export class CardInfoModalController
    extends AbstractController
{
    public isDiscardButtonDisabled(): boolean {
        const button: JQuery<HTMLElement> = $('#cardInfoModal div.modal-footer > button:first-child');
        return button.hasClass('disabled');
    }

    public getCardIdFromDiscardButton(): string {
        const button: JQuery<HTMLElement> = $('#cardInfoModal div.modal-footer > button:first-child');
        return button.attr('cardId') as string;
    }
}
