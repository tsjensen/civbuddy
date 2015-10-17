/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-11
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import org.civbuddy.client.event.CbGameLoadedEvent;
import org.civbuddy.client.model.CbGame;
import org.civbuddy.client.model.CbSituation;
import org.civbuddy.client.model.CbVariantConfig;
import org.civbuddy.client.model.jso.CbGameJSO;
import org.civbuddy.client.model.jso.CbSituationJSO;
import org.civbuddy.client.model.jso.CbVariantConfigJSO;
import org.civbuddy.client.model.vo.CbGameVO;
import org.civbuddy.client.model.vo.CbVariantVO;


/**
 * Handles JSO persistence to HTML5 storage.
 * 
 * <p>TODO Check if it suffices to check for local storage, as session storage
 * does not work if loading the page from file, a frequent development situation.
 *
 * @author Thomas Jensen
 * @see Storage
 */
public final class CbStorage
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbStorage.class);

    /** prefix for everything stored by this app */
    private static final String APP_PREFIX = "CB"; //$NON-NLS-1$

    /** prefix for keys to game objects */
    private static final String GAME_PREFIX = APP_PREFIX + "G_"; //$NON-NLS-1$

    /** prefix for keys to situation objects */
    private static final String SIT_PREFIX = APP_PREFIX + "S_"; //$NON-NLS-1$

    /** prefix for keys to variant objects */
    public static final String VARIANT_PREFIX = APP_PREFIX + "V_"; //$NON-NLS-1$

    /** Maps variant persistence keys to localized variant names */
    private static Map<String, String> sVariantNames = new HashMap<String, String>();

    /** Types of keys in HTML5 local storage. */
    private enum KeyType { Game, Situation, Variant; }



    private static String createKey(final KeyType pKeyType)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("createKey",  //$NON-NLS-1$
                new String[]{"pKeyType"}, new Object[]{pKeyType});  //$NON-NLS-1$
        }
        String result = null;

        switch(pKeyType) {
            case Game:
                result = GAME_PREFIX;
                break;
            case Situation:
                result = SIT_PREFIX;
                break;
            case Variant:
                result = VARIANT_PREFIX;
                break;
            default:
                throw new IllegalArgumentException(
                    "unknown key type: " + pKeyType); //$NON-NLS-1$
        }
        result += CbUtil.getUuid();

        LOG.exit("createKey", result); //$NON-NLS-1$
        return result;
    }



    /**
     * Loads the list of games for direct display in the 'Games' view.
     * @return a list of 'game' view objects
     */
    public static List<CbGameVO> loadGameList()
    {
        List<CbGameVO> result = new ArrayList<CbGameVO>();
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            boolean retry = false;
            do {
                retry = false;
                result.clear();
                int numItems = localStorage.getLength();
                for (int i = 0; i < numItems; i++)
                {
                    String key = localStorage.key(i);
                    if (key.startsWith(GAME_PREFIX)) {
                        String item = localStorage.getItem(key);
                        CbGameJSO gameJso = CbGameJSO.create(item);
                        String variantName = getVariantNameLoc(gameJso.getVariantKey());
                        if (variantName != null) {
                            CbGameVO vo = new CbGameVO(key, gameJso.getName(), variantName);
                            result.add(vo);
                        } else {
                            Window.alert(CbConstants.MESSAGES.gameCorruptNoVariant(
                                gameJso.getName()));
                            deleteGameCascading(key);
                            retry = true;    // too many keys just went up in smoke
                            break;
                        }
                    }
                }
            } while (retry);
        }
        return result;
    }



    private static String getVariantNameLoc(final String pVariantKey)
    {
        if (sVariantNames.isEmpty()) {
            loadVariantList();
        }
        String result = sVariantNames.get(pVariantKey);
        return result;
    }



    /**
     * Loads the list of variants for direct display in the 'Variants' view.
     * @return a list of 'variant' view objects
     */
    public static List<CbVariantVO> loadVariantList()
    {
        List<CbVariantVO> result = new ArrayList<CbVariantVO>();
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();

            int numItems = localStorage.getLength();
            for (int i = 0; i < numItems; i++)
            {
                String key = localStorage.key(i);
                if (key.startsWith(VARIANT_PREFIX)) {
                    String item = localStorage.getItem(key);
                    CbVariantConfigJSO variantJso = CbUtil.createFromJson(item);
                    CbVariantVO vo = new CbVariantVO(key, variantJso.getVariantId(),
                        variantJso.getLocalizedDisplayName(), variantJso.getVariantVersion());
                    result.add(vo);
                    sVariantNames.put(key, vo.getVariantNameLocalized());
                }
            }
        }
        return result;
    }



    /**
     * Loads a variant.
     * @param pVariantKey the variant's persistence key
     * @return the complete variant
     */
    public static CbVariantConfig loadVariant(final String pVariantKey)
    {
        CbVariantConfig result = null;
        if (Storage.isSupported() && pVariantKey != null)
        {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            String item = localStorage.getItem(pVariantKey);
            if (item != null && item.length() > 0) {
                CbVariantConfigJSO jso = CbUtil.createFromJson(item);
                result = new CbVariantConfig(jso);
                result.setPersistenceKey(pVariantKey);
            }
        }
        return result;
    }



    /**
     * Private constructor.
     */
    private CbStorage()
    {
        super();
    }



    /**
     * Makes sure that the game to which the situation with the given persistence key
     * belongs is loaded into {@link CbGlobal} as the currently active game, and
     * the situation with the given key is set as the current situation.
     * <p>Should this be impossible (e.g. because the key does not exist),
     * {@link CbGlobal} is <em>cleared</em>.
     * @param pSitKey the situation's persistence key (may be <code>null</code>)
     * @param pEventBus event bus to fire the event (must <b>not</b> be <code>null</code>)
     */
    public static void ensureGameLoadedWithSitKey(final String pSitKey,
        final EventBus pEventBus)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("ensureGameLoadedWithSitKey",  //$NON-NLS-1$
                new String[]{"pSitKey"}, new Object[]{pSitKey});  //$NON-NLS-1$
        }

        if (pSitKey == null || pSitKey.length() == 0) {
            // no key given
            CbGlobal.clearGame();
            LOG.exit("ensureGameLoadedWithSitKey"); //$NON-NLS-1$
            return;
        }

        CbSituation sit = null;
        if (CbGlobal.isGameSet()) {
            sit = CbGlobal.getGame().getSituationByKey(pSitKey);
        }
        if (sit != null) {
            // it's the game we already have
            LOG.debug("ensureGameLoadedWithSitKey", //$NON-NLS-1$
                "Using globally present game"); //$NON-NLS-1$
            CbGlobal.getGame().setCurrentSituation(sit);
        }
        else {
            // it's a different game which we must load first
            LOG.debug("ensureGameLoadedWithSitKey", //$NON-NLS-1$
                "Loading game from DOM storage"); //$NON-NLS-1$
            CbGlobal.clearGame();
            try {
                CbStorage.loadGameForSituation(pSitKey, pEventBus);
                if (CbGlobal.isGameSet()) {
                    sit = CbGlobal.getGame().getSituationByKey(pSitKey);
                    if (sit != null) {
                        CbGlobal.getGame().setCurrentSituation(sit);
                    }
                }
            }
            catch (Throwable t) {
                LOG.error("ensureGameLoadedWithSitKey(): " //$NON-NLS-1$
                    + t.getClass().getName() + ": " + t.getMessage(), t); //$NON-NLS-1$
                Window.alert(CbConstants.STRINGS.error() + ' ' + t.getMessage());
            }
        }
        
        if (CbGlobal.isGameSet() && CbGlobal.getCurrentSituation() == null) {
            CbGlobal.clearGame();  // must have a current sit
        }

        LOG.exit("ensureGameLoadedWithSitKey"); //$NON-NLS-1$
    }



    /**
     * Makes sure that the game with the given persistence key is loaded into
     * {@link CbGlobal} as the currently active game.
     * <p>Should this be impossible (e.g. because the key does not exist),
     * {@link CbGlobal} is <em>cleared</em>.
     * <p>The current situation is set to <code>null</code> (so no situation is
     * selected).
     * @param pGameKey the game's persistence key (may be <code>null</code>)
     * @param pEventBus event bus to fire the event (must <b>not</b> be <code>null</code>)
     */
    public static void ensureGameLoadedWithGameKey(final String pGameKey,
        final EventBus pEventBus)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("ensureGameLoadedWithGameKey",  //$NON-NLS-1$
                new String[]{"pGameKey"}, new Object[]{pGameKey});  //$NON-NLS-1$
        }
        if (pGameKey == null || pGameKey.length() == 0) {
            // no key given
            CbGlobal.clearGame();
            LOG.exit("ensureGameLoadedWithGameKey"); //$NON-NLS-1$
            return;
        }

        boolean hasChanged = false;
        if (CbGlobal.isGameSet() && pGameKey.equals(CbGlobal.getGame().getPersistenceKey()))
        {
            // it's the game we already have
            LOG.debug("ensureGameLoadedWithSitKey", //$NON-NLS-1$
                "Using globally present game"); //$NON-NLS-1$
            CbGlobal.getGame().setCurrentSituation(null);
        }
        else {
            // it's a different game which we must load first
            LOG.debug("ensureGameLoadedWithSitKey", //$NON-NLS-1$
                "Loading game from DOM storage"); //$NON-NLS-1$
            CbGlobal.clearGame();
            hasChanged = true;
            try {
                CbGame game = loadGame(pGameKey);
                CbGlobal.setGame(game);
            }
            catch (Throwable t) {
                LOG.error("ensureGameLoadedWithSitKey(): " //$NON-NLS-1$
                    + t.getClass().getName() + ": " + t.getMessage(), t); //$NON-NLS-1$
                Window.alert(CbConstants.STRINGS.error() + ' ' + t.getMessage());
            }
        }
        
        if (hasChanged) {
            pEventBus.fireEvent(new CbGameLoadedEvent());
        }
        LOG.exit("ensureGameLoadedWithGameKey"); //$NON-NLS-1$
    }



    /**
     * Load the entire game with the given persistence key.
     * <p>The result is stored in {@link CbGlobal}.
     * @param pPersistenceKey the key to HTML5 storage
     * @return the fully loaded game
     */
    private static CbGame loadGame(final String pPersistenceKey)
    {
        CbGame game = null;
        if (Storage.isSupported() && pPersistenceKey != null) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            String item = localStorage.getItem(pPersistenceKey);
            if (item != null && item.length() > 0)
            {
                // game object
                CbGameJSO gameJso = CbGameJSO.create(item);
                game = new CbGame(gameJso);
                game.setPersistenceKey(pPersistenceKey);

                // variant object
                CbVariantConfig variant = CbStorage.loadVariant(gameJso.getVariantKey());
                game.setVariant(variant);

                // situation objects
                Map<String, CbSituation> sits = new TreeMap<String, CbSituation>();
                if (gameJso.getPlayers() != null) {
                    for (Entry<String, String> entry : gameJso.getPlayers().entrySet()) {
                        String playerName = entry.getKey();
                        String sitKey = entry.getValue();
                        CbSituation sit = loadSituation(sitKey, variant);
                        if (sit != null) {
                            sit.setGame(game);
                            sits.put(playerName, sit);
                        }
                    }
                }
                game.setSituations(sits);
                game.setCurrentSituation(null);
            }
        }
        return game;
    }



    /**
     * Loads the complete game to which the given situation belongs, including
     * variant and all situations, which are all loaded. This is useful
     * for example when the 'Cards' place is invoked by bookmark and we have only a
     * situation persistence key.
     * <p>The result is stored in {@link CbGlobal}.
     * @param pSituationKey the situation's persistence key
     * @param pEventBus event bus to fire the event (must <b>not</b> be <code>null</code>)
     */
    private static void loadGameForSituation(final String pSituationKey, final EventBus pEventBus)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("loadGameForSituation",  //$NON-NLS-1$
                new String[]{"pSituationKey"}, new Object[]{pSituationKey});  //$NON-NLS-1$
        }
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            int numItems = localStorage.getLength();
            for (int i = 0; i < numItems; i++)
            {
                String key = localStorage.key(i);
                if (key.startsWith(GAME_PREFIX)) {
                    String item = localStorage.getItem(key);
                    CbGameJSO gameJso = CbGameJSO.create(item);
                    if (gameJso.getPlayers().containsValue(pSituationKey)) {
                        CbGame game = loadGame(key);
                        CbGlobal.setGame(game);
                        pEventBus.fireEvent(new CbGameLoadedEvent());
                        break;
                    }
                }
            }
        }
        LOG.exit("loadGameForSituation"); //$NON-NLS-1$
    }



    /**
     * Saves the globally active game, replacing a game with the same key if present.
     */
    public static void saveGame()
    {
        LOG.enter("saveGame"); //$NON-NLS-1$
        if (CbGlobal.isGameSet()) {
            String key = CbGlobal.getGame().getPersistenceKey();
            if (key == null) {
                key = createKey(KeyType.Game);
                CbGlobal.getGame().setPersistenceKey(key);
            }
            if (Storage.isSupported()) {
                Storage localStorage = Storage.getLocalStorageIfSupported();
                localStorage.setItem(key, CbGlobal.getGame().toJson());
            }
        }
        LOG.exit("saveGame"); //$NON-NLS-1$
    }



    /**
     * Rename an existing game.
     * @param pGameVO a game to save, must contain a persistence key and the new
     *              name to set
     */
    public static void renameGame(final CbGameVO pGameVO)
    {
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            String key = pGameVO.getPersistenceKey();
            loadGame(key);
            CbGame game = CbGlobal.getGame();
            game.setName(pGameVO.getGameName());
            localStorage.setItem(key, game.toJson());
        }
    }



    /**
     * Scan all games in storage for a game with the given name.
     * @param pGameName the game name to look for
     * @return <code>true</code> if a game with the given name is found
     */
    public static boolean gameExists(final String pGameName)
    {
        boolean result = false;
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            for (int i = 0; i < localStorage.getLength(); i++)
            {
                String key = localStorage.key(i);
                if (key.startsWith(GAME_PREFIX)) {
                    String item = localStorage.getItem(key);
                    CbGameJSO gameJso = CbGameJSO.create(item);
                    if (gameJso != null && gameJso.getName().equalsIgnoreCase(pGameName)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }



    /**
     * Creates a new game in HTML5 storage which was not there before.
     * @param pGameVO a fully set game VO, but without a persistence key
     * @param pVariantKey the variant key
     * @return the game's new persistence key
     */
    public static String saveNewGame(final CbGameVO pGameVO, final String pVariantKey)
    {
        String result = null;
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            String key = createKey(KeyType.Game);
            CbGameJSO gameJso = CbGameJSO.create();
            gameJso.setName(pGameVO.getGameName());
            gameJso.setVariantKey(pVariantKey);
            CbGame game = new CbGame(gameJso);
            localStorage.setItem(key, game.toJson());
            pGameVO.setPersistenceKey(key);
            result = key;
        }
        return result;
    }



    /**
     * Removes the given item from HTML5 local storage.
     * @param pPersistenceKey the item's persistence key
     */
    public static void deleteItem(final String pPersistenceKey)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("deleteItem",  //$NON-NLS-1$
                new String[]{"pPersistenceKey"},  //$NON-NLS-1$
                new Object[]{pPersistenceKey});
        }
        if (Storage.isSupported()) {
            LOG.detail("deleteItem", "HTML5 storage ok"); //$NON-NLS-1$ //$NON-NLS-2$
            Storage localStorage = Storage.getLocalStorageIfSupported();
            localStorage.removeItem(pPersistenceKey);
        }
        LOG.exit("deleteItem"); //$NON-NLS-1$
    }



    /**
     * Performs a cascading delete on a game, which also takes care of all situations
     * that belong to the game.
     * @param pGameKey the game's persistence key (must not be <code>null</code>)
     */
    public static void deleteGameCascading(final String pGameKey)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("deleteGameCascading",  //$NON-NLS-1$
                new String[]{"pGameKey"}, new Object[]{pGameKey});  //$NON-NLS-1$
        }
        if (Storage.isSupported()) {
            LOG.detail("deleteGameCascading", "HTML5 storage ok"); //$NON-NLS-1$ //$NON-NLS-2$
            CbGame game = loadGame(pGameKey);
            if (pGameKey != null && game != null) {
                Map<String, String> players = game.getJso().getPlayers();
                if (players != null) {
                    for (String sitKey : players.values()) {
                        deleteItem(sitKey);
                    }
                }
                deleteItem(pGameKey);
            }
        }
        LOG.exit("deleteGameCascading"); //$NON-NLS-1$
    }



    /**
     * Saves the globally active current situation object (which must include a
     * persistence key). A situation with the same key is replaced in HTML5 storage.
     */
    public static void saveSituation()
    {
        saveSituationInternal(null);
    }



    /**
     * Saves a new situation object.
     * @param pSituation situation; if the situation already includes a persistence
     *          key, the situation with the same key is replaced in HTML5 storage.
     *          If no persistence key is present, a new key is created.
     */
    public static void saveNewSituation(final CbSituation pSituation)
    {
        saveSituationInternal(pSituation);
    }



    /**
     * Saves the situation object.
     * @param pSituation situation; if the situation already includes a persistence
     *          key, the situation with the same key is replaced in HTML5 storage.
     *          If no persistence key is present, a new key is created.
     *          <p>If <code>null</code>, the globally active current situation is
     *          saved.
     */
    private static void saveSituationInternal(final CbSituation pSituation)
    {
        LOG.enter("saveSituation"); //$NON-NLS-1$
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            CbSituation sit = pSituation != null ? pSituation : CbGlobal.getCurrentSituation();
            if (sit != null) {
                String key = sit.getPersistenceKey();
                if (key == null) {
                    key = createKey(KeyType.Situation);
                    sit.setPersistenceKey(key);
                }
                String json = sit.toJson();
                if (LOG.isDetailEnabled()) {
                    LOG.detail("saveSituation", //$NON-NLS-1$
                        "Persisting " + key + " -> " + json); //$NON-NLS-1$ //$NON-NLS-2$
                }
                localStorage.setItem(key, json);
            }
        }
        LOG.exit("saveSituation"); //$NON-NLS-1$
    }



    /**
     * Load a situation from HTML5 storage.
     * @param pSitKey the situation's persistence key
     * @param pVariant the variant configuration
     * @return a new situation object
     */
    private static CbSituation loadSituation(final String pSitKey, final CbVariantConfig pVariant)
    {
        CbSituation result = null;
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            String json = localStorage.getItem(pSitKey);
            if (json != null && json.length() > 0) {
                CbSituationJSO sitJso = CbSituationJSO.create(json);
                result = new CbSituation(sitJso, pVariant);
                result.setPersistenceKey(pSitKey);
                result.evaluateJsoState(sitJso); // again
            }
        }
        return result;
    }



    /**
     * Saves the variant object.
     * @param pVariant game variant; if the variant already includes a persistence
     *          key, the variant with the same key is replaced in HTML5 storage.
     *          If no persistence key is present, a new one is created
     * @return the saved variant's persistence key
     */
    public static String saveVariant(final CbVariantConfig pVariant)
    {
        String result = null;
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorageIfSupported();
            String key = pVariant.getPersistenceKey();
            if (key == null) {
                key = createKey(KeyType.Variant);
                pVariant.setPersistenceKey(key);
            }
            localStorage.setItem(key, pVariant.toJson());
            result = key;
        }
        return result;
    }
}
