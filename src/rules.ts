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
    names: Map<Language, string>;
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
    readonly names: Object;
    /** this card's list price */
    readonly costNominal: number;
    readonly prereq?: string;
    /** textual description of the card attributes (actually Map<Language, string>) */
    readonly attributes: Object;
    /** textual description of the card's effects on calamities (actually Map<Language, string>) */
    readonly calamityEffects: Object;
    readonly groups: Array<CardGroup>;
    /** which credits this card provides to other cards (actually Map<string, number>, which is a map from target card ID to credit points) */
    readonly creditGiven: Object;
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
    /** name of the variant as used on the 'games' page (actually Map<Language, string>) */
    displayNames: Object;
    version: number;
    format: number;
    cardLimit?: number;
    url?: string;
    targetOpts: Array<number>;
    /** The civilization cards used in this game variant (actually Map<string, CardJson>) */
    cards: Object;
    /** the commodity cards in the player's possession (actually Map<string, CommodityJson>, the string being the commodity ID) */
    commodities: Object;
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
        for (let v of pCreditsReceived.values()) {
            result += v;
        }
        return result;
    }


    private calculateMaxCreditsProvided(pCreditsProvided: Object): number {
        let result: number = 0;
        for (let targetCardId of Object.keys(pCreditsProvided)) {
            result += pCreditsProvided[targetCardId];
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
            if (card.maxCreditsReceived > result) {
                result = card.maxCreditsReceived;
            }
        }
        return result;
    }
}
