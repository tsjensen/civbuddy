/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 13.02.2011
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.common.CbConstants;


/**
 * Displays a label '&gt;&gt;' that serves as a 'more' icon.
 *
 * @author Thomas Jensen
 */
public class CbMoreArrow
    extends Composite
    implements HasClickHandlers
{
    /** row index of the card that this more arrow belongs to */
    private int iMyIdx;



    /**
     * Constructor.
     * @param pToolTip Tooltip appearing on the widget
     */
    public CbMoreArrow(final String pToolTip)
    {
        this(pToolTip, -1);
    }



    /**
     * Constructor.
     * @param pToolTip Tooltip appearing on the widget
     * @param pRowIdx row index of the card that this more arrow belongs to
     */
    public CbMoreArrow(final String pToolTip, final int pRowIdx)
    {
        Label lbl = new Label(">>"); //$NON-NLS-1$
        lbl.setStyleName(CbConstants.CSS.cbMoreArrowLabelText());
        iMyIdx = pRowIdx;
        FlowPanel fp = new FlowPanel();
        fp.add(lbl);
        fp.setStyleName(CbConstants.CSS.cbMoreArrowLabel());
        if (pToolTip != null) {
            fp.setTitle(pToolTip);
        }
        initWidget(fp);
    }



    public int getMyIdx()
    {
        return iMyIdx;
    }



    @Override
    public HandlerRegistration addClickHandler(final ClickHandler pHandler)
    {
        return addDomHandler(pHandler, ClickEvent.getType());
    }
}
