/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 25.12.2010
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

import com.google.gwt.resources.client.ImageResource;

import com.tj.civ.client.common.CbConstants;


/**
 * Represents the name of a group of civilization cards.
 * TODO: move hard-coded display names to CbLocalizedConstantsIF
 *
 * @author tsjensen
 */
public enum CbGroup
{
    /** the 'crafts' group */
    Crafts('C', "Crafts", "Handwerk"),  //$NON-NLS-1$ //$NON-NLS-2$

    /** the 'sciences' group */
    Sciences('S', "Sciences", "Wissenschaften"),  //$NON-NLS-1$ //$NON-NLS-2$

    /** the 'arts' group */
    Arts('A', "Arts", "Künste"),  //$NON-NLS-1$ //$NON-NLS-2$

    /** the 'civics' group */
    Civics('G', "Civics", "Gesellschaft"),  //$NON-NLS-1$ //$NON-NLS-2$

    /** the 'religion' group */
    Religion('R', "Religion", "Religion");  //$NON-NLS-1$ //$NON-NLS-2$



    /** the key character */
    private char iKey;

    /** the English name */
    private String iNameEN;

    /** the German name */
    private String iNameDE;



    private CbGroup(final char pKey, final String pNameEN, final String pNameDE)
    {
        iKey = pKey;
        iNameEN = pNameEN;
        iNameDE = pNameDE;
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
     * Getter.
     * @return {@link #iNameEN}
     */
    public String getNameEN()
    {
        return iNameEN;
    }



    /**
     * Getter.
     * @return {@link #iNameDE}
     */
    public String getNameDE()
    {
        return iNameDE;
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
     * Determine the icon image at runtime. Cannot be done statically becuase
     * the image resources may not be available in time.
     * @return the icon image
     */
    public ImageResource getIcon()
    {
        ImageResource result = null;
        switch (this) {
            case Arts:
                result = CbConstants.IMG_BUNDLE.groupArts();
                break;
            case Crafts:
                result = CbConstants.IMG_BUNDLE.groupCrafts();
                break;
            case Civics:
                result = CbConstants.IMG_BUNDLE.groupCivics();
                break;
            case Religion:
                result = CbConstants.IMG_BUNDLE.groupReligion();
                break;
            case Sciences:
                result = CbConstants.IMG_BUNDLE.groupSciences();
                break;
            default:
                throw new IllegalArgumentException("unknown group"); //$NON-NLS-1$
        }
        return result;
    }
}
