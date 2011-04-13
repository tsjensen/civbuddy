/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-15
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

import com.tj.civ.client.model.CcGame;


/**
 * The 'Players' place.
 *
 * @author Thomas Jensen
 */
public class CcPlayersPlace
    extends Place
{
    /** the persistence key of the active game */
    private String iGameKey;

    /** the active game, if we were navigated to from the 'Cards' place */
    private CcGame iGame;



    /**
     * Performs text serialization and deserialization of {@link CcPlayersPlace}s.
     * @author Thomas Jensen
     */
    public static class CcTokenizer implements PlaceTokenizer<CcPlayersPlace>
    {
        @Override
        public String getToken(final CcPlayersPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.iGameKey;
        }

        @Override
        public CcPlayersPlace getPlace(final String pToken)
        {
            return new CcPlayersPlace(pToken);
        }
    }



    /**
     * Constructor.
     * @param pGameKey the token representing the place state saved in the URL
     */
    public CcPlayersPlace(final String pGameKey)
    {
        super();
        iGameKey = pGameKey != null ? pGameKey.trim() : null;
        iGame = null;
    }



    /**
     * Constructor.
     * @param pGame the active game, if we were navigated to from the 'Cards' place
     */
    public CcPlayersPlace(final CcGame pGame)
    {
        super();
        iGameKey = pGame.getPersistenceKey();
        iGame = pGame;
    }



    public String getMarkedGameKey()
    {
        return iGameKey;
    }



    public CcGame getGame()
    {
        return iGame;
    }
}
