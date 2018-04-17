import 'babel-polyfill';

import * as appVersionJson from '../../build/version.json';
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
import { AbstractPageInitializer, Activity, Page, PageContext } from './framework/framework';
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
import { AppOptions } from './storage/dao';
import * as storage from './storage/storage';


let pageContext: PageContext;
export let appOptions: AppOptions = (() => storage.readOptions())();
export const appVersion: AppVersion = appVersionJson as any;


/**
 * Describes the contents of the version.json file.
 */
export interface AppVersion
{
    /** application name as specified in package.json */
    readonly name: string;

    /** build timestamp in milliseconds since the epoch */
    readonly buildDate: number;

    /** application version as specified in package.json */
    readonly version: string;

    /** number of commits in the Git repo */
    readonly numCommits: number;

    /** latest Git commit hash */
    readonly hash: string;

    /** flag is set when uncommitted or untracked changes are present in the workspace */
    readonly dirty: boolean;
}



/**
 * Function that is called by every page when the DOM is ready.
 * @param pPage which page we're on
 */
export function initPage(pPage: Page): void
{
    let initializer: AbstractPageInitializer<PageContext> | undefined = undefined;
    switch (pPage) {
        case Page.GAMES: initializer = new GamesPageInitializer(); break;
        case Page.PLAYERS: initializer = new PlayersPageInitializer(); break;
        case Page.CARDS: initializer = new CardsPageInitializer(); break;
        case Page.FUNDS: initializer = new FundsPageInitializer(); break;
        default:
            console.log('unknown page: ' + pPage + ' - skipping page initialization');
    }
    if (typeof(initializer) !== 'undefined') {
        pageContext = (initializer as AbstractPageInitializer<PageContext>).getInitialPageContext();
        (initializer as AbstractPageInitializer<PageContext>).init();
    }
}


export function buttonClick(pElement: HTMLElement, pPage: Page, pButtonName: string, ...pArguments: string[]): void {
    if (!pElement.classList.contains('disabled')) {
        if (pButtonName === 'switchLanguage') {
            runActivityInternal(Page.CROSS, 'changeLanguage', Language[pArguments[0] as keyof typeof Language]);
        } else if (pButtonName === 'reload') {
            window.location.reload(true);
        } else {
            runActivityInternal(pPage, pButtonName, ...pArguments);
        }
    }
}

export function runActivityInternal(pPage: Page, pButtonName: string, ...pArguments: string[]): void {
    const activity: Activity = new ActivityFactory().createActivity(pageContext, pPage, pButtonName, ...pArguments);
    activity.execute(appOptions.language);
    if ((pPage === Page.GAMES && pButtonName === 'delete')
        || (pPage === Page.PLAYERS && pButtonName === 'delete')) {
        (<any> pArguments[2]).stopPropagation();
    }
}



/**
 * Identifies one activity for use by the ActivityFactory.
 */
class ActivityKey {
    constructor(public readonly pPage: Page, public readonly pActivityName: string) {}
    public toString(): string {
        return this.pPage.toString() + '_' + this.pActivityName;
    }
}



/**
 * Creates activities.
 */
class ActivityFactory
{
    private static readonly CREATORS: object = ActivityFactory.buildCreatorsMap();

