/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-11
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License Version 2 as published by the Free
 * Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.tj.civ.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;

import com.tj.civ.client.model.CbGame;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbVariantConfig;
import com.tj.civ.client.model.jso.CbGameJSO;
import com.tj.civ.client.model.jso.CbSituationJSO;
import com.tj.civ.client.model.jso.CbVariantConfigJSO;
import com.tj.civ.client.model.vo.CbGameVO;
import com.tj.civ.client.model.vo.CbVariantVO;


/**
 * Handles JSO persistence to HTML5 storage.
 *
 * @author Thomas Jensen
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
    private static Map<String, String> VariantNames = null;

    /** Types of keys in HTML5 local storage. */
    private enum KeyType { Game, Situation, Variant; }



    private static String createKey(final KeyType pKeyType)
    {
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
            Storage localStorage = Storage.getLocalStorage();
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
                            Window.alert("The game '" + gameJso.getName()
                                + "'\n is corrupt, because it is based on"
                                + "\nan unknown variant."
                                + "\nGame will be deleted.");
                            deleteGameCascading(key, gameJso);
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
        if (VariantNames == null) {
            loadVariantList();
        }
        String result = null;
        if (VariantNames != null) {
            result = VariantNames.get(pVariantKey);
        }
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
            Storage localStorage = Storage.getLocalStorage();

            if (VariantNames == null) {
                VariantNames = new HashMap<String, String>();
            }

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
                    VariantNames.put(key, vo.getVariantNameLocalized());
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
            Storage localStorage = Storage.getLocalStorage();
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
     * Load the entire game with the given persistence key.
     * @param pPersistenceKey the key to HTML5 storage
     * @return the loaded game, or <code>null</code> if no game was found.
     */
    public static CbGame loadGame(final String pPersistenceKey)
    {
        CbGame result = null;
        if (Storage.isSupported() && pPersistenceKey != null)
        {
            Storage localStorage = Storage.getLocalStorage();
            String item = localStorage.getItem(pPersistenceKey);
            if (item != null && item.length() > 0) {
                result = new CbGame(CbGameJSO.create(item));
                result.setPersistenceKey(pPersistenceKey);
            }
        }
        return result;
    }



    /**
     * Loads the complete game to which the given situation belongs. This is useful
     * for example when the 'Cards' place is invoked by bookmark and we have only a
     * situation persistence key.
     * @param pSituationKey the situation's persistence key
     * @return complete game, including variant and all situations, which are all
     *          loaded
     */
    public static CbGame loadGameForSituation(final String pSituationKey)
    {
        CbGame result = null;
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            int numItems = localStorage.getLength();
            for (int i = 0; i < numItems; i++)
            {
                String key = localStorage.key(i);
                if (key.startsWith(GAME_PREFIX)) {
                    String item = localStorage.getItem(key);
                    CbGameJSO gameJso = CbGameJSO.create(item);
                    if (gameJso.getPlayers().containsValue(pSituationKey)) {
                        result = new CbGame(gameJso);
                        result.setBackrefs();
                    }
                }
            }
        }
        return result;
    }



    /**
     * Saves the given game, replacing a game with the same key if present.
     * @param pGame a game to save
     */
    public static void saveGame(final CbGame pGame)
    {
        String key = pGame.getPersistenceKey();
        if (key == null) {
            key = createKey(KeyType.Game);
            pGame.setPersistenceKey(key);
        }
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            localStorage.setItem(key, pGame.toJson());
        }
    }



    /**
     * Rename an existing game.
     * @param pGameVO a game to save
     */
    public static void saveGame(final CbGameVO pGameVO)
    {
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            String key = pGameVO.getPersistenceKey();
            CbGame game = loadGame(key);
            game.setName(pGameVO.getGameName());
            localStorage.setItem(key, game.toJson());
        }
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
            Storage localStorage = Storage.getLocalStorage();
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
            Storage localStorage = Storage.getLocalStorage();
            localStorage.removeItem(pPersistenceKey);
        }
        LOG.exit("deleteItem"); //$NON-NLS-1$
    }



    /**
     * Performs a cascading delete on a game, which also takes care of all situations
     * that belong to the game.
     * @param pGameKey the game's persistence key (must not be <code>null</code>)
     * @param pGame the game JSO (must not be <code>null</code>)
     */
    public static void deleteGameCascading(final String pGameKey, final CbGameJSO pGame)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("deleteGameCascading",  //$NON-NLS-1$
                new String[]{"pGameKey", "pGame"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{pGameKey, pGame});
        }
        if (Storage.isSupported()) {
            LOG.detail("deleteGameCascading", "HTML5 storage ok"); //$NON-NLS-1$ //$NON-NLS-2$
            if (pGameKey != null && pGame != null) {
                Map<String, String> players = pGame.getPlayers();
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
     * Saves the situation object.
     * @param pSituation situation; if the situation already includes a persistence
     *          key, the situation with the same key is replaced in HTML5 storage.
     *          If no persistence key is present, a new one is created
     */
    public static void saveSituation(final CbSituation pSituation)
    {
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            String key = pSituation.getPersistenceKey();
            if (key == null) {
                key = createKey(KeyType.Situation);
                pSituation.setPersistenceKey(key);
            }
            localStorage.setItem(key, pSituation.toJson());
        }
    }



    /**
     * Load a situation from HTML5 storage.
     * @param pSitKey the situation's persistence key
     * @param pVariant the variant configuration
     * @return a new situation object
     */
    public static CbSituation loadSituation(final String pSitKey, final CbVariantConfig pVariant)
    {
        CbSituation result = null;
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
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
     */
    public static void saveVariant(final CbVariantConfig pVariant)
    {
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            String key = pVariant.getPersistenceKey();
            if (key == null) {
                key = createKey(KeyType.Variant);
                pVariant.setPersistenceKey(key);
            }
            localStorage.setItem(key, pVariant.toJson());
        }
    }
}
