import { Activity } from '../framework/framework';
import { L10nUtil } from '../i18n/util';
import { Language } from '../rules/rules';
import { SituationDao } from '../storage/dao';
import * as storage from '../storage/storage';
import { NewPlayerModalController, PlayersController } from './controllers';
import { PlayersPageContext } from './init';



abstract class AbstractPlayersActivity
    implements Activity
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
        (<any>this.pageContext.selectedGame.situations)[dto.player.name] = dto.key;
        storage.createSituation(this.pageContext.selectedGame, dto);
        this.playerCtrl.addPlayerToPage(dto);
        PlayersController.addButtonClickHandlers('#' + dto.key);
        window.dispatchEvent(new CustomEvent('cardListChanged'));
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
        L10nUtil.getLocalizedStringWithArgs('players-delete-confirm', {'name': this.playerName}, (msg: string[]) => {
            if (window.confirm(msg[0])) {
                storage.deleteSituation(this.pageContext.selectedGame, this.situationKey);
                this.pageContext.playerNames.delete(this.playerName);
                this.playersCtrl.removePlayer(this.situationKey);
                window.dispatchEvent(new CustomEvent('cardListChanged'));
            }
        });
    }
}
