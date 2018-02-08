import * as storage from '../storage/storage';  // TODO a storage object could be passed to the commands
import { GamesController, NewGameModalController } from './controllers';
import { GameDao, GameDaoImpl } from '../storage/dao';
import { RulesJson, Language, builtInVariants } from '../rules/rules';
import { Activity } from '../framework';
import { GamesPageContext } from './init';
import { getLocalizedStringWithArgs, getLocalizedString } from '../main';


/**
 * Create a new game after the corresponding button was pushed on the 'new game' modal.
 */
export class CreateGameActivity
    implements Activity<void, GamesPageContext>
{
    private readonly gamesCtrl: GamesController = new GamesController();

    private readonly modalCtrl: NewGameModalController = new NewGameModalController();


    public execute(pPageContext: GamesPageContext, pLanguage: Language): void {
        const dto: GameDao = this.modalCtrl.getGameDtoFromDialog(storage.newGameKey());
        this.modalCtrl.hideModal();
        pPageContext.gameNames.add(dto.name);
        storage.saveGame(dto);
        this.addGameToPage(dto, pLanguage);
    }

    private addGameToPage(pGame: GameDao, pLanguage: Language): void {
        const variant: RulesJson = builtInVariants[pGame.variantKey];
        const rulesName: string = variant.displayNames[pLanguage];
        const optionDesc: string = GameDaoImpl.buildOptionDescriptor(variant, pGame.options, pLanguage);
        this.gamesCtrl.addGame(pGame.key, pGame.name, rulesName, optionDesc);
    }
}


/**
 * In the 'new game' modal, choose a variant by clicking one of the radio boxes.
 */
export class ChooseVariantActivity
    implements Activity<void, GamesPageContext>
{
    private readonly modalCtrl: NewGameModalController = new NewGameModalController();

    constructor(private readonly variantId: string) {}

    public execute(pPageContext: GamesPageContext, pLanguage: Language): void {
        this.modalCtrl.chooseVariant(this.variantId);
    }
}


/**
 * Select one of the games, which will open the 'players' page.
 */
export class SelectGameActivity
    implements Activity<void, GamesPageContext>
{
    constructor(private readonly gameKey: string) { }

    public execute(pPageContext: GamesPageContext, pLanguage: Language): void {
        window.location.href = 'players.html?ctx=' + this.gameKey;
    }
}


/**
 * Delete a game.
 */
export class DeleteGameActivity
    implements Activity<void, GamesPageContext>
{
    private readonly gamesCtrl: GamesController = new GamesController();

    constructor(private readonly gameKey: string, private readonly gameName: string) { }

    public execute(pPageContext: GamesPageContext, pLanguage: Language): void {
        getLocalizedStringWithArgs('games-delete-confirm', {'name': this.gameName}, (msg: string[]) => {
            if (window.confirm(msg[0])) {
                storage.deleteGame(this.gameKey);
                pPageContext.gameNames.delete(this.gameName);
                this.gamesCtrl.removeGame(this.gameKey);
            }
        });
    }
}


/**
 * Purge all CivBuddy data in local storage, so you start with a clean slate.
 */
export class PurgeActivity
    implements Activity<void, GamesPageContext>
{
    public execute(pPageContext: GamesPageContext, pLanguage: Language): void {
        getLocalizedString('games-purge-confirm', (msg: string[]) => {
            if (window.confirm(msg[0])) {
                storage.purgeStorage();
                window.setTimeout(function(){ window.location.reload(); }, 300);
            }
        });
    }
}
