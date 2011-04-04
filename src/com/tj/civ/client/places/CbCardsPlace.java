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

import com.tj.civ.client.model.CcSituation;


/**
 * The 'Cards' place.
 *
 * @author Thomas Jensen
 */
public class CcCardsPlace
    extends Place
{
    /** the persistence key of the current situation, if we were navigated to by
     *  bookmark */
    private String iSitKey;

    /** the active situation, if we were navigated to from the 'Funds' place */
    private CcSituation iSituation;



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
            return pPlace.getSituationKey();
        }

        @Override
        public CcCardsPlace getPlace(final String pToken)
        {
            return new CcCardsPlace(pToken);
        }
    }



    /**
     * Constructor.
     * @param pSitKey the persistence key of the current situation
     */
    public CcCardsPlace(final String pSitKey)
    {
        super();
        iSitKey = pSitKey;
        iSituation = null;
    }



    /**
     * Constructor.
     * @param pSituation the currently active situation
     */
    public CcCardsPlace(final CcSituation pSituation)
    {
        super();
        iSitKey = pSituation.getPersistenceKey();
        iSituation = pSituation;
    }



    public String getSituationKey()
    {
        return iSitKey;
    }



    public CcSituation getSituation()
    {
        return iSituation;
    }
}
