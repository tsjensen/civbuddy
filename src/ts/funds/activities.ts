import { Activity } from '../framework/framework';
import { Situation } from '../framework/model';
import { Language } from '../rules/rules';
import { FundsDao } from '../storage/dao';
import * as storage from '../storage/storage';
import { FundsCalculator } from './calc';
import { CommodityController, NavbarController, SummaryController } from './controllers';
import { FundsPageContext } from './init';


abstract class AbstractFundsActivity
    implements Activity
{
    protected readonly navbarCtrl: NavbarController = new NavbarController();

    protected readonly commCtrl: CommodityController = new CommodityController();

    protected constructor(protected readonly pageContext: FundsPageContext) { }


    public abstract execute(pLanguage: Language): void;


    protected updateTotalFunds(): void {
        const sit: Situation = this.pageContext.currentSituation;
        const calc: FundsCalculator = this.pageContext.fundsCalculator;
        calc.recalcTotalFunds(sit.getFunds(), this.pageContext.selectedRules.variant);
        sit.totalFundsAvailable = calc.getTotalFunds();
        sit.currentFunds = calc.getTotalFunds();
        this.navbarCtrl.setTotalFunds(calc.getTotalFunds());
        this.navbarCtrl.setSummaryEnabled(calc.getTotalFunds() > 0 || sit.getFunds().treasury !== 0);
        this.commCtrl.setMiningYield(calc.getMaxMiningYield());
    }


    protected saveSituation(): void {
        if (!this.pageContext.selectedRules.miningBonusPossible) {
            this.pageContext.currentSituation.getFunds().wantsToUseMining = false;
        }
        storage.saveSituation(this.pageContext.currentSituation.getDaoForStorage());
    }
}



/**
 * A commodity value button was pressed on a commodity of the 'funds' page.
 */
export class SetCommodityValueActivity
    extends AbstractFundsActivity
{
    public constructor(pPageContext: FundsPageContext, private readonly commodityId: string, private readonly n: number)
    {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        let have: boolean = true;
        if (funds.commodities.hasOwnProperty(this.commodityId)) {
            const previous: number = (<any> funds.commodities)[this.commodityId];
            delete (<any> funds.commodities)[this.commodityId];
            this.commCtrl.setCommodityValue(this.commodityId, previous, false);
            have = this.n !== previous;
        }
        if (have) {
            (<any> funds.commodities)[this.commodityId] = this.n;
            this.commCtrl.setCommodityValue(this.commodityId, this.n, true);
        }
        this.updateTotalFunds();
        this.saveSituation();
    }
}



/**
 * The 'clear' button was pressed on a commodity card.
 */
export class ClearCommodityValueActivity
    extends AbstractFundsActivity
{
    public constructor(pPageContext: FundsPageContext, private readonly commodityId: string) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        if (funds.commodities.hasOwnProperty(this.commodityId)) {
            const previous: number = (<any> funds.commodities)[this.commodityId];
            delete (<any> funds.commodities)[this.commodityId];
            this.commCtrl.setCommodityValue(this.commodityId, previous, false);
            this.updateTotalFunds();
            this.saveSituation();
        }
    }
}



/**
 * The treasury value on the 'funds' page was updated.
 */
export class UpdateTreasuryActivity
    extends AbstractFundsActivity
{
    public constructor(pPageContext: FundsPageContext, private readonly newTreasuryValue: number) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        if (isNaN(this.newTreasuryValue)) {
            funds.treasury = 0;
        } else {
            funds.treasury = Math.max(Math.min(this.newTreasuryValue, 99), -99);
        }
        this.updateTotalFunds();
        this.saveSituation();
    }
}



/**
 * The checkbox was toggled by which the user declares if the mining bonus shall be taken into account.
 */
export class DeclareMiningBonusActivity
    extends AbstractFundsActivity
{
    public constructor(pPageContext: FundsPageContext, private readonly useIt: boolean) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        funds.wantsToUseMining = this.useIt;
        this.updateTotalFunds();
        this.saveSituation();
    }
}



/**
 * The 'Clear Funds' button was pressed, so we reset all funds to zero.
 */
export class ClearFundsActivity
    extends AbstractFundsActivity
{
    public constructor(pPageContext: FundsPageContext) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        funds.commodities = {};
        funds.treasury = 0;
        funds.wantsToUseMining = this.pageContext.selectedRules.miningBonusPossible;
        this.saveSituation();
        window.setTimeout(function(){ window.location.reload(); }, 300);
    }
}



/**
 * The 'summary/lock' button was pressed to toggle the funds summary.
 */
export class SummaryActivity
    extends AbstractFundsActivity
{
    private readonly summaryCtrl: SummaryController = new SummaryController();

    public constructor(pPageContext: FundsPageContext) {
        super(pPageContext);
    }


    public execute(pLanguage: Language): void {
        const summaryActive: boolean = !this.summaryCtrl.isSummaryVisible();
        if (summaryActive) {
            this.prepareSummaryCard(this.pageContext.currentSituation.getFunds(), pLanguage);
        }
        this.summaryCtrl.toggleSummary(summaryActive);
        new NavbarController().setSummaryIcon(summaryActive);
    }


    private prepareSummaryCard(pFunds: FundsDao, pLanguage: Language): void
    {
        const calc: FundsCalculator = this.pageContext.fundsCalculator;
        const miningSupported: boolean = this.pageContext.selectedRules.miningBonusPossible;

        this.summaryCtrl.setTreasury(pFunds.treasury);

        this.summaryCtrl.setMiningBonusVisible(miningSupported);
        if (miningSupported) {
            if (pFunds.wantsToUseMining) {
                this.summaryCtrl.setMiningBonusValue(calc.getMaxMiningYield());
            } else {
                this.summaryCtrl.setMiningBonusValue(undefined);
            }
        }

        this.summaryCtrl.clearCommodities();
        let totalNumCards: number = 0;
        for (const sc of calc.getCommoditySummary()) {
            this.summaryCtrl.addCommodity(sc.id, (<any> sc.names)[pLanguage], sc.n, sc.value);
            totalNumCards += sc.n;
        }

        this.summaryCtrl.setTotalNumCards(totalNumCards);
        this.summaryCtrl.setTotal(calc.getTotalFunds());
    }
}
