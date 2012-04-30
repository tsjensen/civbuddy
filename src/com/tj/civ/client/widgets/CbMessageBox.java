/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-09
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.common.CbConstants;


/**
 * Shows a modal message box.
 * Since all GWT actions are asynchronous, we cannot return directly with a result.
 * Instead, a callback is invoked when the result is available.
 *
 * @author Thomas Jensen
 */
public final class CbMessageBox
    extends DialogBox
{
    // TODO caching of message boxes (important performance aspect)

    /**
     * Callback for getting the result from the message box.
     * @author Thomas Jensen
     */
    public interface CbResultCallbackIF
    {
        /**
         * Fired when the user has selected one of the buttons.
         * @param pOkPressed <code>true</code> --&gt; OK, <code>false</code> --&gt; Cancel
         */
        void onResultAvailable(final boolean pOkPressed);
    }



    /**
     * Click handler for all buttons in this message box.
     * @author Thomas Jensen
     */
    private class CbMsgBoxClickHandler implements ClickHandler
    {
        /** the value we pass to the callback handler when the button is pressed */
        private boolean iExpectedResult;

        /** the callback handler */
        private CbResultCallbackIF iCallback;

        /**
         * Constructor.
         * @param pResult the value we pass to the callback handler when the button is pressed
         * @param pCallback the callback handler, or <code>null</code> for no callback
         */
        public CbMsgBoxClickHandler(final boolean pResult, final CbResultCallbackIF pCallback)
        {
            iExpectedResult = pResult;
            iCallback = pCallback;
        }

        @Override
        public void onClick(final ClickEvent pEvent)
        {
            if (iCallback != null) {
                iCallback.onResultAvailable(iExpectedResult);
            }
            CbMessageBox.this.hide();
        }
    }



    /** flag if the appearance/disappearance of the message box should be animated */
    private static final boolean ANIMATED = true;



    /**
     * Constructor.
     */
    private CbMessageBox()
    {
        super(false, true);
    }



    /**
     * Display a modal message box with an OK/Cancel selection. Since all GWT action
     * is asynchronous, the result is reported by callback.<br/>
     * The message box displayed by this method looks like this:<br/>
     * <img style="margin-left:2em;margin-top:0.5em;"
     *      src="doc-files/MsgBox-OkCancel.png" width="168" height="105"/>
     *
     * @param pTitle title of the message box
     * @param pText contents of the message box
     * @param pBackObject the object that we will center the message box above;
     *              if <code>null</code>, we center on the {@link RootPanel}
     * @param pCallback where we report when the result is available
     */
    public static void showOkCancel(final String pTitle, final SafeHtml pText,
        final UIObject pBackObject, final CbResultCallbackIF pCallback)
    {
        final CbMessageBox msgBox = new CbMessageBox();
        msgBox.setGlassEnabled(true);
        msgBox.setText(pTitle);
        msgBox.setAnimationEnabled(ANIMATED);

        Button btnOk = new Button(CbConstants.STRINGS.ok());
        btnOk.addClickHandler(msgBox.new CbMsgBoxClickHandler(true, pCallback));
        btnOk.setStyleName(CbConstants.CSS.ccButton());
        Button btnCancel = new Button(CbConstants.STRINGS.cancel());
        btnCancel.addClickHandler(msgBox.new CbMsgBoxClickHandler(false, pCallback));
        btnCancel.setStyleName(CbConstants.CSS.ccButton());

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.setStyleName(CbConstants.CSS.ccButtonPanel());
        buttons.addStyleName(CbConstants.CSS_BLUEGRADIENT);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        buttons.add(btnOk);
        buttons.add(btnCancel);

        VerticalPanel vp = new VerticalPanel();
        HTML msg = new HTML(pText);
        msg.setStyleName(CbConstants.CSS.ccMsgboxText());
        vp.add(msg);
        vp.add(buttons);
        msgBox.setWidget(vp);
        
        msgBox.setPopupPositionAndShow(new CbPositionCallback(msgBox, pBackObject));
    }



    /**
     * Display a modal message box with a single OK button. This will be done
     * asynchronously, so event processing will not wait for the user to press OK,
     * but the user will not be able to select anything until OK is pressed.
     *
     * @param pTitle title of the message box
     * @param pText contents of the message box
     * @param pBackObject the object that we will center the message box above;
     *              if <code>null</code>, we center on the {@link RootPanel}
     */
    public static void showAsyncMessage(final String pTitle, final SafeHtml pText,
        final UIObject pBackObject)
    {
        final CbMessageBox msgBox = new CbMessageBox();
        msgBox.setGlassEnabled(true);
        msgBox.setText(pTitle);
        msgBox.setAnimationEnabled(ANIMATED);

        Button btnOk = new Button(CbConstants.STRINGS.ok());
        btnOk.addClickHandler(msgBox.new CbMsgBoxClickHandler(true, null));
        btnOk.setStyleName(CbConstants.CSS.ccButton());

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.setStyleName(CbConstants.CSS.ccButtonPanel());
        buttons.addStyleName(CbConstants.CSS_BLUEGRADIENT);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        buttons.add(btnOk);

        VerticalPanel vp = new VerticalPanel();
        HTML msg = new HTML(pText);
        msg.setStyleName(CbConstants.CSS.ccMsgboxText());
        vp.add(msg);
        vp.add(buttons);
        msgBox.setWidget(vp);
        
        msgBox.setPopupPositionAndShow(new CbPositionCallback(msgBox, pBackObject));
    }
}
