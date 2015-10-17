/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-11
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.model.vo;


/**
 * Objects implementing this interface can render a view object representation
 * of themselves. This only makes sense for objects which are used in app views.
 *
 * @author Thomas Jensen
 * @param <T> the view object type
 */
public interface CbHasViewObjectIF<T extends CbAbstractViewObject>
{
    /**
     * Builds a view object from this object's instance data.
     * @return the view object
     */
    T getViewObject();
}
