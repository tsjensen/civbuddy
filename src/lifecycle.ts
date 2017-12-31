import 'babel-polyfill';
import * as storage from './storage';
import { VariantDescriptor, Language } from './rules';
import { initGamesPage, createGame, deleteGame } from './games';
import { initPlayersPage } from './players';
import { initCardsPage } from './cards';
import { initFundsPage } from './funds';
import { showLanguage, changeLanguage } from './app';


export enum Page {
    GAMES = 'Games',
    PLAYERS = 'Players',
    CARDS = 'Cards',
    FUNDS = 'Funds'
}

/**
 * Function that is called by every page when the DOM is ready.
 * @param pPage which page we're on
 */
export function initPage(pPage: Page): void {
    $(function(): void {
        showLanguage();   // execute after DOM has loaded
    });
    storage.ensureBuiltInVariants();
    let variants: VariantDescriptor[] = storage.default;
    for (let v of variants) {
        console.log("Found variant: " + v.variantId);
    }

    switch (pPage) {
        case Page.GAMES: initGamesPage(); break;
        case Page.PLAYERS: initPlayersPage(); break;
        case Page.CARDS: initCardsPage(); break;
        case Page.FUNDS: initFundsPage(); break;
        default:
            console.log('unknown page: ' + pPage + ' - skipping page initialization');
    }
}


export function buttonClick(pPage: Page, pButtonName: string, ...pArguments: string[]): void {
    if (pButtonName === 'switchLanguage') {
        changeLanguage(Language[pArguments[0]]);
    } else {
        switch (pPage) {
            case Page.GAMES:
                if (pButtonName === 'create') {
                    createGame();
                } else if (pButtonName === 'delete') {
                    deleteGame(pArguments[0]);
                }
                break;
            case Page.PLAYERS:
                // TODO
                break;
            case Page.CARDS:
                // TODO
                break;
            case Page.FUNDS:
                // TODO
                break;
            default:
                console.log('unknown page: ' + pPage + ' - skipping button click');
        }
    }
}
