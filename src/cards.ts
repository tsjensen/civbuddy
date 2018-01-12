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


export function initCardsPage(): void {
    if (getSituationFromUrl()) {
        $(function(): void {
            populateCardsList();   // execute after DOM has loaded
            document.title = currentSituation.dao.player.name + ' - ' + selectedGame.name + ' - CivBuddy';
            setActivePlayer();
            // TODO funds, ruleset, etc.
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
    // TODO
    const variant: RulesJson = selectedRules.variant;
    const cardStates: Map<string, CardData> = new Calculator(selectedRules, buildMap(selectedGame.options)).pageInit(currentSituation.dao.ownedCards);
    const maxCredits: number = selectedRules.maxCredits;
    let htmlTemplate: string = $('#cardTemplate').html();
    Mustache.parse(htmlTemplate);
    for (let cardId of Object.keys(variant.cards)) {
        const card: CardJson = variant.cards[cardId];
        const cardData: CardData = cardStates.get(cardId) as CardData;
        const cardMaxCredits: number = (selectedRules.cards.get(cardId) as Card).maxCredits;
        const creditBarWidth: number = Math.round((cardMaxCredits as number / maxCredits) * 100);
        const state: State = cardData.state;
        let rendered: string = Mustache.render(htmlTemplate, {
            'cardId': cardId,
            'cardTitle': card.names[appOptions.language],
            'status': State[state].toString().toLowerCase(),
            'borderStyle': getBorderStyle(state),
            'textStyle': getTextStyle(state),
            'isOwned': state === State.OWNED,
            'isPlannable': state !== State.UNAFFORDABLE && state !== State.PREREQFAILED,
            'showExplanation': state !== State.ABSENT && state !== State.PLANNED,
            //'explArgs': getExplanationArgumentJson(),
            'costNominal': card.costNominal,
            'costCurrent': card.costNominal - cardData.sumCreditReceived,
            'creditBarWidth': creditBarWidth,
            'creditBarOwnedPercent': Math.round((cardData.sumCreditReceived / (selectedRules.cards.get(cardId) as Card).maxCredits) * 100),
            'creditBarOwnedValue': cardData.sumCreditReceived,
            'totalCredit': cardMaxCredits
        });
        $('#cardList').append(rendered);

        const groupIconHtmlTemplate: string = $('#groupIconTemplate').html();
        Mustache.parse(groupIconHtmlTemplate);
        for (let group of Array.from(card.groups).reverse()) {
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
