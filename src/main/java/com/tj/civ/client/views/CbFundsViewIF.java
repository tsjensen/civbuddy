/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-31
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.views;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

import com.tj.civ.client.model.jso.CbFundsJSO;


/**
 * Describes the 'Funds' view.
 *
 * @author Thomas Jensen
 */
public interface CbFundsViewIF
    extends IsWidget
{
    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CbPresenterIF pPresenter);



    /**
     * Initialize the view with the given situation. This is called once per
     * activity life cycle, so it may eventually be called many times.
     * <p>Prereq is a view which is already correctly initialized with the current
     * game variant. This should always be the case as the view listens for game
     * change events and updates itself accordingly.
     * @param pFundsJso the entire funds data
     * @param pNumCommodities number of commodity cards (counted in <tt>pFundsJso</tt>)
     */
    void initializeSituation(final CbFundsJSO pFundsJso, final int pNumCommodities);



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
     * @param pAnimate animate the checkbox change
     */
    void setDetailTracking(final boolean pDetailed, final boolean pAnimate);



    /**
     * Returns <code>true</code> if funds tracking is enabled,
     * <code>false</code> if not.
     * @return boolean
     */
    boolean isEnabled();

    /**
     * Sets whether funds tracking is enabled.
     * @param pEnabled <code>true</code> to enable funds tracking, <code>false</code>
     *          to disable it
     * @param pAnimate animate the checkbox change
     */
    void setEnabled(final boolean pEnabled, final boolean pAnimate);



    /**
     * Enable or disable the mining row.
     * @param pEnabled set to <code>true</code> when the 'Mining' card is bought, and false when it is no longer
     *      owned (e.g. after a revise action, or when a different player is activated)
     */
    void setMiningEnabled(final boolean pEnabled);



    /**
     * Set the value displayed as mining yield (mining bonus).
     * @param pNewValue the new value
     */
    void setMiningYield(final int pNewValue);



    /**
     * Sets a new value into the 'Treasury' input box.
     * @param pNewValue the value to set
     */
    void setTreasury(final int pNewValue);



    /**
     * Set the title bar text.
     * @param pTitle the new title
     */
    void setTitleHeading(final String pTitle);



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
         * The value of one of the commodity select boxes has changed.
         * @param pIdx index of the commodity that was changed
         * @param pNewNumber the new number of cards in this commodity
         */
        void onCommodityChange(final int pIdx, final int pNewNumber);



        /**
         * A new value was entered into the 'bonus' input field.
         * @param pNewValue the value just entered
         */
        void onBonusChanged(final Integer pNewValue);



        /**
         * A new value was entered into the 'Treasury' input field.
         * @param pNewValue the value just entered
         */
        void onTreasuryBoxChanged(final Integer pNewValue);



        /**
         * Getter.
         * @return our event bus
         */
        EventBus getEventBus();
    }
}
