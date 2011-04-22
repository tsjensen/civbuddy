/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 22.01.2011
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CbCommSpinnerPayload;
import com.tj.civ.client.model.jso.CcCommodityConfigJSO;


/**
 * Shows a commodity and allows changing the number of items and reading the
 * resulting points.
 *
 * @author Thomas Jensen
 */
public class CcCommoditySpinner
    extends VerticalPanel
    implements HasEnabled, HasValue<CbCommSpinnerPayload>,
        MouseWheelHandler, HasMouseWheelHandlers,
        KeyPressHandler, HasKeyPressHandlers
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CcCommoditySpinner.class);

    /** commodity configuration which is the basis for this widget instance */
    private CcCommodityConfigJSO iConfig;

    /** the index of the commodity in the array of commodities defined by the game
     *  variant */
    private int iCommIDx;

    /** number of items owned */
    private int iNumber = 0;

    /** <code>true</code> if the widget is enabled */
    private boolean iIsEnabled = true;
    
    /** List of all widgets which can be enabled/disabled. Used in connection with
     *  {@link #setEnabled(boolean)} */
    private List<HasEnabled> iActivatableWidgets = new ArrayList<HasEnabled>();

    /** the HTML widget for showing number and points */
    private HTML iNumberIndicator;

    /** number of times the {@link MouseWheelHandler} must be called before it does
     *  anything. This is used to make it easier for the user to hit a certain value */
    private static final int MOUSEWHEEL_SLOWDOWN = 1;

    /** current value of the slowdown counter. Handler will run when zero is reached */
    private int iMouseWheelSlowDown = MOUSEWHEEL_SLOWDOWN;
    
    /** the number can be set to something between (and including) 0 .. 9
     *  by pressing the corresponding number key. Higher numbers have no hotkey. */
    private static final int MAX_NUM_BY_HOTKEY = 9;

    /** Shared click handler for handling a click on the left button */
    private static final ClickHandler CLICK_DOWN_HANDLER = new ClickHandler() {
        @Override
        public void onClick(final ClickEvent pEvent)
        {
            final CcCommoditySpinner spinner = (CcCommoditySpinner)
                ((Widget) pEvent.getSource()).getParent().getParent();
            spinner.updateNumber(false);
        }
    };

    /** Shared click handler for handling a click on the right button */
    private static final ClickHandler CLICK_UP_HANDLER = new ClickHandler() {
        @Override
        public void onClick(final ClickEvent pEvent)
        {
            final CcCommoditySpinner spinner = (CcCommoditySpinner)
                ((Widget) pEvent.getSource()).getParent().getParent();
            spinner.updateNumber(true);
        }
    };



    /**
     * Constructor.
     * @param pCommIDx the index of the commodity in the array of commodities
     *              defined by the game variant
     * @param pConfig the commodity metadata, as specified in the game variant
     */
    public CcCommoditySpinner(final int pCommIDx, final CcCommodityConfigJSO pConfig)
    {
        iCommIDx =  pCommIDx;
        iConfig = pConfig;
        
        // TODO untersuchen, ob ein FocusPanel bein den Keyboard-Problemem helfen könnte
        //      vermutlich ja, dann ein MouseMoveEvent den Focus setzen lassen
        //      zusammen mit CSS hover style
        
        CcLabel name = new CcLabel(pConfig.getLocalizedName());
//        name.setStyleName("TODO");
        iActivatableWidgets.add(name);

        PushButton pbDown = new PushButton("-");
//        pbDown.setStyleName("TODO");
        pbDown.addClickHandler(CLICK_DOWN_HANDLER);
        iActivatableWidgets.add(pbDown);
        PushButton pbUp = new PushButton("+");
//        pbDown.setStyleName("TODO");
        pbUp.addClickHandler(CLICK_UP_HANDLER);
        iActivatableWidgets.add(pbUp);

        iNumberIndicator = new HTML(buildHtml());
//        iNumberIndicator.setStyleName("TODO");

        HorizontalPanel hp = new HorizontalPanel();
        hp.setHorizontalAlignment(ALIGN_CENTER);
//        hp.setStyleName("TODO");
        hp.add(pbDown);
        hp.add(iNumberIndicator);
        hp.add(pbUp);
        
        add(name);
        add(hp);
//        setStyleName("TODO");

        registerWidgetEvents();
    }

    private void registerWidgetEvents()
    {
        sinkEvents(Event.ONMOUSEWHEEL | Event.ONKEYPRESS);
        addMouseWheelHandler(this);
        addKeyPressHandler(this);
    }


    private SafeHtml buildHtml()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<span class=\"");             //$NON-NLS-1$
        sb.append("TODO"); // TODO
        sb.append("\">");                        //$NON-NLS-1$
        sb.append(getPoints());
        sb.append("</span><br/><span class=\""); //$NON-NLS-1$
        sb.append("TODO"); // TODO
        sb.append("\">");                        //$NON-NLS-1$
        sb.append(getNumber());
        sb.append("</span>");                    //$NON-NLS-1$
        return SafeHtmlUtils.fromSafeConstant(sb.toString());
    }



    private void updateNumber(final boolean pIncrement)
    {
        int previousNumber = getNumber();
        int previousPoints = getPoints();
        if (pIncrement && iNumber < iConfig.getMaxCount()) {
            iNumber++;
            updateNumber(previousNumber, previousPoints);
        }
        else if (!pIncrement && iNumber > 0) {
            iNumber--;
            updateNumber(previousNumber, previousPoints);
        }
    }

    private void updateNumber(final int pPreviousNumber, final int pPreviousPoints)
    {
        iNumberIndicator.setHTML(buildHtml());
        fireValueChanged(getNumber() - pPreviousNumber, getPoints() - pPreviousPoints);
    }



    public int getNumber()
    {
        return iNumber;
    }



    public int getPoints()
    {
        return iNumber * iNumber * iConfig.getBase();
    }



    @Override
    public void onMouseWheel(final MouseWheelEvent pEvent)
    {
        if (!isEnabled() || pEvent.getDeltaY() == 0) {
            return;
        }
        if (iMouseWheelSlowDown == 0) {
            iMouseWheelSlowDown = MOUSEWHEEL_SLOWDOWN;
        } else {
            iMouseWheelSlowDown--;
            if (LOG.isDetailEnabled()) {
                LOG.detail("onMouseWheel", //$NON-NLS-1$
                    "MouseWheelHandler skipped because of slowdown"); //$NON-NLS-1$
            }
            pEvent.stopPropagation();
            return;
        }

        boolean up = pEvent.getDeltaY() < 0;
        if (LOG.isDetailEnabled()) {
            LOG.detail("onMouseWheel", //$NON-NLS-1$
                "MouseWheelHAndler called on '" //$NON-NLS-1$
                + iConfig.getLocalizedName() + "': number was " + iNumber); //$NON-NLS-1$
        }
        updateNumber(up);
        if (LOG.isDetailEnabled()) {
            LOG.detail("onMouseWheel", //$NON-NLS-1$
                "number changed to " + iNumber); //$NON-NLS-1$
        }
        pEvent.stopPropagation();
    }



    @Override
    public HandlerRegistration addMouseWheelHandler(final MouseWheelHandler pHandler)
    {
        return addDomHandler(pHandler, MouseWheelEvent.getType());
    }



    @Override
    public HandlerRegistration addKeyPressHandler(final KeyPressHandler pHandler)
    {
        return addDomHandler(pHandler, KeyPressEvent.getType());
    }



    @Override
    public void onKeyPress(final KeyPressEvent pEvent)
    {
        if (!isEnabled()) {
            return;
        }
        int code = pEvent.getCharCode();
        // TODO up down, left right, look at textbox
        if (code < '0' || code > '0' + Math.min(iConfig.getMaxCount(), MAX_NUM_BY_HOTKEY)) {
            return;
        }
        int previousNumber = getNumber();
        int previousPoints = getPoints();
        iNumber = code - '0';
        updateNumber(previousNumber, previousPoints);
    }



    @Override
    public boolean isEnabled()
    {
        return iIsEnabled;
    }

    @Override
    public void setEnabled(final boolean pEnabled)
    {
        for (HasEnabled w : iActivatableWidgets) {
            w.setEnabled(pEnabled);
        }
        // special treatment of the number indicator:
        if (pEnabled) {
            //iNumberIndicator.setStyleName("TODO");
        } else {
            //iNumberIndicator.setStyleName("TODO");
        }
        iIsEnabled = pEnabled;
    }



    private void fireValueChanged(final int pDeltaNumber, final int pDeltaPoints)
    {
        CbCommSpinnerPayload payload = new CbCommSpinnerPayload(iCommIDx,
            pDeltaNumber, pDeltaPoints);
        ValueChangeEvent.fire(this, payload);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        final ValueChangeHandler<CbCommSpinnerPayload> pHandler)
    {
        return addHandler(pHandler, ValueChangeEvent.getType());
    }



    /**
     * Setter.
     * @param pNumber the number of commodity cards owned of this commodity
     */
    public void setNumber(final int pNumber)
    {
        iNumber = pNumber;
        iNumberIndicator.setHTML(buildHtml());
    }



    @Override
    public void setValue(final CbCommSpinnerPayload pValue)
    {
        setNumber(pValue.getDeltaNumber());   // CAUTION: ABSOLUTE VALUE, NOT DELTA
    }



    @Override
    public void setValue(final CbCommSpinnerPayload pValue, final boolean pFireEvents)
    {
        int previousNumber = getNumber();
        int previousPoints = getPoints();
        setValue(pValue);
        if (pFireEvents) {
            fireValueChanged(getNumber() - previousNumber, getPoints() - previousPoints);
        }
    }



    @Override
    public CbCommSpinnerPayload getValue()
    {
        return new CbCommSpinnerPayload(iCommIDx, getNumber(), getPoints());
    }



    public int getCommIDx()
    {
        return iCommIDx;
    }
}
