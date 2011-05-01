/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 25.12.2010
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
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;

import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbGroup;


/**
 * Describes a civilization card as configured in the game variant's config file.
 * This includes only values from the variant's config file. Values which are
 * directly deduced from those are place in {@link CbCardConfig}, because JSOs
 * cannot have Java instance fields. Both classes do not cover dynamic information
 * based on haves and plans, which is found in {@link CbCardCurrent}.
 *
 * @author Thomas Jensen
 */
public final class CbCardConfigJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CbCardConfigJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return a new instance of this JSO, with String maps initialized to empty
     *          maps and primitive <code>int</code>s initialized to -1
     */
    public static CbCardConfigJSO create()
    {
        CbCardConfigJSO result = createObject().cast();
        result.setNames(CbStringsI18nJSO.create());
        result.setCostNominal(-1);
        result.setPrereq(-1);
        result.setAttributes(CbStringsI18nJSO.create());
        result.setClamityEffects(CbStringsI18nJSO.create());
        return result;
    }



    /**
     * Getter.
     * @return locale-specific names of this card
     */
    public native CbStringsI18nJSO getNames()
    /*-{
        return this.names;
    }-*/;

    /**
     * Sets the locale-specific names of this card.
     * @param pNames the new values
     */
    private native void setNames(final CbStringsI18nJSO pNames)
    /*-{
        this.names = pNames;
    }-*/;

    /**
     * Determine the card name best matching the current locale.
     * @return the localized card name
     */
    public String getLocalizedName()
    {
        return getNames().getStringI18n();
    }



    /**
     * Get the group(s) this card belongs to.
     * @return array of groups
     */
    public CbGroup[] getGroups()
    {
        JsArrayString arr = getGroupsJs();
        CbGroup[] result = null;
        if (arr != null && arr.length() > 0) {
            result = new CbGroup[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                result[i] = CbGroup.fromKey(arr.get(i).charAt(0));
            }
        }
        return result;
    }

    /**
     * Set the group(s) this card belongs to.
     * @param pGroups the new value
     */
    public void setGroups(final CbGroup[] pGroups)
    {
        JsArrayString arr = createArray().cast();
        if (pGroups != null && pGroups.length > 0) {
            for (CbGroup group : pGroups) {
                arr.push(String.valueOf(group.getKey()));
            }
        }
        setGroupsJs(arr);
    }

    private native JsArrayString getGroupsJs()
    /*-{
        return this.groups;
    }-*/;

    private native void setGroupsJs(final JsArrayString pGroups)
    /*-{
        this.groups = pGroups;
    }-*/;



    /**
     * Get prerequsite card index (index into the variant's card array).
     * @return prerequsite card index, or -1 if no prereq defined
     */
    public native int getPrereq()
    /*-{
        return this.prereq;
    }-*/;

    /**
     * Sets the prerequsite card index (index into the variant's card array).
     * @param pPrereq the new value
     */
    public native void setPrereq(final int pPrereq)
    /*-{
        this.prereq = pPrereq;
    }-*/;

    /**
     * Determine if a prerequisite card is defined for this card.
     * @return <code>true</code> if yes
     */
    public native boolean hasPrereq()
    /*-{
        if (this.hasOwnProperty('prereq')) {
            return this.prereq >= 0;
        } else {
            return false;
        }
    }-*/;



    /**
     * Get the nominal cost of this card as printed on the card.
     * @return prerequsite card index
     */
    public native int getCostNominal()
    /*-{
        return this.costNominal;
    }-*/;

    /**
     * Sets the nominal cost of this card as printed on the card.
     * @param pCostNominal the new value
     */
    public native void setCostNominal(final int pCostNominal)
    /*-{
        this.costNominal = pCostNominal;
    }-*/;



    /**
     * Get credit given by this card to other cards. The index of the benefitting
     * card in this array is the same as that card's index in the array of cards in
     * the game variant. If a value is not present, the credit is 0.
     * @return array
     */
    public int[] getCreditGiven()
    {
        JsArrayInteger arr = getCreditGivenJs();
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
     * Get credit given by this card to one particular other card. The index of the
     * benefitting card in this array is the same as that card's index in the array
     * of cards in the game variant. If a value is not present, the credit is 0.
     * <p>Calling this method is just a faster way of calling
     * <code>getCreditGiven()[pIdx]</code>.
     * @param pIdx index into the creditGiven array field
     * @return the credit given in points
     */
    public int getCreditGiven(final int pIdx)
    {
        return getCreditGivenJs().get(pIdx);
    }

    /**
     * Setter.
     * @param pCreditGiven the new value
     * @see #getCreditGiven()
     */
    public void setCreditGiven(final int[] pCreditGiven)
    {
        JsArrayInteger arr = createArray().cast();
        if (pCreditGiven != null && pCreditGiven.length > 0) {
            for (int v : pCreditGiven) {
                arr.push(v);
            }
        }
        setCreditGivenJs(arr);
    }

    private native JsArrayInteger getCreditGivenJs()
    /*-{
        return this.creditGiven;
    }-*/;
    
    private native void setCreditGivenJs(final JsArrayInteger pCreditGiven)
    /*-{
        this.creditGiven = pCreditGiven;
    }-*/;



    /**
     * Get description of the card's effect as far as it doesn't pertain to calamities.
     * @return description of the card's effect
     */
    public native CbStringsI18nJSO getAttributes()
    /*-{
        return this.attributes;
    }-*/;

    /**
     * Sets the description of the card's effect as far as it doesn't pertain to
     * calamities.
     * @param pAttributes the new values
     */
    private native void setAttributes(final CbStringsI18nJSO pAttributes)
    /*-{
        this.attributes = pAttributes;
    }-*/;

    /**
     * Get description of the card's effect best matching the current locale.
     * @return the localized description of the card's effect
     */
    public String getLocalizedAttributes()
    {
        return getAttributes().getStringI18n();
    }



    /**
     * Get the description of the card's effect on calamities.
     * @return description of the card's effect on calamities
     */
    public native CbStringsI18nJSO getClamityEffects()
    /*-{
        return this.calamityEffects;
    }-*/;

    /**
     * Sets the description of the card's effect on calamities.
     * @param pCalamityEffects the new values
     */
    private native void setClamityEffects(final CbStringsI18nJSO pCalamityEffects)
    /*-{
        this.calamityEffects = pCalamityEffects;
    }-*/;

    /**
     * Get description of the card's effect best matching the current locale.
     * @return the localized description of the card's effect on calamities
     */
    public String getLocalizedCalamityEffects()
    {
        return getClamityEffects().getStringI18n();
    }
}
