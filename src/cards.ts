import * as Mustache from 'mustache';
import * as storage from './storage';
import { SituationDao, GameDao } from './dao';
import { getUrlParameter } from './dom';
import { CardJson, builtInVariants, RulesJson, Rules, Card } from './rules';
import { appOptions, getLocalizedString } from './app';
import { Situation, State, CardData } from './model';
import { Calculator } from './calc';


let currentSituation: Situation;
let selectedGame: GameDao;
let selectedRules: Rules;

const hoverHeaders: Map<string, boolean> = new Map();
const hoverCards: Map<string, boolean> = new Map();


export function initCardsPage(): void {
    if (getSituationFromUrl()) {
        $(function(): void {
            populateCardsList(false);
            setupPlannedHoverEffect();
            document.title = currentSituation.dao.player.name + ' - ' + selectedGame.name + ' - CivBuddy';
            setActivePlayer();
            // TODO funds, ruleset, etc.
        });
        window.addEventListener('applanguagechanged', function(): void {
            populateCardsList(true);
            setupPlannedHoverEffect();
        });
    }
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
            const cardStates: Map<string, CardData> =
                new Calculator(selectedRules, buildMap(game.options), appOptions.language).pageInit(sit.ownedCards);
            currentSituation = new Situation(sit, cardStates);
            result = true;
        }
    }
    if (!result) {
        window.location.replace('index.html');
    }
    return result;
}

