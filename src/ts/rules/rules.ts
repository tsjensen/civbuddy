import * as jsonAdvanced from '../../../resources/rules/advanced.json';
import * as jsonOriginal from '../../../resources/rules/original.json';
import * as jsonOriginalWe from '../../../resources/rules/original_we.json';

export enum Language {
    DE = 'de',
    EN = 'en'
}

export enum CardGroup {
    ARTS = 'Arts',
    CIVICS = 'Civics',
    CRAFTS = 'Crafts',
    RELIGION = 'Religion',
    SCIENCES = 'Sciences'
}

export interface CommodityJson {
    /** display names of the commodity for each supported language (actually Map<Language, string>) */
    names: object;

    base: number;

    /** Maximum number of commodity cards that can be owned of this type.
     *  Even the 'Mining' card cannot extend this limit. */
    maxCount: number;

    /** Flag indicating that this commodity is wine to which the western expansion pack's special rules
     *  for wine apply. Default is false. */
    wine?: boolean;

    /** Determines if this card is basically eligible for the bonus granted by the 'Mining' card from
     *  the 'Advanced Civilization' game variant. Default is false.*/
    mineable?: boolean;
}

export interface CardJson {
    /** name of the card as printed on the physical card (actually Map<Language, string>) */
    readonly names: object;

    /** this card's list price */
    readonly costNominal: number;

    readonly prereq?: string | null;

    /** textual description of the card attributes (actually Map<Language, string>) */
    readonly attributes: object;

    /** Flag indicating if this is a civilization card which grants the "mining bonus", which can be invoked on
     *  metal commodities in Advanced Civilization in order to increase yield. false if missing. */
    readonly grantsMiningBonus?: boolean;

    /** textual description of the card's effects on calamities (actually Map<Language, string>) */
    readonly calamityEffects: object;

    readonly groups: CardGroup[];

    /** which credits this card provides to other cards (actually Map<string, number>,
     *  which is a map from target card ID to credit points) */
    readonly creditGiven: object;
}

export enum RuleOptionUiElement {
    CHECKBOX = 'checkbox'
}

export interface RuleOptionJson {
    id: string;
    displayNames: Map<Language, string>;
    type: RuleOptionUiElement;
    defaultValue: string;
    explanation: Map<Language, string>;
    shortText: Map<string, Map<Language, string>>;
}

export interface RulesJson {
    variantId: string;
    /** name of the variant as used on the 'games' page (actually Map<Language, string>) */
    displayNames: object;
    version: number;
    format: number;
    cardLimit?: number;
    url?: string;
    targetOpts: number[];

    /** The civilization cards used in this game variant (actually Map<string, CardJson>) */
    cards: object;

    /** the commodity cards in the player's possession (actually Map<string, CommodityJson>,
     *  the string being the commodity ID) */
    commodities: object;

    options: RuleOptionJson[];
}



export const builtInVariants: Map<string, RulesJson> = buildMapOfBuiltInVariants();

function buildMapOfBuiltInVariants(): Map<string, RulesJson> {
    const result: Map<string, RulesJson> = new Map();
    result.set('original', jsonOriginal as any);
    result.set('original_we', jsonOriginalWe as any);
    result.set('advanced', jsonAdvanced as any);
    return result;
}


export class Card {
    /** the card ID */
    public readonly id: string;

    /** reference to the data from the variant JSON */
    public readonly dao: CardJson;

    /** the cards which give credits to this one (map from cardId to credit points) */
    public readonly creditsReceived: Map<string, number> = new Map();

    /** the maximum number of credit points which this card can receive */
    public readonly maxCreditsReceived: number;

    /** the maximum number of credit points which this card can provide */
    public readonly maxCreditsProvided: number;


    constructor(pCardId: string, pDao: CardJson, pCreditsReceived: Map<string, number>) {
        this.id = pCardId;
        this.dao = pDao;
        this.creditsReceived = pCreditsReceived;
        this.maxCreditsReceived = this.calculateMaxCreditsReceived(pCreditsReceived);
        this.maxCreditsProvided = this.calculateMaxCreditsProvided(pDao.creditGiven);
    }


    private calculateMaxCreditsReceived(pCreditsReceived: Map<string, number>): number {
        let result: number = 0;
        for (const v of pCreditsReceived.values()) {
            result += v;
        }
        return result;
    }


    private calculateMaxCreditsProvided(pCreditsProvided: object): number {
        let result: number = 0;
        for (const targetCardId of Object.keys(pCreditsProvided)) {
            result += (pCreditsProvided as any)[targetCardId];
        }
        return result;
    }
}



/**
 * Wraps a {@link RulesJson} to add logic that works on the (unmodified) variant description file.
 */
export class Rules {
    /** the wrapped variant JSON */
    public readonly variant: RulesJson;

    /** cardId -> card */
    public readonly cards: Map<string, Card> = new Map();

    /** the highest credit received by any card */
    public readonly maxCredits: number;

    private readonly cardIdsByNominalValue: string[];

    private readonly cardsWithPrereqs: string[];

    private readonly prereqCardIds: string[];

    /** Flag indicating if these rules know a civilization card which grants the "mining bonus", which can be invoked
     *  on metal commodities in Advanced Civilization in order to increase yield. */
    public readonly miningBonusPossible: boolean;

