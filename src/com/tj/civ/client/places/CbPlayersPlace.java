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

import com.google.gwt.place.shared.PlaceTokenizer;


/**
 * The 'Players' place.
 *
 * @author Thomas Jensen
 */
public class CbPlayersPlace
    extends CbAbstractPlace
{
    /** the persistence key of the active game */
    private String iGameKey;



    /**
     * Performs text serialization and deserialization of {@link CbPlayersPlace}s.
     * @author Thomas Jensen
     */
    public static class CcTokenizer implements PlaceTokenizer<CbPlayersPlace>
    {
        @Override
        public String getToken(final CbPlayersPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.iGameKey;
        }

        @Override
        public CbPlayersPlace getPlace(final String pToken)
        {
            return new CbPlayersPlace(pToken);
        }
    }



    /**
     * Constructor.
     * @param pGameKey the token representing the place state saved in the URL
     */
    public CbPlayersPlace(final String pGameKey)
    {
        super();
        iGameKey = pGameKey != null ? pGameKey.trim() : null;
    }



    public String getGameKey()
    {
        return iGameKey;
    }



    @Override
    public String getToken()
    {
        return iGameKey;
    }
}
