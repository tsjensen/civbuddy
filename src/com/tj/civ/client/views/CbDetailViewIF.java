/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 20.07.2011
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
package com.tj.civ.client.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.tj.civ.client.model.vo.CbDetailVO;


/**
 * Decribes the 'Detail' view.
 *
 * @author Thomas Jensen
 */
public interface CbDetailViewIF
    extends IsWidget
{
    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CbPresenterIF pPresenter);

    /**
     * Display the given card details.
     * @param pDetails the pre-prepared card details, ready for display
     */
    void showCard(final CbDetailVO pDetails);



    /**
     * Describes the presenter of the 'Detail' view.
     * 
     * @author Thomas Jensen
     */
    public interface CbPresenterIF
        extends CbCanGoPlacesIF
    {
        /**
         * Prepare detail data for the given card and instruct the view to show it.
         * @param pCardIdx card index into the original array
         */
        void showCard(final int pCardIdx);
    }
}
