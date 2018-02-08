import * as storage from '../storage/storage';
import { PlayersPageContext } from './init';
import { Activity } from '../framework';
import { Language } from '../rules/rules';
import { PlayersController, NewPlayerModalController } from './controllers';
import { SituationDao } from '../storage/dao';
import { getLocalizedStringWithArgs } from '../main';


/**
 * Create a new player after the corresponding button was pushed on the 'new player' modal.
 */
export class CreatePlayerActivity
    implements Activity<void, PlayersPageContext>
{
    private readonly playerCtrl: PlayersController = new PlayersController();

    private readonly modalCtrl: NewPlayerModalController = new NewPlayerModalController();


    public execute(pPageContext: PlayersPageContext, pLanguage: Language): void {
        const dto: SituationDao = this.modalCtrl.getPlayerDtoFromDialog(pPageContext.selectedGame.key,
                pPageContext.selectedGame.variantKey, storage.newSituationKey());
        this.modalCtrl.hideModal();
        pPageContext.playerNames.add(dto.player.name);
        pPageContext.selectedGame.situations[dto.player.name] = dto.key;
        storage.createSituation(pPageContext.selectedGame, dto);
        this.playerCtrl.addPlayerToPage(dto);
    }
}


/**
 * Select one of the players, which will open the 'cards' page.
 */
export class SelectPlayerActivity
    implements Activity<void, PlayersPageContext>
{
    constructor(private readonly situationKey: string) { }

    public execute(pPageContext: PlayersPageContext, pLanguage: Language): void {
        window.location.href = 'cards.html?ctx=' + this.situationKey;
    }
}


/**
 * Delete a player.
 */
export class DeletePlayerActivity
    implements Activity<void, PlayersPageContext>
{
    private readonly playersCtrl: PlayersController = new PlayersController();

    constructor(private readonly situationKey: string, private readonly playerName: string) { }

    public execute(pPageContext: PlayersPageContext, pLanguage: Language): void {
        getLocalizedStringWithArgs('players-delete-confirm', {'name': this.playerName}, (msg: string[]) => {
            if (window.confirm(msg[0])) {
                storage.deleteSituation(pPageContext.selectedGame, this.situationKey);
                pPageContext.playerNames.delete(this.playerName);
                this.playersCtrl.removePlayer(this.situationKey);
            }
        });
    }
}
