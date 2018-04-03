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
package org.civbuddy.client.views;

import org.civbuddy.client.places.CbAbstractPlace;


/**
 * Common super interface of the MVP presenter interfaces.
 *
 * @author Thomas Jensen
 */
public interface CbCanGoPlacesIF
{
    /**
     * Switch the current place to the given place.
     * @param pPlace the target place
     */
    void goTo(final CbAbstractPlace pPlace);
}