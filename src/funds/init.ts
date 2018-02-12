import { PageContext, AbstractPageInitializer, Page } from '../framework';
import { Language } from '../rules/rules';


/**
 * The page context object of the 'funds' page.
 */
export class FundsPageContext implements PageContext {
    // TODO
}


/**
 * The page initializer of the 'funds' page.
 */
export class FundsPageInitializer extends AbstractPageInitializer<FundsPageContext>
{
    constructor() {
        super(Page.FUNDS, new FundsPageContext());
    }


    protected parseTemplates(): void {
        // TODO
    }

    protected pageLoaded(): void {
        // TODO
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        // TODO
    }
}
