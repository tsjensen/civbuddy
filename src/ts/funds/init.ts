import * as Mustache from 'mustache';

import { AbstractPageInitializer, Page, PageContext } from '../framework';
import { appOptions, runActivityInternal } from '../main';
import { Situation } from '../model';
import { builtInVariants, CommodityJson, Language, Rules, RulesJson } from '../rules/rules';
import { GameDao, GameDaoImpl, SituationDao } from '../storage/dao';
import * as storage from '../storage/storage';
import { Util } from '../util';
import { FundsCalculator } from './calc';
import { CommodityController, NavbarController } from './controllers';


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
        Mustache.parse($('#summaryRowTemplate').html());
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
        navbarCtrl.setVariantName(variant.displayNames[pNew]);
        navbarCtrl.setOptionDesc(
                GameDaoImpl.buildOptionDescriptor(variant, this.pageContext.selectedGame.options, pNew));

        if (pUpdateNames) {
            for (let commodityId of Object.keys(this.pageContext.selectedRules.variant.commodities)) {
                const commodity: CommodityJson = this.pageContext.selectedRules.variant.commodities[commodityId];
                this.commCtrl.updateCommodityName(commodityId, commodity.base + ' - ' + commodity.names[pNew]);
            }
            // TODO Update commodity names on the summary card
        }
    }


    private setActivePlayer(): void {
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.updatePlayersDropdown(Page.FUNDS, this.pageContext.currentSituation.getPlayerName(),
                Util.buildMap(this.pageContext.selectedGame.situations));
    }


    private updateTotalFunds(): void {
        const calc: FundsCalculator = this.pageContext.fundsCalculator;
        calc.recalcTotalFunds(this.pageContext.currentSituation.getFunds(), this.pageContext.selectedRules.variant);
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.setTotalFunds(calc.getTotalFunds());
        navCtrl.setSummaryEnabled(calc.getTotalFunds() > 0);
        this.commCtrl.setMiningYield(calc.getMaxMiningYield());
    }


    private populateCommodityList(): void {
        for (let commodityId of Object.keys(this.pageContext.selectedRules.variant.commodities)) {
            const commodity: CommodityJson = this.pageContext.selectedRules.variant.commodities[commodityId];
            const n: number = this.getCommoditiesOwned(commodityId);
            this.commCtrl.putCommodity(commodityId, commodity, n, appOptions.language);
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

    private handleTreasuryInput(event): void {
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
