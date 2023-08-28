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

import * as Mustache from 'mustache';

import { AbstractPageContext, AbstractPageInitializer, Page } from '../framework/framework';
import { Util } from '../framework/util';
import { runActivityInternal } from '../main';
import { Language } from '../rules/rules';
import { GameDao, SituationDao } from '../storage/dao';
import { GameStorage, SituationStorage } from '../storage/storage';
import { NewPlayerModalController, PlayersController } from './controllers';


/**
 * The page context object of the 'players' page.
 */
export class PlayersPageContext extends AbstractPageContext {
    constructor(public readonly selectedGame: GameDao, public readonly playerNames: Set<string>) {
        super();
    }
}


/**
 * The page initializer of the 'players' page.
 */
export class PlayersPageInitializer
    extends AbstractPageInitializer<PlayersPageContext> {

    private readonly playerCtrl: PlayersController = new PlayersController();

    private readonly modalCtrl: NewPlayerModalController = new NewPlayerModalController();

    constructor() {
        super(Page.PLAYERS,
            new PlayersPageContext(PlayersPageInitializer.getGameFromUrl(), new Set<string>()), '#newPlayerModal');
    }


    private static getGameFromUrl(): GameDao {
        const gameKey: string | null = Util.getUrlParameter('ctx');
        const game: GameDao | null = new GameStorage().readGame(gameKey);
        if (game === null) {
            window.location.replace('index.html');
        }
        return game as GameDao;
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
        const situations: SituationDao[] = new SituationStorage().readSituationsForGame(this.pageContext.selectedGame);
        this.pageContext.playerNames.clear();
        for (const situation of situations) {
            this.pageContext.playerNames.add(situation.player.name);
        }
        this.playerCtrl.populatePlayerList(situations);
    }


    private validatePlayerName(event: any): void {
        const s: string = this.modalCtrl.getPlayerNameFromInput();
        const valid: boolean = s.length > 0 && !this.pageContext.playerNames.has(s);
        const empty: boolean = !valid && s.length === 0;
        this.modalCtrl.displayNamingError(!valid, empty);
        if (valid && event !== null && event.which === 13) {
            runActivityInternal(Page.PLAYERS, 'create');
        }
    }
}
