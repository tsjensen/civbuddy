/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 26.12.2010
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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.LocaleInfo;
import com.tj.civ.client.resources.CcConstants;


/**
 * Describes a commodity as per the config file.
 *
 * @author tsjensen
 */
public class CcCommodityConfig
{
    /** German name of the commodity */
    private Map<String, String> iNamesI18n = new HashMap<String, String>();

    /** maximum number of cards of this commodity available in the game */
    private int iMaxCount = -1;

    /** base value of this commodity */
    private int iBase = -1;
 


    /**
     * Getter.
     * @return English (== default) name
     */
    public String getNameDefaultEn()
    {
        return iNamesI18n.get(CcConstants.LOCALE_EN);
    }

    /**
     * Setter.
     * @param pNameEn the new value of the English name, which is the default name
     */
    public void setNameDefaultEn(final String pNameEn)
    {
        iNamesI18n.put(CcConstants.LOCALE_EN, pNameEn);
    }



    /**
     * Getter.
     * @return commodity name in the current locale
     */
    public String getNameI18n()
    {
        return iNamesI18n.get(LocaleInfo.getCurrentLocale().getLocaleName());
    }

    /**
     * Getter.
     * @param pLocale locale name for which to get the name
     * @return commodity name in the given locale
     */
    public String getNameI18n(final String pLocale)
    {
        return iNamesI18n.get(pLocale);
    }

    /**
     * Setter.
     * @param pLocale locale name
     * @param pName the commodity name in the given locale
     */
    public void setNameI18n(final String pLocale, final String pName)
    {
        iNamesI18n.put(pLocale, pName);
    }



    /**
     * Getter.
     * @return {@link #iMaxCount}
     */
    public int getMaxCount()
    {
        return iMaxCount;
    }

    /**
     * Setter.
     * @param pMaxCount the new value of {@link #iMaxCount}
     */
    public void setMaxCount(final int pMaxCount)
    {
        iMaxCount = pMaxCount;
    }



    /**
     * Getter.
     * @return {@link #iBase}
     */
    public int getBase()
    {
        return iBase;
    }

    /**
     * Setter.
     * @param pBase the new value of {@link #iBase}
     */
    public void setBase(final int pBase)
    {
        iBase = pBase;
    }
}
