import { v4 as newUuid } from 'uuid';
import { GameDao, AppOptions, AppOptionsDao, SituationDao, SituationDaoImpl } from './dao';
import { VariantDescriptor, builtInVariants, Language } from '../rules/rules';


/**
 * Flag set if the Browser supports localStorage, false otherwise. TODO use this
 */
export const isSupported: boolean = (() =>
{
    const testKey: string = '_civbuddy_dummy_';
    let readValue: string = '';
    try {
        window.localStorage.setItem(testKey, testKey);
        let readValue = window.localStorage.getItem(testKey);
        window.localStorage.removeItem(testKey);
        return testKey === readValue;
    } catch (e) {
        return false;
    }
})()


export enum StorageKeyType {
    GAME = 'CBG_',
    SITUATION = 'CBS_',
    VARIANT = 'CBV_',
    OPTIONS = 'CBO_'
}

export function newVariantKey(variantId: string): string {
    return StorageKeyType.VARIANT.toString() + variantId;
}

export function newGameKey(): string {
    return StorageKeyType.GAME.toString() + newUuid() + '_' + window.localStorage.length;
}

export function newSituationKey(): string {
    return StorageKeyType.SITUATION.toString() + newUuid() + '_' + window.localStorage.length;
}

const appOptionsKey: string = StorageKeyType.OPTIONS + 'Settings';


function hideFields(...pFieldsToHide: string[]): (pKey: string, pValue: any) => any
{
    return function(pKey: string, pValue: any)
    {
        if (pFieldsToHide.indexOf(pKey) >= 0) {
            return undefined;
        }
        return pValue;
    }
}

function getJsonElement(pElementName: string, pJson: Object): string {
    let result: string = '';
    if (pJson.hasOwnProperty(pElementName)) {
        result = pJson[pElementName];
    }
    return result;
}

function parseQuietly(pContent: string): Object {
    let json: Object = {};
    try {
        json = JSON.parse(pContent);
    } catch (e) {
        // ignore
    }
    return json;
}


export function purgeStorage(): void {
    const ls: Storage = window.localStorage;
    ls.clear();
}


/* ================================================================================================================
 *     GAMES
 * ============================================================================================================= */
// TODO properly wrap the contents of this .ts file into classes

export function readListOfGames(): GameDao[] {
    const ls: Storage = window.localStorage;
    let result: GameDao[] = [];
    for (let i = 0; i < ls.length; ++i) {
        let key: string | null = ls.key(i);
        let game: GameDao | null = readGame(key);
        if (game !== null) {
            result.push(game);
        }
    }
    return result;
}

export function deleteGame(pGameKey: string): void {
    const game: GameDao | null = readGame(pGameKey);
    const ls: Storage = window.localStorage;
    ls.removeItem(pGameKey);
    if (game !== null) {
        for (let playerName of Object.keys(game.situations)) {
            ls.removeItem(game.situations[playerName]);
        }
    }
}

export function saveGame(pGame: GameDao): void {
    const ls: Storage = window.localStorage;
    ls.setItem(pGame.key, JSON.stringify(pGame, hideFields("key")));
}

export function readGame(pGameKey: string | null): GameDao | null {
    const ls: Storage = window.localStorage;
    let result: GameDao | null = null;
    if (pGameKey !== null && pGameKey.startsWith(StorageKeyType.GAME.toString())) {
        let value: string | null = ls.getItem(pGameKey);
        if (value !== null) {
            result = <GameDao>JSON.parse(value);
            result.key = pGameKey;
        }
    }
    return result;
}


/* ================================================================================================================
 *     VARIANTS
 * ============================================================================================================= */

class VariantDescriptorImpl implements VariantDescriptor {
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

let variants: VariantDescriptor[] = (() =>
{
    const ls: Storage = window.localStorage;
    let result: VariantDescriptor[] = [];
    for (let i = 0; i < ls.length; ++i) {
        let key: string | null = ls.key(i);
        if (key !== null && key.startsWith(StorageKeyType.VARIANT.toString())) {
            let value: string | null = ls.getItem(key);
            if (value !== null) {
                const json: Object = parseQuietly(value);
                const variantId: string = getJsonElement('variantId', json);
                if (variantId.length > 0) {
                    result.push(new VariantDescriptorImpl(key, variantId));
                }
            }
        }
    }
    return result;
})();
export default variants;



export function ensureBuiltInVariants(): void {
    for(let variantId in builtInVariants) {
        const variantKey: string = newVariantKey(variantId);
        let currentContent: string | null = window.localStorage.getItem(variantKey);
        if (currentContent === null || currentContent.length === 0) {
            window.localStorage.setItem(variantKey, JSON.stringify(builtInVariants[variantId]));
            console.log('Variant \'' + variantId + '\' stored in localStorage as \'' + variantKey + '\'');
        }
     }
}


/* ================================================================================================================
 *     SITUATIONS
 * ============================================================================================================= */

export function createSituation(pGame: GameDao, pSituation: SituationDao): void {
    saveSituation(pSituation);
    saveGame(pGame);
}

export function saveSituation(pSituation: SituationDao): void {
    const ls: Storage = window.localStorage;
    ls.setItem(pSituation.key, JSON.stringify(pSituation, hideFields("key")));
}

export function readSituationsForGame(pGame: GameDao): SituationDao[] {
    let result: SituationDao[] = [];
    for (let playerName of Object.keys(pGame.situations)) {
        let situation: SituationDao | null = readSituation(pGame.situations[playerName]);
        if (situation !== null) {
            result.push(situation);
        }
    }
    return result;
}

export function readSituation(pSituationKey: string | null): SituationDao | null {
    const ls: Storage = window.localStorage;
    let result: SituationDao | null = null;
    if (pSituationKey !== null && pSituationKey.startsWith(StorageKeyType.SITUATION.toString())) {
        let value: string | null = ls.getItem(pSituationKey);
        if (value !== null) {
            result = <SituationDao>JSON.parse(value);
            result.key = pSituationKey;
        }
    }
    return result;
}

export function deleteSituation(pGame: GameDao, pSituationKey: string): void {
    const ls: Storage = window.localStorage;
    removeSituationFromGame(pGame, pSituationKey);
    saveGame(pGame);
    ls.removeItem(pSituationKey);
}

function removeSituationFromGame(pGame: GameDao, pSituationKey: string): void {
    for (let playerName of Object.keys(pGame.situations)) {
        if (pGame.situations[playerName] === pSituationKey) {
            delete pGame.situations[playerName];
            break;
        }
    }
}


/* ================================================================================================================
 *     GLOBAL APPLICATION OPTIONS
 * ============================================================================================================= */

export function readOptions(): AppOptions {
    const ls: Storage = window.localStorage;
    let result: AppOptions = buildDefaultOptions();
    let value: string | null = ls.getItem(appOptionsKey);
    if (value !== null) {
        const json: Object = parseQuietly(value);
        const languageStr: string = getJsonElement('language', json);
        const langEnum: Language = Language[languageStr.toUpperCase()];
        if (languageStr.length > 0 && typeof(langEnum) !== 'undefined') {
            result = new AppOptionsDao(langEnum);
        }
    }
    console.log("Read application options: " + JSON.stringify(result));
    return result;
}

function buildDefaultOptions(): AppOptions {
    return new AppOptionsDao(Language.EN);
}

export function writeOptions(pAppOptions: AppOptions): void {
    const ls: Storage = window.localStorage;
    const value: string = JSON.stringify(pAppOptions);
    ls.setItem(appOptionsKey, value);
    console.log('Global application options stored in localStorage as \'' + appOptionsKey + '\' = ' + value);
}
