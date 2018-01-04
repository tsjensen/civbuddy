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
    storage.createSituation(selectedGame, dto);   // FIXME selectedGame does not contain its key
    addPlayerToPage(dto);
}

function getPlayerDtoFromDialog(): SituationDto {
    const playerName: string = getValueFromInput('inputPlayerName', 'ERROR - remove me');
    const playerKey: string = storage.newSituationKey();
    const variant: RulesJson = builtInVariants[selectedGame.variantKey];
    const targetPoints: number = Number(getValueFromRadioButtons('pointsTargetRadios', String(variant.targetOpts[0])));

    const player: PlayerDto = new PlayerDtoImpl(playerName, targetPoints);
    const funds: FundsDto = new FundsDtoImpl(0, {}, 0);
    const cardStates: Object = buildEmptyCardStatesMap(variant);
    const dto: SituationDto = new SituationDtoImpl(playerKey, player, funds, cardStates);
    return dto;
}

function buildEmptyCardStatesMap(pVariant: RulesJson): Object {
    let result: Object = {};
    for (let cardId in pVariant.cards) {
        result[cardId] = State.ABSENT;
    }
    return result;
}

function addPlayerToPage(pSituation: SituationDto): void {
    const variant: RulesJson = builtInVariants[selectedGame.variantKey];
    let htmlTemplate: string = $('#playerTemplate').html();
    Mustache.parse(htmlTemplate);
    let rendered: string = Mustache.render(htmlTemplate, {
        'situationKey': pSituation.key,
        'playerName': pSituation.player.name,
        'pointsTarget': pSituation.player.winningTotal
    }, {
        'numCardsOwned': 0
    });
    $('#playerList').append(rendered);
}