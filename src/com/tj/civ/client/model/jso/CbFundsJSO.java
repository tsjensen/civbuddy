/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 04.03.2011
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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;


/**
 * Used for persisting funds panel state.
 *
 * @author Thomas Jensen
 */
public final class CcFundsJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CcFundsJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return the new instance
     */
    public static CcFundsJSO create()
    {
        CcFundsJSO result = createObject().cast();
        result.setEnabled(false);
        result.setDetailed(true);
        result.setTotalFunds(0);
        return result;
    }



    /**
     * Getter.
     * @return <code>true</code> if funds tracking is generally enabled
     */
    public native boolean isEnabled()
    /*-{
        if (this.hasOwnProperty('enabled')) {
            return this.enabled;
        } else {
            return false;     // by default, funds tracking is generally disabled
        }
    }-*/;

    /**
     * Setter.
     * @param pEnabled <code>true</code> if funds tracking is generally enabled
     */
    public native void setEnabled(final boolean pEnabled)
    /*-{
        this.enabled = pEnabled;
    }-*/;



    /**
     * Getter.
     * @return <code>true</code> if detailed funds tracking is enabled
     */
    public native boolean isDetailed()
    /*-{
        if (this.hasOwnProperty('detailed')) {
            return this.detailed;
        } else {
            return true;     // by default, detail tracking is enabled
        }
    }-*/;

    /**
     * Setter.
     * @param pDetailed <code>true</code> if detailed funds tracking is enabled
     */
    public native void setDetailed(final boolean pDetailed)
    /*-{
        this.detailed = pDetailed;
        if (pDetailed) {
            if (this.hasOwnProperty('total')) {
                delete this.total;
            }
        } else {
            if (this.hasOwnProperty('treasury')) {
                delete this.treasury;
            }
            if (this.hasOwnProperty('commodities')) {
                delete this.commodities;
            }
            if (this.hasOwnProperty('bonus')) {
                delete this.bonus;
            }
        }
    }-*/;



    /**
     * Getter.
     * @return total funds if detailed tracking is disabled
     */
    public native int getTotalFunds()
    /*-{
        if (this.hasOwnProperty('total')) {
            return this.total;
        } else {
            return 0;
        }
    }-*/;

    /**
     * Setter.
     * @param pTotalFunds total funds if detailed tracking is disabled
     */
    public native void setTotalFunds(final int pTotalFunds)
    /*-{
        this.total = pTotalFunds;
    }-*/;



    /**
     * Getter.
     * @return treasury funds if detailed tracking is enabled
     */
    public native int getTreasury()
    /*-{
        if (this.hasOwnProperty('treasury')) {
            return this.treasury;
        } else {
            return 0;
        }
    }-*/;

    /**
     * Setter.
     * @param pTreasury treasury funds if detailed tracking is enabled
     */
    public native void setTreasury(final int pTreasury)
    /*-{
        this.treasury = pTreasury;
    }-*/;



    /**
     * Getter.
     * @return bonus funds if detailed tracking is enabled
     */
    public native int getBonus()
    /*-{
        if (this.hasOwnProperty('bonus')) {
            return this.bonus;
        } else {
            return 0;
        }
    }-*/;

    /**
     * Setter.
     * @param pBonus bonus funds if detailed tracking is enabled
     */
    public native void setBonus(final int pBonus)
    /*-{
        this.bonus = pBonus;
    }-*/;



    /**
     * Getter.
     * @return the current commodity counts. The order of cards is the same as in
     *              the variant config
     */
    public int[] getCommodityCounts()
    {
        JsArrayInteger arr = getCommoditiesJs();
        int[] result = null;
        if (arr != null && arr.length() > 0) {
            result = new int[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                result[i] = arr.get(i);
            }
        }
        return result;
    }

    /**
     * Gets the current count for the given commodity.
     * <p>Calling this method is just a faster way of calling
     * <code>getCommodityCounts()[pIdx]</code>.
     * @param pIdx index into the <tt>commodities</tt> array field
     * @return the current count for the given commodity
     */
    public int getCommodityCount(final int pIdx)
    {
        JsArrayInteger commos = getCommoditiesJs();
        int result = 0;
        if (commos != null && commos.length() > pIdx) {
            result = commos.get(pIdx);
        }
        return result;
    }

    /**
     * Setter.
     * @param pCommodities the new value
     * @see #getCommodityCounts()
     */
    void setCommodityCounts(final int[] pCommodities)
    {
        JsArrayInteger arr = createArray().cast();
        if (pCommodities != null && pCommodities.length > 0) {
            for (int v : pCommodities) {
                arr.push(v);
            }
        }
        setCommoditiesJs(arr);
    }

    /**
     * Sets the current count for the given commodity.
     * @param pIdx index into the <tt>commodities</tt> array field
     * @param pCount the current count for the given commodity
     * @see #setCommodityCounts
     */
    public void setCommodityCount(final int pIdx, final int pCount)
    {
        getCommoditiesJs().set(pIdx, pCount);
    }

    private native JsArrayInteger getCommoditiesJs()
    /*-{
        return this.commodities;
    }-*/;
    
    private native void setCommoditiesJs(final JsArrayInteger pCommodities)
    /*-{
        this.commodities = pCommodities;
    }-*/;
}
