/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 28.03.2011
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
 * The 'Funds' place.
 *
 * @author Thomas Jensen
 */
public class CbFundsPlace
    extends Place
{
    /** the persistence key of the current situation, if we were navigated to by
     *  bookmark */
    private String iSitKey;

    /** the active situation, if we were navigated to from the 'Cards' place */
    private CcSituation iSituation;



    /**
     * Constructor.
     * @param pSitKey the persistence key of the current situation
     */
    public CbFundsPlace(final String pSitKey)
    {
        super();
        iSitKey = pSitKey;
        iSituation = null;
    }



    /**
     * Constructor.
     * @param pSituation the currently active situation
     */
    public CbFundsPlace(final CcSituation pSituation)
    {
        super();
        iSitKey = pSituation.getPersistenceKey();
        iSituation = pSituation;
    }



    /**
     * Performs text serialization and deserialization of {@link CbFundsPlace}s.
     * @author Thomas Jensen
     */
    public static class CcTokenizer implements PlaceTokenizer<CbFundsPlace>
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
        return iSitKey;
    }



    public CcSituation getSituation()
    {
        return iSituation;
    }
}