    private static buildCreatorsMap(): object {
        const result: object = {};

        /**
         * Cross-cutting activities for all pages
         */
        (<any> result)[new ActivityKey(Page.CROSS, 'changeLanguage').toString()] =
            function(pc: PageContext, ...pArguments: string[]) {
                return new ChangeLanguageActivity(pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.CROSS, 'activateLanguage').toString()] =
            function(pc: PageContext, ...pArguments: string[]) {
                return new ActivateLanguageActivity();
            };

        /**
         * Activities of the 'games' page
         */
        (<any> result)[new ActivityKey(Page.GAMES, 'create').toString()] =
            function(pc: GamesPageContext, ...pArguments: string[]) {
                return new CreateGameActivity(pc);
            };
        (<any> result)[new ActivityKey(Page.GAMES, 'delete').toString()] =
            function(pc: GamesPageContext, ...pArguments: string[]) {
                return new DeleteGameActivity(pc, pArguments[0], pArguments[1]);
            };
        (<any> result)[new ActivityKey(Page.GAMES, 'chooseVariant').toString()] =
            function(pc: GamesPageContext, ...pArguments: string[]) {
                return new ChooseVariantActivity(pc, pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.GAMES, 'select').toString()] =
            function(pc: GamesPageContext, ...pArguments: string[]) {
                return new SelectGameActivity(pc, pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.GAMES, 'purge').toString()] =
            function(pc: GamesPageContext, ...pArguments: string[]) {
                return new PurgeActivity(pc);
            };

        /**
         * Activities of the 'players' page
         */
        (<any> result)[new ActivityKey(Page.PLAYERS, 'create').toString()] =
            function(pc: PlayersPageContext, ...pArguments: string[]) {
                return new CreatePlayerActivity(pc);
            };
        (<any> result)[new ActivityKey(Page.PLAYERS, 'delete').toString()] =
            function(pc: PlayersPageContext, ...pArguments: string[]) {
                return new DeletePlayerActivity(pc, pArguments[0], pArguments[1]);
            };
        (<any> result)[new ActivityKey(Page.PLAYERS, 'select').toString()] =
            function(pc: PlayersPageContext, ...pArguments: string[]) {
                return new SelectPlayerActivity(pc, pArguments[0]);
            };

        /**
         * Activities of the 'cards' page
         */
        (<any> result)[new ActivityKey(Page.CARDS, 'click').toString()] =
            function(pc: CardsPageContext, ...pArguments: string[]) {
                return new ClickOnCardActivity(pc, pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.CARDS, 'plan').toString()] =
            function(pc: CardsPageContext, ...pArguments: string[]) {
                return new PlanCardActivity(pc, pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.CARDS, 'unplan').toString()] =
            function(pc: CardsPageContext, ...pArguments: string[]) {
                return new UnplanCardActivity(pc, pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.CARDS, 'info').toString()] =
            function(pc: CardsPageContext, ...pArguments: string[]) {
                return new ShowCardInfoActivity(pc, pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.CARDS, 'buy').toString()] =
            function(pc: CardsPageContext, ...pArguments: string[]) {
                return new BuyCardsActivity(pc);
            };
        (<any> result)[new ActivityKey(Page.CARDS, 'filter').toString()] =
            function(pc: CardsPageContext, ...pArguments: string[]) {
                return new ToggleCardsFilterActivity(pc);
            };
        (<any> result)[new ActivityKey(Page.CARDS, 'discard').toString()] =
            function(pc: CardsPageContext, ...pArguments: string[]) {
                return new DiscardCardActivity(pc);
            };

        /**
         * Activities of the 'funds' page
         */
        (<any> result)[new ActivityKey(Page.FUNDS, 'setCommodity').toString()] =
            function(pc: FundsPageContext, ...pArguments: string[]) {
                return new SetCommodityValueActivity(pc, pArguments[0], Number(pArguments[1]));
            };
        (<any> result)[new ActivityKey(Page.FUNDS, 'clearCommodity').toString()] =
            function(pc: FundsPageContext, ...pArguments: string[]) {
                return new ClearCommodityValueActivity(pc, pArguments[0]);
            };
        (<any> result)[new ActivityKey(Page.FUNDS, 'updateTreasury').toString()] =
            function(pc: FundsPageContext, ...pArguments: string[]) {
                return new UpdateTreasuryActivity(pc, Number(pArguments[0]));
            };
        (<any> result)[new ActivityKey(Page.FUNDS, 'toggleMiningBonus').toString()] =
            function(pc: FundsPageContext, ...pArguments: string[]) {
                return new DeclareMiningBonusActivity(pc, Boolean(pArguments[0]));
            };
        (<any> result)[new ActivityKey(Page.FUNDS, 'clear').toString()] =
            function(pc: FundsPageContext, ...pArguments: string[]) {
                return new ClearFundsActivity(pc);
            };
        (<any> result)[new ActivityKey(Page.FUNDS, 'toggleSummary').toString()] =
            function(pc: FundsPageContext, ...pArguments: string[]) {
                return new SummaryActivity(pc);
            };
        return result;
    }


    public createActivity(pPageContext: PageContext, pPage: Page, pButtonName: string, ...pArgs: string[]): Activity {
        const actKey: ActivityKey = new ActivityKey(pPage, pButtonName);
        const factoryMethod = (<any> ActivityFactory.CREATORS)[actKey.toString()];
        if (typeof(factoryMethod) === 'undefined') {
            throw new Error('Unknown activity: ' + actKey.toString());
        }
        const result = factoryMethod(pPageContext, ...pArgs);
        return result;
    }
}
