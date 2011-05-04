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
 * {@link com.tj.civ.client.model.CbVariantConfig} that are shown in the list of
 * variants of the 'Variants' view.
 *
 * @author Thomas Jensen
 */
public class CbVariantVO
    extends CbAbstractViewObject
{
    /** the key in HTML5 storage */
    private String iPersistenceKey;

    /** the variant ID */
    private String iVariantId;

    /** the localized variant name */
    private String iVariantNameLocalized;

    /** <code>true</code> if the variant of this {@link #iVariantId} is unknown */
    private boolean iUnknown;



    /**
     * Constructor.
     * @param pPersistenceKey the key in HTML5 storage (may be
     *          <code>null</code> if the variant is unknown)
     * @param pVariantId the variant ID
     * @param pVariantNameLocalized the localized variant name (may be
     *          <code>null</code> if the variant is unknown)
     */
    public CbVariantVO(final String pPersistenceKey, final String pVariantId,
        final String pVariantNameLocalized)
    {
        super();
        iPersistenceKey = pPersistenceKey;
        iVariantId = pVariantId;
        if (pVariantNameLocalized != null) {
            iVariantNameLocalized = pVariantNameLocalized;
            iUnknown = false;
        } else {
            iVariantNameLocalized = pVariantId + ' ' + CbConstants.STRINGS.unknown();
            iUnknown = true;
        }
    }



    public String getVariantId()
    {
        return iVariantId;
    }



    public String getVariantNameLocalized()
    {
        return iVariantNameLocalized;
    }



    public boolean isUnknown()
    {
        return iUnknown;
    }



    public String getPersistenceKey()
    {
        return iPersistenceKey;
    }
}
