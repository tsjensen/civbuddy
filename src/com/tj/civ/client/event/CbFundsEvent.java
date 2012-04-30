/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-15
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbUtil;



/**
 * Event being fired when the total funds available to the player have changed.
 * This happens when the player switches from the funds tab back to the cards tab.
 *
 * @author Thomas Jensen
 */
public class CbFundsEvent
    extends GwtEvent<CbFundsHandlerIF>
    implements CbEventIF
{
    /** handler type */
    public static final Type<CbFundsHandlerIF> TYPE = new Type<CbFundsHandlerIF>();

    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbFundsEvent.class);

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
        if (LOG.isDebugEnabled()) {
            LOG.debug("dispatch", //$NON-NLS-1$
                "Calling " + CbUtil.simpleName(pHandler.getClass()) //$NON-NLS-1$
                + ".onFundsChanged(" + toString() + ')');  //$NON-NLS-1$
        }
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



    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(CbUtil.simpleName(getClass()));
        sb.append('{');
        sb.append("iFundsEnabled="); //$NON-NLS-1$
        sb.append(iFundsEnabled);
        sb.append(", iFunds="); //$NON-NLS-1$
        sb.append(iFunds);
        sb.append('}');
        return sb.toString();
    }
}
