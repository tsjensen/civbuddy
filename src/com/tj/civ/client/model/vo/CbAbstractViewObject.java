/*
 * CivBuddy - A Civilization Tactics Guide
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
 * Common superclass of all our view objects.
 *
 * @author Thomas Jensen
 */
public abstract class CbAbstractViewObject
{
    /**
     * Constructor.
     */
    protected CbAbstractViewObject()
    {
        super();
    }



    /**
     * Getter.
     * @return the part of the VO which is the primary label of the list entry
     */
    public abstract String getPrimaryText();

    /**
     * Setter.
     * @param pPrimaryText the new value of the part of the VO which is the primary
     *              label of the list entry
     */
    public abstract void setPrimaryText(final String pPrimaryText);



    /**
     * Getter.
     * @return the part of the VO which is the secondary label of the list entry
     */
    public abstract String getSecondaryText();
}
