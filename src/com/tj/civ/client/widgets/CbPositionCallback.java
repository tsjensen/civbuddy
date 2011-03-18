/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 16.03.2011
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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;


/**
 * Position callback for making the message box appear in the right place.
 * @author Thomas Jensen
 */
class CcPositionCallback implements PositionCallback
{
    /** the object that we will center the message box above */
    @SuppressWarnings("unused")
    private UIObject iBackObject;

    /** the popup panel we are positioning */
    private PopupPanel iPopup;



    /**
     * Constructor.
     * @param pPopup the popup panel we are positioning
     * @param pBackObject the object that we will center the message box above
     */
    public CcPositionCallback(final PopupPanel pPopup, final UIObject pBackObject)
    {
        iPopup = pPopup;
        iBackObject = pBackObject != null ? pBackObject : RootPanel.get();
    }



    @Override
    public void setPosition(final int pOffsetWidth, final int pOffsetHeight)
    {
        final int verticalPosition = 100;
        final int viewportWidth = 320;   // TODO determine dynamically
        // center horizontally
        //final int horizontalPos = (iBackObject.getOffsetWidth() - pOffsetWidth) / 2;
        final int horizontalPos = (viewportWidth - pOffsetWidth) / 2;
        iPopup.setPopupPosition(horizontalPos, Window.getScrollTop() + verticalPosition);
    }
}
