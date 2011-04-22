/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 05.03.2011
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

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.model.CcGame;


/**
 * The persitable part of a {@link CcGame}.
 *
 * @author Thomas Jensen
 */
public final class CcGameJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CcGameJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return a new instance
     */
    public static CcGameJSO create()
    {
        CcGameJSO result = createObject().cast();
        result.setSitMapJs(CcStringsI18nJSO.create());
        return result;
    }



    /**
     * Factory method.
     * @param pJson the JSON representation of a {@link CcGameJSO}
     * @return a new instance
     */
    public static CcGameJSO create(final String pJson)
    {
        CcGameJSO result = CbUtil.createFromJson(pJson);
        if (result == null) {
            // fall back to an empty object if the given JSON cannot be grokked
            result = create();
        }
        return result;
    }



    /**
     * Get the game name.
     * @return the game name
     */
    public native String getName()
    /*-{
        return this.name;
    }-*/;

    /**
     * Set the game name.
     * @param pName the new value
     */
    public native void setName(final String pName)
    /*-{
        this.name = pName;
    }-*/;



    /**
     * Get the variant ID.
     * @return the variant ID
     */
    public native String getVariantId()
    /*-{
        return this.variantID;
    }-*/;

    /**
     * Set the variant ID.
     * @param pVariantId the new value
     */
    public native void setVariantId(final String pVariantId)
    /*-{
        this.variantID = pVariantId;
    }-*/;



    /**
     * Remove the given player and his/her situation from the map.
     * @param pPlayerName player name
     */
    public void removePlayer(final String pPlayerName)
    {
        getSitMapJs().remove(pPlayerName);
    }

    /**
     * Put the given situation into our map.
     * @param pPlayerName player name
     * @param pSituationKey situation UUID
     */
    public void addPlayer(final String pPlayerName, final String pSituationKey)
    {
        getSitMapJs().setStringI18n(pPlayerName, pSituationKey);
    }

    public Map<String, String> getPlayers()
    {
        return getSitMapJs().getAsMap();
    }

    private native CcStringsI18nJSO getSitMapJs()
    /*-{
        return this.sitMap;
    }-*/;

    private native void setSitMapJs(final CcStringsI18nJSO pMap)
    /*-{
        this.sitMap = pMap;
    }-*/;
}
