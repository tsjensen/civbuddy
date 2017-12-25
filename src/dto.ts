import { VariantDescriptor, Language } from './rules';

/*
 * Data Transfer Objects used when reading from / persisting to local storage.
 */

 export interface GameDto {
     name: string;                      // chosen by user
     variantKey: string;                // ID of the variant, e.g. 'original'
     options: Map<string, string>;      // option ID to option value
     situations: Map<string, string>;   // player name to situation ID
 }

 export interface SituationDto {
    player: PlayerDto;
    funds: FundsDto;
    cardStates: Map<string, State>;
 }

 export interface PlayerDto {
     name: string;
     winningTotal: number;
 }

 export interface FundsDto {
     bonus: number;
     commodities: Map<string, number>;
     treasury: number;
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