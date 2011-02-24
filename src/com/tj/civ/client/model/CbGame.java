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
    implements Comparable<CcGame>
{
    /** Name of the game */
    private String iName = null;

    /** the game variant we're playing */
    private CcVariantConfig iVariant = null;

    /** the currently active situation */
    private CcSituation iCurrentSituation = null;

    /** Map of players in this game to their individual situations */
    private Map<CcPlayer, CcSituation> iSituations = new TreeMap<CcPlayer, CcSituation>();



    /**
     * Constructor.
     * @param pName name of the game
     */
    public CcGame(final String pName)
    {
        iName = pName;
    }



    /**
     * Adds a player to the game.
     * @param pSituation the player's newly initialized situation, including a link
     *          to the player object
     */
    public void addPlayer(final CcSituation pSituation)
    {
        pSituation.setGame(this);
        iSituations.put(pSituation.getPlayer(), pSituation);
    }



    /**
     * Removes a player from the game. His situation is deleted.
     * @param pPlayer the player
     */
    public void removePlayer(final CcPlayer pPlayer)
    {
        iSituations.remove(pPlayer).setGame(null);
    }



    /**
     * Getter.
     * @return {@link #iName}
     */
    public String getName()
    {
        return iName;
    }

    /**
     * Setter.
     * @param pName the new value of {@link #iName}
     */
    public void setName(final String pName)
    {
        iName = pName;
    }



    /**
     * Getter.
     * @return {@link #iVariant}
     */
    public CcVariantConfig getVariant()
    {
        return iVariant;
    }

    /**
     * Setter.
     * @param pVariant the new value of {@link #iVariant}
     */
    public void setVariant(final CcVariantConfig pVariant)
    {
        iVariant = pVariant;
    }



    public CcSituation getCurrentSituation()
    {
        return iCurrentSituation;
    }

    public void setCurrentSituation(final CcPlayer pPlayer)
    {
        iCurrentSituation = iSituations.get(pPlayer);
    }



    /**
     * Getter.
     * @return {@link #iSituations}
     */
    public Map<CcPlayer, CcSituation> getSituations()
    {
        return iSituations;
    }



    @Override
    public int compareTo(final CcGame pOther)
    {
        int result = 0;
        String otherName = pOther != null ? pOther.iName : null;
        if (iName != null && otherName != null) {
            result = iName.compareToIgnoreCase(otherName);
        } else if (iName != null) {
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
        result = prime * result + ((iName == null) ? 0 : iName.hashCode());
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
        if (iName == null) {
            if (other.iName != null) {
                return false;
            }
        }
        else if (!iName.equals(other.iName)) {
            return false;
        }
        return true;
    }
}
