import * as storage from '../storage/storage';
import { PageContext, AbstractPageInitializer, Page } from '../framework';
import { Language, Rules, RulesJson, builtInVariants } from '../rules/rules';
import { GameDao, SituationDao, GameDaoImpl } from '../storage/dao';
import { Situation } from '../model';
import { Util } from '../util';
import { NavbarController } from './controllers';
import { appOptions } from '../main';


/**
 * The page context object of the 'funds' page.
 */
export class FundsPageContext implements PageContext {
    constructor(
        public readonly selectedGame: GameDao,
        public readonly selectedRules: Rules,
        public readonly currentSituation: Situation) {
    }
}


/**
 * The page initializer of the 'funds' page.
 */
export class FundsPageInitializer extends AbstractPageInitializer<FundsPageContext>
{
    constructor() {
        super(Page.FUNDS, FundsPageInitializer.buildPageContext());
    }

    private static buildPageContext(): FundsPageContext {
        const situationKey: string | null = Util.getUrlParameter('ctx');
        const sit: SituationDao | null = storage.readSituation(situationKey);
        let result: FundsPageContext | null = null;
        if (sit !== null) {
            const game: GameDao | null = storage.readGame(sit.gameId);
            if (game != null) {
                const variant: RulesJson = builtInVariants[game.variantKey];
                let selectedRules: Rules = new Rules(variant);
                let currentSituation: Situation = new Situation(sit, Util.buildMap(game.options), selectedRules);
                currentSituation.recalculate();
                result = new FundsPageContext(game, selectedRules, currentSituation);
            }
        }
        if (result === null) {
            window.location.replace('index.html');
        }
        return result as FundsPageContext;
    }


    protected parseTemplates(): void {
        // TODO
    }

    protected pageLoaded(): void {
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.addGameIdToLinks(this.pageContext.selectedGame.key);
        navCtrl.addSituationIdToLinks(this.pageContext.currentSituation.getId());
        navCtrl.setGameName(this.pageContext.selectedGame.name);
        document.title = this.pageContext.currentSituation.getPlayerName() + ' - '
            + this.pageContext.selectedGame.name + ' - CivBuddy';
        this.setActivePlayer();
        this.languageChanged(appOptions.language, appOptions.language);
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        const navbarCtrl: NavbarController = new NavbarController();
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        navbarCtrl.setVariantName(variant.displayNames[pNew]);
        navbarCtrl.setOptionDesc(
                GameDaoImpl.buildOptionDescriptor(variant, this.pageContext.selectedGame.options, pNew));
    }


    private setActivePlayer(): void {
        const navCtrl: NavbarController = new NavbarController();
        // TODO set funds value
        navCtrl.updatePlayersDropdown(Page.FUNDS, this.pageContext.currentSituation.getPlayerName(),
                Util.buildMap(this.pageContext.selectedGame.situations));
    }
}
