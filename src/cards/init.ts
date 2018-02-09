import * as Mustache from 'mustache';
import * as storage from '../storage/storage';
import { SituationDao, GameDao } from '../storage/dao';
import { getUrlParameter, showElement, hideElement, buildMap } from '../util';
import { builtInVariants, RulesJson, Rules } from '../rules/rules';
import { appOptions } from '../main';
import { Situation, CardData } from '../model';
import { CardController, NavbarController, FundsBarController } from './controllers';
import { PageContext, AbstractPageInitializer, Page } from '../framework';
import { ToggleCardsFilterActivity } from './activities';


/**
 * The page context object of the 'cards' page.
 */
export class CardsPageContext implements PageContext {
    constructor(
        public readonly selectedGame: GameDao,
        public readonly selectedRules: Rules,
        public readonly currentSituation: Situation,
        public readonly hoverHeaders: Map<string, boolean>,
        public readonly hoverCards: Map<string, boolean>) {
    }

    public hoversOnCard(pCardId: string): boolean {
        const isOnHeader: boolean = Boolean(this.hoverHeaders.get(pCardId));
        const isOnCard: boolean = Boolean(this.hoverCards.get(pCardId));
        return isOnCard && !isOnHeader;
    }
}


/**
 * The page initializer of the 'cards' page.
 */
export class CardsPageInitializer extends AbstractPageInitializer<CardsPageContext>
{
    constructor() {
        super(Page.CARDS, CardsPageInitializer.buildPageContext(), '#cardInfoModal');
    }

    private static buildPageContext(): CardsPageContext {
        const situationKey: string | null = getUrlParameter('ctx');
        const sit: SituationDao | null = storage.readSituation(situationKey);
        let result: CardsPageContext | null = null;
        if (sit !== null) {
            const game: GameDao | null = storage.readGame(sit.gameId);
            if (game != null) {
                const variant: RulesJson = builtInVariants[game.variantKey];
                let selectedRules: Rules = new Rules(variant);
                let currentSituation: Situation = new Situation(sit, buildMap(game.options), selectedRules);
                currentSituation.recalculate();
                result = new CardsPageContext(game, selectedRules, currentSituation, new Map(), new Map());
            }
        }
        if (result === null) {
            window.location.replace('index.html');
        }
        return result as CardsPageContext;
    }

    protected parseTemplates(): void {
        Mustache.parse($('#flagTemplate').html());
        Mustache.parse($('#cardTemplate').html());
        Mustache.parse($('#groupIconTemplate').html());
        Mustache.parse($('#cardInfoCreditItemTemplate').html());
        Mustache.parse($('#switchPlayerLinkTemplate').html());
    }

    protected pageLoaded(): void {
        this.addGameIdToLinks();
        const navbarCtrl: NavbarController = new NavbarController();
        navbarCtrl.setGameName(this.pageContext.selectedGame.name);
        navbarCtrl.setVariantName(this.pageContext.selectedRules.variant.displayNames[appOptions.language]);
        this.populateCardsList(false);
        this.setupPlannedHoverEffect();
        document.title = this.pageContext.currentSituation.getPlayerName() + ' - ' + this.pageContext.selectedGame.name + ' - CivBuddy';
        this.setActivePlayer();
        new ToggleCardsFilterActivity(this.pageContext).applyCardsFilter();
    }

    protected languageChanged(): void {
        const navbarCtrl: NavbarController = new NavbarController();
        navbarCtrl.setVariantName(this.pageContext.selectedRules.variant.displayNames[appOptions.language]);
        this.populateCardsList(true);
        this.setupPlannedHoverEffect();
        new ToggleCardsFilterActivity(this.pageContext).applyCardsFilter();
    }


    private setupPlannedHoverEffect(): void {
        for (let cardId of Object.keys(this.pageContext.selectedRules.variant.cards)) {
            $('#card-' + cardId + ' div.card-combined-header').hover(
                () => { this.pageContext.hoverHeaders.set(cardId, true); this.hoverHandler(cardId); },
                () => { this.pageContext.hoverHeaders.set(cardId, false); this.hoverHandler(cardId); }
            );
            $('#card-' + cardId + ' > div.card-civbuddy').hover(
                () => { this.pageContext.hoverCards.set(cardId, true); this.hoverHandler(cardId); },
                () => { this.pageContext.hoverCards.set(cardId, false); this.hoverHandler(cardId); }
            );
        }
    }
    
    private hoverHandler(pCardId: string): void {
        const cardElem: JQuery<HTMLElement> = $('#card-' + pCardId + ' > div.card-civbuddy');
        if (this.pageContext.hoversOnCard(pCardId)) {
            cardElem.addClass('hovered');
        } else {
            cardElem.removeClass('hovered');
        }
    }

    private addGameIdToLinks(): void {
        $('a.add-game-id').attr('href', 'players.html?ctx=' + this.pageContext.selectedGame.key);
    }

    private setActivePlayer(): void {
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.setCardCount(this.pageContext.currentSituation.getNumOwnedCards());
        navCtrl.setCardsLimit(this.pageContext.selectedRules.variant.cardLimit);
        navCtrl.setPointsTarget(this.pageContext.currentSituation.getPointsTarget());
        navCtrl.setScore(this.pageContext.currentSituation.getScore());
        navCtrl.updatePlayersDropdown(this.pageContext.currentSituation.getPlayerName(),
                buildMap(this.pageContext.selectedGame.situations));
        const fundsCtrl: FundsBarController = new FundsBarController();
        fundsCtrl.setTotalAvailableFunds(this.pageContext.currentSituation.getTotalFunds());
    }


    private populateCardsList(pUpdateLanguageTexts: boolean): void {
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        if (pUpdateLanguageTexts) {
            this.pageContext.currentSituation.changeLanguage(appOptions.language);
        }
        let htmlTemplate: string = $('#cardTemplate').html();
        const filterHint: JQuery<HTMLElement> = $('#filterHint').detach();
        $('#cardList > div').remove();
        const ctrl: CardController = new CardController(this.pageContext.selectedRules.cards, appOptions.language);
        for (let cardId of Object.keys(variant.cards)) {
            const cardData: CardData = this.pageContext.currentSituation.getCard(cardId);
            ctrl.putCard(cardData, htmlTemplate, this.pageContext.selectedRules.maxCredits);
        }
        $('#cardList').append(filterHint);
    }
}
