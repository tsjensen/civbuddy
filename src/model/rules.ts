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
    defaultValue: any;
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
