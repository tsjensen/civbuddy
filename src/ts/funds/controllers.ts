import * as Mustache from 'mustache';

import { BaseController, BaseNavbarController } from '../framework/framework';
import { CommodityJson, Language } from '../rules/rules';
import { SummarizedCommodity } from './calc';


/**
 * Manages the display of the 'funds' page navbar.
 */
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

    public setSummaryEnabled(pEnabled: boolean): void {
        if (pEnabled) {
            this.enableLink($('#summaryButton'));
        } else {
            this.disableLink($('#summaryButton'));
        }
    }

    public setSummaryIcon(pSummaryActive: boolean): void {
        const iconSpan: JQuery<HTMLElement> = $('#summaryButton > span:first-child()');
        const labelSpan: JQuery<HTMLElement> = $('#summaryButton > span:nth-child(2)');
        if (pSummaryActive) {
            iconSpan.removeClass('oi-spreadsheet');
            iconSpan.addClass('oi-pencil');
            labelSpan.attr('data-l10n-id', 'funds-navbar-edit');
        } else {
            iconSpan.removeClass('oi-pencil');
            iconSpan.addClass('oi-spreadsheet');
            labelSpan.attr('data-l10n-id', 'funds-navbar-summary');
        }
    }
}



/**
 * Manages the display of the commodity cards.
 */
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
            'commodityName': pCommodity.base + ' - ' + (<any> pCommodity.names)[pLanguage],
            'n': pNumOwned
        });
        clist.append(rendered);

        const buttonTemplate: string = $('#commodityButtonTemplate').html();
        const buttonList: JQuery<HTMLElement> = $('#commodity-' + pCommodityId + ' .card-body > .container > .row');
        for (let i = 1; i <= pCommodity.maxCount; i++) {
            let value: number = pCommodity.base * i;
            if (!pCommodity.wine) {
                value *= i;
            }
            const buttonHtml: string = Mustache.render(buttonTemplate, {
                'commodityId': pCommodityId,
                'n': i,
                'selected': pNumOwned === i,
                'value': value
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



/**
 * Manages the display of the funds summary.
 */
export class SummaryController
    extends BaseController
{
    public constructor() {
        super();
    }


    public setTreasury(pValue: number): void {
        $('#fundsSummary table > tbody > tr:first-child() > td:last-child()').html(String(pValue));
    }

    public setMiningBonusVisible(pVisible: boolean): void {
        const elem: JQuery<HTMLElement> = $('#fundsSummary table > tbody > tr.summary-mining-bonus');
        if (pVisible) {
            this.showElement(elem);
        } else {
            this.hideElement(elem);
        }
    }

    public setMiningBonusValue(pValue: number | undefined): void {
        const selectMbRow: string = '#fundsSummary table > tbody > tr.summary-mining-bonus';
        const commentElem: JQuery<HTMLElement> = $(selectMbRow + ' > td:nth-child(2)');
        const valueElem: JQuery<HTMLElement> = $(selectMbRow + ' > td:last-child()');
        if (typeof (pValue) === 'number') {
            commentElem.removeAttr('data-l10n-id');
            commentElem.html('&nbsp;');
            valueElem.html(String(pValue));
        } else {
            commentElem.attr('data-l10n-id', 'funds-summary-miningBonusUnused');
            valueElem.html('0');
        }
    }

    public clearCommodities(): void {
        $('#fundsSummary table > tbody > tr.summary-row-commodity').remove();
    }

    public addCommodity(pCommodityId: string, pName: string, pNumCards: number, pValue: number): void {
        const summaryRowTemplate: string = $('#summaryRowTemplate').html();
        const rendered: string = Mustache.render(summaryRowTemplate, {
            'commodityId': pCommodityId,
            'commodityName': pName,
            'n': pNumCards,
            'value': pValue
        });
        $(rendered).insertAfter($('#fundsSummary table > tbody > tr:first-child()'));
    }

    public setTotalNumCards(pNumCards: number): void {
        $('#fundsSummary table > tbody > tr.funds-total > td:nth-child(2)').html(String(pNumCards));
    }

    public setTotal(pValue: number): void {
        $('#fundsSummary table > tbody > tr.funds-total > td:last-child()').html(String(pValue));
    }


    public toggleSummary(pSummaryVisible: boolean): void {
        if (pSummaryVisible) {
            this.hideElement($('#commodityList'));
            this.showElement($('#fundsSummary'));
        } else {
            this.hideElement($('#fundsSummary'));
            this.showElement($('#commodityList'));
        }
    }

    public isSummaryVisible(): boolean {
        return !$('#fundsSummary').hasClass('d-none');
    }


    public updateCommodityTranslations(pScs: SummarizedCommodity[], pNewLanguage: Language): void {
        const scs: Map<string, SummarizedCommodity> = this.commoditiesToMap(pScs);
        const resourceNameElems: JQuery<HTMLElement> = $('#fundsSummary table > tbody > tr.summary-row-commodity > th');
        resourceNameElems.each(function() {
            const elem: JQuery<HTMLElement> = jQuery(this);
            const commodityId: string = elem.attr('commodity') as string;
            const names: object = (scs.get(commodityId) as SummarizedCommodity).names;
            const newName: string = (<any> names)[pNewLanguage];
            elem.text(newName);
        });
    }

    private commoditiesToMap(pScs: SummarizedCommodity[]): Map<string, SummarizedCommodity> {
        const result: Map<string, SummarizedCommodity> = new Map();
        for (const sc of pScs) {
            result.set(sc.id, sc);
        }
        return result;
    }
}
