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


/**
 * Describes a game of Civilization. This is the top data object in the model.
 * 
 * <p>The game name serves as the primary key, which must be unique among all
 * games stored on the client. <tt>equals()</tt>, <tt>hashcode()</tt>, and
 * <tt>compareTo()</tt> work only on the game name.
 *
 * @author Thomas Jensen
 */
public class CcGame
    extends CcIndependentlyPersistableObject<CcGameJSO>
    implements Comparable<CcGame>
{
    /** the game variant we're playing */
    private CcVariantConfig iVariant = null;

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
        getJso().addPlayer(playerName, pSituation.getUuid());
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
    public int compareTo(final CcGame pOther)
    {
        int result = 0;
        String otherName = pOther != null ? pOther.getName() : null;
        if (getName() != null && otherName != null) {
            result = getName().compareToIgnoreCase(otherName);
        } else if (getName() != null) {
            result = -1;
        } else if (otherName != null) {
            result = 1;
        }
        return result;
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }



    @Override
    public boolean equals(final Object pOther)
    {
        if (this == pOther) {
            return true;
        }
        if (pOther == null) {
            return false;
        }
        if (getClass() != pOther.getClass()) {
            return false;
        }

        CcGame other = (CcGame) pOther;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        }
        else if (!getName().equals(other.getName())) {
            return false;
        }
        return true;
    }



    @Override
    public void evaluateJsoState(final CcGameJSO pJso)
    {
        // TODO fill sit map by loading sits from html5 storage
    }
}
