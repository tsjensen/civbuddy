import * as Mustache from 'mustache';

import { BaseController, BaseNavbarController } from '../framework';
import { CommodityJson, Language } from '../rules/rules';


export class NavbarController
    extends BaseNavbarController
{
    public constructor() {
        super();
    }

    public addGameIdToLinks(pGameId: string): void {
        $('a.add-game-id').attr('href', 'players.html?ctx=' + pGameId);
    }

    public addSituationIdToLinks(pSituationId: string): void {
        $('a.add-situation-id').attr('href', 'cards.html?ctx=' + pSituationId);
    }

    public setTotalFunds(pTotalFunds: number): void {
        const elem: JQuery<HTMLElement> = $('.navbar .navbarFundsValue > span');
        elem.html(String(pTotalFunds));
    }
}


export class CommodityController
    extends BaseController
{
    public constructor() {
        super();
    }

    public putCommodity(pCommodityId: string, pCommodity: CommodityJson, pNumOwned: number, pLanguage: Language): void
    {
        const commodityTemplate: string = $('#commodityTemplate').html();
        const clist: JQuery<HTMLElement> = $('#commodityList');
        const rendered: string = Mustache.render(commodityTemplate, {
            'commodityId': pCommodityId,
            'commodityName': pCommodity.base + ' - ' + pCommodity.names[pLanguage],
            'n': pNumOwned
        });
        clist.append(rendered);

        const buttonTemplate: string = $('#commodityButtonTemplate').html();
        const buttonList: JQuery<HTMLElement> = $('#commodity-' + pCommodityId + ' .card-body > .container > .row');
        for (let i=1; i <= pCommodity.maxCount; i++) {
            const buttonHtml: string = Mustache.render(buttonTemplate, {
                'selected': pNumOwned === i,
                'commodityId': pCommodityId,
                'n': i,
                'value': pCommodity.base * i * i
            });
            buttonList.append(buttonHtml);
        }
    }


    public updateCommodityName(pCommodityId: string, pNewName: string): void {
        $('#commodity-' + pCommodityId + ' .card-title').html(pNewName);
    }


    public setCommodityValue(pCommodityId: string, pNumOwned: number, pHave: boolean): void
    {
        const button: JQuery<HTMLElement> = $('#commodity-' + pCommodityId
                + ' .card-body .row > div.commodity-pts:nth-child(' + pNumOwned + ') > button');
        if (pHave) {
            button.addClass('btn-info');
            button.removeClass('btn-outline-lightgray');
        } else {
            button.removeClass('btn-info');
            button.addClass('btn-outline-lightgray');
        }

        const pill: JQuery<HTMLElement> = $('#commodity-' + pCommodityId + ' .card-header > span.badge-pill');
        const clearBtn: JQuery<HTMLElement> = $('#commodity-' + pCommodityId + ' .card-header > button');
        if (pHave) {
            pill.html(String(pNumOwned));
            this.showElement(pill);
            this.showElement(clearBtn);
        } else {
            this.hideElement(pill);
            this.hideElement(clearBtn);
        }
    }


    public setMiningYield(pMiningYield: number): void {
        $('#useMiningYield > label > span').attr('data-l10n-args', JSON.stringify({'value': pMiningYield}));
    }

    public displayMiningBonusCheckbox(pVisible: boolean): void {
        if (pVisible) {
            this.showElement($('#useMiningYield'));
        } else {
            this.hideElement($('#useMiningYield'));
        }
    }

    public checkMiningBonusCheckbox(pChecked: boolean): void {
        $('#useMiningYield input').prop('checked', pChecked);
    }

    public enableMiningBonusCheckbox(pEnable: boolean): void {
        if (pEnable) {
            $('#useMiningYield').removeClass('text-muted');
            $('#useMiningYield input').removeProp('disabled');
        } else {
            $('#useMiningYield').addClass('text-muted');
            $('#useMiningYield input').prop('disabled', true);
        }
    }


    public getTreasuryValue(): string {
        return this.getValueFromInput('inputTreasury', '0');
    }

    public setupTreasuryHandler(pHandler: JQuery.EventHandler<HTMLElement>): void {
        const elem: JQuery<HTMLElement> = $('#inputTreasury');
        elem.change(pHandler);
        elem.keyup(pHandler);
        elem.mouseup(function(e) { return false; });
    }

    public setTreasuryValid(pValid: boolean): void {
        if (pValid) {
            $('#inputTreasury').removeClass('is-invalid');
        } else {
            $('#inputTreasury').addClass('is-invalid');
        }
    }

    public setTreasury(pValue: number): void {
        $('#inputTreasury').val(pValue);
    }
}
