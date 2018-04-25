import { Language, RulesJson } from '../rules/rules';

/*
 * Data Access Objects used when reading from / persisting to local storage.
 */


export interface GameDao {

    /** key used to identify this game in local storage */
    key: string;

    /** chosen by user */
    name: string;

    /** ID of the variant, e.g. 'original' */
    variantKey: string;

    /** option ID to option value (actually Map<string, string>) */
    options: object;

    /** player name to situation ID (actually Map<string, string>) */
    situations: object;
}


export class GameDaoImpl implements GameDao {

    constructor(public key: string, public name: string, public variantKey: string, public options: object,
        public situations: object) { }

    public static buildOptionDescriptor(pVariant: RulesJson, pOptionValues: object, pLanguage: Language): string {
        let result: string = '';
        if (pVariant.options.length > 0) {
            for (const option of pVariant.options) {
                let v: string | undefined = (pOptionValues as any)[option.id];
                if (typeof (v) === 'undefined' || v.length === 0) {
                    v = option.defaultValue;
                }
                const shortText: string = (option.shortText as any)[v][pLanguage];
                if (result.length > 0) {
                    result += ', ';
                }
                result += shortText;
            }
        }
        if (result.length === 0) {
            result = '--';
        }
        return result;
    }
}



export interface SituationDao {

    /** key used to identify this situation in local storage */
    key: string;

    /** key used to identify the game that this situation belongs to */
    gameId: string;

    player: PlayerDao;

    funds: FundsDao;

    /** card filter active or not */
    filtered?: boolean;

    /** cardIds of cards in state OWNED */
    ownedCards: string[];
}


export class SituationDaoImpl implements SituationDao {
    constructor(public key: string, public gameId: string, public player: PlayerDao, public funds: FundsDao,
        public ownedCards: string[]) { }
}



export interface PlayerDao {

    /** player name */
    readonly name: string;

    /** points target */
    readonly winningTotal: number;
}

export class PlayerDaoImpl implements PlayerDao {
    constructor(public readonly name: string, public readonly winningTotal: number) { }
}



export interface FundsDao {

    /** commodity ID to number of commodity cards of that type (actually Map<string, number>) */
    commodities: object;

    treasury: number;

    /** set if the player indicated that the bonus from the 'Mining' civilization card shall be used */
    wantsToUseMining: boolean;
}

export class FundsDaoImpl implements FundsDao {
    constructor(public commodities: object, public treasury: number, public wantsToUseMining: boolean) { }
}



/**
 * General application settings.
 */
export interface AppOptions {
    /** the natural language in which to present the user interface */
    language: Language;
}

export class AppOptionsDao {
    constructor(public language: Language) { }
}
