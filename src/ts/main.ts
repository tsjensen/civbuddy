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

import 'babel-polyfill';

import {
    BuyCardsActivity,
    ClickOnCardActivity,
    DiscardCardActivity,
    PlanCardActivity,
    ShowCardInfoActivity,
    ToggleCardsFilterActivity,
    UnplanCardActivity
} from './cards/activities';
import { CardsPageContext, CardsPageInitializer } from './cards/init';
import { ErrorPageInitializer } from './error/init';
import { AbstractPageContext, AbstractPageInitializer, Activity, Page } from './framework/framework';
import {
    ClearCommodityValueActivity,
    ClearFundsActivity,
    DeclareMiningBonusActivity,
    SetCommodityValueActivity,
    SummaryActivity,
    UpdateTreasuryActivity
} from './funds/activities';
import { FundsPageContext, FundsPageInitializer } from './funds/init';
import {
    ChooseVariantActivity,
    CreateGameActivity,
    DeleteGameActivity,
    PurgeActivity,
    SelectGameActivity
} from './games/activities';
import { GamesPageContext, GamesPageInitializer } from './games/init';
import { ActivateLanguageActivity, ChangeLanguageActivity } from './i18n/activities';
import { CreatePlayerActivity, DeletePlayerActivity, SelectPlayerActivity } from './players/activities';
import { PlayersPageContext, PlayersPageInitializer } from './players/init';
import { Language } from './rules/rules';
import { GlobalOptions } from './storage/storage';


let pageContext: AbstractPageContext;


/**
 * Function that is called by every page when the DOM is ready.
 * @param pPage which page we're on
 */
export function initPage(pPage: Page): void {
    let initializer: AbstractPageInitializer<AbstractPageContext> | undefined = undefined;
    switch (pPage) {
        case Page.GAMES: initializer = new GamesPageInitializer(); break;
        case Page.PLAYERS: initializer = new PlayersPageInitializer(); break;
        case Page.CARDS: initializer = new CardsPageInitializer(); break;
        case Page.FUNDS: initializer = new FundsPageInitializer(); break;
        case Page.ERROR: initializer = new ErrorPageInitializer(); break;
        default:
            console.log('unknown page: ' + pPage + ' - skipping page initialization'); break;
    }
    if (typeof (initializer) !== 'undefined') {
        pageContext = initializer.getInitialPageContext();
        initializer.init();
    }
}


export function buttonClick(pElement: HTMLElement, pPage: Page, pButtonName: string, ...pArguments: string[]): void {
    if (!pElement.classList.contains('disabled')) {
        if (pButtonName === 'switchLanguage') {
            runActivityInternal(Page.CROSS, 'changeLanguage', Language[pArguments[0] as keyof typeof Language]);
        } else if (pButtonName === 'reload') {
            window.location.reload(true);  // tslint:disable-line:deprecation
        } else {
            runActivityInternal(pPage, pButtonName, ...pArguments);
        }
    }
}

export function runActivityInternal(pPage: Page, pButtonName: string, ...pArguments: string[]): void {
    const activity: Activity = new ActivityFactory().createActivity(pageContext, pPage, pButtonName, ...pArguments);
    const currentLanguage: Language = new GlobalOptions().get().language;
    activity.execute(currentLanguage);
    if ((pPage === Page.GAMES && pButtonName === 'delete')
        || (pPage === Page.PLAYERS && pButtonName === 'delete')) {
        (pArguments[2] as any).stopPropagation();
    }
}



/**
 * Identifies one activity for use by the ActivityFactory.
 */
class ActivityKey {
    constructor(public readonly pPage: Page, public readonly pActivityName: string) { }
    public toString(): string {
        return this.pPage.toString() + '_' + this.pActivityName;
    }
}



/**
 * Creates activities.
 */
class ActivityFactory {
    private static readonly CREATORS: object = ActivityFactory.buildCreatorsMap();

