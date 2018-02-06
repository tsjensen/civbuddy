import 'babel-polyfill';
import * as storage from './storage';
import { VariantDescriptor, Language } from './rules';
import { initGamesPage, createGame, deleteGame, chooseVariant, selectGame } from './games';
import { initPlayersPage, createPlayer, deletePlayer, selectPlayer } from './players';
import { initCardsPage, clickOnCard, buy, toggleCardsFilter, enterFunds, discard } from './cards';
import { initFundsPage } from './funds';
import { changeLanguage, activateLanguage, appOptions } from './app';
import { purgeStorage } from './storage';


export enum Page {
    GAMES = 'games',
    PLAYERS = 'players',
    CARDS = 'cards',
    FUNDS = 'funds'
}


/**
 * Function that is called by every page when the DOM is ready.
 * @param pPage which page we're on
 */
export function initPage(pPage: Page): void {
    $(function(): void {  // execute after DOM has loaded
        activateLanguage(appOptions.language);
    });
    storage.ensureBuiltInVariants();

    switch (pPage) {
        case Page.GAMES: initGamesPage(); break;
        case Page.PLAYERS: initPlayersPage(); break;
        case Page.CARDS: initCardsPage(); break;
        case Page.FUNDS: initFundsPage(); break;
        default:
            console.log('unknown page: ' + pPage + ' - skipping page initialization');
    }
}


export function buttonClick(pElement: HTMLElement, pPage: Page, pButtonName: string, ...pArguments: string[]): void {
    if (!pElement.classList.contains('disabled')) {
        if (pButtonName === 'switchLanguage') {
            changeLanguage(Language[pArguments[0]]);
        } else {
            switch (pPage) {
                case Page.GAMES:
                    if (pButtonName === 'create') {
                        createGame();
                    } else if (pButtonName === 'delete') {
                        deleteGame(pArguments[0], pArguments[1]);
                        (<any>pArguments[2]).stopPropagation();
                    } else if (pButtonName === 'chooseVariant') {
                        chooseVariant(pArguments[0]);
                    } else if (pButtonName === 'select') {
                        selectGame(pArguments[0]);
                    } else if (pButtonName === 'purge') {
                        purgeStorage();
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
