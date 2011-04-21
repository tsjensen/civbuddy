/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-04-15
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
package com.tj.civ.client.common;

import com.google.gwt.place.shared.Place;

import com.tj.civ.client.model.CcGame;


/**
 * Stores application-global fields.
 * 
 * <p>Passing them around as part of the {@link com.google.gwt.place.shared.Place Place}
 * payload didn't work in production mode.
 *
 * @author Thomas Jensen
 */
public final class CbGlobal
{
    /** the instance of this singleton */
    private static final CbGlobal INSTANCE = new CbGlobal();

    /** the globally active current game */
    private CcGame iGame = null;

    /** the previous place, set upon going to another place */
    private Place iPreviousPlace = null;



    /**
     * Private constructor.
     */
    private CbGlobal()
    {
        super();
    }



    public static CcGame getGame()
    {
        return INSTANCE.iGame;
    }

    /**
     * Setter.
     * @param pGame the new globally active current game
     */
    public static void setGame(final CcGame pGame)
    {
        INSTANCE.iGame = pGame;
    }



    public static Place getPreviousPlace()
    {
        return INSTANCE.iPreviousPlace;
    }

    public static void setPreviousPlace(final Place pPreviousPlace)
    {
        INSTANCE.iPreviousPlace = pPreviousPlace;
    }



    /**
     * Getter.
     * @return <code>true</code> if a global game is set (this should always be
     *      <code>true</code> unless we just entered the app)
     */
    public static boolean isSet()
    {
        return INSTANCE.iGame != null;
    }
}
