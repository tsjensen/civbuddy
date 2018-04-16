import * as Mustache from 'mustache';

import { BaseController } from '../framework/framework';
import { builtInVariants, RulesJson } from '../rules/rules';
import { FundsDao, FundsDaoImpl, PlayerDao, PlayerDaoImpl, SituationDao, SituationDaoImpl } from '../storage/dao';


export class PlayersController
    extends BaseController
{
    public constructor() {
        super();
    }


    public populatePlayerList(pSituations: SituationDao[]): void {
        $('#playerList > div').remove();
        for (let situation of pSituations) {
            this.addPlayerToPage(situation);
        }
    }

    public addPlayerToPage(pSituation: SituationDao): void {
        let htmlTemplate: string = $('#playerTemplate').html();
        let rendered: string = Mustache.render(htmlTemplate, {
            'situationKey': pSituation.key,
            'playerName': pSituation.player.name,
            'pointsTarget': pSituation.player.winningTotal
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
    extends BaseController
{
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
        $('#inputPlayerName').blur(pHandler);
        $('#inputPlayerName').keyup(pHandler);
    }


    public addTargetsToModal(pVariantId: string): void {
        $('#pointsTargetRadios > div').remove();
        let htmlTemplate: string = $('#pointsTargetRadioTemplate').html();
        let first: boolean = true;
        for (let target of (builtInVariants.get(pVariantId) as RulesJson).targetOpts) {
            let rendered: string = Mustache.render(htmlTemplate, {
                'pointsValue': target,
                'checked': first
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
