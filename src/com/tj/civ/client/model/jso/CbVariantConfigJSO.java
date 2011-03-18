/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 01.03.2011
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

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;


/**
 * Describes a game variant as per the config file.
 *
 * @author Thomas Jensen
 */
public final class CcVariantConfigJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CcVariantConfigJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return the new instance
     */
    public static CcVariantConfigJSO create()
    {
        CcVariantConfigJSO result = createObject().cast();
        result.setDisplayNames(CcStringsI18nJSO.create());
        result.setVersion(-1);
        result.setNumCardsLimit(0);
        return result;
    }



    /**
     * The globally unique ID of this game variant.
     * @return the globally unique ID of this game variant
     */
    public native String getVariantId()
    /*-{
        return this.variantId;
    }-*/;

    /**
     * Sets the globally unique ID of this game variant.
     * @param pVariantId the new value
     */
    public native void setVariantId(final String pVariantId)
    /*-{
        this.variantId = pVariantId;
    }-*/;



    /**
     * Get the version of this variant.
     * @return version of this variant
     */
    public native int getVersion()
    /*-{
        return this.version;
    }-*/;

    /**
     * Sets the version of this variant.
     * @param pVersion the new value
     */
    public native void setVersion(final int pVersion)
    /*-{
        this.version = pVersion;
    }-*/;



    /**
     * Get URL where to get updates of this variant.
     * @return URL where to get updates of this variant
     */
    public native String getUrl()
    /*-{
        return this.url;
    }-*/;

    /**
     * Sets the URL where to get updates of this variant.
     * @param pUrl the new value
     */
    public native void setUrl(final String pUrl)
    /*-{
        this.url = pUrl;
    }-*/;



    /**
     * Gets the civilization card configuration.
     * @return the civilization card configuration
     * @see #getCard(int)
     */
    public CcCardConfigJSO[] getCards()
    {
        JsArray<CcCardConfigJSO> arr = getCardsJs();
        CcCardConfigJSO[] result = null;
        if (arr != null) {
            result = new CcCardConfigJSO[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                result[i] = arr.get(i);
            }
        }
        return result;
    }

    /**
     * Gets one card from the civilization card configuration.
     * Using this method is much faster than using {@link #getCards()}.
     * @param pIdx index into the civilization card configuration array
     * @return the civilization card configuration
     */
    public CcCardConfigJSO getCard(final int pIdx)
    {
        return getCardsJs().get(pIdx);
    }

    /**
     * Sets the civilization card configuration.
     * @param pCards the new value (must not be <code>null</code> or empty)
     */
    public void setCards(final CcCardConfigJSO[] pCards)
    {
        JsArray<CcCardConfigJSO> arr = createArray().cast();
        for (CcCardConfigJSO card : pCards) {
            arr.push(card);
        }
        setCardsJs(arr);
    }

    private native JsArray<CcCardConfigJSO> getCardsJs()
    /*-{
        return this.cards;
    }-*/;

    private native void setCardsJs(final JsArray<CcCardConfigJSO> pCards)
    /*-{
        this.cards = pCards;
    }-*/;



    /**
     * Get limit to the number of civilization cards that a player may buy during a
     * game. A value of 0 (zero) indicates that there is no such limit.
     * @return limit to the number of civilization cards
     */
    public native int getNumCardsLimit()
    /*-{
        return this.cardLimit;
    }-*/;

    /**
     * Set the limit to the number of civilization cards that a player may buy during
     * a game. A value of 0 (zero) indicates that there is no such limit.
     * @param pNumCardsLimit the new value
     */
    public native void setNumCardsLimit(final int pNumCardsLimit)
    /*-{
        this.cardLimit = pNumCardsLimit;
    }-*/;



    /**
     * Gets the commodity card configuration.
     * @return the commodity card configuration
     * @see #getCommodity(int)
     */
    public CcCommodityConfigJSO[] getCommodities()
    {
        JsArray<CcCommodityConfigJSO> arr = getCommoditiesJs();
        CcCommodityConfigJSO[] result = null;
        if (arr != null) {
            result = new CcCommodityConfigJSO[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                result[i] = arr.get(i);
            }
        }
        return result;
    }

    /**
     * Gets one commodity from the commodity card configuration.
     * Using this method is much faster than using {@link #getCommodities()}.
     * @param pIdx index into the commodity card configuration array
     * @return the commodity card configuration
     */
    public CcCommodityConfigJSO getCommodity(final int pIdx)
    {
        return getCommoditiesJs().get(pIdx);
    }

    /**
     * Sets the commodity card configuration.
     * @param pCommodities the new value (must not be <code>null</code> or empty)
     */
    public void setCommodities(final CcCommodityConfigJSO[] pCommodities)
    {
        JsArray<CcCommodityConfigJSO> arr = createArray().cast();
        for (CcCommodityConfigJSO card : pCommodities) {
            arr.push(card);
        }
        setCommoditiesJs(arr);
    }

    private native JsArray<CcCommodityConfigJSO> getCommoditiesJs()
    /*-{
        return this.commodities;
    }-*/;

    private native void setCommoditiesJs(final JsArray<CcCommodityConfigJSO> pCommodities)
    /*-{
        this.commodities = pCommodities;
    }-*/;



    /**
     * Getter.
     * @return locale-specific display names of this game variant
     */
    public native CcStringsI18nJSO getDisplayNames()
    /*-{
        return this.displayNames;
    }-*/;

    /**
     * Sets the locale-specific display names of this game variant.
     * @param pDisplayNames the new values
     */
    private native void setDisplayNames(final CcStringsI18nJSO pDisplayNames)
    /*-{
        this.displayNames = pDisplayNames;
    }-*/;

    /**
     * Determine the display name of this game variant best matching the current locale.
     * @return the localized display name of this game variant
     */
    public String getLocalizedDisplayName()
    {
        return getDisplayNames().getStringI18n();
    }



    /**
     * Getter.
     * @return a sorted set of all the target winning points allowed in the variant
     */
    public SortedSet<Integer> getTargetOptions()
    {
        JsArrayInteger arr = getTargetOptionsJs();
        SortedSet<Integer> result = null;
        if (arr != null) {
            result = new TreeSet<Integer>();
            for (int i = 0; i < arr.length(); i++) {
                result.add(Integer.valueOf(arr.get(i)));
            }
        }
        return result;
    }

    /**
     * Setter.
     * @param pTargetOptions a sorted set of all the target winning points allowed
     *          in the variant
     */
    public void setTargetOptions(final SortedSet<Integer> pTargetOptions)
    {
        JsArrayInteger arr = createArray().cast();
        for (Integer tp : pTargetOptions) {
            arr.push(tp.intValue());
        }
        setTargetOptionsJs(arr);
    }

    private native JsArrayInteger getTargetOptionsJs()
    /*-{
        return this.targetOpts;
    }-*/;

    private native void setTargetOptionsJs(final JsArrayInteger pTargetOptions)
    /*-{
        this.targetOpts = pTargetOptions;
    }-*/;
}
