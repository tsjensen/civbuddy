/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-27
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
package com.tj.civ.client.model.jso;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;


/**
 * Stores a map as JSO. Keys are Strings, values can be any JSO.
 *
 * @author Thomas Jensen
 * @param <V> type of the map's values
 */
public final class CcMapJSO<V extends JavaScriptObject>
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CcMapJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @param <T> type of the map's values
     * @param pValueType type of the map's values
     * @return an empty instance
     */
    public static <T extends JavaScriptObject> CcMapJSO<T> create(final Class<T> pValueType)
    {
        if (pValueType != null) {
            return createObject().cast();
        } else {
            throw new IllegalArgumentException("pValueType is null"); //$NON-NLS-1$
        }
    }



    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is
     * replaced by the specified value.  (A map <tt>m</tt> is said to contain a
     * mapping for a key <tt>k</tt> if and only if {@link #containsKey
     * m.containsKey(k)} would return <tt>true</tt>.)
     *
     * @param pKey key with which the specified value is to be associated
     * @param pValue value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>,
     *         if the implementation supports <tt>null</tt> values.)
     */
    public native V put(final String pKey, final V pValue)
    /*-{
        var result = null;
        if (this.hasOwnProperty(pKey)) {
            result = this[pKey];
        }
        this[pKey] = pValue;
        return result;
    }-*/;



    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified key.
     *
     * @param pKey key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified key
     */
    public native boolean containsKey(final String pKey)
    /*-{
        return this.hasOwnProperty(pKey);
    }-*/;



    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param pKey the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     */
    public native V get(final String pKey)
    /*-{
        var result = null;
        if (this.hasOwnProperty(pKey)) {
            result = this[pKey];
        }
        return result;
    }-*/;



    /**
     * Removes the mapping for a key from this map if it is present.
     * 
     * <p>Returns the value to which this map previously associated the key,
     * or <tt>null</tt> if the map contained no mapping for the key.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param pKey key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    public native V remove(final String pKey)
    /*-{
        var result = null;
        if (this.hasOwnProperty(pKey)) {
            result = this[pKey];
        }
        delete this[pKey]; 
        return result;
    }-*/;



    /**
     * Copies all of the mappings from the specified map to this map.
     * The effect of this call is equivalent to that of calling {@link #put put(k, v)}
     * on this map once for each mapping from key <tt>k</tt> to value <tt>v</tt> in
     * the specified map.  The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     *
     * @param pMapToAdd mappings to be stored in this map
     */
    public void putAll(final Map<String, V> pMapToAdd)
    {
        for (Entry<String, V> entry : pMapToAdd.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }



    /**
     * Copies all of the mappings from the specified map to this map.
     * The effect of this call is equivalent to that of calling {@link #put put(k, v)}
     * on this map once for each mapping from key <tt>k</tt> to value <tt>v</tt> in
     * the specified map.  The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     *
     * @param pMapToAdd mappings to be stored in this map
     */
    public void putAll(final CcMapJSO<V> pMapToAdd)
    {
        JsArrayString keys = pMapToAdd.keySetJs();
        if (keys != null) {
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.get(i);
                put(key, pMapToAdd.get(key));
            }
        }
    }



    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public native boolean isEmpty()
    /*-{
        var result = false;
        for (var key in this) {
            if (this.hasOwnProperty(key)) {
                result = true;
                break;
            }
        }
        return result;
    }-*/;



    /**
     * Returns a {@link Set} view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<String> keySet()
    {
        Set<String> result = new TreeSet<String>();
        JsArrayString keys = keySetJs();
        if (keys != null) {
            for (int i = 0; i < keys.length(); i++) {
                result.add(keys.get(i));
            }
        }
        return result;
    }



    private native JsArrayString keySetJs()
    /*-{
        var keys = [];
        for (var key in this) {
            if (this.hasOwnProperty(key)) {
                // only if it is not inherited through the prototype chain
                keys.push(key);
            }
        }
        return keys;
    }-*/;
}
