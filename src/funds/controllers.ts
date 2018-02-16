import { BaseNavbarController } from '../framework';


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
