/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-05
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

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

import org.civbuddy.client.common.CbUtil;
import org.civbuddy.client.model.CbGame;


/**
 * The persitable part of a {@link CbGame}.
 *
 * @author Thomas Jensen
 */
public final class CbGameJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CbGameJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return a new instance
     */
    public static CbGameJSO create()
    {
        CbGameJSO result = createObject().cast();
        result.setSitMapJs(CbStringsI18nJSO.create());
        return result;
    }



    /**
     * Factory method.
     * @param pJson the JSON representation of a {@link CbGameJSO}
     * @return a new instance
     */
    public static CbGameJSO create(final String pJson)
    {
        CbGameJSO result = CbUtil.createFromJson(pJson);
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
     * Get the variant key.
     * @return the variant key
     */
    public native String getVariantKey()
    /*-{
        return this.variant;
    }-*/;

    /**
     * Set the variant key.
     * @param pVariantKey the new value
     */
    public native void setVariantKey(final String pVariantKey)
    /*-{
        this.variant = pVariantKey;
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

    private native CbStringsI18nJSO getSitMapJs()
    /*-{
        return this.sitMap;
    }-*/;

    private native void setSitMapJs(final CbStringsI18nJSO pMap)
    /*-{
        this.sitMap = pMap;
    }-*/;
}
