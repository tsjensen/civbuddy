/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 20.03.2011
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
 * The 'Cards' place.
 *
 * @author Thomas Jensen
 */
public class CcCardsPlace
    extends Place
{
    /** separator character bewteen game and player name in token */
    public static final char SEP = '$';

    /** the persistence key of the current game */
    private String iGameKey;

    /** the name of the current player */
    private String iPlayerName;



    /**
     * Performs text serialization and deserialization of {@link CcCardsPlace}s.
     * @author Thomas Jensen
     */
    public static class CcTokenizer implements PlaceTokenizer<CcCardsPlace>
    {
        @Override
        public String getToken(final CcCardsPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.iGameKey + SEP + pPlace.iPlayerName;
        }

        @Override
        public CcCardsPlace getPlace(final String pToken)
        {
            return new CcCardsPlace(pToken);
        }
    }



    /**
     * Constructor.
     * @param pGameKey the persistence key of the current game
     * @param pPlayerName the name of the current player
     */
    public CcCardsPlace(final String pGameKey, final String pPlayerName)
    {
        super();
        iGameKey = pGameKey;
        iPlayerName = pPlayerName;
    }



    /**
     * Constructor.
     * @param pToken the token representing the place state saved in the URL
     */
    public CcCardsPlace(final String pToken)
    {
        iGameKey = null;
        iPlayerName = null;
        if (pToken != null) {
            int dPos = pToken.indexOf(SEP);
            if (dPos > 0) {
                iGameKey = pToken.substring(0, dPos).trim();
                if (iGameKey.length() < 1) {
                    iGameKey = null;
                } else {
                    iPlayerName = pToken.substring(dPos + 1).trim();
                    if (iPlayerName.length() < 1) {
                        iPlayerName = null;
                    }
                }
            }
        }
    }



    public boolean isValid()
    {
        return iGameKey != null && iGameKey.length() > 0
            && iPlayerName != null && iPlayerName.length() > 0;
    }



    public String getGameKey()
    {
        return iGameKey;
    }

    public void setGameKey(final String pGameKey)
    {
        iGameKey = pGameKey;
    }



    public String getPlayerName()
    {
        return iPlayerName;
    }

    public void setPlayerName(final String pPlayerName)
    {
        iPlayerName = pPlayerName;
    }
}
