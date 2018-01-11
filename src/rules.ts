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
    names: Map<Language, string>;
    costNominal: number;
    prereq?: string;
    attributes: Map<Language, string>;
    calamityEffects: Map<Language, string>;
    groups: Array<CardGroup>;
    creditGiven: Map<string, number>;
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
    displayNames: Map<Language, string>;
    version: number;
    format: number;
    cardLimit?: number;
    url?: string;
    targetOpts: Array<number>;
    cards: Map<string, CardJson>;
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



/**
 * Wraps a {@link RulesJson} to add logic that works on the unmodified variant description file.
 */
export class Rules
{
    /** the wrapped variant JSON */
    public readonly variant: RulesJson;

    /** lists the credits *received* by each card, a map from cardId to (cardId to credit given) */
    public readonly invertedCredits: Map<string, Map<string, number>>;

    /** the total possible credit received by each card */
    public readonly creditReceived: Map<string, number>;

    /** the highest credit received by any card */
    public readonly maxCredits: number;


    constructor (pVariant: RulesJson) {
        this.variant = pVariant;
        this.invertedCredits = this.invertCredits();
        this.creditReceived = new Map();
        this.maxCredits = this.calculateMaxCredits(this.invertedCredits);
    }


    private invertCredits(): Map<string, Map<string, number>> {
        let result: Map<string, Map<string, number>> = new Map();
        for (let cardId in this.variant.cards) {
            result.set(cardId, new Map());
        }
        for (let sourceCardId in this.variant.cards) {
            let sourceCard: CardJson = this.variant.cards[sourceCardId];
            for (let targetCardId in sourceCard.creditGiven) {
                let m: Map<string, number> | undefined = result.get(targetCardId);
                if (m !== undefined) {
                    m.set(sourceCardId, sourceCard.creditGiven[targetCardId]);
                }
            }
        }
        return result;
    }


    private calculateMaxCredits(pInvertedCredits: Map<string, Map<string, number>>): number {
        let result: number = 0;
        for (let [cardId, cm] of pInvertedCredits) {
            let sum: number = 0;
            for (let v of cm.values()) {
                sum += v;
            }
            this.creditReceived.set(cardId, sum);
            if (sum > result) {
                result = sum;
            }
        }
        return result;
    }
}
