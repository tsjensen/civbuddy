import * as Mustache from 'mustache';
import * as storage from './storage';
import { SituationDto, GameDto } from './dto';
import { getUrlParameter } from './dom';
import { CardJson, builtInVariants, RulesJson, Rules } from './rules';
import { appOptions, getLocalizedString } from './app';


let selectedSituation: SituationDto;
let selectedGame: GameDto;
let selectedRules: Rules;


export function initCardsPage(): void {
    if (getSituationFromUrl()) {
        $(function(): void {
            populateCardsList();   // execute after DOM has loaded
            document.title = selectedSituation.player.name + ' - ' + selectedGame.name + ' - CivBuddy';
            setActivePlayer();
            // TODO funds, ruleset, etc.
        });
    }
}

function getSituationFromUrl(): boolean {
    const situationKey: string | null = getUrlParameter('ctx');
    const sit: SituationDto | null = storage.readSituation(situationKey);
    let result: boolean = false;
    if (sit !== null) {
        const game: GameDto | null = storage.readGame(sit.gameId);
        if (game != null) {
            selectedSituation = sit;
            selectedGame = game;
            selectedRules = new Rules(builtInVariants[selectedGame.variantKey]);
            result = true;
        }
    }
    if (!result) {
        window.location.replace('index.html');
    }
    return result;
}

function populateCardsList(): void {
    // TODO
    const variant: RulesJson = selectedRules.variant;
    const maxCredits: number = selectedRules.maxCredits;
    let htmlTemplate: string = $('#cardTemplate').html();
    Mustache.parse(htmlTemplate);
    for (let cardId in variant.cards) {
        const card: CardJson = variant.cards[cardId];
        const cardCredits: number | undefined = selectedRules.creditReceived.get(cardId);
        const creditBarWidth: number = Math.round((cardCredits as number / maxCredits) * 100);
        let rendered: string = Mustache.render(htmlTemplate, {
            'cardId': cardId,
            'cardTitle': card.names[appOptions.language],
            'costNominal': card.costNominal,
            'costCurrent': card.costNominal,
            'creditBarWidth': creditBarWidth,
            'totalCredit': cardCredits
        });
        $('#cardList').append(rendered);

        const groupIconHtmlTemplate: string = $('#groupIconTemplate').html();
        Mustache.parse(groupIconHtmlTemplate);
        for (let group of Array.from(card.groups).reverse()) {
            const lowerCaseName: string = group.toString().toLowerCase();
            getLocalizedString('cards-group-' + lowerCaseName, function(localizedGroupName: string): void {
                let renderedIcon: string = Mustache.render(groupIconHtmlTemplate, {
                    'iconName': lowerCaseName,
                    'groupName': localizedGroupName
                });
                const iconDiv: JQuery<HTMLElement> = $('#card-' + cardId + ' .card-header');
                iconDiv.prepend(renderedIcon);
            });
        }
    }
}

function setActivePlayer(): void {
    // TODO Update dropdown menu to show the active player, and the other players in the dropdown for easy switching
}
