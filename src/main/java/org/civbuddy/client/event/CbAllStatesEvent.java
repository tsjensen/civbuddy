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


/**
 * Event being fired when many cards have changed their states at once, so a full
 * recalc of dependent information is required.
 * Upon firing of this event, the card states are already in their new states.
 *
 * @author Thomas Jensen
 */
public class CbAllStatesEvent
    extends GwtEvent<CbAllStatesHandlerIF>
    implements CbEventIF
{
    /** handler type */
    public static final Type<CbAllStatesHandlerIF> TYPE = new Type<CbAllStatesHandlerIF>();



    /**
     * Constructor.
     */
    public CbAllStatesEvent()
    {
        super();
    }



    @Override
    public Type<CbAllStatesHandlerIF> getAssociatedType()
    {
        return TYPE;
    }



    @Override
    protected void dispatch(final CbAllStatesHandlerIF pHandler)
    {
        pHandler.onAllStatesChanged(this);
    }
}
