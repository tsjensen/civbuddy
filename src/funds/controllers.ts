import * as Mustache from 'mustache';
import { BaseNavbarController, BaseController } from '../framework';
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
            'commodityName': pCommodity.names[pLanguage],
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
}
