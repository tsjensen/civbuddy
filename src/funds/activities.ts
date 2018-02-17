import * as storage from '../storage/storage';
import { Activity } from '../framework';
import { Language } from '../rules/rules';
import { FundsPageContext } from './init';
import { CommodityController } from './controllers';
import { FundsDao } from '../storage/dao';


abstract class AbstractFundsActivity
    implements Activity
{
    constructor(protected readonly pageContext: FundsPageContext) { }

    abstract execute(pLanguage: Language): void;

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
    private readonly commCtrl: CommodityController = new CommodityController();

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
        // TODO update total funds
        this.saveSituation();
    }
}