/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-13
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.activities;

import com.tj.civ.client.views.CbCanGoPlacesIF;


/**
 * Describes the presenter expected by
 * {@link com.tj.civ.client.views.CbAbstractListView}.
 *
 * @author Thomas Jensen
 */
public interface CbListPresenterIF
    extends CbCanGoPlacesIF
{
    /**
     * The 'New' button was clicked.
     */
    void onNewClicked();



    /**
     * The 'Change' button was clicked.
     * @param pItemId ID of the marked item
     */
    void onChangeClicked(final String pItemId);



    /**
     * The 'Remove' button was clicked.
     * @param pItemId ID of the marked item
     */
    void onRemoveClicked(final String pItemId);
}
