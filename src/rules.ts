import * as jsonOriginal from './rules/original.json';
import * as jsonOriginalWe from './rules/original_we.json';
import * as jsonAdvanced from './rules/advanced.json';

export enum Language {
    DE = "de",
    EN = "en"
}

export enum CardGroup {
    ARTS = "Arts",
    CIVICS = "Civics",
    CRAFTS = "Crafts",
    RELIGION = "Religion",
    SCIENCES = "Sciences"
}

export interface CommodityJson {
    id: string;
    names: Map<Language, string>;
    base: number;
    maxCount: number;
}

export interface CardJson {
    /** name of the card as printed on the physical card (actually Map<Language, string>) */
    names: Object;
    /** this card's list price */
    costNominal: number;
    prereq?: string;
    /** textual description of the card attributes (actually Map<Language, string>) */
    attributes: Object;
    /** textual description of the card's effects on calamities (actually Map<Language, string>) */
    calamityEffects: Object;
    groups: Array<CardGroup>;
    /** which credits this card provides to other cards (actually Map<string, number>, which is a map from target card ID to credit points) */
    creditGiven: Object;
}

export enum RuleOptionUiElement {
    CHECKBOX = "checkbox"
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
    /** name of the variant as used on the 'Games' page (actually Map<Language, string>) */
    displayNames: Object;
    version: number;
    format: number;
    cardLimit?: number;
    url?: string;
    targetOpts: Array<number>;
    /** The civilization cards used in this game variant (actually Map<string, CardJson>) */
    cards: Object;
    commodities: Array<CommodityJson>;
    options: Array<RuleOptionJson>;
}

/**
 * Describes a rules file found in localStorage. The {@link persistenceKey} may be used to load it.
 */
export interface VariantDescriptor {
    variantId: string;
    persistenceKey: string;
}


export const builtInVariants: Map<string, RulesJson> = buildMapOfBuiltInVariants();

function buildMapOfBuiltInVariants(): Map<string, RulesJson> {
    let result: Map<string, RulesJson> = new Map<string, RulesJson>();
    result['original'] = <any>jsonOriginal;
    result['original_we'] = <any>jsonOriginalWe;
    result['advanced'] = <any>jsonAdvanced;
    return result;
}


export class Card
{
    /** the card ID */
    public readonly id: string;

    /** reference to the data from the variant JSON */
    public readonly dao: CardJson;

    /** the cards which give credits to this one (map from cardId to credit points) */
    public readonly creditsReceived: Map<string, number> = new Map();

    /** the maximum number of credit points which this card can receive */
    public readonly maxCredits: number;


    constructor(pCardId: string, pDao: CardJson, pCreditsReceived: Map<string, number>) {
        this.id = pCardId;
        this.dao = pDao;
        this.creditsReceived = pCreditsReceived;
        this.maxCredits = this.calculateMaxCredits(pCreditsReceived);
    }


    private calculateMaxCredits(pCreditsReceived: Map<string, number>): number {
        let result: number = 0;
        for (let v of pCreditsReceived.values()) {
            result += v;
        }
        return result;
    }
}



/**
 * Wraps a {@link RulesJson} to add logic that works on the (unmodified) variant description file.
 */
export class Rules
{
    /** the wrapped variant JSON */
    public readonly variant: RulesJson;

    /** cardId -> card */
    public readonly cards: Map<string, Card> = new Map();

    /** the highest credit received by any card */
    public readonly maxCredits: number;


    constructor (pVariant: RulesJson) {
        this.variant = pVariant;
        this.cards = this.buildCardsMap();
        this.maxCredits = this.calculateMaxCredits(this.cards);
    }


    private buildCardsMap(): Map<string, Card> {
        const invertedCredits: Map<string, Map<string, number>> = new Map();
        for (let cardId of Object.keys(this.variant.cards)) {
            invertedCredits.set(cardId, new Map());
        }
        for (let sourceCardId of Object.keys(this.variant.cards)) {
            let sourceCard: CardJson = this.variant.cards[sourceCardId];
            for (let targetCardId of Object.keys(sourceCard.creditGiven)) {
                let m: Map<string, number> = invertedCredits.get(targetCardId) as Map<string, number>;
                m.set(sourceCardId, sourceCard.creditGiven[targetCardId]);
            }
        }

        const result: Map<string, Card> = new Map();
        for (let cardId of Object.keys(this.variant.cards)) {
            const card: Card = new Card(cardId, this.variant.cards[cardId], invertedCredits.get(cardId) as Map<string, number>);
            result.set(cardId, card);
        }
        return result;
    }


    private calculateMaxCredits(pCards: Map<string, Card>): number {
        let result: number = 0;
        for (let card of pCards.values()) {
            if (card.maxCredits > result) {
                result = card.maxCredits;
            }
        }
        return result;
    }
}
