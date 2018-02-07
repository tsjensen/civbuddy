import { FundsDao } from '../storage/dao';
import { RulesJson, CommodityJson } from '../rules/rules';


export function initFundsPage(): void {
    // TODO
}


export class FundsCalculator
{
    private totalFunds: number = 0;

    private maxMiningYield: number = 0;


    /**
     * Recalculate the total funds available to a player.
     * @param pFunds the funds components available to the player
     * @param pVariant the rules, including commodity descriptors
     * @returns the maximum funds value that can be achieved with available funds components
     */
    public recalcTotalFunds(pFunds: FundsDao, pVariant: RulesJson): number
    {
        let sum: number = 0;
        sum += pFunds.treasury;
        sum += pFunds.bonus;

        let wine: number = 0;
        let wineCount: number = 0;
        const miningYields: number[] = [];

        for (let commodityId of Object.keys(pFunds.commodities)) {
            const n: number = Math.max(pFunds.commodities[commodityId], 0);
            const commodityDesc: CommodityJson = pVariant.commodities[commodityId];
            if (commodityDesc.wine) {
                wine += n * commodityDesc.base;
                wineCount += n;
            }
            else {
                sum += n * n * commodityDesc.base;
            }
            if (pFunds.wantsToUseMining && n > 0 && commodityDesc.mineable && n < commodityDesc.maxCount) {
                const current: number = n * n * commodityDesc.base;
                const miningYield: number = ((n + 1) * (n + 1) * commodityDesc.base) - current;
                miningYields.push(miningYield);
            }
        }
        sum += wine * wineCount;

        if (miningYields.length > 0) {
            let maxMiningYield: number = Math.max(...miningYields);
            this.maxMiningYield = maxMiningYield;
            sum += maxMiningYield;
        }

        if (sum < 0) {
            sum = 0;
        }
        this.totalFunds = sum;
        return sum;
    }


    /**
     * Getter.
     * @returns the highest mining yield as calculated by the latest run of this calculator
     */
    public getMaxMiningYield(): number {
        return this.maxMiningYield;
    }

    /**
     * Getter.
     * @returns the maximum funds value as calculated by the latest run of this calculator
     */
    public getTotalFunds(): number {
        return this.totalFunds;
    }
}
