import 'babel-polyfill';
import * as Mustache from 'mustache';
import { Language } from './rules';
import { readOptions, writeOptions } from './storage';
import { AppOptions } from './dto';


export let appOptions: AppOptions = (() => { return readOptions(); })();


export function changeLanguage(pNewLanguage: Language): void {
    appOptions.language = pNewLanguage;
    writeOptions(appOptions);
    activateLanguage(pNewLanguage);
}

export function activateLanguage(pNewLanguage: Language): void {
    showLanguage();
    if (document.hasOwnProperty('l10n')) {
        document['l10n'].requestLanguages([pNewLanguage]);
    }
}

function showLanguage(): void {
    const selectedLanguage: Language = appOptions.language;
    let htmlTemplate: string = $('#flagTemplate').html();
    Mustache.parse(htmlTemplate);

    const activeFlagHtml: string = Mustache.render(htmlTemplate, {
        'fileName': selectedLanguage.toString(),
        'alt': selectedLanguage.toUpperCase()
        // no menuText
    });

    const otherLanguage = selectedLanguage === Language.EN ? Language.DE : Language.EN;
    const otherLabel: string = selectedLanguage === Language.EN ? 'Deutsch' : 'English';
    const otherFlagHtml: string = Mustache.render(htmlTemplate, {
        'fileName': otherLanguage.toString(),
        'alt': otherLanguage.toUpperCase(),
        'menuText': otherLabel
    });

    let elem: JQuery<HTMLElement> = $('#navbarLangDropdownLabel');
    elem.empty();
    elem.append(activeFlagHtml);

    elem = $('#otherLanguageFlags');
    elem.empty();
    elem.append(otherFlagHtml);
}


export function getLocalizedString(pKey: string): string {
    let result: string = 'ERROR';
    if (document.hasOwnProperty('l10n')) {
        const localization = document['l10n'].get('main');
        result = localization.formatValue(pKey);
        if (typeof(result) !== 'string' || result.length === 0) {
            result = 'ERROR';
        }
    }
    return result;
}
