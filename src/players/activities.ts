import * as storage from '../storage/storage';
import { PlayersPageContext } from './init';
import { Activity } from '../framework';
import { Language } from '../rules/rules';
import { PlayersController, NewPlayerModalController } from './controllers';
import { SituationDao } from '../storage/dao';
import { getLocalizedStringWithArgs } from '../main';



abstract class AbstractPlayersActivity
    implements Activity<void>
{
    constructor(protected readonly pageContext: PlayersPageContext) { }

    abstract execute(pLanguage: Language): void;
}



/**
 * Create a new player after the corresponding button was pushed on the 'new player' modal.
 */
export class CreatePlayerActivity
    extends AbstractPlayersActivity
{
    private readonly playerCtrl: PlayersController = new PlayersController();

    private readonly modalCtrl: NewPlayerModalController = new NewPlayerModalController();


    public execute(pLanguage: Language): void {
        const dto: SituationDao = this.modalCtrl.getPlayerDtoFromDialog(this.pageContext.selectedGame.key,
            this.pageContext.selectedGame.variantKey, storage.newSituationKey());
        this.modalCtrl.hideModal();
        this.pageContext.playerNames.add(dto.player.name);
        this.pageContext.selectedGame.situations[dto.player.name] = dto.key;
        storage.createSituation(this.pageContext.selectedGame, dto);
        this.playerCtrl.addPlayerToPage(dto);
    }
}


/**
 * Select one of the players, which will open the 'cards' page.
 */
export class SelectPlayerActivity
    extends AbstractPlayersActivity
{
    constructor(pPageContext: PlayersPageContext, private readonly situationKey: string) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        window.location.href = 'cards.html?ctx=' + this.situationKey;
    }
}


/**
 * Delete a player.
 */
export class DeletePlayerActivity
    extends AbstractPlayersActivity
{
    private readonly playersCtrl: PlayersController = new PlayersController();

    constructor(pPageContext: PlayersPageContext, private readonly situationKey: string,
        private readonly playerName: string)
    {
        super(pPageContext);
    }


    public execute(pLanguage: Language): void {
        getLocalizedStringWithArgs('players-delete-confirm', {'name': this.playerName}, (msg: string[]) => {
            if (window.confirm(msg[0])) {
                storage.deleteSituation(this.pageContext.selectedGame, this.situationKey);
                this.pageContext.playerNames.delete(this.playerName);
                this.playersCtrl.removePlayer(this.situationKey);
            }
        });
    }
}
