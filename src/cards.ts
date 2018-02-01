import * as Mustache from 'mustache';
import * as storage from './storage';
import { SituationDao, GameDao, FundsDao, FundsDaoImpl } from './dao';
import { getUrlParameter, showElement, hideElement, buildMap } from './dom';
import { CardJson, builtInVariants, RulesJson, Rules, Card, CardGroup } from './rules';
import { appOptions, getLocalizedString } from './app';
import { Situation, State, CardData } from './model';


let currentSituation: Situation;
let selectedGame: GameDao;
let selectedRules: Rules;

const hoverHeaders: Map<string, boolean> = new Map();
const hoverCards: Map<string, boolean> = new Map();


export function initCardsPage(): void {
    if (getSituationFromUrl()) {
        $(function(): void {
            parseMustacheTemplates();
            addGameIdToLinks();
            populateCardsList(false);
            setupPlannedHoverEffect();
            document.title = currentSituation.getPlayerName() + ' - ' + selectedGame.name + ' - CivBuddy';
            setActivePlayer();
        });
        window.addEventListener('applanguagechanged', function(): void {
            populateCardsList(true);
            setupPlannedHoverEffect();
        });
    }
}

function parseMustacheTemplates(): void {
    Mustache.parse($('#cardTemplate').html());
    Mustache.parse($('#cardInfoCreditItemTemplate').html());
    Mustache.parse($('#groupIconTemplate').html());
    Mustache.parse($('#switchPlayerLinkTemplate').html());
}

function addGameIdToLinks(): void {
    $('a.add-game-id').attr('href', 'players.html?ctx=' + selectedGame.key);
}

function getSituationFromUrl(): boolean {
    const situationKey: string | null = getUrlParameter('ctx');
    const sit: SituationDao | null = storage.readSituation(situationKey);
    let result: boolean = false;
    if (sit !== null) {
        const game: GameDao | null = storage.readGame(sit.gameId);
        if (game != null) {
            const variant: RulesJson = builtInVariants[game.variantKey];
            selectedRules = new Rules(variant);
            selectedGame = game;
            currentSituation = new Situation(sit, buildMap(game.options), selectedRules);
            currentSituation.recalculate();
            result = true;
        }
    }
    if (!result) {
        window.location.replace('index.html');
    }
    return result;
}

function populateCardsList(pUpdateLanguageTexts: boolean): void {
    const variant: RulesJson = selectedRules.variant;
    if (pUpdateLanguageTexts) {
        currentSituation.changeLanguage(appOptions.language);
    }
    let htmlTemplate: string = $('#cardTemplate').html();
    $('#cardList > div').remove();
    const ctrl: CardController = new CardController();
    for (let cardId of Object.keys(variant.cards)) {
        const card: Card = selectedRules.cards.get(cardId) as Card;
        const cardData: CardData = currentSituation.getCard(cardId);
        ctrl.putCard(card, cardData, htmlTemplate);
    }
}


function buildL10nArgs(pCard: Card, pCardData: CardData): string {
    const d: Object = {
        'currentCards': pCardData.creditReceived.size,
        'plannedCards': pCardData.creditReceived.size + pCardData.creditReceivedPlanned.size,
        'maxCards': pCard.creditsReceived.size,
        'currentCredits': pCardData.sumCreditReceived,
        'plannedCredits': pCardData.sumCreditReceived + pCardData.sumCreditReceivedPlanned,
        'maxCredits': pCard.maxCreditsReceived
    };
    return JSON.stringify(d);
}


function setupPlannedHoverEffect(): void {
    for (let cardId of Object.keys(selectedRules.variant.cards)) {
        $('#card-' + cardId + ' div.card-combined-header').hover(
            function() { hoverHeaders.set(cardId, true); hoverHandler(cardId); },
            function() { hoverHeaders.set(cardId, false); hoverHandler(cardId); }
        );
        $('#card-' + cardId + ' > div.card-civbuddy').hover(
            function() { hoverCards.set(cardId, true); hoverHandler(cardId); },
            function() { hoverCards.set(cardId, false); hoverHandler(cardId); }
        );
    }
}

