import * as Mustache from 'mustache';
import * as storage from './storage';
import { GameDto } from './dto';
import { builtInVariants } from './rules';
import { focusAndPositionCursor } from './games';


let selectedGame: GameDto;

export function initPlayersPage(): void {
    if (getGameFromUrl()) {
        $(document).on('show.bs.modal', '#newPlayerModal', function(): void {   // before fade-in animation
            addTargetsToModal();
        });
        $(document).on('shown.bs.modal', '#newPlayerModal', function(): void {  // after fade-in animation
            focusAndPositionCursor('inputPlayerName');
            //validateGameName(null);   // TODO
        });
        // TODO
    }
}

function getGameFromUrl(): boolean {
    const gameKey: string | null = getUrlParameter('ctx');
    const game: GameDto | null = storage.readGame(gameKey);
    if (game !== null) {
        selectedGame = game;
        return true;
    } else {
        window.location.replace('index.html');
        return false;
    }
}

function getUrlParameter(pParamName: string): string | null {
    let result: string | null = null;
    let pageUrl: string = decodeURIComponent(window.location.search.substring(1));
    let params: string[] = pageUrl.split('&');
    for (let i: number = 0; i < params.length; i++) {
        let paramKeyValue: string[] = params[i].split('=');
        if (paramKeyValue[0] === pParamName && typeof(paramKeyValue[1]) !== undefined) {
            result = paramKeyValue[1];
            break;
        }
    }
    return result;
}

function addTargetsToModal(): void {
    $('#pointsTargetRadios > div').remove();
    let htmlTemplate: string = $('#pointsTargetRadioTemplate').html();
    Mustache.parse(htmlTemplate);
    let first: boolean = true;
    for (let target of builtInVariants[selectedGame.variantKey].targetOpts) {
        let rendered: string = Mustache.render(htmlTemplate, {
            'pointsValue': target,
            'checked': first
        });
        $('#pointsTargetRadios').append(rendered);
        first = false;
    }
}
