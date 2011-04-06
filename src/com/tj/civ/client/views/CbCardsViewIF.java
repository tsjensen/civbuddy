/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-22
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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CcPlayersPlace;


/**
 * Describes the 'Cards' view.
 *
 * @author Thomas Jensen
 */
public interface CbCardsViewIF
    extends IsWidget
{
    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CcPresenterIF pPresenter);



    /**
     *  Getter.
     *  @return flag indicating if the view is in 'Revise' mode
     */
    boolean isRevising();



    /**
     * Getter.
     * @return <code>true</code> if the commit button is currently enabled
     */
    boolean isCommitButtonEnabled();

    /**
     * Setter.
     * @param pEnabled enable (<code>true</code>) or disable (<code>false</code>)
     *              the commit button
     */
    void setCommitButtonEnabled(final boolean pEnabled);



    /**
     * Initialize the entire cards grid with new cards and states. This is called
     * initially when a new player situation was selected.
     * @param pCardsCurrent the current card states and configs
     */
    void initializeGridContents(final CcCardCurrent[] pCardsCurrent);



    /**
     * Set the state of a card in the view display. This updates the row style,
     * the state widget, and the state reason tooltip on the given row.
     * @param pRowIdx the row index
     * @param pNewState the new state
     * @param pStateReason the state reason tooltip text, or <code>null</code> if none
     */
    void setState(final int pRowIdx, final CcState pNewState, final String pStateReason);



    /**
     * Set the current cost displayed of a card.
     * @param pRowIdx the card's row index
     * @param pCurrentCost the value to set
     */
    void setCostDisplay(final int pRowIdx, final int pCurrentCost);



    /**
     * Updates the credit bar of the card at position <tt>pRowIdx</tt> as effected
     * by the card at position <tt>pGivingCardIdx</tt>.
     * @param pRowIdx the index of the card which is updated
     * @param pGivingCardIdx the index of the card giving the credit which justifies
     *              the update
     */
    void updateCreditBar(final int pRowIdx, final int pGivingCardIdx);



    /**
     * Update the funds display.
     * @param pTotalFunds new value of total funds
     * @param pEnabled whether funds tracking is generally enabled or not
     */
    void updateFunds(final int pTotalFunds, final boolean pEnabled);



    /**
     * Getter.
     * @param pRowIdx row index
     * @return gets the state reason set on the given row
     */
    String getStateReason(final int pRowIdx);



    /**
     * Getter.
     * @return the topmost widget that defines the composite which is this view
     */
    Widget getWidget();



    /**
     * Describes the presenter of the 'Cards' view.
     * 
     * @author Thomas Jensen
     */
    public interface CcPresenterIF
        extends CcCanGoPlacesIF
    {
        /**
         * Return the 'Players' place for the current game.
         * @return the 'Players' place
         */
        CcPlayersPlace getPlayersPlace();



        /**
         * Return the 'Funds' place for the current game.
         * @return the 'Funds' place
         */
        CbFundsPlace getFundsPlace();



        /**
         * Determine if any cards are in the state 'Planned'. 
         * @return <code>true</code> if so
         */
        boolean hasAnyPlans();



        /**
         * Set cards flagged as 'Planned' to 'Owned'.
         */
        void commit();



        /**
         * Switches the 'Cards' presenter from planning mode to revise mode.
         */
        void enterReviseMode();



        /**
         * Switches the 'Cards' presenter from revise mode to planning mode.
         */
        void leaveReviseMode();



        /**
         * Called when the 'More' arrows of a card are clicked.
         * @param pRowIdx the card's index
         */
        void onMoreClicked(final int pRowIdx);



        /**
         * Called when a state change click was detected in the view.
         * @param pRowIdx the card's index
         */
        void onStateClicked(final int pRowIdx);



        /**
         * Getter.
         * @return the current array of cards in the order defined by the variant
         */
        CcCardCurrent[] getCardsCurrent();



        /**
         * Getter.
         * @return sum of current costs of the currently planned cards
         */
        int getPlannedInvestment();



        /**
         * Sets the state of a card.
         * @param pCard the card to change (unmodified!)
         * @param pNewState the new state to set
         * @param pStateReason the state reason to display as a tooltip, or
         *              <code>null</code> for no reason
         */
        void setState(final CcCardCurrent pCard, final CcState pNewState,
            final String pStateReason);



        /**
         * Determine the number of cards in states 'Owned' or 'Planned'.
         * @return just that
         */
        int getNumCardsAffectingCredit();



        /**
         * Getter.
         * @return the sum of the nominal costs of all planned cards, plus the sum
         *          of the nominal costs of all cards already owned (i.e. the
         *          potential future score)
         */
        int getNominalSumInclPlan();



        /**
         * Getter.
         * @return the view that this presenter is currently associated to
         */
        CbCardsViewIF getView();



        /**
         * Getter.
         * @return our event bus
         */
        EventBus getEventBus();
    }
}
