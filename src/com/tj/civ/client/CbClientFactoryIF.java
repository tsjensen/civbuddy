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
import com.google.gwt.place.shared.PlaceController;

import com.tj.civ.client.views.CbCardsViewIF;
import com.tj.civ.client.views.CbFundsViewIF;
import com.tj.civ.client.views.CcGamesViewIF;
import com.tj.civ.client.views.CcPlayersViewIF;


/**
 * Our application's client factory.
 * 
 * @author Thomas Jensen
 */
public interface CcClientFactoryIF
{
    /**
     * Get the event bus.
     * 
     * @return the event bus instance
     */
    EventBus getEventBus();



    /**
     * Get the place controller.
     * @return the place controller instance
     */
    PlaceController getPlaceController();



    /**
     * Get the 'Games' view.
     * @return the 'Games' view instance
     */
    CcGamesViewIF getGamesView();



    /**
     * Get the 'Players' view.
     * @return the 'Players' view instance
     */
    CcPlayersViewIF getPlayersView();



    /**
     * Get the 'Cards' view.
     * @return the 'Cards' view instance
     */
    CbCardsViewIF getCardsView();



    /**
     * Get the 'Funds' view.
     * @return the 'Funds' view instance
     */
    CbFundsViewIF getFundsView();
}