function hoverHandler(pCardId: string): void {
    const cardElem: JQuery<HTMLElement> = $('#card-' + pCardId + ' > div.card-civbuddy');
    if (hoversOnCard(pCardId)) {
        cardElem.addClass('hovered');
    } else {
        cardElem.removeClass('hovered');
    }
}

function hoversOnCard(pCardId: string): boolean {
    const isOnHeader: boolean = Boolean(hoverHeaders.get(pCardId));
    const isOnCard: boolean = Boolean(hoverCards.get(pCardId));
    return isOnCard && !isOnHeader;
}


function setActivePlayer(): void {
    const navCtrl: NavbarController = new NavbarController();
    navCtrl.setCardCount(currentSituation.getNumOwnedCards());
    navCtrl.setCardsLimit(selectedRules.variant.cardLimit);
    navCtrl.setPointsTarget(currentSituation.getPointsTarget());
    navCtrl.setScore(currentSituation.getScore());
    navCtrl.updatePlayersDropdown(currentSituation.getPlayerName(), selectedGame);
    const fundsCtrl: FundsBarController = new FundsBarController();
    fundsCtrl.setTotalAvailableFunds(currentSituation.getTotalFunds());
}


export function clickOnCard(pCardId: string): void {
    if (hoversOnCard(pCardId)) {
        const currentState: State = currentSituation.getCardState(pCardId);
        if (currentState === State.ABSENT || currentState === State.DISCOURAGED) {
            planCard(pCardId);
        }
        else if (currentState === State.PLANNED) {
            unPlanCard(pCardId);
        }
    } else {
        displayCardInfo(pCardId);
    }
}


function planCard(pCardId: string): void {
    // TODO check for DISCOURAGED state, prompt
    const changedCreditBars: string[] = currentSituation.planCard(pCardId);

    const cardCtrl: CardController = new CardController();
    cardCtrl.changeState(pCardId, State.PLANNED);  // TODO if the card was discourged, the warning must remain
    for (let targetCardId of changedCreditBars) {
        cardCtrl.changeCreditBarPlanned(targetCardId, currentSituation.getSumCreditReceivedPlanned(targetCardId));
    }
    cardCtrl.syncCardStates(currentSituation);

    const fundsCtrl: FundsBarController = new FundsBarController();
    fundsCtrl.setRemainingFunds(currentSituation.getCurrentFunds());
}


function unPlanCard(pCardId: string): void {
    if (currentSituation.getCardState(pCardId) === State.PLANNED) {
        const changedCreditBars: string[] = currentSituation.unplanCard(pCardId);

        const cardCtrl: CardController = new CardController();
        cardCtrl.changeState(pCardId, currentSituation.getCardState(pCardId));
        for (let targetCardId of changedCreditBars) {
            cardCtrl.changeCreditBarPlanned(targetCardId, currentSituation.getSumCreditReceivedPlanned(targetCardId));
        }
        cardCtrl.syncCardStates(currentSituation);

        const fundsCtrl: FundsBarController = new FundsBarController();
        fundsCtrl.setRemainingFunds(currentSituation.getCurrentFunds());
    }
}



/**
 * Manages the display of a card.
 */
class CardController
{
    /**
     * Add a card to the display. If it exists already, the existing card is replaced.
     * @param pCard the card information as specified in the rules
     * @param pCardState the runtime card information, representing its current state
     */
    public putCard(pCard: Card, pCardState: CardData, pHtmlTemplate: string): void
    {
        const state: State = pCardState.state;
        const renderedCard: string = Mustache.render(pHtmlTemplate, {
            'cardId': pCard.id,
            'cardTitle': pCard.dao.names[appOptions.language],
            'status': State[state].toString().toLowerCase(),
            'borderStyle': this.getBorderStyle(state),
            'textStyle': this.getTextStyle(state),
            'isOwned': state === State.OWNED,
            'explArgs': this.getExplanationArgumentJson(pCardState.stateExplanationArg),
            'costNominal': pCard.dao.costNominal,
            'costCurrent': pCardState.getCurrentCost(),
            'creditBarWidth': Math.round((pCard.maxCreditsReceived / selectedRules.maxCredits) * 100),
            'creditBarOwnedPercent': Math.round((pCardState.sumCreditReceived / pCard.maxCreditsReceived) * 100),
            'creditBarOwnedValue': pCardState.sumCreditReceived,
            'creditBarPlannedPercent': Math.round((pCardState.sumCreditReceivedPlanned / pCard.maxCreditsReceived) * 100),
            'creditBarPlannedValue': pCardState.sumCreditReceivedPlanned,
            'totalCredit': pCard.maxCreditsReceived
        });

        // Add the new card to the list, or replace an already existing card of the same ID
        let cardElem: JQuery<HTMLElement> = $('#card-' + pCard.id);
        if (cardElem.length) {
            cardElem.replaceWith(renderedCard);
        } else {
            $('#cardList').append(renderedCard);
        }

        // set credit bar info text
        this.setCreditBarInfoText(pCard.id);

        // card group icons
        const iconDiv: JQuery<HTMLElement> = $('#card-' + pCard.id + ' .card-header');
        this.addGroupIcons(iconDiv, pCard.dao.groups);
    }


