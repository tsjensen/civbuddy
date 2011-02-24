/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 21.02.2011
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
package com.tj.civ.client.model;

import com.google.gwt.json.client.JSONObject;


/**
 * TODO Description, ending with a dot.
 *
 * @author Thomas Jensen
 */
public interface CcJsonObjectIF
{

    /**
     * Create a JSON representation of the object instance.
     * @return String in JSO notation
     */
    String toJson();



    /**
     * Set all values of the implementing Java object instance from a JSON object.
     * @param pJsonObject JSON object
     */
    void fromJson(JSONObject pJsonObject);
}
