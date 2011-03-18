/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-05
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
package com.tj.civ.client.model;

import java.util.Map;
import java.util.TreeMap;

import com.tj.civ.client.common.CcStorage;
import com.tj.civ.client.model.jso.CcGameJSO;
import com.tj.civ.client.model.vo.CcGameVO;
import com.tj.civ.client.model.vo.CcHasViewObjectIF;


/**
 * Describes a game of Civilization. This is the top data object in the model.
 * 
 * @author Thomas Jensen
 */
public class CcGame
    extends CcIndependentlyPersistableObject<CcGameJSO>
    implements CcHasViewObjectIF<CcGameVO>
{
    /** the game variant we're playing */
    private CcVariantConfig iVariant;

    /** the currently active situation */
    private CcSituation iCurrentSituation = null;

    /** Map of players in this game to their individual situations.
     *  The keys are player names. The player objects are linked from the situation. */
    private Map<String, CcSituation> iSituations = new TreeMap<String, CcSituation>();



    /**
     * Constructor.
     * @param pJso the game JSO
     */
    public CcGame(final CcGameJSO pJso)
    {
        super(pJso);
    }



    /**
     * Adds a player to the game.
     * @param pSituation the player's newly initialized situation, including a link
     *          to the player object
     */
    public void addPlayer(final CcSituation pSituation)
    {
        String playerName = pSituation.getPlayer().getName();
        iSituations.put(playerName, pSituation);
        getJso().addPlayer(playerName, pSituation.getPersistenceKey());
    }



    /**
     * Removes a player from the game. His situation is deleted.
     * @param pSituation the situation to remove
     */
    public void removePlayer(final CcSituation pSituation)
    {
        String playerName = pSituation.getPlayer().getName();
        iSituations.remove(playerName);
        getJso().removePlayer(playerName);
    }



    public String getName()
    {
        return getJso().getName();
    }

    /**
     * Setter.
     * @param pName the new value
     */
    public void setName(final String pName)
    {
        getJso().setName(pName);
    }


    public CcVariantConfig getVariant()
    {
        return iVariant;
    }



    public CcSituation getCurrentSituation()
    {
        return iCurrentSituation;
    }

    /**
     * Setter.
     * @param pCurrentSit the new value
     */
    public void setCurrentSituation(final CcSituation pCurrentSit)
    {
        iCurrentSituation = pCurrentSit;
        addPlayer(pCurrentSit);
    }



    public Map<String, CcSituation> getSituations()
    {
        return iSituations;
    }



    @Override
    public void evaluateJsoState(final CcGameJSO pJso)
    {
        iVariant = CcStorage.loadVariant(pJso.getVariantId());
        // TODO fill sit map by loading sits from html5 storage
    }



    @Override
    public CcGameVO getViewObject()
    {
        return new CcGameVO(getPersistenceKey(), getName(), iVariant.getLocalizedDisplayName());
    }
}
