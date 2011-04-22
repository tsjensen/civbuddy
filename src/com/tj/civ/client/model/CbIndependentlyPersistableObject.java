/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 07.03.2011
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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import com.tj.civ.client.common.CbUtil;


/**
 * Common superclass of objects which can convert their representative state into
 * JSON and reconstruct from a JSON string.
 *
 * @author Thomas Jensen
 * @param <T> type implementing this interface
 */
public abstract class CcIndependentlyPersistableObject<T extends JavaScriptObject>
{
    /** this object's persistable, representative state */
    private T iJso;

    /** UUID used for identification of the persisted object in HTML5 storage */
    private String iPersistenceKey;



    /**
     * Constructor.
     * @param pJso the JSO
     */
    public CcIndependentlyPersistableObject(final T pJso)
    {
        iJso = pJso;
        evaluateJsoState(pJso);
    }



    /**
     * Constructor.
     * @param pJson the JSON representation of the JSO
     */
    public CcIndependentlyPersistableObject(final String pJson)
    {
        T jso = fromJson(pJson);
        iJso = jso;
        evaluateJsoState(jso);
    }



    /**
     * Uses the value just set for the wrapped JSO to recalculate the instance
     * fields which depend on the JSO values (often, that's <em>all</em>
     * instance fields).
     * <p>This method is called automatically upon construction.
     * @param pJso the new JSO
     */
    public abstract void evaluateJsoState(final T pJso);



    /**
     * Build the JSON representation of the persitable parts of this object.
     * @return the JSON representation for storage and export
     */
    public String toJson()
    {
        return new JSONObject(iJso).toString();
    }



    /**
     * Reconstruct the persistable parts of this object from their JSON
     * representation.
     * @param pJson the JSON representation for storage and export
     * @return a new instance of a <tt>Cc<i>Xxx</i>JSO</tt>
     */
    protected T fromJson(final String pJson)
    {
        return CbUtil.createFromJson(pJson);
    }



    public T getJso()
    {
        return iJso;
    }



    public String getPersistenceKey()
    {
        return iPersistenceKey;
    }

    public void setPersistenceKey(final String pPersistenceKey)
    {
        iPersistenceKey = pPersistenceKey;
    }
}
