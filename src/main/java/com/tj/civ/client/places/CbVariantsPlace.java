/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-05-05
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;


/**
 * The 'Variants' place.
 *
 * @author Thomas Jensen
 */
public class CbVariantsPlace
    extends CbAbstractPlace
{
    /** a game name (<em>not</em> a game key, because the game does not exist yet) */
    private String iGameName;



    /**
     * Performs text serialization and deserialization of {@link CbVariantsPlace}s.
     * @author Thomas Jensen
     */
    public static class CbTokenizer implements PlaceTokenizer<CbVariantsPlace>
    {
        @Override
        public String getToken(final CbVariantsPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.iGameName;
        }

        @Override
        public CbVariantsPlace getPlace(final String pToken)
        {
            return new CbVariantsPlace(pToken);
        }
    }



    /**
     * Constructor.
     * @param pGameName the token representing the place state saved in the URL
     */
    public CbVariantsPlace(final String pGameName)
    {
        super();
        iGameName = pGameName != null ? pGameName.trim() : null;
    }



    public String getGameName()
    {
        return iGameName;
    }



    @Override
    public String getToken()
    {
        return iGameName;
    }
}