    private static buildCreatorsMap(): object {
        const result: object = {};

        /**
         * Cross-cutting activities for all pages
         */
        (result as any)[new ActivityKey(Page.CROSS, 'changeLanguage').toString()] =
            (pc: AbstractPageContext, ...pArguments: string[]) => new ChangeLanguageActivity(pArguments[0]);
        (result as any)[new ActivityKey(Page.CROSS, 'activateLanguage').toString()] =
            (pc: AbstractPageContext, ...pArguments: string[]) => new ActivateLanguageActivity();

        /**
         * Activities of the 'games' page
         */
        (result as any)[new ActivityKey(Page.GAMES, 'create').toString()] =
            (pc: GamesPageContext, ...pArguments: string[]) => new CreateGameActivity(pc);
        (result as any)[new ActivityKey(Page.GAMES, 'delete').toString()] =
            (pc: GamesPageContext, ...pArguments: string[]) => new DeleteGameActivity(pc, pArguments[0], pArguments[1]);
        (result as any)[new ActivityKey(Page.GAMES, 'chooseVariant').toString()] =
            (pc: GamesPageContext, ...pArguments: string[]) => new ChooseVariantActivity(pc, pArguments[0]);
        (result as any)[new ActivityKey(Page.GAMES, 'select').toString()] =
            (pc: GamesPageContext, ...pArguments: string[]) => new SelectGameActivity(pc, pArguments[0]);
        (result as any)[new ActivityKey(Page.GAMES, 'purge').toString()] =
            (pc: GamesPageContext, ...pArguments: string[]) => new PurgeActivity(pc);

        /**
         * Activities of the 'players' page
         */
        (result as any)[new ActivityKey(Page.PLAYERS, 'create').toString()] =
            (pc: PlayersPageContext, ...pArguments: string[]) => new CreatePlayerActivity(pc);
        (result as any)[new ActivityKey(Page.PLAYERS, 'delete').toString()] =
            (pc: PlayersPageContext, ...pArguments: string[]) =>
                new DeletePlayerActivity(pc, pArguments[0], pArguments[1]);
        (result as any)[new ActivityKey(Page.PLAYERS, 'select').toString()] =
            (pc: PlayersPageContext, ...pArguments: string[]) => new SelectPlayerActivity(pc, pArguments[0]);

        /**
         * Activities of the 'cards' page
         */
        (result as any)[new ActivityKey(Page.CARDS, 'click').toString()] =
            (pc: CardsPageContext, ...pArguments: string[]) => new ClickOnCardActivity(pc, pArguments[0]);
        (result as any)[new ActivityKey(Page.CARDS, 'plan').toString()] =
            (pc: CardsPageContext, ...pArguments: string[]) => new PlanCardActivity(pc, pArguments[0]);
        (result as any)[new ActivityKey(Page.CARDS, 'unplan').toString()] =
            (pc: CardsPageContext, ...pArguments: string[]) => new UnplanCardActivity(pc, pArguments[0]);
        (result as any)[new ActivityKey(Page.CARDS, 'info').toString()] =
            (pc: CardsPageContext, ...pArguments: string[]) => new ShowCardInfoActivity(pc, pArguments[0]);
        (result as any)[new ActivityKey(Page.CARDS, 'buy').toString()] =
            (pc: CardsPageContext, ...pArguments: string[]) => new BuyCardsActivity(pc);
        (result as any)[new ActivityKey(Page.CARDS, 'filter').toString()] =
            (pc: CardsPageContext, ...pArguments: string[]) => new ToggleCardsFilterActivity(pc);
        (result as any)[new ActivityKey(Page.CARDS, 'discard').toString()] =
            (pc: CardsPageContext, ...pArguments: string[]) => new DiscardCardActivity(pc);

        /**
         * Activities of the 'funds' page
         */
        (result as any)[new ActivityKey(Page.FUNDS, 'setCommodity').toString()] =
            (pc: FundsPageContext, ...pArguments: string[]) =>
                new SetCommodityValueActivity(pc, pArguments[0], Number(pArguments[1]));
        (result as any)[new ActivityKey(Page.FUNDS, 'clearCommodity').toString()] =
            (pc: FundsPageContext, ...pArguments: string[]) => new ClearCommodityValueActivity(pc, pArguments[0]);
        (result as any)[new ActivityKey(Page.FUNDS, 'updateTreasury').toString()] =
            (pc: FundsPageContext, ...pArguments: string[]) => new UpdateTreasuryActivity(pc, Number(pArguments[0]));
        (result as any)[new ActivityKey(Page.FUNDS, 'toggleMiningBonus').toString()] =
            (pc: FundsPageContext, ...pArguments: string[]) =>
                new DeclareMiningBonusActivity(pc, Boolean(pArguments[0]));
        (result as any)[new ActivityKey(Page.FUNDS, 'clear').toString()] =
            (pc: FundsPageContext, ...pArguments: string[]) => new ClearFundsActivity(pc);
        (result as any)[new ActivityKey(Page.FUNDS, 'toggleSummary').toString()] =
            (pc: FundsPageContext, ...pArguments: string[]) => new SummaryActivity(pc);
        return result;
    }


    public createActivity(pPageContext: AbstractPageContext, pPage: Page, pButtonName: string, ...pArgs: string[]):
        Activity {
        const actKey: ActivityKey = new ActivityKey(pPage, pButtonName);
        const factoryMethod = (ActivityFactory.CREATORS as any)[actKey.toString()];
        if (typeof (factoryMethod) === 'undefined') {
            throw new Error('Unknown activity: ' + actKey.toString());
        }
        const result = factoryMethod(pPageContext, ...pArgs);
        return result;
    }
}
