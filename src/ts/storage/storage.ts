import { v4 as newUuid } from 'uuid';

import { Util } from '../framework/util';
import { builtInVariants, Language, RulesJson } from '../rules/rules';
import { AppOptions, AppOptionsDao, GameDao, SituationDao } from './dao';



/**
 * The kinds of entities that we have in local storage, with their key prefixes.
 */
export enum StorageKeyType {
    GAME = 'CBG_',
    SITUATION = 'CBS_',
    VARIANT = 'CBV_',
    OPTIONS = 'CBO_'
}



export class StorageSupport {

    public isLocalStorageUsed(): boolean {
        return $('html[localStorageUsed="false"]').length === 0;
    }

    /**
     * Make sure that local storage is supported by the current browser. If not, redirect to error page.
     * If the HTML page contains an attribute `localStorageUsed="false"` on the topmost `<html>` element, then the
     * check is considered ok regardless.
     */
    public ensureSupported(): void {
        if (this.isLocalStorageUsed() && !this.isSupported()) {
            window.location.replace('error.html');
        }
    }

    /**
     * Flag set if the Browser supports localStorage, false otherwise.
     */
    private isSupported(): boolean {
        const testKey: string = '_civbuddy_dummy_';
        try {
            window.localStorage.setItem(testKey, testKey);
            const readValue = window.localStorage.getItem(testKey);
            window.localStorage.removeItem(testKey);
            return testKey === readValue;
        } catch (e) {
            return false;
        }
    }

    public newVariantKey(variantId: string): string {
        return StorageKeyType.VARIANT.toString() + variantId;
    }

    public newGameKey(): string {
        return StorageKeyType.GAME.toString() + newUuid() + '_' + window.localStorage.length;
    }

    public newSituationKey(): string {
        return StorageKeyType.SITUATION.toString() + newUuid() + '_' + window.localStorage.length;
    }

    public getAppOptionsKey(): string {
        return StorageKeyType.OPTIONS + 'Settings';
    }


    public purgeStorage(): void {
        const ls: Storage = window.localStorage;
        ls.clear();
    }
}



/* ================================================================================================================
 *     GAMES
 * ============================================================================================================= */

export class GameStorage {

    public readListOfGames(): GameDao[] {
        const ls: Storage = window.localStorage;
        const result: GameDao[] = [];
        for (let i = 0; i < ls.length; ++i) {
            const key: string | null = ls.key(i);
            const game: GameDao | null = this.readGame(key);
            if (game !== null) {
                result.push(game);
            }
        }
        return result;
    }

    public deleteGame(pGameKey: string): void {
        const game: GameDao | null = this.readGame(pGameKey);
        const ls: Storage = window.localStorage;
        ls.removeItem(pGameKey);
        if (game !== null) {
            for (const playerName of Object.keys(game.situations)) {
                ls.removeItem((game.situations as any)[playerName]);
            }
        }
    }

    public saveGame(pGame: GameDao): void {
        const ls: Storage = window.localStorage;
        ls.setItem(pGame.key, JSON.stringify(pGame, Util.hideFields('key')));
    }

    public readGame(pGameKey: string | null): GameDao | null {
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
}



/* ================================================================================================================
 *     VARIANTS
 * ============================================================================================================= */

export class VariantStorage {

    public ensureBuiltInVariants(): void {
        if (new StorageSupport().isLocalStorageUsed()) {
            const ls: Storage = window.localStorage;
            for (const variantId in builtInVariants) {
                if (builtInVariants.hasOwnProperty(variantId)) {
                    const variantKey: string = new StorageSupport().newVariantKey(variantId);
                    const currentContent: string | null = ls.getItem(variantKey);
                    if (currentContent === null || currentContent.length === 0) {
                        ls.setItem(variantKey, JSON.stringify(builtInVariants.get(variantId) as RulesJson));
                        console.log('Variant \'' + variantId + '\' stored in localStorage as \'' + variantKey + '\'');
                    }
                }
            }
        }
    }
}



/* ================================================================================================================
 *     SITUATIONS
 * ============================================================================================================= */

export class SituationStorage {

    public createSituation(pGame: GameDao, pSituation: SituationDao): void {
        this.saveSituation(pSituation);
        new GameStorage().saveGame(pGame);
    }

    public saveSituation(pSituation: SituationDao): void {
        const ls: Storage = window.localStorage;
        ls.setItem(pSituation.key, JSON.stringify(pSituation, Util.hideFields('key')));
    }

    public readSituationsForGame(pGame: GameDao): SituationDao[] {
        const result: SituationDao[] = [];
        for (const playerName of Object.keys(pGame.situations)) {
            const situation: SituationDao | null = this.readSituation((pGame.situations as any)[playerName]);
            if (situation !== null) {
                result.push(situation);
            }
        }
        return result;
    }

    public readSituation(pSituationKey: string | null): SituationDao | null {
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

    public deleteSituation(pGame: GameDao, pSituationKey: string): void {
        const ls: Storage = window.localStorage;
        this.removeSituationFromGame(pGame, pSituationKey);
        new GameStorage().saveGame(pGame);
        ls.removeItem(pSituationKey);
    }

    private removeSituationFromGame(pGame: GameDao, pSituationKey: string): void {
        for (const playerName of Object.keys(pGame.situations)) {
            if ((pGame.situations as any)[playerName] === pSituationKey) {
                delete (pGame.situations as any)[playerName];
                break;
            }
        }
    }
}



/* ================================================================================================================
 *     GLOBAL APPLICATION OPTIONS
 * ============================================================================================================= */

export class GlobalOptions {

    private static appOptions: AppOptions = GlobalOptions.buildDefaultOptions();

    private static buildDefaultOptions(): AppOptions {
        return new AppOptionsDao(Language.EN);
    }

    public readOptions(): void {
        const ls: Storage = window.localStorage;
        let result: AppOptions = GlobalOptions.buildDefaultOptions();
        const value: string | null = ls.getItem(new StorageSupport().getAppOptionsKey());
        if (value !== null) {
            const json: object = Util.parseQuietly(value);
            const languageStr: string = Util.getJsonElement('language', json);
            const langEnum: Language = Language[languageStr.toUpperCase() as keyof typeof Language];
            if (languageStr.length > 0) {
                result = new AppOptionsDao(langEnum);
            }
        }
        console.log('Read application options: ' + JSON.stringify(result));
        GlobalOptions.appOptions = result;
    }

    public get(): AppOptions {
        return GlobalOptions.appOptions;
    }

    public writeOptions(): void {
        const ls: Storage = window.localStorage;
        const storageKey: string = new StorageSupport().getAppOptionsKey();
        const value: string = JSON.stringify(GlobalOptions.appOptions);
        ls.setItem(storageKey, value);
        console.log('Global application options stored in localStorage as \'' + storageKey + '\' = ' + value);
    }
}
