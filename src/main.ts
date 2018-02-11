import 'babel-polyfill';
import * as Mustache from 'mustache';
import * as storage from './storage/storage';
import { AppOptions } from './storage/dao';
import { Language } from './rules/rules';
import { Page, PageContext, AbstractPageInitializer, Activity } from './framework';
import { GamesPageInitializer, GamesPageContext } from './games/init';
import { CreateGameActivity, DeleteGameActivity, ChooseVariantActivity, SelectGameActivity, PurgeActivity } from './games/activities';
import { PlayersPageInitializer, PlayersPageContext } from './players/init';
import { CreatePlayerActivity, DeletePlayerActivity, SelectPlayerActivity } from './players/activities';
import { CardsPageInitializer, CardsPageContext } from './cards/init';
import { ClickOnCardActivity, PlanCardActivity, UnplanCardActivity, ShowCardInfoActivity, BuyCardsActivity, ToggleCardsFilterActivity, EnterFundsActivity, DiscardCardActivity } from './cards/activities';
import { FundsPageInitializer } from './funds/init';


let pageContext: PageContext;


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
            changeLanguage(Language[pArguments[0]]);  // TODO handle this
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


class ActivityKey {
    constructor(public readonly pPage: Page, public readonly pActivityName: string) {}
    public toString(): string {
        return this.pPage.toString() + '_' + this.pActivityName;
    }
}



class ActivityFactory
{
    private static readonly CREATORS: Object = ActivityFactory.buildCreatorsMap();

    private static buildCreatorsMap(): Object {
        const result: Object = {};

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


    public createActivity(pPageContext: PageContext, pPage: Page, pButtonName: string, ...pArguments: string[]): Activity {
        const actKey: ActivityKey = new ActivityKey(pPage, pButtonName);
        const factoryMethod = ActivityFactory.CREATORS[actKey.toString()];
        if (typeof(factoryMethod) === undefined) {
            throw new Error('Unknown activity: ' + actKey.toString());
        }
        let result = factoryMethod(pPageContext, ...pArguments);
        return result;
    }
}



// TODO move to a new i18n package and convert changeLanguage() into an activity etc.
export let appOptions: AppOptions = (() => { return storage.readOptions(); })();

function changeLanguage(pNewLanguage: Language): void {
    appOptions.language = pNewLanguage;
    storage.writeOptions(appOptions);
    activateLanguage(pNewLanguage);
    window.dispatchEvent(new CustomEvent('applanguagechanged'));
}

export function activateLanguage(pNewLanguage: Language): void {
    showLanguage();
    if (document.hasOwnProperty('l10n')) {
        document['l10n'].requestLanguages([pNewLanguage]);   // It's ok to list only the requested language.
    }
}

function showLanguage(): void {
    const selectedLanguage: Language = appOptions.language;
    let htmlTemplate: string = $('#flagTemplate').html();  // TODO parse these in the AbstractPageInitializer 
    Mustache.parse(htmlTemplate);

    // TODO modify 'players' and 'cards' pages so that we never show the active flag - then remove it here
    const activeFlagHtml: string = Mustache.render(htmlTemplate, {
        'fileName': selectedLanguage.toString(),
        'alt': selectedLanguage.toUpperCase()
        // no menuText
    });

    const otherLanguage = selectedLanguage === Language.EN ? Language.DE : Language.EN;
    const otherLabel: string = selectedLanguage === Language.EN ? 'Deutsch' : 'English';
    const otherFlagHtml: string = Mustache.render(htmlTemplate, {
        'fileName': otherLanguage.toString(),
        'alt': otherLanguage.toUpperCase(),
        'menuText': otherLabel
    });

    let elem: JQuery<HTMLElement> = $('#navbarLangDropdownLabel');
    if (elem.length > 0) {
        elem.empty();
        elem.append(activeFlagHtml);
    }

    for (let divId of ['#otherLanguageFlags', '#otherLanguageFlags2']) {
        elem = $(divId);
        if (elem.length > 0) {
            elem.empty();
            elem.append(otherFlagHtml);
        }
    }
}


export function getLocalizedString(pKey: string, pCallback: (v: string[]) => void): void {
    getLocalizedStringInternal(pKey, pCallback);
}

export function getLocalizedStringWithArgs(pKey: string, pArgs: Object, pCallback: (v: string[]) => void): void {
    getLocalizedStringInternal([pKey, pArgs], pCallback);
}

export function getLocalizedStringInternal(pKey: any, pCallback: (v: string[]) => void): void {
    if (document.hasOwnProperty('l10n')) {
        const localization = document['l10n'];
        localization.formatValues(pKey).then(pCallback, pCallback);
    }
}
