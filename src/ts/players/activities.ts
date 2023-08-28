/*
 * CivBuddy - A calculator app for players of Francis Tresham's original Civilization board game (1980)
 * Copyright (C) 2012-2023 Thomas Jensen
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

import { Activity } from '../framework/framework';
import { L10nUtil } from '../i18n/util';
import { Language } from '../rules/rules';
import { SituationDao } from '../storage/dao';
import { SituationStorage, StorageSupport } from '../storage/storage';
import { NewPlayerModalController, PlayersController } from './controllers';
import { PlayersPageContext } from './init';



abstract class AbstractPlayersActivity
    implements Activity {

    constructor(protected readonly pageContext: PlayersPageContext) { }

    public abstract execute(pLanguage: Language): void;
}



/**
 * Create a new player after the corresponding button was pushed on the 'new player' modal.
 */
export class CreatePlayerActivity
    extends AbstractPlayersActivity {

    private readonly playerCtrl: PlayersController = new PlayersController();

    private readonly modalCtrl: NewPlayerModalController = new NewPlayerModalController();


    public execute(pLanguage: Language): void {
        const dto: SituationDao = this.modalCtrl.getPlayerDtoFromDialog(this.pageContext.selectedGame.key,
            this.pageContext.selectedGame.variantKey, new StorageSupport().newSituationKey());
        this.modalCtrl.hideModal();
        this.pageContext.playerNames.add(dto.player.name);
        (this.pageContext.selectedGame.situations as any)[dto.player.name] = dto.key;
        new SituationStorage().createSituation(this.pageContext.selectedGame, dto);
        this.playerCtrl.addPlayerToPage(dto);
        PlayersController.addButtonClickHandlers('#' + dto.key);
        window.dispatchEvent(new CustomEvent<object>('cardListChanged'));
    }
}



/**
 * Select one of the players, which will open the 'cards' page.
 */
export class SelectPlayerActivity
    extends AbstractPlayersActivity {

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
    extends AbstractPlayersActivity {

    private readonly playersCtrl: PlayersController = new PlayersController();

    constructor(pPageContext: PlayersPageContext, private readonly situationKey: string,
        private readonly playerName: string) {
        super(pPageContext);
    }


    public execute(pLanguage: Language): void {
        L10nUtil.getLocalizedStringWithArgs('players-delete-confirm', { 'name': this.playerName }, (msg: string[]) => {
            if (window.confirm(msg[0])) {
                new SituationStorage().deleteSituation(this.pageContext.selectedGame, this.situationKey);
                this.pageContext.playerNames.delete(this.playerName);
                this.playersCtrl.removePlayer(this.situationKey);
                window.dispatchEvent(new CustomEvent<object>('cardListChanged'));
            }
        });
    }
}
