/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 11.03.2011
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
package com.tj.civ.client.model.vo;

import com.tj.civ.client.common.CbConstants;



/**
 * View object representing those attributes of a
 * {@link com.tj.civ.client.model.CbGame} that are shown in the list of games of
 * the 'Games' view.
 * 
 * <p>The game name serves as the primary key, which must be unique among all
 * games stored on the client. <tt>equals()</tt> and <tt>hashcode()</tt> work only
 * on the game name.
 *
 * @author Thomas Jensen
 */
public class CbGameVO
    extends CbAbstractViewObject
{
    /** the key in HTML5 storage */
    private String iPersistenceKey;

    /** the game name */
    private String iGameName;

    /** the localized name of the variant */
    private String iVariantNameLocalized;



    /**
     * Constructor.
     * @param pPersistenceKey the key in HTML5 storage
     * @param pGameName the game key (game primary key)
     * @param pVariantNameLocalized the localized name of the variant
     */
    public CbGameVO(final String pPersistenceKey, final String pGameName,
        final String pVariantNameLocalized)
    {
        super();
        iPersistenceKey = pPersistenceKey;
        iGameName = pGameName;
        iVariantNameLocalized = pVariantNameLocalized;
    }



    public String getPersistenceKey()
    {
        return iPersistenceKey;
    }

    public void setPersistenceKey(final String pPersistenceKey)
    {
        iPersistenceKey = pPersistenceKey;
    }



    public String getGameName()
    {
        return iGameName;
    }

    public void setGameName(final String pGameName)
    {
        iGameName = pGameName;
    }



    public String getVariantNameLocalized()
    {
        return iVariantNameLocalized;
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGameName() == null) ? 0 : getGameName().hashCode());
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

        CbGameVO other = (CbGameVO) pOther;
        if (getGameName() == null) {
            if (other.getGameName() != null) {
                return false;
            }
        }
        else if (!getGameName().equals(other.getGameName())) {
            return false;
        }
        return true;
    }



    @Override
    public String getPrimaryText()
    {
        return getGameName();
    }

    @Override
    public void setPrimaryText(final String pPrimaryText)
    {
        setGameName(pPrimaryText);
    }



    @Override
    public String getSecondaryText()
    {
        return CbConstants.STRINGS.rules() + ": " //$NON-NLS-1$
            + getVariantNameLocalized();
    }
}
