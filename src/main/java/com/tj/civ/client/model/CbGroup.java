/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-25
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.model;

import com.google.gwt.resources.client.ImageResource;

import com.tj.civ.client.common.CbConstants;


/**
 * Represents the groups of civilization cards.
 *
 * @author tsjensen
 */
public enum CbGroup
{
    /** the 'crafts' group */
    Crafts('C'),

    /** the 'sciences' group */
    Sciences('S'),

    /** the 'arts' group */
    Arts('A'),

    /** the 'civics' group */
    Civics('G'),

    /** the 'religion' group */
    Religion('R');



    /** the key character */
    private char iKey;



    private CbGroup(final char pKey)
    {
        iKey = pKey;
    }



    /**
     * Getter.
     * @return {@link #iKey}
     */
    public char getKey()
    {
        return iKey;
    }



    /**
     * Convert a primitive key into an instance of this enum.
     * @param pKey the key char
     * @return an enum instance, or <code>null</code> of the key is invalid
     */
    public static CbGroup fromKey(final char pKey)
    {
        CbGroup result = null;
        for (CbGroup grp : CbGroup.values()) {
            if (grp.getKey() == pKey) {
                result = grp;
                break;
            }
        }
        return result;
    }



    /**
     * Determine the localized name of the group.
     * @return the localized name
     */
    public String getLocalizedName()
    {
        String result = null;
        switch (this) {
            case Crafts:
                result = CbConstants.STRINGS.groupNameC();
                break;
            case Sciences:
                result = CbConstants.STRINGS.groupNameS();
                break;
            case Arts:
                result = CbConstants.STRINGS.groupNameA();
                break;
            case Civics:
                result = CbConstants.STRINGS.groupNameG();
                break;
            case Religion:
                result = CbConstants.STRINGS.groupNameR();
                break;
            default:
                throw new IllegalArgumentException("unknown group"); //$NON-NLS-1$
        }
        return result;
    }



    /**
     * Determine the icon image at runtime. Cannot be done statically because
     * the image resources may not be available in time.
     * @return the icon image
     */
    public ImageResource getIcon()
    {
        ImageResource result = null;
        switch (this) {
            case Crafts:
                result = CbConstants.IMG_BUNDLE.groupCrafts();
                break;
            case Arts:
                result = CbConstants.IMG_BUNDLE.groupArts();
                break;
            case Sciences:
                result = CbConstants.IMG_BUNDLE.groupSciences();
                break;
            case Civics:
                result = CbConstants.IMG_BUNDLE.groupCivics();
                break;
            case Religion:
                result = CbConstants.IMG_BUNDLE.groupReligion();
                break;
            default:
                throw new IllegalArgumentException("unknown group"); //$NON-NLS-1$
        }
        return result;
    }
}
