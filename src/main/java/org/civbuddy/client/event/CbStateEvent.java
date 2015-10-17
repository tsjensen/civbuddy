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
package org.civbuddy.client.event;

import com.google.gwt.event.shared.GwtEvent;

import org.civbuddy.client.model.CbState;



/**
 * Event being fired when a card changes to or from
 * {@link org.civbuddy.client.model.CbState#Planned}.
 * Upon firing of this event, the card state is already in its new state.
 *
 * @author Thomas Jensen
 */
public class CbStateEvent
    extends GwtEvent<CbStateHandlerIF>
    implements CbEventIF
{
    /** handler type */
    public static final Type<CbStateHandlerIF> TYPE = new Type<CbStateHandlerIF>();

    /** index of the card that was changed */
    private int iRowIdx;

    /** the new state of the card (already set) */
    private CbState iNewSate;



    /**
     * Constructor.
     * @param pRoxIdx index of the card that was changed
     * @param pNewState the new state of the card (already set)
     */
    public CbStateEvent(final int pRoxIdx, final CbState pNewState)
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
    public CbState getNewSate()
    {
        return iNewSate;
    }
}
