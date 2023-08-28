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
import { builtInVariants, Language, RulesJson } from '../rules/rules';
import { GameDao, GameDaoImpl } from '../storage/dao';
import { GameStorage, StorageSupport } from '../storage/storage';
import { GamesController, NewGameModalController } from './controllers';
import { GamesPageContext } from './init';



abstract class AbstractGamesActivity
    implements Activity {

    constructor(protected readonly pageContext: GamesPageContext) { }

    public abstract execute(pLanguage: Language): void;
}



/**
 * Create a new game after the corresponding button was pushed on the 'new game' modal.
 */
export class CreateGameActivity
    extends AbstractGamesActivity {

    private readonly gamesCtrl: GamesController = new GamesController();

    private readonly modalCtrl: NewGameModalController = new NewGameModalController();


    public execute(pLanguage: Language): void {
        const dto: GameDao = this.modalCtrl.getGameDtoFromDialog(new StorageSupport().newGameKey());
        this.modalCtrl.hideModal();
        this.pageContext.gameNames.add(dto.name);
        new GameStorage().saveGame(dto);
        this.addGameToPage(dto, pLanguage);
        window.dispatchEvent(new CustomEvent<object>('cardListChanged'));
    }

    private addGameToPage(pGame: GameDao, pLanguage: Language): void {
        const variant: RulesJson = builtInVariants.get(pGame.variantKey) as RulesJson;
        const rulesName: string = (variant.displayNames as any)[pLanguage];
        const optionDesc: string = GameDaoImpl.buildOptionDescriptor(variant, pGame.options, pLanguage);
        this.gamesCtrl.addGame(pGame.key, pGame.name, rulesName, optionDesc);
        GamesController.addButtonClickHandlers('#' + pGame.key);
    }
}



/**
 * In the 'new game' modal, choose a variant by clicking one of the radio boxes.
 */
export class ChooseVariantActivity
    extends AbstractGamesActivity {

    private readonly modalCtrl: NewGameModalController = new NewGameModalController();

    constructor(pPageContext: GamesPageContext, private readonly variantId: string) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        this.modalCtrl.chooseVariant(this.variantId);
    }
}



/**
 * Select one of the games, which will open the 'players' page.
 */
export class SelectGameActivity
    extends AbstractGamesActivity {

    constructor(pPageContext: GamesPageContext, private readonly gameKey: string) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        window.location.href = 'players.html?ctx=' + this.gameKey;
    }
}



/**
 * Delete a game.
 */
export class DeleteGameActivity
    extends AbstractGamesActivity {

    private readonly gamesCtrl: GamesController = new GamesController();

    constructor(pPageContext: GamesPageContext, private readonly gameKey: string, private readonly gameName: string) {
        super(pPageContext);
    }

    public execute(pLanguage: Language): void {
        L10nUtil.getLocalizedStringWithArgs('games-delete-confirm', { 'name': this.gameName }, (msg: string[]) => {
            if (window.confirm(msg[0])) {
                new GameStorage().deleteGame(this.gameKey);
                this.pageContext.gameNames.delete(this.gameName);
                this.gamesCtrl.removeGame(this.gameKey);
                window.dispatchEvent(new CustomEvent<object>('cardListChanged'));
            }
        });
    }
}



/**
 * Purge all CivBuddy data in local storage, so you start with a clean slate.
 */
export class PurgeActivity
    extends AbstractGamesActivity {

    public execute(pLanguage: Language): void {
        L10nUtil.getLocalizedString('games-purge-confirm', (msg: string[]) => {
            if (window.confirm(msg[0])) {
                new StorageSupport().purgeStorage();
                window.setTimeout(() => { window.location.reload(); }, 300);
            }
        });
    }
}
