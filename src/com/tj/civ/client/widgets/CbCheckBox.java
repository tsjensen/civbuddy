/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-11-17
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
package com.tj.civ.client.widgets;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbUtil;


/**
 * An iOS 4 style checkbox modeled as a horizontally sliding switch labeled ON
 * and OFF.
 * <p>Missing a click handler, which we don't need here, but which anyone would need
 * who is reusing this widget in their own app.
 * 
 * <p>TODO Screenshot
 *
 * @author Thomas Jensen
 */
public class CbCheckBox
    extends Widget
    implements HasEnabled, HasValue<Boolean>, HasValueChangeHandlers<Boolean>
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbCheckBox.class);

    /** name of the DOM attribute used to set the CSS animation name on Mozilla */
    private static final String DOMATTR_ANIMATION_NAME_MOZILLA = "MozAnimationName"; //$NON-NLS-1$

    /** name of the DOM attribute used to set the CSS animation name on Webkit */
    private static final String DOMATTR_ANIMATION_NAME_WEBKIT = "webkitAnimationName"; //$NON-NLS-1$

    /** check box value (<code>true</code> == checked/on,
     *  <code>false</code> == unchecked/off) */
    private boolean iValue = false;



    /**
     * Constructor.
     */
    public CbCheckBox()
    {
        setElement(DOM.createSpan());
        setStyleName(CbConstants.CSS.cbCheckBox());
    }



    @Override
    public boolean isEnabled()
    {
        return !DOM.getElementPropertyBoolean(getElement(), CbConstants.DOMATTR_DISABLED);
    }



    @Override
    public void setEnabled(final boolean pEnabled)
    {
        DOM.setElementPropertyBoolean(getElement(), CbConstants.DOMATTR_DISABLED, !pEnabled);
        setStyleName(getStyle(pEnabled, iValue));
    }



    private String getStyle(final boolean pEnabled, final boolean pChecked)
    {
        String result = null;
        if (pEnabled) {
            if (pChecked) {
                result = CbConstants.CSS.cbCheckBoxChecked();
            } else {
                result = CbConstants.CSS.cbCheckBox();
            }
        } else {
            if (pChecked) {
                result = CbConstants.CSS.cbCheckBoxCheckedDisabled();
            } else {
                result = CbConstants.CSS.cbCheckBoxDisabled();
            }
        }
        return result;
    }



    @Override
    public HandlerRegistration addValueChangeHandler(
        final ValueChangeHandler<Boolean> pHandler)
    {
        return addHandler(pHandler, ValueChangeEvent.getType());
    }



    @Override
    public Boolean getValue()
    {
        return Boolean.valueOf(iValue);
    }



    @Override
    public void setValue(final Boolean pNewValue)
    {
        setValue(pNewValue, true, false);
    }

    @Override
    public void setValue(final Boolean pNewValue, final boolean pFireEvents)
    {
        setValue(pNewValue, pFireEvents, false);
    }

    /**
     * Sets this object's value. Fires
     * {@link com.google.gwt.event.logical.shared.ValueChangeEvent} when
     * <tt>pFireEvents</tt> is <code>true</code> and the new value does not equal
     * the existing value.
     *
     * @param pNewValue the object's new value
     * @param pFireEvents fire events if <code>true</code> and value is new
     * @param pAnimate perform a CSS animation of the value change
     */
    public void setValue(final Boolean pNewValue, final boolean pFireEvents,
        final boolean pAnimate)
    {
        boolean newValue = pNewValue != null ? pNewValue.booleanValue() : false;
        if (newValue != iValue) {
            iValue = newValue;
            setStyleName(getStyle(isEnabled(), newValue));
            if (pAnimate) {
                String aniName = getAnimationName(newValue);
                // FIXME animation not happening on Webkit
                String propName = DOMATTR_ANIMATION_NAME_WEBKIT;
                if (CbUtil.isGecko()) {
                    propName = DOMATTR_ANIMATION_NAME_MOZILLA;
                }
                getElement().getStyle().setProperty(propName, aniName);
                if (LOG.isDetailEnabled()) {
                    LOG.detail("setValue", //$NON-NLS-1$
                        propName + '=' + aniName + ';');
                }
            }
            if (pFireEvents) {
                ValueChangeEvent.fire(this, Boolean.valueOf(iValue));
            }
        }
    }



    private String getAnimationName(final boolean pTargetState)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("checkbox-"); //$NON-NLS-1$
        sb.append(!pTargetState ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append('-');
        sb.append(pTargetState ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        return sb.toString();
    }
}
