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


/**
 * Represents a player in an actual game.
 *
 * @author Thomas Jensen
 */
public class CcPlayer
    implements Comparable<CcPlayer>
{
    /** Player name */
    private String iName = null;

    /** Target points of this player's civilization */
    private int iWinningTotal = 0;



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
     * @return {@link #iWinningTotal}
     */
    public int getWinningTotal()
    {
        return iWinningTotal;
    }

    /**
     * Setter.
     * @param pWinningTotal the new value of {@link #iWinningTotal}
     */
    public void setWinningTotal(final int pWinningTotal)
    {
        iWinningTotal = pWinningTotal;
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((iName == null) ? 0 : iName.hashCode());
        // iWinningTotal does not figure
        return result;
    }



    @Override
    public boolean equals(final Object pObj)
    {
        if (this == pObj) {
            return true;
        }
        if (pObj == null) {
            return false;
        }
        if (getClass() != pObj.getClass()) {
            return false;
        }

        CcPlayer other = (CcPlayer) pObj;
        if (iName == null) {
            if (other.iName != null) {
                return false;
            }
        } else if (!iName.equals(other.iName)) {
            return false;
        }
        return true;
    }



    @Override
    public int compareTo(final CcPlayer pObj)
    {
        int result = 0;
        if (iName == null) {
            if (pObj.iName != null) {
                result = 1;
            }
        } else {
            if (pObj.iName != null) {
                result = iName.compareToIgnoreCase(pObj.iName);
            } else {
                result = -1;
            }
        }
        return result;
    }
}
