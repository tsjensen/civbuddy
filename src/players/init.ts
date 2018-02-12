import * as Mustache from 'mustache';
import * as storage from '../storage/storage';
import { GameDao, SituationDao } from '../storage/dao';
import { getUrlParameter } from '../util';
import { runActivityInternal } from '../main';
import { PageContext, AbstractPageInitializer, Page } from '../framework';
import { NewPlayerModalController, PlayersController } from './controllers';
import { Language } from '../rules/rules';


/**
 * The page context object of the 'players' page.
 */
export class PlayersPageContext implements PageContext {
    constructor(public readonly selectedGame: GameDao, public readonly playerNames: Set<string>) {}
}


/**
 * The page initializer of the 'players' page.
 */
export class PlayersPageInitializer
    extends AbstractPageInitializer<PlayersPageContext>
{
    private readonly playerCtrl: PlayersController = new PlayersController();

    private readonly modalCtrl: NewPlayerModalController = new NewPlayerModalController();

    constructor() {
        super(Page.PLAYERS,
             new PlayersPageContext(PlayersPageInitializer.getGameFromUrl(), new Set<string>()), '#newPlayerModal');
    }


    protected parseTemplates(): void {
        Mustache.parse($('#playerTemplate').html());
        Mustache.parse($('#pointsTargetRadioTemplate').html());
    }

    protected modalDisplayed(): void {
        this.modalCtrl.focusCursorInModal();
        this.validatePlayerName(null);
    }

    protected pageLoaded(): void {
        this.populatePlayerList();
        this.modalCtrl.setupPlayerNameValidation(this.validatePlayerName.bind(this));
        document.title = this.pageContext.selectedGame.name + ' - CivBuddy';
        this.playerCtrl.setGameName(this.pageContext.selectedGame.name);
        this.modalCtrl.addTargetsToModal(this.pageContext.selectedGame.variantKey);
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        // nothing to do
    }


    private populatePlayerList(): void {
        const situations: SituationDao[] = storage.readSituationsForGame(this.pageContext.selectedGame);
        this.pageContext.playerNames.clear();
        for (let situation of situations) {
            this.pageContext.playerNames.add(situation.player.name);
        }
        this.playerCtrl.populatePlayerList(situations);
    }
    
    private static getGameFromUrl(): GameDao {
        const gameKey: string | null = getUrlParameter('ctx');
        const game: GameDao | null = storage.readGame(gameKey);
        if (game === null) {
            window.location.replace('index.html');
        }
        return game as GameDao;
    }


    private validatePlayerName(event): void {
        const s: string = this.modalCtrl.getPlayerNameFromInput();
        const valid: boolean = s.length > 0 && !this.pageContext.playerNames.has(s);
        const empty: boolean = !valid && s.length === 0;
        this.modalCtrl.displayNamingError(!valid, empty);
        if (valid && event !== null && event.which == 13) {
            runActivityInternal(Page.PLAYERS, 'create');
        }
    }
}