    /** Flag indicating if the rule options are chosen by the user so that credits apply to multiple cards purchased in
     *  the same turn (true) or not (false). For example, if set, Mysticism will provide credits for both Music and
     *  Medicine in the same turn. If unset, only the credits for Medicine will be used (because they are greater).
     *  If the rules do not define this option, true is used. */
    public readonly ruleOptionCardMultiUse: boolean;


    constructor(pVariant: RulesJson, pGameOptions: object) {
        this.variant = pVariant;
        this.cards = this.buildCardsMap();
        this.maxCredits = this.calculateMaxCredits(this.cards);
        this.cardIdsByNominalValue = this.getCardIdsSortedByNominalValue(pVariant);
        this.cardsWithPrereqs = this.buildCardsWithPrereqs(pVariant);
        this.prereqCardIds = this.buildPrereqCardIds(pVariant);
        this.miningBonusPossible = this.determinePossibleMining(pVariant);
        this.ruleOptionCardMultiUse = this.determineRuleOptionCardMultiUse(pGameOptions);
        // more rule options should be handled here
    }

    private determineRuleOptionCardMultiUse(pGameOptions: object): boolean {
        let result: boolean = true;
        if (pGameOptions.hasOwnProperty('cardMultiUse')) {
            const v: string = (pGameOptions as any)['cardMultiUse'];  // tslint:disable-line:no-string-literal
            result = v === 'true';
        }
        return result;
    }

    private buildCardsMap(): Map<string, Card> {
        const invertedCredits: Map<string, Map<string, number>> = new Map();
        for (const cardId of Object.keys(this.variant.cards)) {
            invertedCredits.set(cardId, new Map());
        }
        for (const sourceCardId of Object.keys(this.variant.cards)) {
            const sourceCard: CardJson = (this.variant.cards as any)[sourceCardId];
            for (const targetCardId of Object.keys(sourceCard.creditGiven)) {
                const m: Map<string, number> = invertedCredits.get(targetCardId) as Map<string, number>;
                m.set(sourceCardId, (sourceCard.creditGiven as any)[targetCardId]);
            }
        }

        const result: Map<string, Card> = new Map();
        for (const cardId of Object.keys(this.variant.cards)) {
            const card: Card = new Card(cardId, (this.variant.cards as any)[cardId],
                invertedCredits.get(cardId) as Map<string, number>);
            result.set(cardId, card);
        }
        return result;
    }


    private calculateMaxCredits(pCards: Map<string, Card>): number {
        let result: number = 0;
        for (const card of pCards.values()) {
            if (card.maxCreditsReceived > result) {
                result = card.maxCreditsReceived;
            }
        }
        return result;
    }

    private buildCardsWithPrereqs(pVariant: RulesJson): string[] {
        const result: string[] = [];
        for (const cardId of Object.keys(pVariant.cards)) {
            if (typeof (((pVariant.cards as any)[cardId] as CardJson).prereq) === 'string') {
                result.push(cardId);
            }
        }
        return result;
    }

    /** Get the card IDs of cards which have a prereq card. */
    public getCardsWithPrereqs(): string[] {
        return this.cardsWithPrereqs;
    }


    private buildPrereqCardIds(pVariant: RulesJson): string[] {
        const coll: Set<string> = new Set();
        for (const cardId of Object.keys(pVariant.cards)) {
            const prereq = ((pVariant.cards as any)[cardId] as CardJson).prereq;
            if (typeof (prereq) === 'string') {
                coll.add(prereq);
            }
        }
        return Array.from(coll);
    }

    /** Get the IDs of cards which are prereq cards for other cards. */
    public getPrereqCardIds(): string[] {
        return this.prereqCardIds;
    }


    private getCardIdsSortedByNominalValue(pVariant: RulesJson): string[] {
        const result: string[] = Object.keys(pVariant.cards);
        result.sort((cardId1: string, cardId2: string) => {
            const nomVal1: number = ((pVariant.cards as any)[cardId1] as CardJson).costNominal;
            const nomVal2: number = ((pVariant.cards as any)[cardId2] as CardJson).costNominal;
            return nomVal1 - nomVal2;
        });
        return result;
    }

    /** Get all card IDs, sorted by their card's nominal values. */
    public getCardIdsByNominalValue(): string[] {
        return Array.from(this.cardIdsByNominalValue);
    }


    private determinePossibleMining(pVariant: RulesJson): boolean {
        let result: boolean = false;
        for (const cardId of Object.keys(pVariant.cards)) {
            if ((pVariant.cards as any)[cardId].grantsMiningBonus) {
                result = true;
                break;
            }
        }
        return result;
    }


    public getNominalValue(pCardId: string): number {
        return (this.cards.get(pCardId) as Card).dao.costNominal;
    }


    public hasPrereq(pCardId: string): boolean {
        return typeof (this.getPrereq(pCardId)) === 'string';
    }

    public getPrereq(pCardId: string): string | undefined {
        const p: string | undefined | null = (this.cards.get(pCardId) as Card).dao.prereq;
        if (typeof (p) === 'string') {
            return p;
        }
        return undefined;
    }
}
