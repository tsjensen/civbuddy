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
package com.tj.civ.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * Handler interface for {@link CbStateEvent} events.
 *
 * @author Thomas Jensen
 */
public interface CbStateHandlerIF
    extends EventHandler
{
    /**
     * Called when {@link CbStateEvent} is fired.
     * 
     * @param pEvent the {@link CbStateEvent} that was fired
     */
    void onStateChanged(final CbStateEvent pEvent);
}
