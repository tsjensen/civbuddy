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

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import org.civbuddy.client.views.CbCardsView;
import org.civbuddy.client.views.CbCardsViewIF;
import org.civbuddy.client.views.CbDetailView;
import org.civbuddy.client.views.CbDetailViewIF;
import org.civbuddy.client.views.CbFundsView;
import org.civbuddy.client.views.CbFundsViewIF;
import org.civbuddy.client.views.CbGamesView;
import org.civbuddy.client.views.CbGamesViewIF;
import org.civbuddy.client.views.CbPlayersView;
import org.civbuddy.client.views.CbPlayersViewIF;
import org.civbuddy.client.views.CbVariantsView;
import org.civbuddy.client.views.CbVariantsViewIF;


/**
 * Our application's client factory default implementation.
 *
 * @author Thomas Jensen
 */
public class CbDefaultClientFactory
    implements CbClientFactoryIF
{
    /** the event bus instance */
    private static final EventBus EVENT_BUS = new SimpleEventBus();

    /** the place controller instance */
    private static final PlaceController PLACE_CTRL = new PlaceController(EVENT_BUS);

    /** the 'Games' view instance */
    private static final CbGamesViewIF GAMES_VIEW = new CbGamesView();

    /** the 'Players' view instance */
    private static final CbPlayersViewIF PLAYERS_VIEW = new CbPlayersView();

    /** the 'Cards' view instance */
    private static final CbCardsViewIF CARDS_VIEW = new CbCardsView();

    /** the 'Funds' view instance */
    private static final CbFundsViewIF FUNDS_VIEW = new CbFundsView(EVENT_BUS);

    /** the 'Variants' view instance */
    private static final CbVariantsViewIF VARIANTS_VIEW = new CbVariantsView();

    /** the 'Detail' view instance */
    private static final CbDetailViewIF DETAIL_VIEW = new CbDetailView();



    @Override
    public EventBus getEventBus()
    {
        return EVENT_BUS;
    }



    @Override
    public PlaceController getPlaceController()
    {
        return PLACE_CTRL;
    }



    @Override
    public CbGamesViewIF getGamesView()
    {
        return GAMES_VIEW;
    }



    @Override
    public CbPlayersViewIF getPlayersView()
    {
        return PLAYERS_VIEW;
    }



    @Override
    public CbCardsViewIF getCardsView()
    {
        return CARDS_VIEW;
    }



    @Override
    public CbFundsViewIF getFundsView()
    {
        return FUNDS_VIEW;
    }



    @Override
    public CbVariantsViewIF getVariantsView()
    {
        return VARIANTS_VIEW;
    }



    @Override
    public CbDetailViewIF getDetailView()
    {
        return DETAIL_VIEW;
    }
}
