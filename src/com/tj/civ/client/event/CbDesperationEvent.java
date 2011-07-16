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
 * Event being fired when the desperation state has been recalculated. The event
 * does not indicate a change in desperation, only the fact that it was recalculated.
 * <p>The event indicates if desperation mode is active (i.e. there is no way to
 * reach the point target with cards anymore), and if yes, by how much we will miss
 * the target.
 *
 * @author Thomas Jensen
 */
public class CbDesperationEvent
    extends GwtEvent<CbDesperationHandlerIF>
    implements CbEventIF
{
    /** handler type */
    public static final Type<CbDesperationHandlerIF> TYPE = new Type<CbDesperationHandlerIF>();

    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbDesperationEvent.class);

    /** <code>true</code> if desperation mode is active (i.e. there is no way to
     *  reach the point target with cards anymore) */
    private boolean iIsDesperate;

    /** by how many points are we missing the target (0 if not desperate) */
    private int iDelta;



    /**
     * Constructor.
     * @param pIsDesperate <code>true</code> if desperation mode is active (i.e.
     *              there is no way to reach the point target with cards anymore)
     * @param pDelta by how many points are we missing the target (0 if not desperate)
     */
    public CbDesperationEvent(final boolean pIsDesperate, final int pDelta)
    {
        super();
        iIsDesperate = pIsDesperate;
        iDelta = pDelta;
    }



    @Override
    public Type<CbDesperationHandlerIF> getAssociatedType()
    {
        return TYPE;
    }



    @Override
    protected void dispatch(final CbDesperationHandlerIF pHandler)
    {
        if (LOG.isDebugEnabled()) {
            LOG.debug("dispatch", //$NON-NLS-1$
                "Calling " + CbUtil.simpleName(pHandler.getClass()) //$NON-NLS-1$
                + ".onDesperationCalculated(" + toString() + ')');  //$NON-NLS-1$
        }
        pHandler.onDesperationCalculated(this);
    }



    public boolean isIsDesperate()
    {
        return iIsDesperate;
    }



    public int getDelta()
    {
        return iDelta;
    }



    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(CbUtil.simpleName(getClass()));
        sb.append('{');
        sb.append("iIsDesperate="); //$NON-NLS-1$
        sb.append(iIsDesperate);
        sb.append(", iDelta="); //$NON-NLS-1$
        sb.append(iDelta);
        sb.append('}');
        return sb.toString();
    }
}
