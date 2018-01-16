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
            populateCardsList();
            setupPlannedHoverEffect();
            document.title = currentSituation.dao.player.name + ' - ' + selectedGame.name + ' - CivBuddy';
            setActivePlayer();
            // TODO funds, ruleset, etc.
        });
        window.addEventListener('applanguagechanged', function(): void {
            populateCardsList();
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
            currentSituation = new Situation(sit, variant);
            selectedGame = game;
            selectedRules = new Rules(variant);
            result = true;
        }
    }
    if (!result) {
        window.location.replace('index.html');
    }
    return result;
}

function populateCardsList(): void {
    $('#cardList > div[keep!="true"]').remove();
    const variant: RulesJson = selectedRules.variant;
    const cardStates: Map<string, CardData> = new Calculator(selectedRules, buildMap(selectedGame.options)).pageInit(currentSituation.dao.ownedCards);
    let htmlTemplate: string = $('#cardTemplate').html();
    Mustache.parse(htmlTemplate);
    const ctrl: CardController = new CardController(htmlTemplate);
    for (let cardId of Object.keys(variant.cards)) {
        const card: Card = selectedRules.cards.get(cardId) as Card;
        const cardData: CardData = cardStates.get(cardId) as CardData;
        ctrl.putCard(card, cardData);
    }
}


function buildL10nArgs(pNumCurrentCards: number, pNumPlannedCards: number, pMaxCards: number,
     pCurrentCredits: number, pPlannedCredits: number, pMaxCredits: number): string
{
    let d: Object = {
        'currentCards': pNumCurrentCards,
        'plannedCards': pNumPlannedCards,
        'maxCards': pMaxCards,
        'currentCredits': pCurrentCredits,
        'plannedCredits': pPlannedCredits,
        'maxCredits': pMaxCredits
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
    const creditsInfoElem: JQuery<HTMLElement> = $('#card-' + pCardId + ' p.card-credits-info');
    if (hoversOnCard(pCardId)) {
        cardElem.addClass('hovered');
        creditsInfoElem.removeClass('text-muted');
    } else {
        cardElem.removeClass('hovered');
        creditsInfoElem.addClass('text-muted');
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
        const currentState: State = currentSituation.states.get(pCardId) as State;
        if (currentState === State.ABSENT || currentState === State.DISCOURAGED) {
            // set card status to PLANNED
            // TODO
            window.alert('set to planned - not implemented');
        } else if (currentState === State.PLANNED) {
            // recalculate card status (no longer PLANNED)
            // TODO
            window.alert('set to NOT planned - not implemented');
        }
    } else {
        // show card information (always)
        // TODO
        window.alert('card info - not implemented');
    }
}


class CardController {
    private readonly htmlTemplate: string;

    constructor(pTemplate: string) {
        this.htmlTemplate = pTemplate;
    }


    /**
     * Add a card to the display. If it exists already, the existing card is replaced.
     * @param pCard the card information as specified in the rules
     * @param pCardState the runtime card information, representing its current state
     */
    public putCard(pCard: Card, pCardState: CardData): void
    {
        const creditBarWidth: number = Math.round((pCard.maxCredits / selectedRules.maxCredits) * 100);
        const state: State = pCardState.state;
        const rendered: string = Mustache.render(this.htmlTemplate, {
            'cardId': pCard.id,
            'cardTitle': pCard.dao.names[appOptions.language],
            'status': State[state].toString().toLowerCase(),
            'borderStyle': this.getBorderStyle(state),
            'textStyle': this.getTextStyle(state),
            'isOwned': state === State.OWNED,
            'isPlannable': this.isPlannable(state),
            'showExplanation': this.requiresExplanation(state),
            //'explArgs': getExplanationArgumentJson(),   // TODO
            'costNominal': pCard.dao.costNominal,
            'costCurrent': pCard.dao.costNominal - pCardState.sumCreditReceived,
            'creditBarWidth': creditBarWidth,
            'creditBarOwnedPercent': Math.round((pCardState.sumCreditReceived / pCard.maxCredits) * 100),
            'creditBarOwnedValue': pCardState.sumCreditReceived,
            'creditBarPlannedPercent': Math.round((pCardState.sumCreditReceivedPlanned / pCard.maxCredits) * 100),
            'creditBarPlannedValue': pCardState.sumCreditReceivedPlanned,
            'totalCredit': pCard.maxCredits
        });
        $('#card-' + pCard.id).remove();
        $('#cardList').append(rendered);

        const creditInfoElem: JQuery<HTMLElement> = $('#card-' + pCard.id + ' p.card-credits-info');
        const l10nArgs: string = buildL10nArgs(pCardState.creditReceived.size, pCardState.creditReceivedPlanned.size, pCard.creditsReceived.size,
            pCardState.sumCreditReceived, pCardState.sumCreditReceivedPlanned, pCard.maxCredits);
        creditInfoElem.attr('data-l10n-args', l10nArgs);
        let l10nId: string = 'cards-card-credits';
        if (pCardState.creditReceivedPlanned.size > 0) {
            l10nId += '-plan';
        }
        creditInfoElem.attr('data-l10n-id', l10nId);

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
            return (className.match(/\b(?:card-status-|border-)\S+/g) || []).join(' ');
        });
        elem.addClass('card-status-' + State[pNewState].toLowerCase());
        elem.addClass(this.getBorderStyle(pNewState));

        // title color  TODO this could be achieved via CSS from the card status class
        elem = $('#card-' + pCardId + ' div.card-combined-title').children();
        if (this.isPlannable(pNewState)) {
            elem.removeClass('text-muted');
        } else {
            elem.addClass('text-muted');
        }

        // state explanantion text
        elem = $('#card-' + pCardId + ' div.card-body > p:first-of-type');
        elem.removeClass(function (index: number, className: string): string {
            return (className.match(/\btext-\S+/g) || []).join(' ');
        });
        elem.addClass(this.getTextStyle(pNewState));
        if (pStateArg !== undefined) {
            elem.attr('data-l10n-args', JSON.stringify({'arg': pStateArg}));
        }
        elem.attr('data-l10n-id', `cards-card-${State[pNewState].toLowerCase()}-expl`);
        if (this.requiresExplanation(pNewState)) {
            elem.removeClass('d-none');
        } else {
            elem.addClass('d-none');
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
    }


    private isPlannable(pState: State): boolean {
        return pState !== State.UNAFFORDABLE && pState !== State.PREREQFAILED && pState !== State.OWNED;
    }

    private requiresExplanation(pState: State): boolean {
        return pState !== State.ABSENT && pState !== State.PLANNED;
    }


    private getBorderStyle(pState: State): string {
        let result: string = '';
        switch (pState) {
            case State.ABSENT:       result = 'border-info'; break;
            case State.DISCOURAGED:  result = 'border-warning'; break;
            case State.OWNED:        result = 'border-success'; break;
            case State.PLANNED:      result = ''; /* empty */ break;
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
}
