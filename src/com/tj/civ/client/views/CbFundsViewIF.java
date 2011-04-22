/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-31
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

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;

import com.tj.civ.client.event.CbCommSpinnerPayload;
import com.tj.civ.client.model.jso.CcCommodityConfigJSO;
import com.tj.civ.client.model.jso.CcFundsJSO;


/**
 * Describes the 'Funds' view.
 *
 * @author Thomas Jensen
 */
public interface CbFundsViewIF
    extends IsWidget, HasEnabled
{
    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CbPresenterIF pPresenter);



    /**
     * Initialize the view with the given data. This is called once per
     * activity life cycle, so it may eventually be called many times.
     * @param pCommodities the commodity definition of the game variant
     * @param pFundsJso the entire funds data
     */
    void initialize(final CcCommodityConfigJSO[] pCommodities, final CcFundsJSO pFundsJso);



    /**
     * Sets a new value into the total funds input box.
     * @param pNewValue the value to set
     */
    void setTotalFundsBoxOnly(final int pNewValue);



    /**
     * Sets a new value into the total funds input box and also updates the total
     * funds indicator at the top of the view.
     * @param pNewValue the value to set
     */
    void setTotalFunds(final int pNewValue);



    /**
     * Updates the display value of the current number of commodity cards held.
     * @param pNewValue the new number
     */
    void setNumCommodities(final int pNewValue);



    /**
     * Sets a new value into the 'bonus' input box.
     * @param pBonus the value to set
     */
    void setBonusBoxOnly(final int pBonus);



    /**
     * Puts the 'Enable Detailed Tracking' toggle button into the given state.
     * @param pDetailed the value to set
     */
    void setDetailTracking(final boolean pDetailed);



    /**
     * Describes the presenter of the 'Funds' view.
     * 
     * @author Thomas Jensen
     */
    public interface CbPresenterIF
        extends CbCanGoPlacesIF
    {
        /**
         * Reset all funds values in the model to zero, then trigger a view update.
         */
        void reset();



        /**
         * The 'Enable Funds Tracking' overall activation toggle button was pressed.
         * @param pEnabled the new button state
         */
        void onEnableToggled(final boolean pEnabled);



        /**
         * The 'Enable Detailed Tracking' toggle button was pressed.
         * @param pDetailed the new button state
         */
        void onDetailToggled(final boolean pDetailed);



        /**
         * A new value was entered into the total funds input field. 
         * @param pNewValue the value just entered
         */
        void onTotalFundsBoxChanged(final Integer pNewValue);



        /**
         * The value of one of the commodity spinners has changed.
         * @param pValue a data packet describing the change, i.e. delta points
         *          and delta number of commodity cards
         */
        void onSpinnerChanged(final CbCommSpinnerPayload pValue);



        /**
         * A new value was entered into the 'bonus' input field.
         * @param pNewValue the value just entered
         */
        void onBonusChanged(final Integer pNewValue);



        /**
         * Navigate back to the 'Cards' view.
         */
        void goBack();
    }
}
