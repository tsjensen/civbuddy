/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-05-03
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

import com.google.gwt.resources.client.TextResource;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.model.jso.CbVariantConfigJSO;
import com.tj.civ.client.resources.CbClientBundleIF;


/**
 * The game variants which come with the application.
 * 
 * <p>The hard-coded persistence keys must be updated when the variant changes to
 * a new version or format.
 *
 * @author Thomas Jensen
 */
public enum CbVariantsBuiltIn
{
    /** the 'Original Civilization' game variant */
    Original(CbStorage.VARIANT_PREFIX + "6BDB6D0E-56A9-47E0-8E76" //$NON-NLS-1$
        + CbConstants.DEFAULT_VARIANT_MARK + "01", //$NON-NLS-1$
        CbClientBundleIF.INSTANCE.variantOrg()),

    /** the 'Original Civilization with Western Expansion' game variant */
    OriginalWE(CbStorage.VARIANT_PREFIX + "6BDB6D0E-56A9-47E0-8E76" //$NON-NLS-1$
        + CbConstants.DEFAULT_VARIANT_MARK + "02", //$NON-NLS-1$
        CbClientBundleIF.INSTANCE.variantOrgWE());



    /** the game variant in ready-to-use form */
    private CbVariantConfig iVariantConfig;



    /**
     * Constructor.
     * @param pPersistenceKey hard-coded persistence key
     * @param pResource the JSON data defining the variant
     */
    private CbVariantsBuiltIn(final String pPersistenceKey,
        final TextResource pResource)
    {
        CbVariantConfigJSO variantJso = CbUtil.createFromJson(pResource.getText());
        iVariantConfig = new CbVariantConfig(variantJso);
        iVariantConfig.setPersistenceKey(pPersistenceKey);
    }



    public CbVariantConfig getVariantConfig()
    {
        return iVariantConfig;
    }
}