    public changeState(pCardId: string, pNewState: State, pStateArg?: string | number): void
    {
        if (pNewState === State.OWNED) {
            const card: Card = selectedRules.cards.get(pCardId) as Card;
            const cardState: CardData = currentSituation.getCard(pCardId);
            this.putCard(card, cardState, $('#cardTemplate').html());
            return;
        }

        // border color
        let elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' div.card-civbuddy');
        this.changeBorderStyle(elem, pNewState);

        // status class
        elem.removeClass(function (index: number, className: string): string {
            return (className.match(/\b(?:card-status-\S+)/g) || []).join(' ');
        });
        elem.addClass('card-status-' + State[pNewState].toLowerCase());

        // state explanantion text
        elem = $('#card-' + pCardId + ' div.card-body > p.card-status-expl');
        this.changeTextStyle(elem, pNewState);
        this.changeStateExplanationText(elem, pNewState, pStateArg);
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
     * Change the 'owned' value of a card's credit bar.
     * @param pCardId the card ID whose credit bar to modify
     * @param pOwnedValue the new value of active credits from owned cards
     */
    public changeCreditBarOwned(pCardId: string, pOwnedValue: number): void {
        this.changeCreditBarFragment(pCardId, pOwnedValue, true);
        this.setCreditBarInfoText(pCardId);
    }

    /**
     * Change the 'planned' value of a card's credit bar.
     * @param pCardId the card ID whose credit bar to modify
     * @param pPlannedValue the new value of planned credits
     */
    public changeCreditBarPlanned(pCardId: string, pPlannedValue: number): void {
        this.changeCreditBarFragment(pCardId, pPlannedValue, false);
        this.setCreditBarInfoText(pCardId);
    }

    /**
     * Update the entire credit bar of a card to the values from the current situation.
     * @param pCardId the card's ID
     */
    public changeCreditBar(pCardId: string): void {
        const cardState: CardData = currentSituation.getCard(pCardId);
        if (!cardState.isOwned()) {
            this.changeCreditBarFragment(pCardId, cardState.sumCreditReceived, true);
            this.changeCreditBarFragment(pCardId, cardState.sumCreditReceivedPlanned, false);
            this.setCreditBarInfoText(pCardId);
        }
    }

