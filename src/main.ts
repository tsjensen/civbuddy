import 'babel-polyfill';
import * as storage from './storage/storage';
import * as appVersionJson from './version.json';
import { AppOptions } from './storage/dao';
import { Language } from './rules/rules';
import { Page, PageContext, AbstractPageInitializer, Activity } from './framework';
import { GamesPageInitializer, GamesPageContext } from './games/init';
import { CreateGameActivity, DeleteGameActivity, ChooseVariantActivity, SelectGameActivity, PurgeActivity } from './games/activities';
import { PlayersPageInitializer, PlayersPageContext } from './players/init';
import { CreatePlayerActivity, DeletePlayerActivity, SelectPlayerActivity } from './players/activities';
import { CardsPageInitializer, CardsPageContext } from './cards/init';
import { ClickOnCardActivity, PlanCardActivity, UnplanCardActivity, ShowCardInfoActivity, BuyCardsActivity,
         ToggleCardsFilterActivity, EnterFundsActivity, DiscardCardActivity } from './cards/activities';
import { FundsPageInitializer } from './funds/init';
import { ActivateLanguageActivity, ChangeLanguageActivity } from './i18n/activities';


let pageContext: PageContext;
export let appOptions: AppOptions = (() => { return storage.readOptions(); })();
export const appVersion: AppVersion = <any>appVersionJson;


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
            runActivityInternal(Page.CROSS, 'changeLanguage', Language[pArguments[0]]);
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
        (<any>pArguments[2]).stopPropagation();
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
    private static readonly CREATORS: Object = ActivityFactory.buildCreatorsMap();

    private static buildCreatorsMap(): Object {
        const result: Object = {};

        /**
         * Cross-cutting activities for all pages
         */
        result[new ActivityKey(Page.CROSS, 'changeLanguage').toString()] =
            function (pc: PageContext, ...pArguments: string[]) {
                return new ChangeLanguageActivity(pArguments[0]);
            };
        result[new ActivityKey(Page.CROSS, 'activateLanguage').toString()] =
            function (pc: PageContext, ...pArguments: string[]) {
                return new ActivateLanguageActivity();
            };

        /**
         * Activities of the 'games' page
         */
        result[new ActivityKey(Page.GAMES, 'create').toString()] =
            function (pc: GamesPageContext, ...pArguments: string[]) {
                return new CreateGameActivity(pc);
            };
        result[new ActivityKey(Page.GAMES, 'delete').toString()] =
            function (pc: GamesPageContext, ...pArguments: string[]) {
                return new DeleteGameActivity(pc, pArguments[0], pArguments[1]);
            };
        result[new ActivityKey(Page.GAMES, 'chooseVariant').toString()] =
            function (pc: GamesPageContext, ...pArguments: string[]) {
                return new ChooseVariantActivity(pc, pArguments[0]);
            };
        result[new ActivityKey(Page.GAMES, 'select').toString()] =
            function (pc: GamesPageContext, ...pArguments: string[]) {
                return new SelectGameActivity(pc, pArguments[0]);
            };
        result[new ActivityKey(Page.GAMES, 'purge').toString()] =
            function (pc: GamesPageContext, ...pArguments: string[]) {
                return new PurgeActivity(pc);
            };

        /**
         * Activities of the 'players' page
         */
        result[new ActivityKey(Page.PLAYERS, 'create').toString()] =
            function (pc: PlayersPageContext, ...pArguments: string[]) {
                return new CreatePlayerActivity(pc);
            };
        result[new ActivityKey(Page.PLAYERS, 'delete').toString()] =
            function (pc: PlayersPageContext, ...pArguments: string[]) {
                return new DeletePlayerActivity(pc, pArguments[0], pArguments[1]);
            };
        result[new ActivityKey(Page.PLAYERS, 'select').toString()] =
            function (pc: PlayersPageContext, ...pArguments: string[]) {
                return new SelectPlayerActivity(pc, pArguments[0]);
            };

        /**
         * Activities of the 'cards' page
         */
        result[new ActivityKey(Page.CARDS, 'click').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new ClickOnCardActivity(pc, pArguments[0]);
            };
        result[new ActivityKey(Page.CARDS, 'plan').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new PlanCardActivity(pc, pArguments[0]);
            };
        result[new ActivityKey(Page.CARDS, 'unplan').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new UnplanCardActivity(pc, pArguments[0]);
            };
        result[new ActivityKey(Page.CARDS, 'info').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new ShowCardInfoActivity(pc, pArguments[0]);
            };
        result[new ActivityKey(Page.CARDS, 'buy').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new BuyCardsActivity(pc);
            };
        result[new ActivityKey(Page.CARDS, 'filter').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new ToggleCardsFilterActivity(pc);
            };
        result[new ActivityKey(Page.CARDS, 'funds').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new EnterFundsActivity(pc);
            };
        result[new ActivityKey(Page.CARDS, 'discard').toString()] =
            function (pc: CardsPageContext, ...pArguments: string[]) {
                return new DiscardCardActivity(pc);
            };
        return result;
    }


    public createActivity(pPageContext: PageContext, pPage: Page, pButtonName: string, ...pArgs: string[]): Activity {
        const actKey: ActivityKey = new ActivityKey(pPage, pButtonName);
        const factoryMethod = ActivityFactory.CREATORS[actKey.toString()];
        if (typeof(factoryMethod) === 'undefined') {
            throw new Error('Unknown activity: ' + actKey.toString());
        }
        let result = factoryMethod(pPageContext, ...pArgs);
        return result;
    }
}
