/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-07-13
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
 * Event being fired when the active game has changed.
 * This happens when a new game is loaded from storage.
 *
 * @author Thomas Jensen
 */
public class CbGameLoadedEvent
    extends GwtEvent<CbGameLoadedHandlerIF>
    implements CbEventIF
{
    /** handler type */
    public static final Type<CbGameLoadedHandlerIF> TYPE = new Type<CbGameLoadedHandlerIF>();

    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbGameLoadedEvent.class);



    /**
     * Constructor.
     */
    public CbGameLoadedEvent()
    {
        super();
    }



    @Override
    public Type<CbGameLoadedHandlerIF> getAssociatedType()
    {
        return TYPE;
    }



    @Override
    protected void dispatch(final CbGameLoadedHandlerIF pHandler)
    {
        if (LOG.isDebugEnabled()) {
            LOG.debug("dispatch", //$NON-NLS-1$
                "Calling " + CbUtil.simpleName(pHandler.getClass()) //$NON-NLS-1$
                + ".onGameLoaded()");  //$NON-NLS-1$
        }
        pHandler.onGameLoaded(this);
    }
}
