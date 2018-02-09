import 'babel-polyfill';
import * as Mustache from 'mustache';
import * as storage from './storage/storage';
import { AppOptions } from './storage/dao';
import { Language } from './rules/rules';
import { Page, PageContext, AbstractPageInitializer } from './framework';
import { GamesPageInitializer, GamesPageContext } from './games/init';
import { CreateGameActivity, DeleteGameActivity, SelectGameActivity, ChooseVariantActivity, PurgeActivity } from './games/activities';
import { PlayersPageContext, PlayersPageInitializer } from './players/init';
import { CreatePlayerActivity, DeletePlayerActivity, SelectPlayerActivity } from './players/activities';
import { CardsPageContext, CardsPageInitializer } from './cards/init';
import { ClickOnCardActivity, BuyCardsActivity, ToggleCardsFilterActivity, EnterFundsActivity, DiscardCardActivity, PlanCardActivity, UnplanCardActivity, ShowCardInfoActivity } from './cards/activities';
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
    // TODO replace this with a factory for commands, and a dispatcher
    if (!pElement.classList.contains('disabled')) {
        if (pButtonName === 'switchLanguage') {
            changeLanguage(Language[pArguments[0]]);
        } else {
            switch (pPage) {
                case Page.GAMES:
                    if (pButtonName === 'create') {
                        new CreateGameActivity(pageContext as GamesPageContext).execute(appOptions.language);
                    } else if (pButtonName === 'delete') {
                        new DeleteGameActivity(pageContext as GamesPageContext, pArguments[0], pArguments[1]).execute(appOptions.language);
                        (<any>pArguments[2]).stopPropagation();
                    } else if (pButtonName === 'chooseVariant') {
                        new ChooseVariantActivity(pageContext as GamesPageContext, pArguments[0]).execute(appOptions.language);
                    } else if (pButtonName === 'select') {
                        new SelectGameActivity(pageContext as GamesPageContext, pArguments[0]).execute(appOptions.language);
                    } else if (pButtonName === 'purge') {
                        new PurgeActivity(pageContext as GamesPageContext).execute(appOptions.language);
                    }
                    break;

                case Page.PLAYERS:
                    if (pButtonName === 'create') {
                        new CreatePlayerActivity(pageContext as PlayersPageContext).execute(appOptions.language);
                    } else if (pButtonName === 'delete') {
                        new DeletePlayerActivity(pageContext as PlayersPageContext, pArguments[0], pArguments[1]).execute(appOptions.language);
                        (<any>pArguments[2]).stopPropagation();
                    } else if (pButtonName === 'select') {
                        new SelectPlayerActivity(pageContext as PlayersPageContext, pArguments[0]).execute(appOptions.language);
                    }
                    break;

                case Page.CARDS:
                    const pc: CardsPageContext = pageContext as CardsPageContext;
                    if (pButtonName === 'click') {
                        new ClickOnCardActivity(pc, pArguments[0]).execute(appOptions.language);
                    } else if (pButtonName === 'plan') {
                        new PlanCardActivity(pc, pArguments[0]).execute(appOptions.language);
                    } else if (pButtonName === 'unplan') {
                        new UnplanCardActivity(pc, pArguments[0]).execute(appOptions.language);
                    } else if (pButtonName === 'info') {
                        new ShowCardInfoActivity(pc, pArguments[0]).execute(appOptions.language);
                    } else if (pButtonName === 'buy') {
                        new BuyCardsActivity(pc).execute(appOptions.language);
                    } else if (pButtonName === 'filter') {
                        new ToggleCardsFilterActivity(pc).execute(appOptions.language);
                    } else if (pButtonName === 'funds') {
                        new EnterFundsActivity(pc).execute(appOptions.language);
                    } else if (pButtonName === 'discard') {
                        new DiscardCardActivity(pc).execute(appOptions.language);
                    }
                    break;

                case Page.FUNDS:
                    // TODO
                    break;

                default:
                    console.log('unknown page: ' + pPage + ' - skipping button click');
            }
        }
    }
}



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
    let htmlTemplate: string = $('#flagTemplate').html();
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
