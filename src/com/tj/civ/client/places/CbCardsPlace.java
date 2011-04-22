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

import com.google.gwt.place.shared.PlaceTokenizer;


/**
 * The 'Cards' place.
 *
 * @author Thomas Jensen
 */
public class CbCardsPlace
    extends CbAbstractPlace
{
    /** the persistence key of the current situation */
    private String iSituationKey;



    /**
     * Performs text serialization and deserialization of {@link CbCardsPlace}s.
     * @author Thomas Jensen
     */
    public static class CbTokenizer implements PlaceTokenizer<CbCardsPlace>
    {
        @Override
        public String getToken(final CbCardsPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.getSituationKey();
        }

        @Override
        public CbCardsPlace getPlace(final String pToken)
        {
            return new CbCardsPlace(pToken);
        }
    }



    /**
     * Constructor.
     * @param pSitKey the persistence key of the current situation
     */
    public CbCardsPlace(final String pSitKey)
    {
        super();
        iSituationKey = pSitKey;
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
