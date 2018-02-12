import * as Mustache from 'mustache';
import { BaseController } from '../framework';
import { Language } from '../rules/rules';


/**
 * Manages display of the active language and its alternatives.
 */
export class LanguageController
    extends BaseController
{
    public constructor() {
        super();
    }


    public showLanguage(pSelectedLanguage: Language): void {
        // TODO modify 'players' and 'cards' pages so that we never show the active flag - then remove it here
        let htmlTemplate: string = $('#flagTemplate').html();
        const activeFlagHtml: string = Mustache.render(htmlTemplate, {
            'fileName': pSelectedLanguage.toString(),
            'alt': pSelectedLanguage.toUpperCase()
            // no menuText
        });
    
        const otherLanguage = pSelectedLanguage === Language.EN ? Language.DE : Language.EN;
        const otherLabel: string = pSelectedLanguage === Language.EN ? 'Deutsch' : 'English';
        const otherFlagHtml: string = Mustache.render(htmlTemplate, {
            'fileName': otherLanguage.toString(),
            'alt': otherLanguage.toUpperCase(),
            'menuText': otherLabel
        });
    
        let elem: JQuery<HTMLElement> = $('#navbarLangDropdownLabel');
        if (elem.length > 0) {
            elem.empty();
            elem.append(activeFlagHtml);
        }
    
        for (let divId of ['#otherLanguageFlags', '#otherLanguageFlags2']) {
            elem = $(divId);
            if (elem.length > 0) {
                elem.empty();
                elem.append(otherFlagHtml);
            }
        }
    }


    public requestL10nLanguage(pRequestedLanguage: Language): void {
        if (document.hasOwnProperty('l10n')) {
            document['l10n'].requestLanguages([pRequestedLanguage]);   // It's ok to list only the requested language.
        }
    }
}
