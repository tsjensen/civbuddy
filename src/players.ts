import * as Mustache from 'mustache';
import * as storage from './storage';
import { GameDto, SituationDto, PlayerDto, PlayerDtoImpl, FundsDto, FundsDtoImpl, SituationDtoImpl, State } from './dto';
import { builtInVariants, RulesJson } from './rules';
import { focusAndPositionCursor, getValueFromInput, getValueFromRadioButtons } from './dom';


let selectedGame: GameDto;
let playerNames: Set<string> = new Set<string>();


export function initPlayersPage(): void {
    if (getGameFromUrl()) {
        $(document).on('show.bs.modal', '#newPlayerModal', function(): void {   // before fade-in animation
            addTargetsToModal();
        });
        $(document).on('shown.bs.modal', '#newPlayerModal', function(): void {  // after fade-in animation
            focusAndPositionCursor('inputPlayerName');
            //validateGameName(null);   // TODO
        });
        $(function(): void {
            populatePlayerList();   // execute after DOM has loaded
            //setupPlayerNameValidation();
        });
        // TODO
    }
}

function getGameFromUrl(): boolean {
    const gameKey: string | null = getUrlParameter('ctx');
    const game: GameDto | null = storage.readGame(gameKey);
    if (game !== null) {
        selectedGame = game;
        return true;
    } else {
        window.location.replace('index.html');
        return false;
    }
}

function getUrlParameter(pParamName: string): string | null {
    let result: string | null = null;
    let pageUrl: string = decodeURIComponent(window.location.search.substring(1));
    let params: string[] = pageUrl.split('&');
    for (let i: number = 0; i < params.length; i++) {
        let paramKeyValue: string[] = params[i].split('=');
        if (paramKeyValue[0] === pParamName && typeof(paramKeyValue[1]) !== undefined) {
            result = paramKeyValue[1];
            break;
        }
    }
    return result;
}

function addTargetsToModal(): void {
    $('#pointsTargetRadios > div').remove();
    let htmlTemplate: string = $('#pointsTargetRadioTemplate').html();
    Mustache.parse(htmlTemplate);
    let first: boolean = true;
    for (let target of builtInVariants[selectedGame.variantKey].targetOpts) {
        let rendered: string = Mustache.render(htmlTemplate, {
            'pointsValue': target,
            'checked': first
        });
        $('#pointsTargetRadios').append(rendered);
        first = false;
    }
}

export function createPlayer(): void {
    const dto: SituationDto = getPlayerDtoFromDialog();
    $('#newPlayerModal').modal('hide');
    playerNames.add(dto.player.name);
    selectedGame.situations[dto.player.name] = dto.key;
    storage.createSituation(selectedGame, dto);
    addPlayerToPage(dto);
}

function getPlayerDtoFromDialog(): SituationDto {
    const playerName: string = getValueFromInput('inputPlayerName', 'ERROR - remove me');
    const playerKey: string = storage.newSituationKey();
    const variant: RulesJson = builtInVariants[selectedGame.variantKey];
    const targetPoints: number = Number(getValueFromRadioButtons('pointsTargetRadios', String(variant.targetOpts[0])));

    const player: PlayerDto = new PlayerDtoImpl(playerName, targetPoints);
    const funds: FundsDto = new FundsDtoImpl(0, {}, 0);
    const dto: SituationDto = new SituationDtoImpl(playerKey, player, funds, {});
    return dto;
}

function addPlayerToPage(pSituation: SituationDto): void {
    let htmlTemplate: string = $('#playerTemplate').html();
    Mustache.parse(htmlTemplate);
    let rendered: string = Mustache.render(htmlTemplate, {
        'situationKey': pSituation.key,
        'playerName': pSituation.player.name,
        'pointsTarget': pSituation.player.winningTotal
    });
    $('#playerList').append(rendered);
    showNumCardsOwned(pSituation);
}

function showNumCardsOwned(pSituation: SituationDto): void {
    let numCardsOwned: number = countCardsOwned(pSituation);
    let cardsTranslationKey: string = 'players-cards-';
    const elem: JQuery<HTMLElement> = $('#' + pSituation.key + ' div.card-header > span');
    if (numCardsOwned === 0) {
        cardsTranslationKey += '0';
    } else if (numCardsOwned === 1) {
        cardsTranslationKey += 'one';
    } else {
        cardsTranslationKey += 'other';
        elem.attr('data-l10n-args', `{"count": ${numCardsOwned}}`);
    }
    elem.attr('data-l10n-id', cardsTranslationKey);
}

function countCardsOwned(pSituation: SituationDto): number {
    let result: number = 0;
    for (let cardId of Object.keys(pSituation.cardStates)) {
        let stateStr: string = pSituation.cardStates[cardId];
        if (State.OWNED.toString() === stateStr) {
            result++;
        }
    }
    return result;
}

function populatePlayerList(): void {
    $('#playerList > div').remove();
    playerNames.clear();
    const situations:SituationDto[] = storage.readSituationsForGame(selectedGame);
    for (let situation of situations) {
        playerNames.add(situation.player.name);
        addPlayerToPage(situation);
    }
}