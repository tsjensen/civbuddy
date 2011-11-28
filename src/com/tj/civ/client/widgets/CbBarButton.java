/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-11-27
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.common.CbConstants;


/**
 * A button in the button bar which has a text caption.
 *
 * @author Thomas Jensen
 */
public class CbBarButton
    extends Composite
    implements HasEnabled, HasClickHandlers
{
    /** Gives the position of the {@link CbBarButton}.
     *  @author Thomas Jensen */
    public static enum CbPosition { left, center, right }

    /** title backup when disabled */
    private CbTitleBackup iTitleManager;



    /**
     * Constructor.
     * @param pPosition button position on the bottom bar
     * @param pCaption the button's caption text
     * @param pTooltip the button's tooltip
     */
    public CbBarButton(final CbPosition pPosition, final String pCaption,
        final String pTooltip)
    {
        // TODO use Button widget on MSIE
        Label caption = new InlineLabel(pCaption);
        caption.setStyleName(CbConstants.CSS.cbNavButtonText());
        FlowPanel fp = new FlowPanel();
        fp.add(caption);
        if (pTooltip != null) {
            fp.setTitle(pTooltip);
        }
        DOM.setElementAttribute(fp.getElement(), CbConstants.DOMATTR_ID,
            CbConstants.CSS_BARBUTTON + pPosition);
        initWidget(fp);
        iTitleManager = new CbTitleBackup(getElement());  // after initWidget()
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
            DOM.setElementPropertyBoolean(getElement(), CbConstants.DOMATTR_DISABLED, !pEnabled);
            if (pEnabled) {
                getWidget().removeStyleName(CbConstants.CSS.cbBarButtonDisabled());
                iTitleManager.show();
            } else {
                getWidget().setStyleName(CbConstants.CSS.cbBarButtonDisabled());
                iTitleManager.hide();
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



    @Override
    public void setTitle(final String pTitle)
    {
        iTitleManager.setTitle(pTitle);
    }
}
