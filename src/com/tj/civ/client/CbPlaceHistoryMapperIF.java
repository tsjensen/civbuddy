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

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CbGamesPlace;
import com.tj.civ.client.places.CbPlayersPlace;


/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 * 
 * @author Thomas Jensen
 */
@WithTokenizers({ CbGamesPlace.CbTokenizer.class, CbPlayersPlace.CbTokenizer.class,
    CbCardsPlace.CbTokenizer.class, CbFundsPlace.CbTokenizer.class })
public interface CbPlaceHistoryMapperIF
    extends PlaceHistoryMapper
{
    // empty body
}
