/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-05
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.common;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;


/**
 * Common utility methods used throughout the code.
 * 
 * @author Thomas Jensen
 */
public final class CbUtil
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbUtil.class);



    /**
     * Generate a unique ID using Robert Kieffer's JavaScript code from <tt>uuid.cache.js</tt>.
     * 
     * @return a new UUID
     */
    public static native String getUuid()
    /*-{
        return $wnd.uuid();
    }-*/;



    /**
     * Factory method.
     * 
     * @param <J> type of {@link JavaScriptObject} returned by this method
     * @param pJson the JSON representation of a {@link org.civbuddy.client.model.jso.CbGameJSO}
     * @return a new instance, or <code>null</code> if the instance could not be created
     */
    @SuppressWarnings("unchecked")
    public static <J extends JavaScriptObject> J createFromJson(final String pJson)
    {
        J result = null;
        JSONValue v = JSONParser.parseStrict(pJson);
        if (v != null) {
            JSONObject obj = v.isObject();
            if (obj != null) {
                result = (J) obj.getJavaScriptObject().cast();
            }
        }
        return result;
    }



    /**
     * Set the browser title, postfixed with the app name.
     * 
     * @param pTitleText text to set, <code>null</code> sets no special title
     */
    public static void setBrowserTitle(final String pTitleText)
    {
        String msg = CbConstants.APPNAME;
        if (pTitleText != null) {
            msg = pTitleText + " - " + msg; //$NON-NLS-1$
        }
        Window.setTitle(msg);
    }



    /**
     * Return the given class' simple name, as would Class.getSimpleName(), which is unavailable in GWT.
     * 
     * @param pClazz the class
     * @return the class' simple name
     */
    public static String simpleName(final Class<?> pClazz)
    {
        String result = null;
        if (pClazz != null) {
            String name = pClazz.getName();
            result = name.substring(name.lastIndexOf('.') + 1);
        }
        return result;
    }



    /**
     * Determines an identity String for the given object that consists of the object class' simple name followed by an
     * at-sign and the system identity hashcode. This behavior is similar to {@link Object#toString()}.
     * 
     * @param pObj any object or <code>null</code>
     * @return the identity String, uniquely identifying the object within the VM
     */
    public static String identityRef(final Object pObj)
    {
        String result = "null"; //$NON-NLS-1$
        if (pObj != null) {
            result = simpleName(pObj.getClass()) + '@' + System.identityHashCode(pObj);
        }
        return result;
    }



    /**
     * Determine the maximum of a number of ints.
     * 
     * @param pValues any number of int values
     * @return the greatest of the given values
     * @see Math#max(int, int)
     */
    public static int max(final int... pValues)
    {
        int result = Integer.MIN_VALUE;
        if (pValues != null) {
            for (int p : pValues) {
                if (p > result) {
                    result = p;
                }
            }
        }
        return result;
    }



    /**
     * Determine the user's browser.
     * 
     * @return the user agent String, in lowercase
     */
    public static native String getUserAgent() /*-{
        return navigator.userAgent.toLowerCase();
    }-*/;



    /**
     * Determine if the current browser is Microsoft Internet Explorer.
     * 
     * @return <code>true</code> if so
     */
    public static boolean isMSIE()
    {
        return getUserAgent().indexOf("msie") >= 0; //$NON-NLS-1$
    }



    /**
     * Determine if the current browser runs on a touch screen device (which we will assume to feature tap
     * highlighting).
     * 
     * @return <code>true</code> if yes
     */
    public static native boolean isTouchDevice() /*-{
        try {
            document.createEvent("TouchEvent");
            return true;
        }
        catch (e) {
            return false;
        }
    }-*/;



    /**
     * Determine if the given click was inside the boundaries of the given widget.
     * 
     * @param pWidget the widget
     * @param pClickEvent the click
     * @return <code>true</code> if yes
     */
    public static boolean isInside(final Widget pWidget, final ClickEvent pClickEvent)
    {
        LOG.enter("isInside"); //$NON-NLS-1$

        boolean result = false;
        final int cx = pClickEvent.getClientX();
        final int cy = pClickEvent.getClientY();
        final int wleft = pWidget.getAbsoluteLeft();
        final int wtop = pWidget.getAbsoluteTop();

        if (LOG.isDetailEnabled()) {
            LOG.detail("isInside", //$NON-NLS-1$
                "Click at (" + cx + ',' + cy //$NON-NLS-1$
                    + "), widget pos (" + wleft + ',' + wtop //$NON-NLS-1$
                    + "), widget dims [" + pWidget.getOffsetWidth() + ',' //$NON-NLS-1$
                    + pWidget.getOffsetHeight() + ']');
        }
        if (cx >= wleft && cy >= wtop
            && cx < wleft + pWidget.getOffsetWidth() && cy < wtop + pWidget.getOffsetHeight())
        {
            result = true;
        }

        LOG.exit("isInside", Boolean.valueOf(result)); //$NON-NLS-1$
        return result;
    }



    /**
     * Determine if the given string is empty, which is the case if it is <code>null</code> or consisting entirely of
     * whitespace as understood by the {@link String#trim()} method.
     * 
     * @param pString any string, including <code>null</code>
     * @return <code>true</code> if empty
     */
    public static boolean isEmpty(final String pString)
    {
        boolean result = true;
        if (pString != null) {
            if (pString.trim().length() > 0) {
                result = false;
            }
        }
        return result;
    }



    private CbUtil()
    {
        super();
    }
}
