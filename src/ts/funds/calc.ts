/*
 * CivBuddy - A calculator app for players of Francis Tresham's original Civilization board game (1980)
 * Copyright (C) 2012-2023 Thomas Jensen
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

import { CommodityJson, RulesJson } from '../rules/rules';
import { FundsDao } from '../storage/dao';


export class FundsCalculator {

    private totalFunds: number = 0;

    private maxMiningYield: number = 0;

    private commoditySummary: SummarizedCommodity[] = [];


    /**
     * Recalculate the total funds available to a player.
     * @param pFunds the funds components available to the player
     * @param pVariant the rules, including commodity descriptors
     */
    public recalcTotalFunds(pFunds: FundsDao, pVariant: RulesJson): void {
        let sum: number = pFunds.treasury;

        let wine: number = 0;
        let wineCount: number = 0;
        const miningYields: number[] = [];
        this.commoditySummary = [];

        for (const commodityId of Object.keys(pFunds.commodities)) {
            const commodityDesc: CommodityJson = (pVariant.commodities as any)[commodityId];
            const n: number = Math.min(Math.max((pFunds.commodities as any)[commodityId], 0), commodityDesc.maxCount);
            if (commodityDesc.wine) {
                wine += n * commodityDesc.base;
                wineCount += n;
            }
            else {
                const v: number = n * n * commodityDesc.base;
                sum += v;
                this.commoditySummary.push(new SummarizedCommodity(commodityId, commodityDesc.names, n, v));
            }
            if (n > 0 && commodityDesc.mineable && n < commodityDesc.maxCount) {
                const current: number = n * n * commodityDesc.base;
                const miningYield: number = ((n + 1) * (n + 1) * commodityDesc.base) - current;
                miningYields.push(miningYield);
            }
        }

        const wineValue: number = wine * wineCount;
        sum += wineValue;
        if (wineValue > 0) {
            const wineCommodityId: string = this.getFirstWineCommodityId(pVariant);
            const wineNames: object = (pVariant.commodities as any)[wineCommodityId].names;
            this.commoditySummary.push(new SummarizedCommodity(wineCommodityId, wineNames, wineCount, wineValue));
        }

        if (miningYields.length > 0) {
            const maxMiningYield: number = Math.max(...miningYields);
            this.maxMiningYield = maxMiningYield;
            if (pFunds.wantsToUseMining) {
                sum += maxMiningYield;
            }
        }

        if (sum < 0) {
            sum = 0;
        }
        this.totalFunds = sum;
    }


    private getFirstWineCommodityId(pVariant: RulesJson): string {
        for (const commodityId of Object.keys(pVariant.commodities)) {
            if ((pVariant.commodities as any)[commodityId].wine) {
                return commodityId;
            }
        }
        throw new Error('no wine commodity found, but wine value > 0');
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


    public getCommoditySummary(): SummarizedCommodity[] {
        return this.commoditySummary;
    }
}



/**
 * Holds the data for one row of the funds summary. Created by the funds calculator.
 */
export class SummarizedCommodity {
    public constructor(public readonly id: string, public readonly names: object, public readonly n: number,
        public readonly value: number) { }
}
