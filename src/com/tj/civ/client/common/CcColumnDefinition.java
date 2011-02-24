/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 14.02.2011
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
package com.tj.civ.client.common;

import com.google.gwt.user.client.ui.Widget;


/**
 * Type-specific code to render data in views.
 *
 * @author Thomas Jensen
 * @param <T> type of data item given to the view to render
 */
public abstract class CcColumnDefinition<T>
{
    /**
     * Provide a widget to render the data item.
     * @param pDataItem data item to render
     * @return widget
     */
    public abstract Widget render(T pDataItem);



    /**
     * Getter.
     * @return <code>true</code> if the widget should be clickable
     */
    public boolean isClickable()
    {
        return false;
    }



    /**
     * Getter.
     * @return <code>true</code> if the widget should be selectable
     */
    public boolean isSelectable()
    {
        return false;
    }
}
