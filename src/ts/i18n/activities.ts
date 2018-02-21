import * as storage from '../storage/storage';
import { Activity, Page } from '../framework';
import { Language } from '../rules/rules';
import { LanguageController } from './controllers';
import { appOptions, runActivityInternal } from '../main';



/**
 * Change the active display language.
 */
export class ChangeLanguageActivity
    implements Activity
{
    public constructor(private readonly newLanguage: string) { }

    public execute(pPreviousLanguage: Language): void {
        const newLanguage: Language = Language[this.newLanguage.toUpperCase()];
        appOptions.language = newLanguage;
        storage.writeOptions(appOptions);
        runActivityInternal(Page.CROSS, 'activateLanguage', newLanguage.toString());
        window.dispatchEvent(new CustomEvent('applanguagechanged', {
            'detail': {'oldLang': pPreviousLanguage, 'newLang': newLanguage}
        }));
    }
}



/**
 * Activate the language that is already set on the current page.
 */
export class ActivateLanguageActivity
    implements Activity
{
    private readonly langCtrl: LanguageController = new LanguageController();

    public execute(pLanguage: Language): void {
        this.langCtrl.showLanguage(pLanguage);
        this.langCtrl.requestL10nLanguage(pLanguage);
    }
}
