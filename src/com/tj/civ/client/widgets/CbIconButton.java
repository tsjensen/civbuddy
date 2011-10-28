/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: Oct 26, 2011
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;

import com.tj.civ.client.common.CbConstants;


/**
 * An icon based button in the bottom bar. 
 *
 * @author Thomas Jensen
 */
public class CbIconButton
    extends Composite
    implements HasEnabled, HasClickHandlers
{
    /** CSS selector fragment for the glow styles */
    private static final String GLOW = "glow-"; //$NON-NLS-1$

    /** Gives the position of the {@link CbIconButton}.
     *  @author Thomas Jensen */
    public static enum CbPosition { left, center, right };

    /** the <tt>&lt;div&gt;</tt> element containing the glow image */ 
    private FlowPanel iGlowDiv;



    /**
     * Constructor.
     * @param pPosition button position on the bottom bar
     * @param pIcon the button's icon, 30 pixels wide and 22 pixels high
     */
    public CbIconButton(final CbPosition pPosition, final ImageResource pIcon)
    {
        Image icon = new Image(pIcon);
        Image glow = new Image(CbConstants.IMG_BUNDLE.iconGlow());
        iGlowDiv = new FlowPanel();
        iGlowDiv.add(glow);
        DOM.setElementAttribute(iGlowDiv.getElement(), CbConstants.DOMATTR_ID,
            CbConstants.CSS_ICONBUTTON + GLOW + pPosition);
        
        FlowPanel fp = new FlowPanel();
        fp.add(icon);
        fp.add(iGlowDiv);
        DOM.setElementAttribute(fp.getElement(), CbConstants.DOMATTR_ID,
            CbConstants.CSS_ICONBUTTON + pPosition);

        fp.sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER);
        fp.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(final MouseOverEvent pEvent)
            {
                if (isEnabled()) {
                    setGlow(true);
                }
            }
        }, MouseOverEvent.getType());

        fp.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(final MouseOutEvent pEvent)
            {
                setGlow(false);
            }
        }, MouseOutEvent.getType());

        initWidget(fp);
    }



    private void setGlow(final boolean pGlowing)
    {
        if (pGlowing) {
            DOM.setElementAttribute(iGlowDiv.getElement(), CbConstants.DOMATTR_STYLE,
                "display:block;"); //$NON-NLS-1$
        } else {
            DOM.removeElementAttribute(iGlowDiv.getElement(), CbConstants.DOMATTR_STYLE);
        }
    }



    @Override
    public boolean isEnabled()
    {
        return !DOM.getElementPropertyBoolean(getElement(), "disabled"); //$NON-NLS-1$
    }

    @Override
    public void setEnabled(final boolean pEnabled)
    {
        DOM.setElementPropertyBoolean(getElement(), "disabled", !pEnabled); //$NON-NLS-1$
        if (pEnabled) {
            getWidget().removeStyleName(CbConstants.CSS.cbIconButtonDisabled());
        } else {
            setGlow(false);
            getWidget().setStyleName(CbConstants.CSS.cbIconButtonDisabled());
        }
    }



    @Override
    public HandlerRegistration addClickHandler(final ClickHandler pHandler)
    {
        return addDomHandler(pHandler, ClickEvent.getType());
    }
}
