/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 11.03.2011
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
package com.tj.civ.client.model.vo;


/**
 * Objects implementing this interface can render a view object representation
 * of themselves. This only makes sense for objects which are used in app views.
 *
 * @author Thomas Jensen
 * @param <T> the view object type
 */
public interface CcHasViewObjectIF<T extends CcAbstractViewObject>
{
    /**
     * Builds a view object from this object's instance data.
     * @return the view object
     */
    T getViewObject();
}
