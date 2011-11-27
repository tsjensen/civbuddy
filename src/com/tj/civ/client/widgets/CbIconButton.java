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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
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
    /** Gives the position of the {@link CbIconButton}.
     *  @author Thomas Jensen */
    public static enum CbPosition { left, center, right }

    /** title backup when disabled */
    private CbTitleBackup iTitleBackup;



    /**
     * Constructor.
     * @param pPosition button position on the bottom bar
     * @param pIcon the button's icon, 30 pixels wide and 30 pixels high
     */
    public CbIconButton(final CbPosition pPosition, final ImageResource pIcon)
    {
        Image icon = new Image(pIcon);
        FlowPanel fp = new FlowPanel();
        fp.add(icon);
        DOM.setElementAttribute(fp.getElement(), CbConstants.DOMATTR_ID,
            CbConstants.CSS_ICONBUTTON + pPosition);
        initWidget(fp);
        iTitleBackup = new CbTitleBackup(getElement());  // after initWidget()
    }



    @Override
    public boolean isEnabled()
    {
        return !DOM.getElementPropertyBoolean(getElement(), CbConstants.DOMATTR_DISABLED);
    }

    @Override
    public void setEnabled(final boolean pEnabled)
    {
        if (isEnabled() != pEnabled) {
            // TODO disable icon by showing a grayed-out icon, not just by filtering it
            //      through an opacity filter. Older IE versions don't interpret the
            //      filter, which makes the icons appear active.
            DOM.setElementPropertyBoolean(getElement(), CbConstants.DOMATTR_DISABLED, !pEnabled);
            if (pEnabled) {
                getWidget().removeStyleName(CbConstants.CSS.cbIconButtonDisabled());
                iTitleBackup.show();
            } else {
                getWidget().setStyleName(CbConstants.CSS.cbIconButtonDisabled());
                iTitleBackup.hide();
            }
        }
    }



    @Override
    public HandlerRegistration addClickHandler(final ClickHandler pHandler)
    {
        // Wrap the given handler with another handler that only calls the given
        // handler if the button is enabled.
        return addDomHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (isEnabled()) {
                    pHandler.onClick(pEvent);
                } else {
                    pEvent.stopPropagation();
                }
            }
        }, ClickEvent.getType());
    }
}
