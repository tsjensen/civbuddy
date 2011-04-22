/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-04-15
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
package com.tj.civ.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.views.CcCanGoPlacesIF;


/**
 * Common superclass of all this app's activities.
 *
 * @author Thomas Jensen
 */
public abstract class CbAbstractActivity
    extends AbstractActivity
    implements CcCanGoPlacesIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbAbstractActivity.class);

    /** our client factory */
    private CbClientFactoryIF iClientFactory;

    /** the place that this activity was started at */
    private CbAbstractPlace iCurrentPlace;



    /**
     * Constructor.
     * @param pPlace the place that this activity was started at
     * @param pClientFactory our client factory
     */
    public CbAbstractActivity(final CbAbstractPlace pPlace, final CbClientFactoryIF pClientFactory)
    {
        super();
        LOG.touch(CbLogAdapter.CONSTRUCTOR);
        iClientFactory = pClientFactory;
        iCurrentPlace = pPlace;
    }



    @Override
    public void goTo(final CbAbstractPlace pPlace)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("goTo",  //$NON-NLS-1$
                new String[]{"pPlace"}, new Object[]{pPlace}); //$NON-NLS-1$
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("goTo", "Changing places from '" //$NON-NLS-1$ //$NON-NLS-2$
                + (iCurrentPlace != null ? CbUtil.simpleName(iCurrentPlace.getClass()) : null)
                + "' to '" //$NON-NLS-1$
                + (pPlace != null ? CbUtil.simpleName(pPlace.getClass()) : null)
                + "'"); //$NON-NLS-1$
        }
        CbGlobal.setPreviousPlace(iCurrentPlace);
        iClientFactory.getPlaceController().goTo(pPlace);

        LOG.exit("goTo"); //$NON-NLS-1$
    }



    public CbClientFactoryIF getClientFactory()
    {
        return iClientFactory;
    }
}
