/*
 * CivCounsel - A Civilization Tactics Guide
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
import java.util.TreeMap;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.LocaleInfo;

import com.tj.civ.client.common.CbConstants;


/**
 * Stores a virtual map of locale-specific Strings. For example, one instance of
 * this object could store the name of a commodity in several different languages.
 *
 * @author Thomas Jensen
 */
public final class CcStringsI18nJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CcStringsI18nJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @return an empty instance
     */
    public static CcStringsI18nJSO create()
    {
        return createObject().cast();
    }



    /**
     * Getter.
     * @return English (== default) string
     */
    public String getDefaultEn()
    {
        return getStringI18nInternal(CbConstants.LOCALE_EN);
    }

    /**
     * Setter.
     * @param pNameEn the new value of the English String, which is the default String
     */
    public void setDefaultEn(final String pNameEn)
    {
        setStringI18n(CbConstants.LOCALE_EN, pNameEn);
    }



    /**
     * Getter.
     * @return String in the current locale
     */
    public String getStringI18n()
    {
        return getStringI18n(LocaleInfo.getCurrentLocale().getLocaleName());
    }



    /**
     * Getter.
     * @param pLocale locale name for which to get the String
     * @return String in the given locale
     */
    public String getStringI18n(final String pLocale)
    {
        String result = getStringI18nInternal(pLocale);
        if (result == null || result.length() < 1) {
            // use default locale if given locale is not found
            result = getStringI18nInternal(CbConstants.LOCALE_EN);
        }
        return result;
    }



    /**
     * Getter.
     * @param pLocale locale name for which to get the String
     * @return String in the given locale
     */
    private native String getStringI18nInternal(final String pLocale)
    /*-{
        var v;
        if (this.hasOwnProperty(pLocale)) {
            v = this[pLocale];
        }
        return v;
    }-*/;



    /**
     * Setter.
     * @param pLocale locale name
     * @param pName the String in the given locale
     */
    public native void setStringI18n(final String pLocale, final String pName)
    /*-{
        this[pLocale] = pName;
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
     * @param pLocale key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     */
    public native String remove(final String pLocale)
    /*-{
        var result = null;
        if (this.hasOwnProperty(pLocale)) {
            result = this[pLocale];
        }
        delete this[pLocale]; 
        return result;
    }-*/;



    /**
     * Build a map containing all entries.
     * @return the map, where keys are locales and values are the respective Strings
     */
    public Map<String, String> getAsMap()
    {
        Map<String, String> result = new TreeMap<String, String>();
        JsArrayString keys = keySetJs();
        if (keys != null) {
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.get(i);
                result.put(key, getStringI18n(key));
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
