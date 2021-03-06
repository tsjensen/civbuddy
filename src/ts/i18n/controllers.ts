import * as Mustache from 'mustache';

import { BaseController } from '../framework/framework';
import { Language } from '../rules/rules';
import { StorageSupport } from '../storage/storage';


/**
 * Manages display of the active language and its alternatives.
 */
export class LanguageController
    extends BaseController {

    public constructor() {
        super();
    }


    public showLanguage(pSelectedLanguage: Language): void {
        const otherLanguage = pSelectedLanguage === Language.EN ? Language.DE : Language.EN;
        const otherLabel: string = pSelectedLanguage === Language.EN ? 'Deutsch' : 'English';

        const htmlTemplate: string = $('#flagTemplate').html();
        const otherFlagHtml: string = Mustache.render(htmlTemplate, {
            'alt': otherLanguage.toUpperCase(),
            'fileName': otherLanguage.toString(),
            'langParam': this.pageWantsLangParam() ? otherLanguage.toString() : undefined,
            'menuText': otherLabel
        });

        const elem: JQuery<HTMLElement> = $('#otherLanguageFlags');
        if (elem.length > 0) {
            elem.empty();
            elem.append(otherFlagHtml);
        }
    }


    private pageWantsLangParam(): boolean {
        return !new StorageSupport().isLocalStorageUsed();
    }


    public requestL10nLanguage(pRequestedLanguage: Language): void {
        if (document.hasOwnProperty('l10n')) {
            // It's ok to list only the requested language.
            (document as any)['l10n'].requestLanguages([pRequestedLanguage]);  // tslint:disable-line:no-string-literal
        }
    }
}
