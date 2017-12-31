import * as Mustache from 'mustache';
import * as storage from './storage';
import { builtInVariants, RulesJson, Language, RuleOptionJson } from './rules';
import { appOptions } from './app';


export function initGamesPage(): void {
    $(document).on('show.bs.modal', '#newGameModal', function () {   // before fade-in animation
        setDefaultGameName();
        addVariantsToModal();
    });
    $(document).on('shown.bs.modal', '#newGameModal', function () {  // after fade-in animation
        focusAndPositionCursor('inputGameName');
    });
}

function setDefaultGameName(): void {
    const inputField: HTMLElement | null = document.getElementById('inputGameName');
    if (inputField !== null) {
        const today: Date = new Date();
        const date: string = today.getFullYear() + '-' + leadingZero(today.getMonth() + 1) + '-' + leadingZero(today.getDate());
        inputField.setAttribute('value', date);
    }
}

function leadingZero(pNumber: number): string {
    let s: string = pNumber.toString();
    if (pNumber >= 0 && pNumber < 10) {
        s = '0' + s;
    }
    return s;
}

function focusAndPositionCursor(pInputFieldName: string): void {
    const inputField: HTMLInputElement | null = <HTMLInputElement>document.getElementById(pInputFieldName);
    if (inputField !== null) {
        inputField.focus();
        inputField.selectionStart = inputField.selectionEnd = inputField.value.length;
    }
}

export function createGame(): void {
    const gameName: string = getValueFromInput('inputGameName', 'ERROR - remove me');
    const gameKey: string = storage.newGameKey();
    const ruleKey: string = getValueFromRadioButtons('rulesRadios', builtInVariants.keys[0]);
    const variant: RulesJson = builtInVariants[ruleKey];
    const rulesName: string = variant.displayNames[appOptions.language];
    const optionDesc: string = buildOptionDescriptor(variant);

    // TODO save to localStorage
    $('#newGameModal').modal('hide');
    let htmlTemplate: string = $('#gameTemplate').html();
    Mustache.parse(htmlTemplate);
    let rendered: string = Mustache.render(htmlTemplate, {
        'ruleDisplayName': rulesName,
        'gameKey': gameKey,
        'gameName': gameName,
        'options': optionDesc
    });
    $('#gameList').append(rendered);
}

function buildOptionDescriptor(pVariant: RulesJson): string {
    let result: string = '';
    if (pVariant.options !== null && pVariant.options.length > 0) {
        for (let opt of pVariant.options) {
            // TODO get selected value
            let shortText: string = opt.shortText['true'][appOptions.language];
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

function getValueFromInput(pInputFieldName: string, pDefault: string): string {
    let result: string = pDefault;
    const nameInputField: HTMLElement | null = document.getElementById(pInputFieldName);
    if (nameInputField !== null) {
        const v: string | null = nameInputField.getAttribute('value');
        if (v !== null && v.trim().length > 0) {
            result = v.trim();
        }
    }
    return result;
}

function getValueFromRadioButtons(pRadioGroupName: string, pDefault: string): string {
    let result: string = pDefault;
    const checkedRadioField: JQuery<HTMLElement> = $('#' + pRadioGroupName + ' input:radio:checked');
    const v: string | number | string[] | undefined = checkedRadioField.val();
    if (typeof(v) === 'string' && v.length > 0) {
        result = v;
    }
    return result;
}

export function deleteGame(pGameKey: string, pGameName: string): void {
    if (window.confirm('Really delete game "' + pGameName + '"?')) {
        storage.deleteGame(pGameKey);
        $('#'+pGameKey).remove();
    }
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
    // TODO HERE
}