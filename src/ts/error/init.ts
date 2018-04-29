import { AbstractPageContext, AbstractPageInitializer, Page } from '../framework/framework';
import { AppVersion } from '../framework/version';
import { GamesController } from '../games/controllers';
import { Language } from '../rules/rules';


/**
 * The page context object of the 'error' page.
 */
export class ErrorPageContext extends AbstractPageContext {
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
        const v: AppVersion = this.pageContext.appVersion;
        this.gamesCtrl.setAppVersion(v.getCombinedVersion(), v.isDirty());
    }
}
