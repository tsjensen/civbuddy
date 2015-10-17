/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-05
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.model;

import java.util.Map;

import org.civbuddy.client.common.CbLogAdapter;
import org.civbuddy.client.model.jso.CbGameJSO;
import org.civbuddy.client.model.vo.CbGameVO;
import org.civbuddy.client.model.vo.CbHasViewObjectIF;


/**
 * Describes a game of Civilization. This is the top data object in the model.
 * 
 * @author Thomas Jensen
 */
public class CbGame
    extends CbIndependentlyPersistableObject<CbGameJSO>
    implements CbHasViewObjectIF<CbGameVO>
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbGame.class);

    /** the game variant we're playing */
    private CbVariantConfig iVariant;

    /** the currently active situation */
    private CbSituation iCurrentSituation = null;

    /**
     * Map of players in this game to their individual situations. The keys are player names. The player objects are
     * linked from the situation.<br>
     * TODO key should be the sit's persistence key
     */
    private Map<String, CbSituation> iSituations;



    /**
     * Constructor.
     * 
     * @param pJso the game JSO
     */
    public CbGame(final CbGameJSO pJso)
    {
        super(pJso);
        LOG.touch(CbLogAdapter.CONSTRUCTOR);
    }



    /**
     * Adds a player to the game.
     * 
     * @param pSituation the player's newly initialized situation, including a link to the player object
     */
    public void addPlayer(final CbSituation pSituation)
    {
        String playerName = pSituation.getPlayer().getName();
        iSituations.put(playerName, pSituation);
        getJso().addPlayer(playerName, pSituation.getPersistenceKey());
    }



    /**
     * Removes a player from the game. His situation is deleted.
     * 
     * @param pSituation the situation to remove
     */
    public void removePlayer(final CbSituation pSituation)
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
     * 
     * @param pName the new value
     */
    public void setName(final String pName)
    {
        getJso().setName(pName);
    }



    public CbVariantConfig getVariant()
    {
        return iVariant;
    }



    public void setVariant(final CbVariantConfig pVariant)
    {
        iVariant = pVariant;
    }



    public CbSituation getCurrentSituation()
    {
        return iCurrentSituation;
    }



    /**
     * Setter.
     * 
     * @param pCurrentSit the new value
     */
    public void setCurrentSituation(final CbSituation pCurrentSit)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setCurrentSituation",  //$NON-NLS-1$
                new String[]{"pCurrentSit"}, new Object[]{pCurrentSit}); //$NON-NLS-1$
        }

        iCurrentSituation = pCurrentSit;
        if (pCurrentSit != null) {
            addPlayer(pCurrentSit);
        }
        else if (LOG.isDebugEnabled()) {
            LOG.debug("setCurrentSituation", "clearing"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        LOG.exit("setCurrentSituation"); //$NON-NLS-1$
    }



    public Map<String, CbSituation> getSituations()
    {
        return iSituations;
    }



    public void setSituations(final Map<String, CbSituation> pSituations)
    {
        iSituations = pSituations;
    }



    /**
     * Looks through the values of {@link #iSituations} to find the situation with the given persistence key.
     * 
     * @param pSituationKey the situation's persistence key
     * @return the situation itself
     */
    public CbSituation getSituationByKey(final String pSituationKey)
    {
        CbSituation result = null;
        if (iSituations != null) {
            for (CbSituation sit : iSituations.values()) {
                if (sit != null && sit.getPersistenceKey().equals(pSituationKey)) {
                    result = sit;
                    break;
                }
            }
        }
        return result;
    }



    @Override
    public void evaluateJsoState(final CbGameJSO pJso)
    {
        // do nothing
    }



    @Override
    public CbGameVO getViewObject()
    {
        return new CbGameVO(getPersistenceKey(), getName(), iVariant.getLocalizedDisplayName());
    }
}
