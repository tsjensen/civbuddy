/*
 * CivCounsel - A Civilization Tactics Guide
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

import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.CcVariantConfig;
import com.tj.civ.client.model.CcVariantConfigMock;
import com.tj.civ.client.model.jso.CcGameJSO;
import com.tj.civ.client.model.jso.CcSituationJSO;
import com.tj.civ.client.model.vo.CcGameVO;
import com.tj.civ.client.model.vo.CcVariantVO;


/**
 * Handles JSO persistence to HTML5 storage.
 *
 * @author Thomas Jensen
 */
public final class CcStorage
{
    /** prefix for everything stored by this app */
    private static final String APP_PREFIX = "CB"; //$NON-NLS-1$

    /** prefix for keys to game objects */
    private static final String GAME_PREFIX = APP_PREFIX + "G_"; //$NON-NLS-1$

    /** prefix for keys to situation objects */
    private static final String SIT_PREFIX = APP_PREFIX + "S_"; //$NON-NLS-1$

    /** prefix for keys to variant objects */
    private static final String VARIANT_PREFIX = APP_PREFIX + "V_"; //$NON-NLS-1$

    /** Maps variant IDs to localized variant names */
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
        result += CcUtil.getUuid();
        return result;
    }



    /**
     * Loads the list of games for direct display in the 'Games' view.
     * @return a list of 'game' view objects
     */
    public static List<CcGameVO> loadGameList()
    {
        List<CcGameVO> result = new ArrayList<CcGameVO>();
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            int numItems = localStorage.getLength();
            for (int i = 0; i < numItems; i++)
            {
                String key = localStorage.key(i);
                if (key.startsWith(GAME_PREFIX)) {
                    String item = localStorage.getItem(key);
                    CcGameJSO gameJso = CcGameJSO.create(item);
                    CcGameVO vo = new CcGameVO(key, gameJso.getName(),
                        getVariantNameLoc(gameJso.getVariantId()));
                    result.add(vo);
                }
            }
        }
        return result;
    }



    private static String getVariantNameLoc(final String pVariantId)
    {
        if (VariantNames == null) {
            loadVariantList();
        }
        String result = null;
        if (VariantNames != null) {
            result = VariantNames.get(pVariantId);
        }
        if (result == null) {
            result = pVariantId + " (unknown)";  // TODO: stattdessen Fehler anzeigen?
        }
        return result;
    }



    /**
     * Loads the list of variants for direct display in the 'Variants' view.
     * @return a list of 'variant' view objects
     */
    public static List<CcVariantVO> loadVariantList()
    {
        List<CcVariantVO> result = new ArrayList<CcVariantVO>();
        // TODO as soon as we stop using the mock variants
        CcVariantConfigMock mock = new CcVariantConfigMock();
        result.add(new CcVariantVO(mock.getVariantId(), mock.getLocalizedDisplayName()));

        if (VariantNames == null) {
            VariantNames = new HashMap<String, String>();
        }
        VariantNames.put(mock.getVariantId(), mock.getLocalizedDisplayName());

        return result;
    }



    /**
     * Loads a variant.
     * @param pVariantId the variant ID
     * @return the complete variant
     */
    public static CcVariantConfig loadVariant(final String pVariantId)
    {
        // TODO as soon as we stop using the mock variants
        return new CcVariantConfigMock();
    }



    /**
     * Private constructor.
     */
    private CcStorage()
    {
        super();
    }



    /**
     * Load the entire game with the given persistence key.
     * @param pPersistenceKey the key to HTML5 storage
     * @return the loaded game, or <code>null</code> if no game was found.
     */
    public static CcGame loadGame(final String pPersistenceKey)
    {
        CcGame result = null;
        if (Storage.isSupported() && pPersistenceKey != null)
        {
            Storage localStorage = Storage.getLocalStorage();
            String item = localStorage.getItem(pPersistenceKey);
            if (item != null && item.length() > 0) {
                result = new CcGame(CcGameJSO.create(item));
                result.setPersistenceKey(pPersistenceKey);
            }
        }
        return result;
    }



    /**
     * Saves the given game, replacing a game with the same key if present.
     * @param pGame a game to save
     */
    public static void saveGame(final CcGame pGame)
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
    public static void saveGame(final CcGameVO pGameVO)
    {
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            String key = pGameVO.getPersistenceKey();
            CcGame game = loadGame(key);
            game.setName(pGameVO.getGameName());
            localStorage.setItem(key, game.toJson());
        }
    }



    /**
     * Creates a new game in HTML5 storage which was not there before.
     * @param pGameVO a fully set game VO, but without a persistence key
     * @param pVariantId the variant ID
     */
    public static void saveNewGame(final CcGameVO pGameVO, final String pVariantId)
    {
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            String key = createKey(KeyType.Game);
            CcGameJSO gameJso = CcGameJSO.create();
            gameJso.setName(pGameVO.getGameName());
            gameJso.setVariantId(pVariantId);
            CcGame game = new CcGame(gameJso);
            localStorage.setItem(key, game.toJson());
            pGameVO.setPersistenceKey(key);
        }
    }



    /**
     * Removes the given item from HTML5 local storage.
     * @param pPersistenceKey the item's persistence key
     */
    public static void deleteItem(final String pPersistenceKey)
    {
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            localStorage.removeItem(pPersistenceKey);
        }        
    }



    /**
     * Saves the situation object.
     * @param pSituation situation; if the situation already includes a persistence
     *          key, the situation with the same key is replaced in HTML5 storage.
     *          If no persistence key is present, a new one is created
     */
    public static void saveSituation(final CcSituation pSituation)
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
    public static CcSituation loadSituation(final String pSitKey, final CcVariantConfig pVariant)
    {
        CcSituation result = null;
        if (Storage.isSupported()) {
            Storage localStorage = Storage.getLocalStorage();
            String json = localStorage.getItem(pSitKey);
            if (json != null && json.length() > 0) {
                CcSituationJSO sitJso = CcSituationJSO.create(json);
                result = new CcSituation(sitJso, pVariant);
                result.setPersistenceKey(pSitKey);
            }
        }
        return result;
    }
}
