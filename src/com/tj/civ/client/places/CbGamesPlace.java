/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 15.02.2011
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
package com.tj.civ.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;


/**
 * The 'Games' place.
 *
 * @author Thomas Jensen
 */
public class CcGamesPlace
    extends Place
{
    /** the name of the currently marked game */
    private String iMarkedGame;



    /**
     * Constructor.
     * @param pToken the token representing the place state saved in the URL
     */
    public CcGamesPlace(final String pToken)
    {
        super();
        iMarkedGame = pToken;
    }



    /**
     * Performs text serialization and deserialization of {@link CcGamesPlace}s.
     * @author Thomas Jensen
     */
    public static class CcTokenizer implements PlaceTokenizer<CcGamesPlace>
    {
        @Override
        public String getToken(final CcGamesPlace pPlace)
        {
            return pPlace.iMarkedGame;
        }

        @Override
        public CcGamesPlace getPlace(final String pToken)
        {
            return new CcGamesPlace(pToken);
        }
    }



    public String getMarkedGame()
    {
        return iMarkedGame;
    }

    public void setMarkedGame(final String pMarkedGame)
    {
        iMarkedGame = pMarkedGame;
    }
}
