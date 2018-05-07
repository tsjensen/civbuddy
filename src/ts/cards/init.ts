import * as Mustache from 'mustache';

import { AbstractPageContext, AbstractPageInitializer, Page } from '../framework/framework';
import { CardData, Situation } from '../framework/model';
import { Util } from '../framework/util';
import { builtInVariants, Language, Rules, RulesJson } from '../rules/rules';
import { GameDao, GameDaoImpl, SituationDao } from '../storage/dao';
import { GameStorage, SituationStorage } from '../storage/storage';
import { ToggleCardsFilterActivity } from './activities';
import { CardController, FundsBarController, NavbarController } from './controllers';



/**
 * The page context object of the 'cards' page.
 */
export class CardsPageContext
    extends AbstractPageContext {

    constructor(
        public readonly selectedGame: GameDao,
        public readonly selectedRules: Rules,
        public readonly currentSituation: Situation,
        public readonly hoverHeaders: Map<string, boolean>,
        public readonly hoverCards: Map<string, boolean>) {
        super();
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
export class CardsPageInitializer
    extends AbstractPageInitializer<CardsPageContext> {

    constructor() {
        super(Page.CARDS, CardsPageInitializer.buildPageContext(), '#cardInfoModal');
    }

    private static buildPageContext(): CardsPageContext {
        const situationKey: string | null = Util.getUrlParameter('ctx');
        const sit: SituationDao | null = new SituationStorage().readSituation(situationKey);
        let result: CardsPageContext | null = null;
        if (sit !== null) {
            const game: GameDao | null = new GameStorage().readGame(sit.gameId);
            if (game !== null) {
                const variant: RulesJson = builtInVariants.get(game.variantKey) as RulesJson;
                const selectedRules: Rules = new Rules(variant, game.options);
                const currentSituation: Situation = new Situation(sit, selectedRules);
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
        Mustache.parse($('#cardTemplate').html());
        Mustache.parse($('#groupIconTemplate').html());
        Mustache.parse($('#cardInfoCreditItemTemplate').html());
    }

    protected pageLoaded(): void {
        const cardCtrl: CardController = new CardController(this.pageContext.selectedRules.cards,
            this.getAppOptions().language);
        cardCtrl.addGameIdToLinks(this.pageContext.selectedGame.key);
        cardCtrl.addSituationIdToLinks(this.pageContext.currentSituation.getId());
        cardCtrl.addJsHandlerToAnchors();

        const navbarCtrl: NavbarController = new NavbarController();
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        const optionDesc: string = GameDaoImpl.buildOptionDescriptor(variant,
            this.pageContext.selectedGame.options, this.getAppOptions().language);
        navbarCtrl.setGameName(this.pageContext.selectedGame.name);
        navbarCtrl.setVariantName((variant.displayNames as any)[this.getAppOptions().language]);
        navbarCtrl.setOptionDesc(optionDesc);
        this.populateCardsList(false);
        this.setupPlannedHoverEffect();
        document.title = this.pageContext.currentSituation.getPlayerName() + ' - '
            + this.pageContext.selectedGame.name + ' - CivBuddy';
        this.setActivePlayer();
        this.setupCardFiltering();
    }


    protected languageChanged(pPrevious: Language, pNew: Language): void {
        const navbarCtrl: NavbarController = new NavbarController();
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        navbarCtrl.setVariantName((variant.displayNames as any)[pNew]);
        navbarCtrl.setOptionDesc(
            GameDaoImpl.buildOptionDescriptor(variant, this.pageContext.selectedGame.options, pNew));
        this.populateCardsList(true);
        this.setupPlannedHoverEffect();
        new ToggleCardsFilterActivity(this.pageContext).applyCardsFilter();
    }


    private setupPlannedHoverEffect(): void {
        for (const cardId of Object.keys(this.pageContext.selectedRules.variant.cards)) {
            $('#card-' + cardId + ' div.card-combined-header').hover(
                () => {
                    this.pageContext.hoverHeaders.set(cardId, true);
                    CardController.handleCustomHover(this.pageContext, cardId);
                },
                () => {
                    this.pageContext.hoverHeaders.set(cardId, false);
                    CardController.handleCustomHover(this.pageContext, cardId);
                }
            );
            $('#card-' + cardId + ' > div.card-civbuddy').hover(
                () => {
                    this.pageContext.hoverCards.set(cardId, true);
                    CardController.handleCustomHover(this.pageContext, cardId);
                },
                () => {
                    this.pageContext.hoverCards.set(cardId, false);
                    CardController.handleCustomHover(this.pageContext, cardId);
                }
            );
        }
    }


    private setActivePlayer(): void {
        const navCtrl: NavbarController = new NavbarController();
        navCtrl.setCardCount(this.pageContext.currentSituation.getNumOwnedCards());
        navCtrl.setCardsLimit(this.pageContext.selectedRules.variant.cardLimit);
        navCtrl.setPointsTarget(this.pageContext.currentSituation.getPointsTarget());
        navCtrl.setScore(this.pageContext.currentSituation.getScore());
        navCtrl.updatePlayersDropdown(Page.CARDS, this.pageContext.currentSituation.getPlayerName(),
            Util.buildMap(this.pageContext.selectedGame.situations));
        const fundsCtrl: FundsBarController = new FundsBarController();
        fundsCtrl.setTotalAvailableFunds(this.pageContext.currentSituation.totalFundsAvailable);
    }


    private setupCardFiltering(): void {
        const navbarCtrl: NavbarController = new NavbarController();
        const activity: ToggleCardsFilterActivity = new ToggleCardsFilterActivity(this.pageContext);
        const filterEnabled: boolean = this.pageContext.currentSituation.isFilteringUseful();
        navbarCtrl.setFilterButtonEnabled(filterEnabled);
        if (!filterEnabled) {
            this.pageContext.currentSituation.setCardFilterActive(false);
            window.setTimeout(activity.saveSituation.bind(this), 100);
        }
        activity.applyCardsFilter();
    }


    private populateCardsList(pAddClickHandlers: boolean): void {
        const variant: RulesJson = this.pageContext.selectedRules.variant;
        this.pageContext.currentSituation.changeLanguage(this.getAppOptions().language);
        const htmlTemplate: string = $('#cardTemplate').html();
        const cardCtrl: CardController = new CardController(this.pageContext.selectedRules.cards,
            this.getAppOptions().language);
        const filterHint: JQuery<HTMLElement> = cardCtrl.detachFilterHint();
        cardCtrl.clearCardList();
        for (const cardId of Object.keys(variant.cards)) {
            const cardData: CardData = this.pageContext.currentSituation.getCard(cardId);
            cardCtrl.putCard(cardData, htmlTemplate, this.pageContext.selectedRules.maxCredits);
            if (pAddClickHandlers) {
                CardController.addButtonClickHandlers('#card-' + cardData.id);
            }
        }
        cardCtrl.reattachFilterHint(filterHint);
    }
}
