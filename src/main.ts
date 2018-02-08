import 'babel-polyfill';
import * as Mustache from 'mustache';
import * as storage from './storage/storage';
import { AppOptions } from './storage/dao';
import { Language } from './rules/rules';
import { Page, PageContext, AbstractPageInitializer } from './framework';
import { GamesPageInitializer, GamesPageContext } from './games/init';
import { CreateGameActivity, DeleteGameActivity, SelectGameActivity, ChooseVariantActivity, PurgeActivity } from './games/activities';
import { initPlayersPage, createPlayer, deletePlayer, selectPlayer } from './players/players';
import { initCardsPage, clickOnCard, buy, toggleCardsFilter, enterFunds, discard } from './cards/cards';
import { initFundsPage } from './funds/funds';


let pageContext: PageContext;

/**
 * Function that is called by every page when the DOM is ready.
 * @param pPage which page we're on
 */
export function initPage(pPage: Page): void {
    $(function(): void {  // execute after DOM has loaded
        activateLanguage(appOptions.language);
    });
    storage.ensureBuiltInVariants();

    let initializer: AbstractPageInitializer<PageContext> | undefined = undefined;
    switch (pPage) {
        case Page.GAMES: initializer = new GamesPageInitializer(); break;
        case Page.PLAYERS: initPlayersPage(); break;
        case Page.CARDS: initCardsPage(); break;
        case Page.FUNDS: initFundsPage(); break;
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
                        new CreateGameActivity().execute(pageContext as GamesPageContext, appOptions.language);
                    } else if (pButtonName === 'delete') {
                        new DeleteGameActivity(pArguments[0], pArguments[1]).execute(pageContext as GamesPageContext, appOptions.language);
                        (<any>pArguments[2]).stopPropagation();
                    } else if (pButtonName === 'chooseVariant') {
                        new ChooseVariantActivity(pArguments[0]).execute(pageContext as GamesPageContext, appOptions.language);
                    } else if (pButtonName === 'select') {
                        new SelectGameActivity(pArguments[0]).execute(pageContext as GamesPageContext, appOptions.language);
                    } else if (pButtonName === 'purge') {
                        new PurgeActivity().execute(pageContext as GamesPageContext, appOptions.language);
                    }
                    break;

                case Page.PLAYERS:
                    if (pButtonName === 'create') {
                        createPlayer();
                    } else if (pButtonName === 'delete') {
                        deletePlayer(pArguments[0], pArguments[1]);
                        (<any>pArguments[2]).stopPropagation();
                    } else if (pButtonName === 'select') {
                        selectPlayer(pArguments[0]);
                    }
                    break;

                case Page.CARDS:
                    if (pButtonName === 'click') {
                        clickOnCard(pArguments[0]);
                    } else if (pButtonName === 'buy') {
                        buy();
                    } else if (pButtonName === 'filter') {
                        toggleCardsFilter();
                    } else if (pButtonName === 'funds') {
                        enterFunds();
                    } else if (pButtonName === 'discard') {
                        discard();
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
