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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.tj.civ.client.model.CcSituation;


/**
 * This application's event bus (Singleton).
 * All events are enriched with the current player's situation (if set).
 *
 * @author Thomas Jensen
 */
public final class CcEventBus
    extends SimpleEventBus
{
    /** the instance of this singleton */
    public static final CcEventBus INSTANCE = new CcEventBus();

    /** the player situation we're currently acting on (may be <code>null</code>, in
     *  which case events are not enriched) */
    private CcSituation iSituation = null;



    /**
     * Constructor.
     */
    private CcEventBus()
    {
        super();
    }



    @Override
    public void fireEvent(final GwtEvent<?> pEvent)
    {
        if (pEvent != null && pEvent instanceof CcEvent<?>) {
            ((CcEvent<?>) pEvent).setSituation(iSituation);
        }
        super.fireEvent(pEvent);
    }



    @Override
    public void fireEventFromSource(final GwtEvent<?> pEvent, final Object pSource)
    {
        if (pEvent != null && pEvent instanceof CcEvent<?>) {
            ((CcEvent<?>) pEvent).setSituation(iSituation);
        }
        super.fireEventFromSource(pEvent, pSource);
    }



    /**
     * Getter.
     * @return {@link #iSituation}
     */
    public CcSituation getSituation()
    {
        return iSituation;
    }

    /**
     * Setter.
     * @param pSituation the new value of {@link #iSituation}
     */
    public void setSituation(final CcSituation pSituation)
    {
        iSituation = pSituation;
    }
}
