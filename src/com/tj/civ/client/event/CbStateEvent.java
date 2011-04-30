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

import com.tj.civ.client.model.CcState;



/**
 * Event being fired when a card changes to or from
 * {@link com.tj.civ.client.model.CcState#Planned}.
 * Upon firing of this event, the card state is already in its new state.
 *
 * @author Thomas Jensen
 */
public class CbStateEvent
    extends CbEvent<CbStateHandlerIF>
{
    /** handler type */
    public static final Type<CbStateHandlerIF> TYPE = new Type<CbStateHandlerIF>();

    /** index of the card that was changed */
    private int iRowIdx;

    /** the new state of the card (already set) */
    private CcState iNewSate;



    /**
     * Constructor.
     * @param pRoxIdx index of the card that was changed
     * @param pNewState the new state of the card (already set)
     */
    public CbStateEvent(final int pRoxIdx, final CcState pNewState)
    {
        super();
        iRowIdx = pRoxIdx;
        iNewSate = pNewState;
    }



    @Override
    public Type<CbStateHandlerIF> getAssociatedType()
    {
        return TYPE;
    }



    @Override
    protected void dispatch(final CbStateHandlerIF pHandler)
    {
        pHandler.onStateChanged(this);
    }



    /**
     * Getter.
     * @return {@link #iRowIdx}
     */
    public int getRowIdx()
    {
        return iRowIdx;
    }



    /**
     * Getter.
     * @return {@link #iNewSate}
     */
    public CcState getNewSate()
    {
        return iNewSate;
    }
}
