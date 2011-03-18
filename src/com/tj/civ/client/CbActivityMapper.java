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
import com.tj.civ.client.activities.CcGamesActivity;
import com.tj.civ.client.activities.CcPlayersActivity;
import com.tj.civ.client.places.CcGamesPlace;
import com.tj.civ.client.places.CcPlayersPlace;


/**
 * Maps places to activities.
 *
 * @author Thomas Jensen
 */
public class CcActivityMapper
    implements ActivityMapper
{
    /** our client factory */
    private CcClientFactoryIF iClientFactory;



    /**
     * Constructor.
     * @param pClientFactory our client factory
     */
    public CcActivityMapper(final CcClientFactoryIF pClientFactory)
    {
        super();
        iClientFactory = pClientFactory;
    }



    @Override
    public Activity getActivity(final Place pPlace)
    {
        if (pPlace instanceof CcGamesPlace) {
            return new CcGamesActivity(iClientFactory);
        } else if (pPlace instanceof CcPlayersPlace) {
            return new CcPlayersActivity((CcPlayersPlace) pPlace, iClientFactory);
        }
        return null;
    }
}
