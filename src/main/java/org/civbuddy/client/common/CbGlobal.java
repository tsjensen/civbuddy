/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-04-15
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.common;

import com.google.gwt.place.shared.Place;

import org.civbuddy.client.model.CbCardCurrent;
import org.civbuddy.client.model.CbGame;
import org.civbuddy.client.model.CbSituation;
import org.civbuddy.client.model.jso.CbFundsJSO;


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
    private CbGame iGame = null;

    /** the previous place, set upon going to another place */
    private Place iPreviousPlace = null;



    /**
     * Private constructor.
     */
    private CbGlobal()
    {
        super();
    }



    public static CbGame getGame()
    {
        return INSTANCE.iGame;
    }

    /**
     * Setter.
     * @param pGame the new globally active current game
     */
    public static void setGame(final CbGame pGame)
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
     *      <code>true</code> unless we just entered the app). A situation does not
     *      need to be selected.
     */
    public static boolean isGameSet()
    {
        return INSTANCE.iGame != null;
    }



    /**
     * Getter.
     * @return <code>true</code> if a global game is set and a situation of that
     *      game is marked as the current situation
     */
    public static boolean isSituationSet()
    {
        return getCurrentSituation() != null;
    }



    /**
     * Convenience method for getting the current situation of the current game.
     * <p>Should be cached on method level (not above!).
     * @return the situation, or <code>null</code> if no game is set or the game
     *          has no situations yet
     */
    public static CbSituation getCurrentSituation()
    {
        CbSituation result = null;
        if (isGameSet()) {
            result = getGame().getCurrentSituation();
        }
        return result;
    }



    /**
     * Convenience method for getting the current cards array from the current
     * situation of the current game.
     * <p>Should be cached on method level (not above!).
     * @return reference to the array of current cards, or <code>null</code> if no
     *      game is set or the game has no situations yet
     */
    public static CbCardCurrent[] getCardsCurrent()
    {
        CbCardCurrent[] result = null;
        if (isGameSet()) {
            CbSituation sit = getCurrentSituation();
            if (sit != null) {
                result = sit.getCardsCurrent();
            }
        }
        return result;
    }



    /**
     * Convenience method for getting the current funds object from the current
     * situation of the current game.
     * <p>Should be cached on method level (not above!).
     * @return reference to the current funds, or <code>null</code> if no game is
     *      set or the game has no situations yet
     */
    public static CbFundsJSO getCurrentFunds()
    {
        CbFundsJSO result = null;
        if (isGameSet()) {
            CbSituation sit = getCurrentSituation();
            if (sit != null) {
                result = sit.getJso().getFunds();
            }
        }
        return result;
    }



    /**
     * Removes the current game from this instance.
     */
    public static void clearGame()
    {
        INSTANCE.iGame = null;
    }
}
