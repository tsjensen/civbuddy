/*
 * CivBuddy - A Civilization Tactics Guide
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
package com.tj.civ.client.model.jso;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * Describes a commodity as per the config file.
 *
 * @author tsjensen
 */
public final class CcCommodityConfigJSO
    extends JavaScriptObject
{
    /**
     * JSO Constructor.
     */
    protected CcCommodityConfigJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return a new instance with base and maxCount initialized to -1
     */
    public static CcCommodityConfigJSO create()
    {
        CcCommodityConfigJSO result = createObject().cast();
        result.setNames(CcStringsI18nJSO.create());
        return result;
    }



    /**
     * Getter.
     * @return locale-specific names of this commodity
     */
    public native CcStringsI18nJSO getNames()
    /*-{
        return this.names;
    }-*/;

    /**
     * Sets the locale-specific names of this commodity.
     * @param pNames the new values
     */
    private native void setNames(final CcStringsI18nJSO pNames)
    /*-{
        this.names = pNames;
    }-*/;

    /**
     * Determine the commodity name best matching the current locale.
     * @return the localized commodity name
     */
    public String getLocalizedName()
    {
        return getNames().getStringI18n();
    }



    /**
     * Getter.
     * @return maximum number of cards of this commodity available in the game
     */
    public native int getMaxCount()
    /*-{
        if (this.hasOwnProperty('maxCount')) {
            return this.maxCount;
        } else {
            return -1;
        }
    }-*/;

    /**
     * Sets the maximum number of cards of this commodity available in the game.
     * @param pMaxCount the new value
     */
    public native void setMaxCount(final int pMaxCount)
    /*-{
        this.maxCount = pMaxCount;
    }-*/;



    /**
     * Getter.
     * @return base value of this commodity
     */
    public native int getBase()
    /*-{
        if (this.hasOwnProperty('base')) {
            return this.base;
        } else {
            return -1;
        }
    }-*/;

    /**
     * Sets the base value of this commodity.
     * @param pBase the new value
     */
    public native void setBase(final int pBase)
    /*-{
        this.base = pBase;
    }-*/;
}
