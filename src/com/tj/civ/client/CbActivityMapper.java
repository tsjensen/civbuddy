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
package com.tj.civ.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.tj.civ.client.activities.CbFundsActivity;
import com.tj.civ.client.activities.CbCardsActivity;
import com.tj.civ.client.activities.CbGamesActivity;
import com.tj.civ.client.activities.CbPlayersActivity;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbGamesPlace;
import com.tj.civ.client.places.CbPlayersPlace;


/**
 * Maps places to activities.
 *
 * @author Thomas Jensen
 */
public class CbActivityMapper
    implements ActivityMapper
{
    /** our client factory */
    private CbClientFactoryIF iClientFactory;



    /**
     * Constructor.
     * @param pClientFactory our client factory
     */
    public CbActivityMapper(final CbClientFactoryIF pClientFactory)
    {
        super();
        iClientFactory = pClientFactory;
    }



    @Override
    public Activity getActivity(final Place pPlace)
    {
        if (pPlace instanceof CbGamesPlace) {
            return new CbGamesActivity(iClientFactory);
        } else if (pPlace instanceof CbPlayersPlace) {
            return new CbPlayersActivity((CbPlayersPlace) pPlace, iClientFactory);
        } else if (pPlace instanceof CbCardsPlace) {
            return new CbCardsActivity((CbCardsPlace) pPlace, iClientFactory);
        } else if (pPlace instanceof CbFundsPlace) {
            return new CbFundsActivity((CbFundsPlace) pPlace, iClientFactory);
        }
        return null;
    }
}
