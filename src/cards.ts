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
    for (let cardId of Object.keys(variant.cards)) {
        const card: Card = selectedRules.cards.get(cardId) as Card;
        const cardData: CardData = cardStates.get(cardId) as CardData;
        const creditBarWidth: number = Math.round((card.maxCredits / selectedRules.maxCredits) * 100);
        const state: State = cardData.state;
        let rendered: string = Mustache.render(htmlTemplate, {
            'cardId': cardId,
            'cardTitle': card.dao.names[appOptions.language],
            'status': State[state].toString().toLowerCase(),
            'borderStyle': getBorderStyle(state),
            'textStyle': getTextStyle(state),
            'isOwned': state === State.OWNED,
            'isPlannable': state !== State.UNAFFORDABLE && state !== State.PREREQFAILED,
            'showExplanation': state !== State.ABSENT && state !== State.PLANNED,
            //'explArgs': getExplanationArgumentJson(),   // TODO
            'costNominal': card.dao.costNominal,
            'costCurrent': card.dao.costNominal - cardData.sumCreditReceived,
            'creditBarWidth': creditBarWidth,
            'creditBarOwnedPercent': Math.round((cardData.sumCreditReceived / card.maxCredits) * 100),
            'creditBarOwnedValue': cardData.sumCreditReceived,
            'creditBarPlannedPercent': Math.round((cardData.sumCreditReceivedPlanned / card.maxCredits) * 100),
            'creditBarPlannedValue': cardData.sumCreditReceivedPlanned,
            'totalCredit': card.maxCredits
        });
        $('#cardList').append(rendered);

        const creditInfoElem: JQuery<HTMLElement> = $('#card-' + cardId + ' p.card-credits-info');
        const l10nArgs: string = buildL10nArgs(cardData.creditReceived.size, cardData.creditReceivedPlanned.size, card.creditsReceived.size,
            cardData.sumCreditReceived, cardData.sumCreditReceivedPlanned, card.maxCredits);
        creditInfoElem.attr('data-l10n-args', l10nArgs);
        let l10nId: string = 'cards-card-credits';
        if (cardData.creditReceivedPlanned.size > 0) {
            l10nId += '-plan';
        }
        creditInfoElem.attr('data-l10n-id', l10nId);

        const groupIconHtmlTemplate: string = $('#groupIconTemplate').html();
        Mustache.parse(groupIconHtmlTemplate);
        for (let group of Array.from(card.dao.groups).reverse()) {
            const lowerCaseName: string = group.toString().toLowerCase();
            getLocalizedString('cards-group-' + lowerCaseName, function(localizedGroupName: string): void {
                let renderedIcon: string = Mustache.render(groupIconHtmlTemplate, {
                    'iconName': lowerCaseName,
                    'groupName': localizedGroupName
                });
                const iconDiv: JQuery<HTMLElement> = $('#card-' + cardId + ' .card-header');
                iconDiv.prepend(renderedIcon);
            });
        }
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

function getBorderStyle(pState: State): string {
    let result: string = '';
    switch (pState) {
        case State.ABSENT:       result = 'border-info'; break;
        case State.DISCOURAGED:  result = 'border-warning'; break;
        case State.OWNED:        result = 'border-success'; break;
        case State.PLANNED:      result = ''; /* empty */ break;
        case State.PREREQFAILED: result = 'border-secondary'; break;
        case State.UNAFFORDABLE: result = 'border-danger'; break;
        default: result = ''; /* empty */ break;
    }
    return result;
}

function getTextStyle(pState: State): string {
    let result: string = '';
    switch (pState) {
        case State.ABSENT:       result = ''; /* empty */ break;
        case State.DISCOURAGED:  result = 'text-warning'; break;
        case State.OWNED:        result = 'text-muted'; break;
        case State.PLANNED:      result = ''; /* empty */ break;
        case State.PREREQFAILED: result = 'text-muted'; break;
        case State.UNAFFORDABLE: result = 'text-danger'; break;
        default: result = ''; /* empty */ break;
    }
    return result;
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
