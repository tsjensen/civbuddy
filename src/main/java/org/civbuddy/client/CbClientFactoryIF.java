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

import org.civbuddy.client.views.CbCardsViewIF;
import org.civbuddy.client.views.CbDetailViewIF;
import org.civbuddy.client.views.CbFundsViewIF;
import org.civbuddy.client.views.CbGamesViewIF;
import org.civbuddy.client.views.CbPlayersViewIF;
import org.civbuddy.client.views.CbVariantsViewIF;


/**
 * Our application's client factory.
 * 
 * @author Thomas Jensen
 */
public interface CbClientFactoryIF
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
    CbGamesViewIF getGamesView();



    /**
     * Get the 'Players' view.
     * @return the 'Players' view instance
     */
    CbPlayersViewIF getPlayersView();



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



    /**
     * Get the 'Variants' view.
     * @return the 'Variants' view instance
     */
    CbVariantsViewIF getVariantsView();



    /**
     * Get the 'Detail' view.
     * @return the 'Detail' view instance
     */
    CbDetailViewIF getDetailView();
}
