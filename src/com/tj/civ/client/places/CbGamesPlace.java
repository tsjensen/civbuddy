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
    /** dummy token */
    private static final String TOKEN = "ok"; //$NON-NLS-1$



    /**
     * Constructor.
     */
    public CcGamesPlace()
    {
        super();
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
            return TOKEN;
        }

        @Override
        public CcGamesPlace getPlace(final String pToken)
        {
            return new CcGamesPlace();
        }
    }
}
