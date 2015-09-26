/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-01
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
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
public final class CbVariantConfigJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CbVariantConfigJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return the new instance
     */
    public static CbVariantConfigJSO create()
    {
        CbVariantConfigJSO result = createObject().cast();
        result.setDisplayNames(CbStringsI18nJSO.create());
        result.setVariantVersion(-1);
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
    public native int getVariantVersion()
    /*-{
        return this.version;
    }-*/;

    /**
     * Sets the version of this variant.
     * @param pVersion the new value
     */
    public native void setVariantVersion(final int pVersion)
    /*-{
        this.version = pVersion;
    }-*/;



    /**
     * Get the version of the JSON format of this variant.
     * @return version of this variant
     */
    public native int getFormatVersion()
    /*-{
        if (this.hasOwnProperty('format')) {
            return this.format;
        } else {
            return 1;
        }
    }-*/;

    /**
     * Sets the version of the JSON format of this variant.
     * @param pFormatVersion the new value
     */
    public native void setFormatVersion(final int pFormatVersion)
    /*-{
        this.format = pVersion;
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
    public CbCardConfigJSO[] getCards()
    {
        JsArray<CbCardConfigJSO> arr = getCardsJs();
        CbCardConfigJSO[] result = null;
        if (arr != null) {
            result = new CbCardConfigJSO[arr.length()];
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
    public CbCardConfigJSO getCard(final int pIdx)
    {
        return getCardsJs().get(pIdx);
    }

    /**
     * Sets the civilization card configuration.
     * @param pCards the new value (must not be <code>null</code> or empty)
     */
    public void setCards(final CbCardConfigJSO[] pCards)
    {
        JsArray<CbCardConfigJSO> arr = createArray().cast();
        for (CbCardConfigJSO card : pCards) {
            arr.push(card);
        }
        setCardsJs(arr);
    }

    private native JsArray<CbCardConfigJSO> getCardsJs()
    /*-{
        return this.cards;
    }-*/;

    private native void setCardsJs(final JsArray<CbCardConfigJSO> pCards)
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
    public CbCommodityConfigJSO[] getCommodities()
    {
        JsArray<CbCommodityConfigJSO> arr = getCommoditiesJs();
        CbCommodityConfigJSO[] result = null;
        if (arr != null) {
            result = new CbCommodityConfigJSO[arr.length()];
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
    public CbCommodityConfigJSO getCommodity(final int pIdx)
    {
        return getCommoditiesJs().get(pIdx);
    }

    /**
     * Sets the commodity card configuration.
     * @param pCommodities the new value (must not be <code>null</code> or empty)
     */
    public void setCommodities(final CbCommodityConfigJSO[] pCommodities)
    {
        JsArray<CbCommodityConfigJSO> arr = createArray().cast();
        for (CbCommodityConfigJSO card : pCommodities) {
            arr.push(card);
        }
        setCommoditiesJs(arr);
    }

    private native JsArray<CbCommodityConfigJSO> getCommoditiesJs()
    /*-{
        return this.commodities;
    }-*/;

    private native void setCommoditiesJs(final JsArray<CbCommodityConfigJSO> pCommodities)
    /*-{
        this.commodities = pCommodities;
    }-*/;



    /**
     * Getter.
     * @return locale-specific display names of this game variant
     */
    public native CbStringsI18nJSO getDisplayNames()
    /*-{
        return this.displayNames;
    }-*/;

    /**
     * Sets the locale-specific display names of this game variant.
     * @param pDisplayNames the new values
     */
    private native void setDisplayNames(final CbStringsI18nJSO pDisplayNames)
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
