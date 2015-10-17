/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-05
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
 * Represents a player in an actual game.
 *
 * @author Thomas Jensen
 */
public final class CbPlayerJSO
    extends JavaScriptObject
{
    /** a player name may be at most 25 characters in length */
    public static final int PLAYER_NAME_MAXLEN = 25;



    /**
     * JSO constructor.
     */
    protected CbPlayerJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return the new instance
     */
    public static CbPlayerJSO create()
    {
        CbPlayerJSO result = createObject().cast();
        result.setWinningTotal(0);
        return result;
    }



    /**
     * Get the player name.
     * @return the player name
     */
    public native String getName()
    /*-{
        return this.name;
    }-*/;

    /**
     * Set the player name.
     * @param pName the new value
     */
    public native void setName(final String pName)
    /*-{
        this.name = pName;
    }-*/;



    /**
     * Get the target points of this player's civilization.
     * @return points
     */
    public native int getWinningTotal()
    /*-{
        return this.target;
    }-*/;

    /**
     * Set the target points of this player's civilization.
     * @param pWinningTotal the new value
     */
    public native void setWinningTotal(final int pWinningTotal)
    /*-{
        this.target = pWinningTotal;
    }-*/;
}
