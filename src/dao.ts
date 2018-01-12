/*
 * Data Access Objects used when reading from / persisting to local storage.
 */
import { VariantDescriptor, Language } from './rules';



 export interface GameDao {
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
    key: string; 
    name: string;
    variantKey: string;
    options: Object;
    situations: Object;

    constructor(pKey: string, pName: string, pVariantKey: string, pOptions: Object, pSituations: Object) {
        this.key = pKey;
        this.name = pName;
        this.variantKey = pVariantKey;
        this.options = pOptions;
        this.situations = pSituations;
    }
}

 export interface SituationDao {
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
    key: string;
    gameId: string;
    player: PlayerDao;
    funds: FundsDao;
    ownedCards: Array<string>;

    constructor(pKey: string, pGameId: string, pPlayer: PlayerDao, pFunds: FundsDao, pCardStates: Array<string>) {
        this.key = pKey;
        this.gameId = pGameId;
        this.player = pPlayer;
        this.funds = pFunds;
        this.ownedCards = pCardStates;
    }
 }

 export interface PlayerDao {
     name: string;
     winningTotal: number;
 }

 export class PlayerDaoImpl implements PlayerDao {
    name: string;
    winningTotal: number;

    constructor(pName: string, pWinningTotal: number) {
        this.name = pName;
        this.winningTotal = pWinningTotal;
    }
 }

 export interface FundsDao {
     bonus: number;
     /** commodity ID to number of commodity cards of that type (actually Map<string, number>) */
     commodities: Object;
     treasury: number;
 }

 export class FundsDaoImpl implements FundsDao {
    bonus: number;
    commodities: Object;
    treasury: number;
    
    constructor(pBonus: number, pCommodities: Object, pTreasury: number) {
        this.bonus = pBonus;
        this.commodities = pCommodities;
        this.treasury = pTreasury;
    }
 }


 export interface AppOptions {
    language: Language;
 }

 export class AppOptionsDao {
    language: Language;

    constructor(pLanguage: Language) {
        this.language = pLanguage;
    }
 }