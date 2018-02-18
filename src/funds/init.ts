import * as Mustache from 'mustache';
import * as storage from '../storage/storage';
import { PageContext, AbstractPageInitializer, Page } from '../framework';
import { Language, Rules, RulesJson, builtInVariants, CommodityJson } from '../rules/rules';
import { GameDao, SituationDao, GameDaoImpl } from '../storage/dao';
import { Situation } from '../model';
import { Util } from '../util';
import { NavbarController, CommodityController } from './controllers';
import { appOptions } from '../main';
import { FundsCalculator } from './calc';


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
        Mustache.parse($('#commodityTemplate').html());
        Mustache.parse($('#commodityButtonTemplate').html());
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
        this.updateTotalFunds();
        this.populateCommodityList();
        this.languageChangedInternal(appOptions.language, false);
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        this.languageChangedInternal(pNew, true);
    }

    private languageChangedInternal(pNew: Language, pUpdateNames: boolean): void {
        const navbarCtrl: NavbarController = new NavbarController();
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        navbarCtrl.setVariantName(variant.displayNames[pNew]);
        navbarCtrl.setOptionDesc(
                GameDaoImpl.buildOptionDescriptor(variant, this.pageContext.selectedGame.options, pNew));

        if (pUpdateNames) {
            const commCtrl: CommodityController = new CommodityController();
            for (let commodityId of Object.keys(this.pageContext.selectedRules.variant.commodities)) {
                const commodity: CommodityJson = this.pageContext.selectedRules.variant.commodities[commodityId];
                commCtrl.updateCommodityName(commodityId, commodity.names[pNew]);
            }
        }
    }


    private setActivePlayer(): void {
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.updatePlayersDropdown(Page.FUNDS, this.pageContext.currentSituation.getPlayerName(),
                Util.buildMap(this.pageContext.selectedGame.situations));
    }


    private updateTotalFunds(): void {
        const calc: FundsCalculator = new FundsCalculator();
        calc.recalcTotalFunds(this.pageContext.currentSituation.getFunds(), this.pageContext.selectedRules.variant);
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.setTotalFunds(calc.getTotalFunds());
        const commCtrl: CommodityController = new CommodityController();
        commCtrl.setMiningYield(calc.getMaxMiningYield());
    }


    private populateCommodityList(): void {
        const commCtrl: CommodityController = new CommodityController();
        for (let commodityId of Object.keys(this.pageContext.selectedRules.variant.commodities)) {
            const commodity: CommodityJson = this.pageContext.selectedRules.variant.commodities[commodityId];
            const n: number = this.getCommoditiesOwned(commodityId);
            commCtrl.putCommodity(commodityId, commodity, n, appOptions.language);
        }
    }

    private getCommoditiesOwned(pCommodityId: string): number {
        let result: number = 0;
        const commodities: Object = this.pageContext.currentSituation.getFunds().commodities;
        if (commodities.hasOwnProperty(pCommodityId) && typeof(commodities[pCommodityId]) === 'number') {
            result = commodities[pCommodityId];
            if (result < 0) {
                result = 0;
            }
        }
        return result;
    }
}
