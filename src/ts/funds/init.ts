import * as Mustache from 'mustache';

import { AbstractPageInitializer, Page, PageContext } from '../framework/framework';
import { Situation } from '../framework/model';
import { Util } from '../framework/util';
import { appOptions, runActivityInternal } from '../main';
import { builtInVariants, CommodityJson, Language, Rules, RulesJson } from '../rules/rules';
import { FundsDao, GameDao, GameDaoImpl, SituationDao } from '../storage/dao';
import * as storage from '../storage/storage';
import { FundsCalculator } from './calc';
import { CommodityController, NavbarController, SummaryController } from './controllers';


/**
 * The page context object of the 'funds' page.
 */
export class FundsPageContext implements PageContext {
    constructor(
        public readonly selectedGame: GameDao,
        public readonly selectedRules: Rules,
        public readonly currentSituation: Situation,
        public readonly fundsCalculator: FundsCalculator = new FundsCalculator()) {
    }
}


/**
 * The page initializer of the 'funds' page.
 */
export class FundsPageInitializer extends AbstractPageInitializer<FundsPageContext>
{
    private readonly commCtrl: CommodityController = new CommodityController();

    constructor() {
        super(Page.FUNDS, FundsPageInitializer.buildPageContext());
    }

    private static buildPageContext(): FundsPageContext {
        const situationKey: string | null = Util.getUrlParameter('ctx');
        const sit: SituationDao | null = storage.readSituation(situationKey);
        let result: FundsPageContext | null = null;
        if (sit !== null) {
            const game: GameDao | null = storage.readGame(sit.gameId);
            if (game !== null) {
                const variant: RulesJson = builtInVariants.get(game.variantKey) as RulesJson;
                const selectedRules: Rules = new Rules(variant, game.options);
                const currentSituation: Situation = new Situation(sit, selectedRules);
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
        Mustache.parse($('#summaryRowTemplate').html());
    }

    protected pageLoaded(): void {
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.addGameIdToLinks(this.pageContext.selectedGame.key);
        navCtrl.addSituationIdToLinks(this.pageContext.currentSituation.getId());
        navCtrl.addJsHandlerToAnchors();
        navCtrl.setGameName(this.pageContext.selectedGame.name);
        document.title = this.pageContext.currentSituation.getPlayerName() + ' - '
            + this.pageContext.selectedGame.name + ' - CivBuddy';
        this.setActivePlayer();
        this.commCtrl.setTreasury(this.pageContext.currentSituation.getFunds().treasury);
        this.initMiningBonus();
        this.updateTotalFunds();
        this.populateCommodityList();
        this.languageChangedInternal(appOptions.language, false);
        this.commCtrl.setupTreasuryHandler(this.handleTreasuryInput.bind(this));
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        this.languageChangedInternal(pNew, true);
    }

    private languageChangedInternal(pNew: Language, pUpdateNames: boolean): void {
        const navbarCtrl: NavbarController = new NavbarController();
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        navbarCtrl.setVariantName((<any> variant.displayNames)[pNew]);
        navbarCtrl.setOptionDesc(
                GameDaoImpl.buildOptionDescriptor(variant, this.pageContext.selectedGame.options, pNew));

        if (pUpdateNames) {
            for (const commodityId of Object.keys(this.pageContext.selectedRules.variant.commodities)) {
                const commodity: CommodityJson =
                        (<any> this.pageContext.selectedRules.variant.commodities)[commodityId];
                this.commCtrl.updateCommodityName(commodityId, commodity.base + ' - ' + (<any> commodity.names)[pNew]);
            }
            const summaryCtrl: SummaryController = new SummaryController();
            summaryCtrl.updateCommodityTranslations(this.pageContext.fundsCalculator.getCommoditySummary(), pNew);
        }
    }


    private setActivePlayer(): void {
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.updatePlayersDropdown(Page.FUNDS, this.pageContext.currentSituation.getPlayerName(),
                Util.buildMap(this.pageContext.selectedGame.situations));
    }


    private updateTotalFunds(): void {
        const calc: FundsCalculator = this.pageContext.fundsCalculator;
        const dao: FundsDao = this.pageContext.currentSituation.getFunds();
        calc.recalcTotalFunds(dao, this.pageContext.selectedRules.variant);
        const anyFundsSpecified: boolean = this.pageContext.currentSituation.hasAnyFunds();
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.setTotalFunds(calc.getTotalFunds());
        navCtrl.setSummaryEnabled(anyFundsSpecified);
        navCtrl.setClearButtonEnabled(anyFundsSpecified);
        this.commCtrl.setMiningYield(calc.getMaxMiningYield());
    }


    private populateCommodityList(): void {
        for (const commodityId of Object.keys(this.pageContext.selectedRules.variant.commodities)) {
            const commodity: CommodityJson = (<any> this.pageContext.selectedRules.variant.commodities)[commodityId];
            const n: number = this.getCommoditiesOwned(commodityId);
            this.commCtrl.putCommodity(commodityId, commodity, n, appOptions.language);
        }
    }

    private getCommoditiesOwned(pCommodityId: string): number {
        let result: number = 0;
        const commodities: object = this.pageContext.currentSituation.getFunds().commodities;
        if (commodities.hasOwnProperty(pCommodityId) && typeof((<any> commodities)[pCommodityId]) === 'number') {
            result = (<any> commodities)[pCommodityId];
            if (result < 0) {
                result = 0;
            }
        }
        return result;
    }

    private handleTreasuryInput(event: any): void {
        const s: string = this.commCtrl.getTreasuryValue();
        let n: number = 0;
        let valid: boolean = false;
        if (s.length > 0) {
            n = Number(s);
            if (!isNaN(n)) {
                n = Math.round(n);
                valid = true;
            }
        }
        runActivityInternal(Page.FUNDS, 'updateTreasury', String(n));
        this.commCtrl.setTreasuryValid(valid);
    }


    private initMiningBonus(): void {
        if (this.pageContext.selectedRules.miningBonusPossible) {
            if (this.pageContext.currentSituation.meetsMiningBonusPrereq()) {
                this.commCtrl.enableMiningBonusCheckbox(true);
            } else {
                this.pageContext.currentSituation.getFunds().wantsToUseMining = false;
                this.commCtrl.enableMiningBonusCheckbox(false);
            }
            this.commCtrl.checkMiningBonusCheckbox(this.pageContext.currentSituation.getFunds().wantsToUseMining);
        }
        this.commCtrl.displayMiningBonusCheckbox(this.pageContext.selectedRules.miningBonusPossible);
    }
}