function populateCardsList(pRecalc: boolean): void {
    $('#cardList > div[keep!="true"]').remove();
    const variant: RulesJson = selectedRules.variant;
    if (pRecalc) {
        const cardStates: Map<string, CardData> =
            new Calculator(selectedRules, buildMap(selectedGame.options), appOptions.language).pageInit(currentSituation.dao.ownedCards);
        currentSituation.states = cardStates;
    }
    let htmlTemplate: string = $('#cardTemplate').html();
    Mustache.parse(htmlTemplate);
    const ctrl: CardController = new CardController();
    for (let cardId of Object.keys(variant.cards)) {
        const card: Card = selectedRules.cards.get(cardId) as Card;
        const cardData: CardData = currentSituation.states.get(cardId) as CardData;
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
        'maxCredits': pCard.maxCredits
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

function buildMap(pObj: Object): Map<string, string> {
    return Object.keys(pObj).reduce((map, key: string) => map.set(key, pObj[key]), new Map<string, string>());
}

function setActivePlayer(): void {
    // TODO Update dropdown menu to show the active player, and the other players in the dropdown for easy switching
}


export function clickOnCard(pCardId: string): void {
    if (hoversOnCard(pCardId)) {
        const currentState: State = (currentSituation.states.get(pCardId) as CardData).state;
        if (currentState === State.ABSENT || currentState === State.DISCOURAGED) {
            // set card status to PLANNED
            planCard(pCardId);
        }
        else if (currentState === State.PLANNED) {
            // recalculate card status (no longer PLANNED)
            unPlanCard(pCardId);
        }
    } else {
        // show card information (always)
        // TODO
        window.alert('card info - not implemented');
    }
}


function planCard(pCardId: string): void {
    // TODO check for DISCOURAGED state
    const cardData: CardData = currentSituation.states.get(pCardId) as CardData;
    cardData.state = State.PLANNED;
    const ctrl: CardController = new CardController();
    ctrl.changeState(pCardId, State.PLANNED);
    const card: Card = selectedRules.cards.get(pCardId) as Card;
    for (let targetCardId of Object.keys(card.dao.creditGiven)) {
        const targetCardData: CardData = currentSituation.states.get(targetCardId) as CardData;
        const credit: number = card.dao.creditGiven[targetCardId] as number;
        if (targetCardData.state !== State.OWNED) {
            targetCardData.addCreditPlanned(pCardId, credit);
            ctrl.changeCreditBarPlanned(targetCardId, targetCardData.sumCreditReceivedPlanned);
        }
    }
    // TODO some other cards might become DISCOURAGED or UNAFFORDABLE
}

function unPlanCard(pCardId: string): void {
    // TODO
    const cardData: CardData = currentSituation.states.get(pCardId) as CardData;
    cardData.state = State.ABSENT;
    const ctrl: CardController = new CardController();
    ctrl.changeState(pCardId, State.ABSENT);
    const card: Card = selectedRules.cards.get(pCardId) as Card;
    for (let targetCardId of Object.keys(card.dao.creditGiven)) {
        const targetCardData: CardData = currentSituation.states.get(targetCardId) as CardData;
        if (targetCardData.state !== State.OWNED) {
            targetCardData.subtractCreditPlanned(pCardId);
            ctrl.changeCreditBarPlanned(targetCardId, targetCardData.sumCreditReceivedPlanned);
        }
    }
    // TODO recalculate the other cards (and this one, too!)
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
            'costCurrent': Math.max(pCard.dao.costNominal - pCardState.sumCreditReceived, 0),
            'creditBarWidth': Math.round((pCard.maxCredits / selectedRules.maxCredits) * 100),
            'creditBarOwnedPercent': Math.round((pCardState.sumCreditReceived / pCard.maxCredits) * 100),
            'creditBarOwnedValue': pCardState.sumCreditReceived,
            'creditBarPlannedPercent': Math.round((pCardState.sumCreditReceivedPlanned / pCard.maxCredits) * 100),
            'creditBarPlannedValue': pCardState.sumCreditReceivedPlanned,
            'totalCredit': pCard.maxCredits
        });

        // Add the new card to the list, or replace an already existing card of the same ID
        let cardElem: JQuery<HTMLElement> = $('#card-' + pCard.id);
        if (cardElem.length) {
            cardElem.replaceWith(renderedCard);
        } else {
            $('#cardList').append(renderedCard);
        }

        // set credit bar info text
        this.setCreditBarInfoText(pCard, pCardState);

        // card group icons
        const groupIconHtmlTemplate: string = $('#groupIconTemplate').html();
        Mustache.parse(groupIconHtmlTemplate);
        for (let group of Array.from(pCard.dao.groups).reverse()) {
            const lowerCaseName: string = group.toString().toLowerCase();
            getLocalizedString('cards-group-' + lowerCaseName, function(localizedGroupName: string): void {
                let renderedIcon: string = Mustache.render(groupIconHtmlTemplate, {
                    'iconName': lowerCaseName,
                    'groupName': localizedGroupName
                });
                const iconDiv: JQuery<HTMLElement> = $('#card-' + pCard.id + ' .card-header');
                iconDiv.prepend(renderedIcon);
            });
        }
    }


    public changeState(pCardId: string, pNewState: State, pStateArg?: string | number): void
    {
        // border color and status class
        let elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' div.card-civbuddy');
        elem.removeClass(function (index: number, className: string): string {
            return (className.match(/\b(?:bg-success|card-status-\S+|border-\S+)/g) || []).join(' ');
        });
        elem.addClass('card-status-' + State[pNewState].toLowerCase());
        elem.addClass(this.getBorderStyle(pNewState));

        // state explanantion text
        elem = $('#card-' + pCardId + ' div.card-body > p.card-status-expl');
        elem.removeClass(function (index: number, className: string): string {
            return (className.match(/\btext-\S+/g) || []).join(' ');
        });
        elem.addClass(this.getTextStyle(pNewState));
        if (pStateArg !== undefined) {
            elem.attr('data-l10n-args', this.getExplanationArgumentJson(pStateArg) as string);
        }
        elem.attr('data-l10n-id', `cards-card-${State[pNewState].toLowerCase()}-expl`);
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
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' div.progress > div.bar-owned');
        const cardMaxCredits: number = (selectedRules.cards.get(pCardId) as Card).maxCredits;
        const percent: number = Math.round((pOwnedValue / cardMaxCredits) * 100);
        elem.attr('style', 'width: ' + percent + '%');
        elem.attr('aria-valuenow', pOwnedValue);

        // Adjust credit bar text, too.
        const card: Card = selectedRules.cards.get(pCardId) as Card;
        const cardState: CardData = currentSituation.states.get(pCardId) as CardData;
        this.setCreditBarInfoText(card, cardState);
    }


    /**
     * Change the 'planned' value of a card's credit bar.
     * @param pCardId the card ID whose credit bar to modify
     * @param pPlannedValue the new value of planned credits
     */
    public changeCreditBarPlanned(pCardId: string, pPlannedValue: number): void {
        const elem: JQuery<HTMLElement> = $('#card-' + pCardId + ' div.progress > div.bar-planned');
        elem.attr('aria-valuenow', pPlannedValue);
        if (pPlannedValue > 0) {
            const cardMaxCredits: number = (selectedRules.cards.get(pCardId) as Card).maxCredits;
            const percent: number = Math.round((pPlannedValue / cardMaxCredits) * 100);
            elem.attr('style', 'width: ' + percent + '%');
            elem.removeClass('d-none');
        } else {
            elem.addClass('d-none');
        }

        // Adjust credit bar text, too.
        const card: Card = selectedRules.cards.get(pCardId) as Card;
        const cardState: CardData = currentSituation.states.get(pCardId) as CardData;
        this.setCreditBarInfoText(card, cardState);
    }


    private setCreditBarInfoText(pCard: Card, pCardState: CardData): void {
        const creditInfoElem: JQuery<HTMLElement> = $('#card-' + pCard.id + ' .card-credits-info');
        const l10nArgs: string = buildL10nArgs(pCard, pCardState);
        creditInfoElem.attr('data-l10n-args', l10nArgs);
        let l10nId: string = 'cards-card-credits';
        if (pCardState.creditReceivedPlanned.size > 0) {
            l10nId += '-plan';
        }
        creditInfoElem.attr('data-l10n-id', l10nId);
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
}
