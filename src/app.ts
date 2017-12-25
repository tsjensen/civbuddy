import * as Mustache from 'mustache';
import { Language } from './rules';
import { readOptions, writeOptions } from './storage';
import { AppOptions } from './dto';


export let appOptions: AppOptions = (() => { return readOptions(); })();


export function showLanguage(): void {
    const selectedLanguage: Language = appOptions.language;
    let htmlTemplate: string = $('#flagTemplate').html();
    Mustache.parse(htmlTemplate);

    const activeFlagHtml: string = Mustache.render(htmlTemplate, {
        'fileName': selectedLanguage.toString(),
        'alt': selectedLanguage.toUpperCase()
        // no menuText
    });

    const otherLanguage = selectedLanguage === Language.EN ? Language.DE : Language.EN;
    const otherLabel: string = selectedLanguage === Language.EN ? 'German' : 'English';
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

export function changeLanguage(pNewLanguage: Language): void {
    appOptions.language = pNewLanguage;
    writeOptions(appOptions);
    showLanguage();
}