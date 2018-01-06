import * as Mustache from 'mustache';
import * as storage from './storage';
import { builtInVariants, RulesJson, Language, RuleOptionJson } from './rules';
import { appOptions, getLocalizedStringWithArgs } from './app';
import { GameDtoImpl, GameDto } from './dto';
import { error } from 'util';
import { getValueFromInput, getValueFromRadioButtons, focusAndPositionCursor, setNameIsInvalid } from './dom';

let gameNames: Set<string> = new Set<string>();

export function initGamesPage(): void {
    $(document).on('show.bs.modal', '#newGameModal', function(): void {   // before fade-in animation
        addVariantsToModal();
        chooseVariant(Object.keys(builtInVariants)[0]);
    });
    $(document).on('shown.bs.modal', '#newGameModal', function(): void {  // after fade-in animation
        setDefaultGameName();
        focusAndPositionCursor('inputGameName');
        validateGameName(null);
    });
    $(function(): void {
        populateGameList();   // execute after DOM has loaded
        setupGameNameValidation();
    });
    window.addEventListener('applanguagechanged', function(): void {
        populateGameList();
    });
}

function setupGameNameValidation(): void {
    $('#inputGameName').blur(validateGameName);
    $('#inputGameName').keyup(validateGameName);
}

function validateGameName(event): void {
    const s: string = getValueFromInput('inputGameName', '');
    const valid: boolean = s.length > 0 && !gameNames.has(s);
    const empty: boolean = !valid && s.length === 0;
    setNameIsInvalid('newGameModal', 'inputGameName', 'games-newModal-label-', !valid, empty);
    if (valid && event !== null && event.which == 13) {
        createGame();
    }
}

function populateGameList(): void {
    $('#gameList > div').remove();
    gameNames.clear();
    const games:GameDto[] = storage.readListOfGames();
    for (let game of games) {
        gameNames.add(game.name);
        addGameToPage(game);
    }
}

function setDefaultGameName(): void {
    const inputField: HTMLElement | null = document.getElementById('inputGameName');
    if (inputField !== null) {
        const today: Date = new Date();
        const date: string = today.getFullYear() + '-' + leadingZero(today.getMonth() + 1) + '-' + leadingZero(today.getDate());
        let count: number = 1;
        let defaultName: string = date;
        while (gameNames.has(defaultName)) {
            defaultName = date + '.' + count++;
        }
        inputField.setAttribute('value', defaultName);
    }
}

function leadingZero(pNumber: number): string {
    let s: string = pNumber.toString();
    if (pNumber >= 0 && pNumber < 10) {
        s = '0' + s;
    }
    return s;
}

export function createGame(): void {
    const dto: GameDto = getGameDtoFromDialog();
    $('#newGameModal').modal('hide');
    gameNames.add(dto.name);
    storage.saveGame(dto);
    addGameToPage(dto);
}

function getGameDtoFromDialog(): GameDto {
    const gameName: string = getValueFromInput('inputGameName', 'ERROR - remove me');
    const gameKey: string = storage.newGameKey();
    const ruleKey: string = getValueFromRadioButtons('rulesRadios', builtInVariants.keys[0]);
    const variant: RulesJson = builtInVariants[ruleKey];
    const optionValues: Object = buildOptionValueMap(variant);
    const dto: GameDto = new GameDtoImpl(gameKey, gameName, ruleKey, optionValues, {});
    return dto;
}

function addGameToPage(pGame: GameDto): void {
    const variant: RulesJson = builtInVariants[pGame.variantKey];
    const rulesName: string = variant.displayNames[appOptions.language];
    const optionDesc: string = buildOptionDescriptor(variant, pGame.options);

    let htmlTemplate: string = $('#gameTemplate').html();
    Mustache.parse(htmlTemplate);
    let rendered: string = Mustache.render(htmlTemplate, {
        'ruleDisplayName': rulesName,
        'gameKey': pGame.key,
        'gameName': pGame.name,
        'options': optionDesc
    });
    $('#gameList').append(rendered);
}

function buildOptionValueMap(pVariant: RulesJson): Object {
    let result: Object = {};
    if (pVariant.options !== null && pVariant.options.length > 0) {
        for (let option of pVariant.options) {
            let v: string = option.defaultValue;
            if (option.type === 'checkbox') {
                v = $('#option-' + option.id).is(':checked').toString();
            } else {
                console.log('ERROR: Unknown option type - ' + option.type);
            }
            result[option.id] = v;
        }
    }
    return result;
}

function buildOptionDescriptor(pVariant: RulesJson, pOptionValues: Object): string {
    let result: string = '';
    if (pVariant.options !== null && pVariant.options.length > 0) {
        for (let option of pVariant.options) {
            let v: string | undefined = pOptionValues[option.id];
            if (typeof(v) === 'undefined' || v.length === 0) {
                v = option.defaultValue;
            }
            let shortText: string = option.shortText[v][appOptions.language];
            if (result.length > 0) {
                result += ', ';
            }
            result += shortText;
        }
    }
    if (result.length == 0) {
        result = '--';
    }
    return result;
}

export function deleteGame(pGameKey: string, pGameName: string): void {
    getLocalizedStringWithArgs('games-delete-confirm', {'name': pGameName}, function(msg: string): void {
        if (window.confirm(msg)) {
            storage.deleteGame(pGameKey);
            gameNames.delete(pGameName);
            $('#'+pGameKey).remove();
        }
    });
}

function addVariantsToModal(): void {
    $('#rulesRadios > div').remove();
    let htmlTemplate: string = $('#rulesRadioTemplate').html();
    Mustache.parse(htmlTemplate);
    let first: boolean = true;
    for (let variantId in builtInVariants) {
        let variant: RulesJson = builtInVariants[variantId];
        let rendered: string = Mustache.render(htmlTemplate, {
            'variantId': variantId,
            'checked': first,
            'displayName': variant.displayNames[appOptions.language]
        });
        first = false;
        $('#rulesRadios').append(rendered);
    }
}

export function chooseVariant(pVariantId: string): void {
    const variant: RulesJson = builtInVariants[pVariantId];
    const options: RuleOptionJson[] = variant.options;
    $('#rulesOptions > div').remove();

    if (options.length === 0) {
        $('#rulesOptions > p').removeClass('d-none');
        return;
    }
    $('#rulesOptions > p').addClass('d-none');

    for (let option of options) {
        if (option.type === 'checkbox') {
            const defaultValue: boolean = option.defaultValue === 'true';
            let htmlTemplate: string = $('#optionCheckBoxTemplate').html();
            Mustache.parse(htmlTemplate);
            let rendered: string = Mustache.render(htmlTemplate, {
                'optionId': option.id,
                'optionDisplayName': option.displayNames[appOptions.language],
                'checked': defaultValue,
                'explanation': option.explanation[appOptions.language]
            });
            $('#rulesOptions').append(rendered);
        } else {
            console.log('ERROR: Unknown option type - ' + option.type);
        }
    }
}


export function selectGame(pGameKey: string): void {
    window.location.href = 'players.html?ctx=' + pGameKey;
}
