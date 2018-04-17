import { v4 as newUuid } from 'uuid';

import { builtInVariants, Language, RulesJson, VariantDescriptor } from '../rules/rules';
import { AppOptions, AppOptionsDao, GameDao, SituationDao } from './dao';


/**
 * Flag set if the Browser supports localStorage, false otherwise. TODO use this
 */
export const isSupported: boolean = (() =>
{
    const testKey: string = '_civbuddy_dummy_';
    try {
        window.localStorage.setItem(testKey, testKey);
        const readValue = window.localStorage.getItem(testKey);
        window.localStorage.removeItem(testKey);
        return testKey === readValue;
    } catch (e) {
        return false;
    }
})();


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
    };
}

function getJsonElement(pElementName: string, pJson: object): string {
    let result: string = '';
    if (pJson.hasOwnProperty(pElementName)) {
        result = (<any> pJson)[pElementName];
    }
    return result;
}

function parseQuietly(pContent: string): object {
    let json: object = {};
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
    const result: GameDao[] = [];
    for (let i = 0; i < ls.length; ++i) {
        const key: string | null = ls.key(i);
        const game: GameDao | null = readGame(key);
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
        for (const playerName of Object.keys(game.situations)) {
            ls.removeItem((<any> game.situations)[playerName]);
        }
    }
}

export function saveGame(pGame: GameDao): void {
    const ls: Storage = window.localStorage;
    ls.setItem(pGame.key, JSON.stringify(pGame, hideFields('key')));
}

export function readGame(pGameKey: string | null): GameDao | null {
    const ls: Storage = window.localStorage;
    let result: GameDao | null = null;
    if (pGameKey !== null && pGameKey.startsWith(StorageKeyType.GAME.toString())) {
        const value: string | null = ls.getItem(pGameKey);
        if (value !== null) {
            result = JSON.parse(value) as GameDao;
            result.key = pGameKey;
        }
    }
    return result;
}


/* ================================================================================================================
 *     VARIANTS
 * ============================================================================================================= */

class VariantDescriptorImpl
    implements VariantDescriptor
{
    /**
     * Constructor.
     * @param persistenceKey the key in browser local storage
     * @param variantId the ID of the variant (e.g. 'original', or 'original_we')
     */
    constructor(public readonly persistenceKey: string, public readonly variantId: string) {}
}

const variants: VariantDescriptor[] = (() =>
{
    const ls: Storage = window.localStorage;
    const result: VariantDescriptor[] = [];
    for (let i = 0; i < ls.length; ++i) {
        const key: string | null = ls.key(i);
        if (key !== null && key.startsWith(StorageKeyType.VARIANT.toString())) {
            const value: string | null = ls.getItem(key);
            if (value !== null) {
                const json: object = parseQuietly(value);
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
    for (const variantId in builtInVariants) {
        if (builtInVariants.hasOwnProperty(variantId)) {
            const variantKey: string = newVariantKey(variantId);
            const currentContent: string | null = window.localStorage.getItem(variantKey);
            if (currentContent === null || currentContent.length === 0) {
                window.localStorage.setItem(variantKey, JSON.stringify(builtInVariants.get(variantId) as RulesJson));
                console.log('Variant \'' + variantId + '\' stored in localStorage as \'' + variantKey + '\'');
            }
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
    ls.setItem(pSituation.key, JSON.stringify(pSituation, hideFields('key')));
}

export function readSituationsForGame(pGame: GameDao): SituationDao[] {
    const result: SituationDao[] = [];
    for (const playerName of Object.keys(pGame.situations)) {
        const situation: SituationDao | null = readSituation((<any> pGame.situations)[playerName]);
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
        const value: string | null = ls.getItem(pSituationKey);
        if (value !== null) {
            result = JSON.parse(value) as SituationDao;
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
    for (const playerName of Object.keys(pGame.situations)) {
        if ((<any> pGame.situations)[playerName] === pSituationKey) {
            delete (<any> pGame.situations)[playerName];
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
    const value: string | null = ls.getItem(appOptionsKey);
    if (value !== null) {
        const json: object = parseQuietly(value);
        const languageStr: string = getJsonElement('language', json);
        const langEnum: Language = Language[languageStr.toUpperCase() as keyof typeof Language];
        if (languageStr.length > 0) {
            result = new AppOptionsDao(langEnum);
        }
    }
    console.log('Read application options: ' + JSON.stringify(result));
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
