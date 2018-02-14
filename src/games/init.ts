import * as Mustache from 'mustache';
import * as storage from '../storage/storage';
import { sprintf } from 'sprintf-js';
import { builtInVariants, Language } from '../rules/rules';
import { runActivityInternal, appVersion } from '../main';
import { AbstractPageInitializer, Page, PageContext } from '../framework';
import { GameDao } from '../storage/dao';
import { GamesController, NewGameModalController } from './controllers';


/**
 * The page context object of the 'games' page.
 */
export class GamesPageContext implements PageContext {
    constructor(public readonly gameNames: Set<string>) {}
}


/**
 * The page initializer of the 'games' page.
 */
export class GamesPageInitializer extends AbstractPageInitializer<GamesPageContext>
{
    private readonly modalCtrl: NewGameModalController = new NewGameModalController();


    constructor() {
        super(Page.GAMES, new GamesPageContext(new Set<string>()), '#newGameModal');
    }


    protected parseTemplates(): void {
        Mustache.parse($('#gameTemplate').html());
        Mustache.parse($('#rulesRadioTemplate').html());
        Mustache.parse($('#optionCheckBoxTemplate').html());
    }

    protected modalDisplayed(): void {
        this.setDefaultGameName();
        this.validateGameName(null);
        this.modalCtrl.focusCursorInModal();
    }

    protected pageLoaded(): void {
        this.displayAppVersion();
        this.populateGameList();
        this.modalCtrl.setupGameNameValidation(this.validateGameName.bind(this));
        this.modalCtrl.addVariantsToModal();
        this.modalCtrl.chooseVariant(Object.keys(builtInVariants)[0]);
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        this.populateGameList();
        this.modalCtrl.addVariantsToModal();
        this.modalCtrl.chooseVariant(Object.keys(builtInVariants)[0]);
    }


    private displayAppVersion(): void {
        const v: string = appVersion.version + '.' + appVersion.numCommits + ' (' + appVersion.hash + ')';
        const gamesCtrl: GamesController = new GamesController();
        gamesCtrl.setAppVersion(v);
    }


    private populateGameList(): void {
        const games: GameDao[] = storage.readListOfGames();
        this.pageContext.gameNames.clear();
        for (let game of games) {
            this.pageContext.gameNames.add(game.name);
        }
        const gamesCtrl: GamesController = new GamesController();
        gamesCtrl.populateGameList(games);
    }


    private validateGameName(event): void {
        const s: string = this.modalCtrl.getGameNameFromInput();
        const valid: boolean = s.length > 0 && !this.pageContext.gameNames.has(s);
        const empty: boolean = !valid && s.length === 0;
        this.modalCtrl.displayNamingError(!valid, empty);
        if (valid && event !== null && event.which == 13) {
            runActivityInternal(Page.GAMES, 'create');
        }
    }


    private setDefaultGameName(): void {
        const today: Date = new Date();
        const date: string = sprintf('%4d-%02d-%02d', today.getFullYear(), today.getMonth() + 1, today.getDate());
        let count: number = 1;
        let defaultName: string = date;
        while (this.pageContext.gameNames.has(defaultName)) {
            defaultName = date + '.' + count++;
        }
        this.modalCtrl.setGameName(defaultName);
    }
}
