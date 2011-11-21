/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-11-19
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

import com.google.gwt.event.shared.EventHandler;


/**
 * Handler interface for {@link CbGameLoadedEvent}s.
 *
 * @author Thomas Jensen
 */
public interface CbGameLoadedHandlerIF
    extends EventHandler
{
    /**
     * Called when a {@link CbGameLoadedEvent} is fired.
     * 
     * @param pEvent the event fired
     */
    void onGameLoaded(final CbGameLoadedEvent pEvent);
}
