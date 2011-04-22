/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 17.04.2011
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
package com.tj.civ.client.places;

import com.google.gwt.place.shared.Place;

import com.tj.civ.client.common.CbUtil;


/**
 * Common superclass of all this app's places.
 *
 * @author Thomas Jensen
 */
public abstract class CbAbstractPlace
    extends Place
{
    /**
     * Constructor.
     */
    protected CbAbstractPlace()
    {
        super();
    }



    /**
     * Return the token representing the place (excluding the place's class name).
     * @return the token
     */
    public abstract String getToken();



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        String token = getToken();
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }



    @Override
    public boolean equals(final Object pOther)
    {
        if (this == pOther) {
            return true;
        }
        if (pOther == null) {
            return false;
        }
        if (!getClass().getName().equals(pOther.getClass().getName())) {
            return false;
        }

        String otherToken = ((CbAbstractPlace) pOther).getToken();
        String token = getToken();

        if (token == null) {
            if (otherToken != null) {
                return false;
            }
        }
        else if (!token.equals(otherToken)) {
            return false;
        }
        return true;
    }



    @Override
    public String toString()
    {
        return CbUtil.simpleName(getClass()) + ':' + getToken();
    }
}
