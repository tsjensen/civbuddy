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

import { BaseController } from '../framework/framework';
import { builtInVariants, RulesJson } from '../rules/rules';
import { FundsDao, FundsDaoImpl, PlayerDao, PlayerDaoImpl, SituationDao, SituationDaoImpl } from '../storage/dao';


export class PlayersController
    extends BaseController {

    public constructor() {
        super();
    }


    public populatePlayerList(pSituations: SituationDao[]): void {
        const situations: SituationDao[] = pSituations.slice();
        situations.sort((a: SituationDao, b: SituationDao) => a.player.name.localeCompare(b.player.name));
        $('#playerList > div').remove();
        for (const situation of situations) {
            this.addPlayerToPage(situation);
        }
    }

    public addPlayerToPage(pSituation: SituationDao): void {
        const htmlTemplate: string = $('#playerTemplate').html();
        const rendered: string = Mustache.render(htmlTemplate, {
            'playerName': pSituation.player.name,
            'pointsTarget': pSituation.player.winningTotal,
            'situationKey': pSituation.key
        });
        $('#playerList').append(rendered);
        this.showNumCardsOwned(pSituation.key, pSituation.ownedCards.length);
    }

    private showNumCardsOwned(pSituationKey: string, pNumCardsOwned: number): void {
        let cardsTranslationKey: string = 'players-cards-';
        const elem: JQuery<HTMLElement> = $('#' + pSituationKey + ' div.card-header > span');
        if (pNumCardsOwned === 0) {
            cardsTranslationKey += '0';
        } else if (pNumCardsOwned === 1) {
            cardsTranslationKey += 'one';
        } else {
            cardsTranslationKey += 'other';
            elem.attr('data-l10n-args', `{"count": ${pNumCardsOwned}}`);
        }
        elem.attr('data-l10n-id', cardsTranslationKey);
    }

    public removePlayer(pSituationKey: string): void {
        $('#' + pSituationKey).remove();
    }

    public setGameName(pGameName: string): void {
        $('#gameName').html(pGameName);
    }
}



export class NewPlayerModalController
    extends BaseController {

    public constructor() {
        super();
    }


    public getPlayerDtoFromDialog(pGameKey: string, pVariantId: string, pNewPlayerKey: string): SituationDao {
        const playerName: string = this.getValueFromInput('inputPlayerName', 'ERROR - remove me');
        const variant: RulesJson = builtInVariants.get(pVariantId) as RulesJson;
        const targetPoints: number =
            Number(this.getValueFromRadioButtons('pointsTargetRadios', String(variant.targetOpts[0])));

        const player: PlayerDao = new PlayerDaoImpl(playerName, targetPoints);
        const funds: FundsDao = new FundsDaoImpl({}, 0, true);
        const dto: SituationDao = new SituationDaoImpl(pNewPlayerKey, pGameKey, player, funds, []);
        return dto;
    }


    public setupPlayerNameValidation(pHandler: JQuery.EventHandler<HTMLElement>): void {
        $('#inputPlayerName').on('blur', pHandler);
        $('#inputPlayerName').on('keyup', pHandler);
    }


    public addTargetsToModal(pVariantId: string): void {
        $('#pointsTargetRadios > div').remove();
        const htmlTemplate: string = $('#pointsTargetRadioTemplate').html();
        let first: boolean = true;
        for (const target of (builtInVariants.get(pVariantId) as RulesJson).targetOpts) {
            const rendered: string = Mustache.render(htmlTemplate, {
                'checked': first,
                'pointsValue': target
            });
            $('#pointsTargetRadios').append(rendered);
            first = false;
        }
    }

    public getPlayerNameFromInput(): string {
        return this.getValueFromInput('inputPlayerName', '');
    }

    public focusCursorInModal(): void {
        this.focusAndPositionCursor('inputPlayerName');
    }

    public displayNamingError(pIsInvalid: boolean, pNoNameGiven: boolean): void {
        this.setNameIsInvalid('newPlayerModal', 'inputPlayerName', 'players-newModal-label-', pIsInvalid, pNoNameGiven);
    }

    public hideModal(): void {
        $('#newPlayerModal').modal('hide');
    }
}
