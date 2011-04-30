/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 15.01.2011
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
package com.tj.civ.client.event;



/**
 * Event being fired when the total funds available to the player have changed.
 * This happens when the player switches from the funds tab back to the cards tab.
 *
 * @author Thomas Jensen
 */
public class CbFundsEvent
    extends CbEvent<CbFundsHandlerIF>
{
    /** handler type */
    public static final Type<CbFundsHandlerIF> TYPE = new Type<CbFundsHandlerIF>();

    /** new value of the player's total funds */
    private int iFunds;

    /** <code>true</code> if funds tracking is enabled */
    private boolean iFundsEnabled;



    /**
     * Constructor.
     * @param pFunds new value of the player's total funds
     * @param pEnabled <code>true</code> if funds tracking is enabled
     */
    public CbFundsEvent(final int pFunds, final boolean pEnabled)
    {
        super();
        iFunds = pFunds;
        iFundsEnabled = pEnabled;
    }



    @Override
    public Type<CbFundsHandlerIF> getAssociatedType()
    {
        return TYPE;
    }



    @Override
    protected void dispatch(final CbFundsHandlerIF pHandler)
    {
        pHandler.onFundsChanged(this);
    }



    /**
     * Getter.
     * @return {@link #iFunds}
     */
    public int getFunds()
    {
        return iFunds;
    }



    /**
     * Getter.
     * @return {@link #iFundsEnabled}
     */
    public boolean isFundsEnabled()
    {
        return iFundsEnabled;
    }
}
