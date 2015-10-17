/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-26
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.model.jso;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * Describes a commodity as per the config file.
 *
 * @author tsjensen
 */
public final class CbCommodityConfigJSO
    extends JavaScriptObject
{
    /** <code>true</code> if the commodity is affected by the 'Mining' card from
     *  the <i>Advanced Civilization</i> game variant */
    private static final String PROP_MINEABLE = "mineable"; //$NON-NLS-1$



    /**
     * JSO Constructor.
     */
    protected CbCommodityConfigJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return a new instance with base and maxCount initialized to -1
     */
    public static CbCommodityConfigJSO create()
    {
        CbCommodityConfigJSO result = createObject().cast();
        result.setNames(CbStringsI18nJSO.create());
        return result;
    }



    /**
     * Getter.
     * @return locale-specific names of this commodity
     */
    public native CbStringsI18nJSO getNames()
    /*-{
        return this.names;
    }-*/;

    /**
     * Sets the locale-specific names of this commodity.
     * @param pNames the new values
     */
    private native void setNames(final CbStringsI18nJSO pNames)
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



    /**
     * Getter.
     * @return <code>true</code> if this commodity is wine to which the western
     *          expansion pack's special rules for wine apply
     */
    public native boolean isWineSpecial()
    /*-{
        if (this.hasOwnProperty('wine')) {
            return this.wine;
        } else {
            return false;
        }
    }-*/;

    /**
     * Sets the flag indicating that this commodity is wine to which the western
     * expansion pack's special rules for wine apply.
     * @param pWineSpecial the new value
     */
    public native void setWineSpecial(final boolean pWineSpecial)
    /*-{
        this.wine = pWineSpecial;
    }-*/;



    /**
     * Indicate whether this commodity is basically eligible for the bonus granted
     * by the 'Mining' card from the <i>Advanced Civilization</i> game variant.
     * @param pMineable the new value
     */
    public native void setMineable(final boolean pMineable)
    /*-{
        this[@org.civbuddy.client.model.jso.CbCommodityConfigJSO::PROP_MINEABLE] = pMineable;
    }-*/;

    /**
     * Determine if this card is basically eligible for the bonus granted by the
     * 'Mining' card from the <i>Advanced Civilization</i> game variant.
     * @return <code>true</code> if the flag is set accordingly. If the flag is not
     *      present, <code>false</code> is assumed as default
     */
    public native boolean isMineable()
    /*-{
        if (this.hasOwnProperty(@org.civbuddy.client.model.jso.CbCommodityConfigJSO::PROP_MINEABLE)) {
            return this[@org.civbuddy.client.model.jso.CbCommodityConfigJSO::PROP_MINEABLE];
        } else {
            return false;
        }
    }-*/;
}
