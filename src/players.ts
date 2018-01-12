import * as Mustache from 'mustache';
import * as storage from './storage';
import { GameDto, SituationDto, PlayerDto, PlayerDtoImpl, FundsDto, FundsDtoImpl, SituationDtoImpl } from './dto';
import { builtInVariants, RulesJson } from './rules';
import { focusAndPositionCursor, getValueFromInput, getValueFromRadioButtons, setNameIsInvalid, getUrlParameter } from './dom';
import { getLocalizedStringWithArgs } from './app';


let selectedGame: GameDto;
let playerNames: Set<string> = new Set<string>();


export function initPlayersPage(): void {
    if (getGameFromUrl()) {
        $(document).on('show.bs.modal', '#newPlayerModal', function(): void {   // before fade-in animation
            addTargetsToModal();
        });
        $(document).on('shown.bs.modal', '#newPlayerModal', function(): void {  // after fade-in animation
            focusAndPositionCursor('inputPlayerName');
            validatePlayerName(null);
        });
        $(function(): void {
            populatePlayerList();   // execute after DOM has loaded
            setupPlayerNameValidation();
            document.title = selectedGame.name + ' - CivBuddy';
            $('#gameName').html(selectedGame.name);
        });
    }
}

function setupPlayerNameValidation() {
    $('#inputPlayerName').blur(validatePlayerName);
    $('#inputPlayerName').keyup(validatePlayerName);
}

function validatePlayerName(event): void {
    const s: string = getValueFromInput('inputPlayerName', '');
    const valid: boolean = s.length > 0 && !playerNames.has(s);
    const empty: boolean = !valid && s.length === 0;
    setNameIsInvalid('newPlayerModal', 'inputPlayerName', 'players-newModal-label-', !valid, empty);
    if (valid && event !== null && event.which == 13) {
        createPlayer();
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
    const dto: SituationDto = new SituationDtoImpl(playerKey, selectedGame.key, player, funds, []);
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
    const numCardsOwned: number = pSituation.ownedCards.length;
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

function populatePlayerList(): void {
    $('#playerList > div').remove();
    playerNames.clear();
    const situations:SituationDto[] = storage.readSituationsForGame(selectedGame);
    for (let situation of situations) {
        playerNames.add(situation.player.name);
        addPlayerToPage(situation);
    }
}

export function deletePlayer(pSituationKey: string, pPlayerName: string): void {
    getLocalizedStringWithArgs('players-delete-confirm', {'name': pPlayerName}, function(msg: string): void {
        if (window.confirm(msg)) {
            storage.deleteSituation(selectedGame, pSituationKey);
            playerNames.delete(pPlayerName);
            $('#' + pSituationKey).remove();
        }
    });
}

export function selectPlayer(pSituationKey: string): void {
    window.location.href = 'cards.html?ctx=' + pSituationKey;
}
