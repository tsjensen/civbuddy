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

    /** the variant's version number (<em>not</em> the variant format's version number) */
    private int iVersion;



    /**
     * Constructor. All arguments must not be <code>null</code>.
     * @param pPersistenceKey the key in HTML5 storage
     * @param pVariantId the variant ID
     * @param pVariantNameLocalized the localized variant name
     * @param pVersion the variant's version number
     */
    public CbVariantVO(final String pPersistenceKey, final String pVariantId,
        final String pVariantNameLocalized, final int pVersion)
    {
        super();
        iPersistenceKey = pPersistenceKey;
        iVariantId = pVariantId;
        iVariantNameLocalized = pVariantNameLocalized;
        iVersion = pVersion;
    }



    public String getVariantId()
    {
        return iVariantId;
    }



    public String getVariantNameLocalized()
    {
        return iVariantNameLocalized;
    }



    public String getPersistenceKey()
    {
        return iPersistenceKey;
    }



    public int getVersion()
    {
        return iVersion;
    }



    @Override
    public String getPrimaryText()
    {
        return getVariantNameLocalized();
    }

    @Override
    public void setPrimaryText(final String pPrimaryText)
    {
        // this is impossible for variants
        throw new UnsupportedOperationException();
    }



    @Override
    public String getSecondaryText()
    {
        return getVariantId() + " / " + getVersion(); //$NON-NLS-1$
    }
}
