/*
 * CivCounsel - A Civilization Tactics Guide
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
 * Event being fired when many cards have changed their states at once, so a full
 * recalc of dependent information is required.
 * Upon firing of this event, the card states are already in their new states.
 *
 * @author Thomas Jensen
 */
public class CcAllStatesEvent
    extends CcEvent<CcAllStatesHandler>
{
    /** handler type */
    public static final Type<CcAllStatesHandler> TYPE = new Type<CcAllStatesHandler>();



    /**
     * Constructor.
     */
    public CcAllStatesEvent()
    {
        super();
    }



    @Override
    public Type<CcAllStatesHandler> getAssociatedType()
    {
        return TYPE;
    }



    @Override
    protected void dispatch(final CcAllStatesHandler pHandler)
    {
        pHandler.onAllStatesChanged(this);
    }
}