    private changeCreditBarFragment(pCardId: string, pNewValue: number, pIsOwned: boolean): void {
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' div.progress > div.bar-'
                + (pIsOwned? 'owned' : 'planned'));
        elem.attr('aria-valuenow', pNewValue);
        if (pIsOwned || pNewValue > 0) {
            const cardMaxCredits: number = (selectedRules.cards.get(pCardId) as Card).maxCreditsReceived;
            const percent: number = Math.round((pNewValue / cardMaxCredits) * 100);
            elem.attr('style', 'width: ' + percent + '%');
            showElement(elem);
        } else {
            hideElement(elem);
        }
    }

    private setCreditBarInfoText(pCardId: string): void {
        const card: Card = selectedRules.cards.get(pCardId) as Card;
        const cardState: CardData = currentSituation.getCard(pCardId);
        const creditInfoElem: JQuery<HTMLElement> = $('#card-' + pCardId + ' .card-credits-info');
        const l10nArgs: string = buildL10nArgs(card, cardState);
        creditInfoElem.attr('data-l10n-args', l10nArgs);
        let l10nId: string = 'cards-card-credits';
        if (cardState.creditReceivedPlanned.size > 0) {
            l10nId += '-plan';
        }
        creditInfoElem.attr('data-l10n-id', l10nId);
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
     * @param pSituation the current player's situation
     */
    public syncCardStates(pSituation: Situation): void {
        for (let cardId of pSituation.getCardIdIterator()) {
            const currentState: State | undefined = this.getDisplayedStatus(cardId);
            if (pSituation.isCardState(cardId, currentState)) {
                const stateArg: string | number | undefined = pSituation.getStateExplanationArg(cardId);
                if (typeof(stateArg) === 'number') {
                    const elem: JQuery<HTMLElement> = $('#card-' + cardId + ' p.card-status-expl');
                    elem.attr('data-l10n-args', this.getExplanationArgumentJson(stateArg) as string);
                }
            } else {
                this.changeState(cardId, pSituation.getCardState(cardId), pSituation.getStateExplanationArg(cardId));
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


export function displayCardInfo(pCardId: string): void {
    const card: Card = selectedRules.cards.get(pCardId) as Card;
    const cardState: CardData = currentSituation.getCard(pCardId);
    const ctrl: CardController = new CardController();

    // Border style
    let elem: JQuery<HTMLElement> = $('#cardInfoModal .modal-content');
    ctrl.changeBorderStyle(elem, cardState.state, function(pClass: string): string {
        return pClass.replace('bg-success', 'border-success');
    });

    // Card title
    elem = $('#cardInfoModal .modal-title');
    elem.html(card.dao.names[appOptions.language] + ' (' + card.dao.costNominal + ')');
    ctrl.addGroupIcons(elem, card.dao.groups);

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
        ctrl.changeTextStyle(elem, cardState.state);
        ctrl.changeStateExplanationText(elem, cardState.state, cardState.stateExplanationArg);
        showElement(elem);
    }

    // Effects descriptions
    $('#cardInfoModal .cardInfoModal-attributes').html(card.dao.attributes[appOptions.language]);
    $('#cardInfoModal .cardInfoModal-calamity-effects').html(card.dao.calamityEffects[appOptions.language]);

    // Credit Provided
    $('#cardInfoModal .cardInfoModal-credit-provided-heading').attr('data-l10n-args',
        JSON.stringify({'totalProvided': card.maxCreditsProvided}));
    showListOfCards(ctrl, $('#cardInfoModal .cardInfoModal-credit-provided-list'), card.dao.creditGiven, false);

    // Credit Received
    elem = $('#cardInfoModal .cardInfoModal-credit-received-list');
    $('#cardInfoModal .cardInfoModal-credit-received-heading').attr('data-l10n-args',
        JSON.stringify({'percent': Math.round((cardState.sumCreditReceived / card.maxCreditsReceived) * 100)}));
    if (cardState.isOwned()) {
        hideElement(elem);
        hideElement($('#cardInfoModal .cardInfoModal-credit-received-heading'));
    } else {
        showElement($('#cardInfoModal .cardInfoModal-credit-received-heading'));
        showListOfCards(ctrl, elem, card.creditsReceived, true);
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



/**
 * Manages the display of the navigation bar.
 */
class NavbarController
{
    public setCardCount(pNumCards: number): void {
        $('#navbarCards .navbarCurrentNumCards').html(String(pNumCards));
    }

    public setCardsLimit(pMaxCards: number | undefined | null): void {
        const elem: JQuery<HTMLElement> = $('#navbarCards .navbarMaxCards');
        if (typeof(pMaxCards) === 'number') {
            elem.html('/' + pMaxCards);
            showElement(elem);
        } else {
            hideElement(elem);
        }
    }

    public setScore(pScore: number): void {
        $('.navbar .navbarCurrentPoints').html(String(pScore));
    }

    public setPointsTarget(pPointsTarget: number): void {
        $('.navbar .navbarPointsTarget').html('/' + pPointsTarget);
    }


    public updatePlayersDropdown(pCurrentPlayerName: string, pGame: GameDao): void
    {
        $('#currentPlayerName').html(pCurrentPlayerName);

        $('#playerDropdown > a.switch-player-link').remove();

        const parent: JQuery<HTMLElement> = $('#playerDropdown');
        const switchPlayerLinkTemplate: string = $('#switchPlayerLinkTemplate').html();
        const dropdownDivider: JQuery<HTMLElement> = $('#playerDropdown > div.dropdown-divider');
        const playerNames: string[] = Object.keys(pGame.situations);
        if (playerNames.length > 1) {
            showElement(dropdownDivider);
            for (let playerName of playerNames.sort().reverse()) {
                const situationId: string = pGame.situations[playerName];
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
            hideElement(dropdownDivider);
        }
    }
}



/**
 * Manages the display of the funds bar (footer).
 */
class FundsBarController
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



function showListOfCards(pCtrl: CardController, pTargetElement: JQuery<HTMLElement>,
    pCreditList: Map<string, number> | Object, pShowStatusByColor: boolean): void
{
    pTargetElement.children().remove();
    const creditItemHtmlTemplate: string = $('#cardInfoCreditItemTemplate').html();
    const cardIds: string[] | IterableIterator<string> =
            pCreditList instanceof Map ? pCreditList.keys() : Object.keys(pCreditList);
    for (let cardId of cardIds) {
        const card: Card = selectedRules.cards.get(cardId) as Card;
        const state: State = currentSituation.getCardState(cardId);
        const renderedItem: string = Mustache.render(creditItemHtmlTemplate, {
            'cardTitle': card.dao.names[appOptions.language],
            'creditPoints': '+' + (pCreditList instanceof Map ? pCreditList.get(cardId) : pCreditList[cardId]),
            'textColor': pShowStatusByColor ? getCreditItemColor(state) : ''
        });
        pTargetElement.append(renderedItem);
        const iconDiv: JQuery<HTMLElement> = pTargetElement.children().last().children('.card-groups');
        pCtrl.addGroupIcons(iconDiv, card.dao.groups);
    }
}

function getCreditItemColor(pState: State): string {
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


export function buy()
{
    // perform the 'buy' operation on the model
    const cardIdsBought: string[] = currentSituation.buyPlannedCards();
    if (cardIdsBought.length === 0) {
        return;  // the button was pressed without any cards planned
    }

    // update the card display accordingly
    const cardCtrl: CardController = new CardController();
    cardCtrl.syncCardStates(currentSituation);
    for (let cardId of cardIdsBought) {
        const supportedCardIds: string[] = Array(...currentSituation.getCreditGiven(cardId).keys());
        supportedCardIds.forEach(cardCtrl.changeCreditBar.bind(cardCtrl));
    }

    // update the navbar accordingly
    const navbarCtrl: NavbarController = new NavbarController();
    navbarCtrl.setCardCount(currentSituation.getNumOwnedCards());
    navbarCtrl.setScore(currentSituation.getScore());

    // save to local storage
    storage.saveSituation(currentSituation.getDaoForStorage());
}


export function toggleCardsFilter() {
    // TODO
    window.alert("filter cards - not implemented");
}


export function enterFunds() {
    // TODO replace workaround with real 'funds' page invocation
    const s: string | null = window.prompt('Enter total funds (no \'funds\' page yet):');
    if (s !== null && s.trim().length > 0) {
        let totalFunds: number = Number(s);
        if (!isNaN(totalFunds) && totalFunds >= 0) {
            totalFunds = Math.round(totalFunds);
            const newFunds: FundsDao = new FundsDaoImpl(totalFunds, {}, 0, false);
            currentSituation.updateTotalFunds(newFunds);
            storage.saveSituation(currentSituation.getDaoForStorage());
            window.location.reload();
        }
    }
}


export function discard(): void
{
    const button: JQuery<HTMLElement> = $('#cardInfoModal div.modal-footer > button:first-child');
    if (!button.hasClass('disabled')) {
        const cardId: string = button.attr('cardId') as string;
        currentSituation.discard(cardId);
        storage.saveSituation(currentSituation.getDaoForStorage());
        window.location.reload();
    }
}
