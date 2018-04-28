import { Activity, Page } from '../framework/framework';
import { runActivityInternal } from '../main';
import { Language } from '../rules/rules';
import { GlobalOptions } from '../storage/storage';
import { LanguageController } from './controllers';



/**
 * Change the active display language.
 */
export class ChangeLanguageActivity
    implements Activity {

    public constructor(private readonly newLanguage: string) { }

    public execute(pPreviousLanguage: Language): void {
        const globalOptions: GlobalOptions = new GlobalOptions();
        const newLanguage: Language = Language[this.newLanguage.toUpperCase() as keyof typeof Language];
        globalOptions.get().language = newLanguage;
        globalOptions.writeOptions();
        runActivityInternal(Page.CROSS, 'activateLanguage', newLanguage.toString());
        window.dispatchEvent(new CustomEvent('applanguagechanged', {
            'detail': { 'oldLang': pPreviousLanguage, 'newLang': newLanguage }
        }));
    }
}



/**
 * Activate the language that is already set on the current page.
 */
export class ActivateLanguageActivity
    implements Activity {

    private readonly langCtrl: LanguageController = new LanguageController();

    public execute(pLanguage: Language): void {
        this.langCtrl.showLanguage(pLanguage);
        this.langCtrl.requestL10nLanguage(pLanguage);
    }
}
