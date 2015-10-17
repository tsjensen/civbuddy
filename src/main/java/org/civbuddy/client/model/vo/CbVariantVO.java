/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-11
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.model.vo;



/**
 * View object representing those attributes of a
 * {@link org.civbuddy.client.model.CbVariantConfig} that are shown in the list of
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
        return getVariantId() + " (v." + getVersion() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
