/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-04-14
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;


/**
 * Converts anything to a String in a GWT client-side compatible manner.
 *
 * @author Thomas Jensen
 */
public final class CbToString
{
    /**
     * Convert anything into a String.
     * @param pObj any object or <code>null</code>
     * @return its String representation
     */
    public static String obj2str(final Object pObj)
    {
        StringBuilder sb = new StringBuilder();
        obj2str(sb, pObj);
        return sb.toString();
    }



    /**
     * Convert anything into a String.
     * @param pSb StringBuilder to append to
     * @param pObj any object or <code>null</code>
     */
    public static void obj2str(final StringBuilder pSb, final Object pObj)
    {
        if (pObj != null) {
            Class<?> clazz = pObj.getClass();
            if (clazz.isArray())
            {
                Class<?> ct = clazz.getComponentType();
                if (ct.isPrimitive()) {
                    if (int.class.equals(ct)) {
                        pSb.append(Arrays.toString((int[]) pObj));
                    } else if (boolean.class.equals(ct)) {
                        pSb.append(Arrays.toString((boolean[]) pObj));
                    } else if (byte.class.equals(ct)) {
                        pSb.append(Arrays.toString((byte[]) pObj));
                    } else if (char.class.equals(ct)) {
                        pSb.append(Arrays.toString((char[]) pObj));
                    } else if (double.class.equals(ct)) {
                        pSb.append(Arrays.toString((double[]) pObj));
                    } else if (float.class.equals(ct)) {
                        pSb.append(Arrays.toString((float[]) pObj));
                    } else if (short.class.equals(ct)) {
                        pSb.append(Arrays.toString((short[]) pObj));
                    } else {
                        pSb.append(pObj.toString());
                    }
                }
                else {
                    pSb.append(Arrays.deepToString((Object[]) pObj));
                }
            }
            else if (pObj instanceof CharSequence) {
                pSb.append('"');
                pSb.append(pObj.toString());
                pSb.append('"');
            }
            else if (pObj instanceof Collection<?>)
            {
                pSb.append('{');
                for (Iterator<?> iter = ((Collection<?>) pObj).iterator(); iter.hasNext();)
                {
                    pSb.append(obj2str(iter.next()));
                    if (iter.hasNext()) {
                        pSb.append(", "); //$NON-NLS-1$
                    }
                }
                pSb.append('}');
            }
            else if (pObj instanceof Map<?, ?>) {
                map2str(pSb, (Map<?, ?>) pObj);
            }
            else if (pObj instanceof JavaScriptObject) {
                JavaScriptObject jso = (JavaScriptObject) pObj;
                pSb.append(new JSONObject(jso).toString());
            }
            else {
                pSb.append(pObj.toString());
            }
        }
        else {
            pSb.append("null"); //$NON-NLS-1$
        }
    }



    private static <K, V> void map2str(final StringBuilder pSb, final Map<K, V> pMap)
    {
        if (pMap != null) {
            pSb.append('{');
            for (Iterator<Map.Entry<K, V>> iter = pMap.entrySet().iterator(); iter.hasNext();)
            {
                Map.Entry<K, V> entry = iter.next();
                obj2str(pSb, entry.getKey());
                pSb.append("->"); //$NON-NLS-1$
                obj2str(pSb, entry.getValue());
                if (iter.hasNext()) {
                    pSb.append(", "); //$NON-NLS-1$
                }
            }
            pSb.append('}');
        }
        else {
            pSb.append("null"); //$NON-NLS-1$
        }
    }



    /**
     * Private constructor.
     */
    private CbToString()
    {
        super();
    }
}
