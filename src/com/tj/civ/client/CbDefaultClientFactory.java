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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

import com.tj.civ.client.views.CbCardsView;
import com.tj.civ.client.views.CbCardsViewIF;
import com.tj.civ.client.views.CbFundsView;
import com.tj.civ.client.views.CbFundsViewIF;
import com.tj.civ.client.views.CcGamesView;
import com.tj.civ.client.views.CcGamesViewIF;
import com.tj.civ.client.views.CcPlayersView;
import com.tj.civ.client.views.CcPlayersViewIF;


/**
 * Our application's client factory default implementation.
 *
 * @author Thomas Jensen
 */
public class CcDefaultClientFactory
    implements CcClientFactoryIF
{
    /** the event bus instance */
    private static final EventBus EVENT_BUS = new SimpleEventBus();

    /** the place controller instance */
    private static final PlaceController PLACE_CTRL = new PlaceController(EVENT_BUS);

    /** the 'Games' view instance */
    private static final CcGamesViewIF GAMES_VIEW = new CcGamesView();

    /** the 'Players' view instance */
    private static final CcPlayersViewIF PLAYERS_VIEW = new CcPlayersView();

    /** the 'Cards' view instance */
    private static final CbCardsViewIF CARDS_VIEW = new CbCardsView();

    /** the 'Funds' view instance */
    private static final CbFundsViewIF FUNDS_VIEW = new CbFundsView();



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
    public CcGamesViewIF getGamesView()
    {
        return GAMES_VIEW;
    }



    @Override
    public CcPlayersViewIF getPlayersView()
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
}
