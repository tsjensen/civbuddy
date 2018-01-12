import { VariantDescriptor, Language } from './rules';

/*
 * Data Transfer Objects used when reading from / persisting to local storage.
 */

 export interface GameDto {
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

 export class GameDtoImpl implements GameDto {
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

 export interface SituationDto {
    /** key used to identify this situation in local storage */
    key: string;
    /** key used to identify the game that this situation belongs to */
    gameId: string;
    player: PlayerDto;
    funds: FundsDto;
    /** cardIds of cards in state OWNED */
    ownedCards: Array<string>;
 }

 export class SituationDtoImpl implements SituationDto {
    key: string;
    gameId: string;
    player: PlayerDto;
    funds: FundsDto;
    ownedCards: Array<string>;

    constructor(pKey: string, pGameId: string, pPlayer: PlayerDto, pFunds: FundsDto, pCardStates: Array<string>) {
        this.key = pKey;
        this.gameId = pGameId;
        this.player = pPlayer;
        this.funds = pFunds;
        this.ownedCards = pCardStates;
    }
 }

 export interface PlayerDto {
     name: string;
     winningTotal: number;
 }

 export class PlayerDtoImpl implements PlayerDto {
    name: string;
    winningTotal: number;

    constructor(pName: string, pWinningTotal: number) {
        this.name = pName;
        this.winningTotal = pWinningTotal;
    }
 }

 export interface FundsDto {
     bonus: number;
     /** commodity ID to number of commodity cards of that type (actually Map<string, number>) */
     commodities: Object;
     treasury: number;
 }

 export class FundsDtoImpl implements FundsDto {
    bonus: number;
    commodities: Object;
    treasury: number;
    
    constructor(pBonus: number, pCommodities: Object, pTreasury: number) {
        this.bonus = pBonus;
        this.commodities = pCommodities;
        this.treasury = pTreasury;
    }
 }

 export enum State {
     OWNED = "O",
     PLANNED = "P",
     ABSENT = "A",
     DISCOURAGED = "D",
     PREREQ_FAILED = "F",
     UNAFFORDABLE = "U"
 }

 export class VariantDescriptorDto implements VariantDescriptor {
     variantId: string;
     persistenceKey: string;

     /**
      * Constructor.
      * @param pPersistenceKey the key in browser local storage
      * @param pVariantId the ID of the variant (e.g. 'original', or 'original_we')
      */
     constructor(pPersistenceKey: string, pVariantId: string) {
          this.persistenceKey = pPersistenceKey;
          this.variantId = pVariantId;
     }
 }

 export interface AppOptions {
    language: Language;
 }

 export class AppOptionsDto {
    language: Language;

    constructor(pLanguage: Language) {
        this.language = pLanguage;
    }
 }