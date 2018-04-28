import { AbstractPageInitializer, Page, PageContext } from '../framework/framework';
import { GamesController } from '../games/controllers';
import { appVersion } from '../main';
import { Language } from '../rules/rules';


/**
 * The page context object of the 'error' page.
 */
export class ErrorPageContext implements PageContext {
}


/**
 * The page initializer of the 'error' page.
 */
export class ErrorPageInitializer
    extends AbstractPageInitializer<ErrorPageContext> {

    private readonly gamesCtrl: GamesController = new GamesController();

    constructor() {
        super(Page.ERROR, new ErrorPageContext());
    }


    protected parseTemplates(): void {
        // no templates
    }

    protected pageLoaded(): void {
        this.displayAppVersion();
        this.activateLocalizedErrorMessage(this.getAppOptions().language);
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        this.activateLocalizedErrorMessage(pNew);
    }

    private activateLocalizedErrorMessage(pLang: Language): void {
        this.gamesCtrl.hideElement($('*[class^="civbuddy-lang-"],*[class*=" civbuddy-lang-"]'));
        this.gamesCtrl.showElement($('.civbuddy-lang-' + pLang.toLowerCase()));
    }

    private displayAppVersion(): void {
        // TODO avoid copy/paste from games/init.ts
        const v: string = appVersion.version + '.' + appVersion.numCommits + ' (' + appVersion.hash + ')';
        this.gamesCtrl.setAppVersion(v, appVersion.dirty);
    }
}
