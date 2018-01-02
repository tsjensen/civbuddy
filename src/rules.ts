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