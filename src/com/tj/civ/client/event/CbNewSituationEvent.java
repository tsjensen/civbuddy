/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-07-13
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

import com.google.gwt.event.shared.GwtEvent;

import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbUtil;


/**
 * Event being fired when the situation set as current situation in
 * {@link com.tj.civ.client.common.CbGlobal} has changed.
 * This happens when the active player is changed or a new siutation is loaded from
 * storage.
 *
 * @author Thomas Jensen
 */
public class CbNewSituationEvent
    extends GwtEvent<CbNewSituationHandlerIF>
    implements CbEventIF
{
    /** handler type */
    public static final Type<CbNewSituationHandlerIF> TYPE = new Type<CbNewSituationHandlerIF>();

    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbNewSituationEvent.class);



    /**
     * Constructor.
     */
    public CbNewSituationEvent()
    {
        super();
    }



    @Override
    public Type<CbNewSituationHandlerIF> getAssociatedType()
    {
        return TYPE;
    }



    @Override
    protected void dispatch(final CbNewSituationHandlerIF pHandler)
    {
        if (LOG.isDebugEnabled()) {
            LOG.debug("dispatch", //$NON-NLS-1$
                "Calling " + CbUtil.simpleName(pHandler.getClass()) //$NON-NLS-1$
                + ".onNewSituationSet()");  //$NON-NLS-1$
        }
        pHandler.onNewSituationSet(this);
    }
}
