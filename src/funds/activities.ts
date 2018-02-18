import * as storage from '../storage/storage';
import { FundsDao } from '../storage/dao';
import { Activity } from '../framework';
import { Language } from '../rules/rules';
import { FundsPageContext } from './init';
import { CommodityController, NavbarController } from './controllers';
import { FundsCalculator } from './calc';


abstract class AbstractFundsActivity
    implements Activity
{
    protected readonly navbarCtrl: NavbarController = new NavbarController();

    protected readonly commCtrl: CommodityController = new CommodityController();

    protected constructor(protected readonly pageContext: FundsPageContext) { }


    abstract execute(pLanguage: Language): void;


    protected updateTotalFunds(): void {
        const calc: FundsCalculator = new FundsCalculator();
        calc.recalcTotalFunds(this.pageContext.currentSituation.getFunds(), this.pageContext.selectedRules.variant);
        this.navbarCtrl.setTotalFunds(calc.getTotalFunds());
        this.commCtrl.setMiningYield(calc.getMaxMiningYield());
    }


    protected saveSituation(): void {
        storage.saveSituation(this.pageContext.currentSituation.getDaoForStorage());
    }
}



/**
 * A commodity value button was pressed on a commodity of the 'funds' page.
 */
export class SetCommodityValueActivity
    extends AbstractFundsActivity
{
    public constructor(pPageContext: FundsPageContext, private readonly commodityId: string, private readonly n: number) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        let have: boolean = true;
        if (funds.commodities.hasOwnProperty(this.commodityId)) {
            const previous: number = funds.commodities[this.commodityId];
            delete funds.commodities[this.commodityId];
            this.commCtrl.setCommodityValue(this.commodityId, previous, false);
            have = this.n !== previous;
        }
        if (have) {
            funds.commodities[this.commodityId] = this.n;
            this.commCtrl.setCommodityValue(this.commodityId, this.n, true);
        }
        this.updateTotalFunds();
        this.saveSituation();
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
