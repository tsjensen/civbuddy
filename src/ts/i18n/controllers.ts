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


    public showLanguage(pSelectedLanguage: Language): void
    {
        const otherLanguage = pSelectedLanguage === Language.EN ? Language.DE : Language.EN;
        const otherLabel: string = pSelectedLanguage === Language.EN ? 'Deutsch' : 'English';

        const htmlTemplate: string = $('#flagTemplate').html();
        const otherFlagHtml: string = Mustache.render(htmlTemplate, {
            'fileName': otherLanguage.toString(),
            'alt': otherLanguage.toUpperCase(),
            'menuText': otherLabel
        });
    
        let elem: JQuery<HTMLElement> = $('#otherLanguageFlags');
        if (elem.length > 0) {
            elem.empty();
            elem.append(otherFlagHtml);
        }
    }


    public requestL10nLanguage(pRequestedLanguage: Language): void {
        if (document.hasOwnProperty('l10n')) {
            document['l10n'].requestLanguages([pRequestedLanguage]);   // It's ok to list only the requested language.
        }
    }
}
