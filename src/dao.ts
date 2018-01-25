/*
 * Data Access Objects used when reading from / persisting to local storage.
 */
import { Language } from './rules';



export interface GameDao
{
    /** key used to identify this game in local storage */
    key: string;

    /** chosen by user */
    name: string;

    /** ID of the variant, e.g. 'original' */
    variantKey: string;

    /** option ID to option value (actually Map<string, string>) */
    options: Object;

    /** player name to situation ID (actually Map<string, string>) */
    situations: Object;
}

export class GameDaoImpl implements GameDao {
    constructor(public key: string, public name: string, public variantKey: string, public options: Object,
        public situations: Object) { }
}



export interface SituationDao
{
    /** key used to identify this situation in local storage */
    key: string;

    /** key used to identify the game that this situation belongs to */
    gameId: string;

    player: PlayerDao;

    funds: FundsDao;

    /** cardIds of cards in state OWNED */
    ownedCards: Array<string>;
}

export class SituationDaoImpl implements SituationDao {
    constructor(public key: string, public gameId: string, public player: PlayerDao, public funds: FundsDao,
        public ownedCards: Array<string>) {}
}



export interface PlayerDao
{
    /** player name */
    readonly name: string;

    /** points target */
    readonly winningTotal: number;
}

export class PlayerDaoImpl implements PlayerDao {
    constructor(public readonly name: string, public readonly winningTotal: number) { }
}



export interface FundsDao
{
    bonus: number;

    /** commodity ID to number of commodity cards of that type (actually Map<string, number>) */
    commodities: Object;

    treasury: number;

    /** set if the player indicated that the bonus from the 'Mining' civilization card shall be used */
    wantsToUseMining: boolean;
}

export class FundsDaoImpl implements FundsDao {
    constructor(public bonus: number, public commodities: Object, public treasury: number,
        public wantsToUseMining: boolean) { }
}



/**
 * General application settings.
 */
export interface AppOptions
{
    /** the natural language in which to present the user interface */
    language: Language;
}

export class AppOptionsDao {
    constructor(public language: Language) { }
}
