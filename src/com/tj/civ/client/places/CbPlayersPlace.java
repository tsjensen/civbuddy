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
 * The 'Players' place.
 *
 * @author Thomas Jensen
 */
public class CcPlayersPlace
    extends Place
{
    /** the name of the currently marked game */
    private String iMarkedGame;

    /** the name of the currently marked player */
    private String iMarkedPlayer;

    /** separator character bewteen game and player name in token */
    public static final char SEP = '$';



    /**
     * Constructor.
     * @param pToken the token representing the place state saved in the URL
     */
    public CcPlayersPlace(final String pToken)
    {
        super();
        iMarkedGame = null;
        iMarkedPlayer = null;
        if (pToken != null) {
            int dPos = pToken.indexOf(SEP);
            if (dPos > 0) {
                iMarkedGame = pToken.substring(0, dPos).trim();
                if (iMarkedGame.length() < 1) {
                    iMarkedGame = null;
                } else {
                    iMarkedPlayer = pToken.substring(dPos + 1).trim();
                    if (iMarkedPlayer.length() < 1) {
                        iMarkedPlayer = null;
                    }
                }
            } else {
                iMarkedGame = pToken.trim();
            }
        }
    }



    /**
     * Performs text serialization and deserialization of {@link CcPlayersPlace}s.
     * @author Thomas Jensen
     */
    public static class CcTokenizer implements PlaceTokenizer<CcPlayersPlace>
    {
        @Override
        public String getToken(final CcPlayersPlace pPlace)
        {
            return pPlace.iMarkedGame + SEP + pPlace.iMarkedPlayer;
        }

        @Override
        public CcPlayersPlace getPlace(final String pToken)
        {
            return new CcPlayersPlace(pToken);
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



    public String getMarkedPlayer()
    {
        return iMarkedPlayer;
    }

    public void setMarkedPlayer(final String pMarkedPlayer)
    {
        iMarkedPlayer = pMarkedPlayer;
    }
}
