/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-15
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import org.civbuddy.client.activities.CbDetailActivity;
import org.civbuddy.client.activities.CbFundsActivity;
import org.civbuddy.client.activities.CbCardsActivity;
import org.civbuddy.client.activities.CbGamesActivity;
import org.civbuddy.client.activities.CbPlayersActivity;
import org.civbuddy.client.activities.CbVariantsActivity;
import org.civbuddy.client.places.CbDetailPlace;
import org.civbuddy.client.places.CbFundsPlace;
import org.civbuddy.client.places.CbCardsPlace;
import org.civbuddy.client.places.CbGamesPlace;
import org.civbuddy.client.places.CbPlayersPlace;
import org.civbuddy.client.places.CbVariantsPlace;


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
            return new CbGamesActivity((CbGamesPlace) pPlace, iClientFactory);
        } else if (pPlace instanceof CbPlayersPlace) {
            return new CbPlayersActivity((CbPlayersPlace) pPlace, iClientFactory);
        } else if (pPlace instanceof CbCardsPlace) {
            return new CbCardsActivity((CbCardsPlace) pPlace, iClientFactory);
        } else if (pPlace instanceof CbFundsPlace) {
            return new CbFundsActivity((CbFundsPlace) pPlace, iClientFactory);
        } else if (pPlace instanceof CbDetailPlace) {
            return new CbDetailActivity((CbDetailPlace) pPlace, iClientFactory);
        } else if (pPlace instanceof CbVariantsPlace) {
            return new CbVariantsActivity((CbVariantsPlace) pPlace, iClientFactory);
        }
        return null;
    }
}
