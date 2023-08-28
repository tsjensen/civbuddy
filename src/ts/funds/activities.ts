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

import { Activity } from '../framework/framework';
import { Situation } from '../framework/model';
import { CommodityJson, Language } from '../rules/rules';
import { FundsDao } from '../storage/dao';
import { SituationStorage } from '../storage/storage';
import { FundsCalculator } from './calc';
import { CommodityController, NavbarController, SummaryController } from './controllers';
import { FundsPageContext } from './init';


abstract class AbstractFundsActivity
    implements Activity {

    protected readonly navbarCtrl: NavbarController = new NavbarController();

    protected readonly commCtrl: CommodityController = new CommodityController();

    protected constructor(protected readonly pageContext: FundsPageContext) { }


    public abstract execute(pLanguage: Language): void;


    protected updateTotalFunds(): void {
        const sit: Situation = this.pageContext.currentSituation;
        const calc: FundsCalculator = this.pageContext.fundsCalculator;
        calc.recalcTotalFunds(sit.getFunds(), this.pageContext.selectedRules.variant);
        const anyFundsSpecified: boolean = sit.hasAnyFunds();
        sit.totalFundsAvailable = calc.getTotalFunds();
        sit.currentFunds = calc.getTotalFunds();
        this.navbarCtrl.setTotalFunds(calc.getTotalFunds());
        this.navbarCtrl.setSummaryEnabled(anyFundsSpecified);
        this.navbarCtrl.setClearButtonEnabled(anyFundsSpecified);
        this.commCtrl.setMiningYield(calc.getMaxMiningYield());
    }


    protected saveSituation(): void {
        if (!this.pageContext.selectedRules.miningBonusPossible) {
            this.pageContext.currentSituation.getFunds().wantsToUseMining = false;
        }
        new SituationStorage().saveSituation(this.pageContext.currentSituation.getDaoForStorage());
    }
}



/**
 * A commodity value button was pressed on a commodity of the 'funds' page.
 */
export class SetCommodityValueActivity
    extends AbstractFundsActivity {

    public constructor(pPageCtx: FundsPageContext, private readonly commodityId: string, private readonly n: number) {
        super(pPageCtx);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        const commodity: CommodityJson = (this.pageContext.selectedRules.variant.commodities as any)[this.commodityId];
        let have: boolean = true;
        if (funds.commodities.hasOwnProperty(this.commodityId)) {
            const previous: number = (funds.commodities as any)[this.commodityId];
            delete (funds.commodities as any)[this.commodityId];
            this.commCtrl.setCommodityValue(this.commodityId, commodity, previous, false);
            have = this.n !== previous;
        }
        if (have) {
            (funds.commodities as any)[this.commodityId] = this.n;
            this.commCtrl.setCommodityValue(this.commodityId, commodity, this.n, true);
        }
        this.updateTotalFunds();
        this.saveSituation();
    }
}



/**
 * The 'clear' button was pressed on a commodity card.
 */
export class ClearCommodityValueActivity
    extends AbstractFundsActivity {

    public constructor(pPageContext: FundsPageContext, private readonly commodityId: string) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        if (funds.commodities.hasOwnProperty(this.commodityId)) {
            const commodity: CommodityJson =
                (this.pageContext.selectedRules.variant.commodities as any)[this.commodityId];
            const previous: number = (funds.commodities as any)[this.commodityId];
            delete (funds.commodities as any)[this.commodityId];
            this.commCtrl.setCommodityValue(this.commodityId, commodity, previous, false);
            this.updateTotalFunds();
            this.saveSituation();
        }
    }
}



/**
 * The treasury value on the 'funds' page was updated.
 */
export class UpdateTreasuryActivity
    extends AbstractFundsActivity {

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
    extends AbstractFundsActivity {

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
    extends AbstractFundsActivity {

    public constructor(pPageContext: FundsPageContext) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        const funds: FundsDao = this.pageContext.currentSituation.getFunds();
        funds.commodities = {};
        funds.treasury = 0;
        funds.wantsToUseMining = this.pageContext.selectedRules.miningBonusPossible;
        this.saveSituation();
        window.setTimeout(() => { window.location.reload(); }, 300);
    }
}



/**
 * The 'summary/lock' button was pressed to toggle the funds summary.
 */
export class SummaryActivity
    extends AbstractFundsActivity {

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
        window.setTimeout(() => window.dispatchEvent(new CustomEvent<object>('cardListChanged')), 100);
    }


    private prepareSummaryCard(pFunds: FundsDao, pLanguage: Language): void {
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
            this.summaryCtrl.addCommodity(sc.id, (sc.names as any)[pLanguage], sc.n, sc.value);
            totalNumCards += sc.n;
        }

        this.summaryCtrl.setTotalNumCards(totalNumCards);
        this.summaryCtrl.setTotal(calc.getTotalFunds());
    }
}
