/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-28
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;


/**
 * The 'Funds' place.
 *
 * @author Thomas Jensen
 */
public class CbFundsPlace
    extends CbAbstractPlace
{
    /** the persistence key of the current situation */
    private String iSituationKey;



    /**
     * Constructor.
     * @param pSituationKey the persistence key of the current situation
     */
    public CbFundsPlace(final String pSituationKey)
    {
        super();
        iSituationKey = pSituationKey;
    }



    /**
     * Performs text serialization and deserialization of {@link CbFundsPlace}s.
     * @author Thomas Jensen
     */
    public static class CbTokenizer implements PlaceTokenizer<CbFundsPlace>
    {
        @Override
        public String getToken(final CbFundsPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.getSituationKey();
        }

        @Override
        public CbFundsPlace getPlace(final String pToken)
        {
            return new CbFundsPlace(pToken);
        }
    }



    public String getSituationKey()
    {
        return iSituationKey;
    }



    @Override
    public String getToken()
    {
        return iSituationKey;
    }
}
