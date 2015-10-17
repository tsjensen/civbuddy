/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-10-28
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.widgets;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import org.civbuddy.client.common.CbConstants;
import org.civbuddy.client.common.CbUtil;


/**
 * The left or right pointing navigational title bar buttons.
 * 
 * <p>This class supports mouse hover highlighting via CSS. The button face can be a
 * text or an icon.
 * 
 * <p>Using {@link #addButton}, more than one button can be shown in a linked
 * fashion.
 * 
 * <p><img src="doc-files/navbuttons.png" style="margin-left:3em;"/>
 *
 * <p>Open issue: There is currently no retina (high-rez) support.
 * <br>TODO somewhow limit the maximum width of this widget in CSS. Some older
 *     versions of IE size the button to cover the whole width of the page.
 * 
 * @author Thomas Jensen
 */
public class CbNavigationButton
    extends Composite
    implements HasClickHandlers, HasEnabled
{
    /** Gives the position of the {@link CbNavigationButton}.
     *  @author Thomas Jensen */
    public static enum CbPosition { left, right }

    /** where we store the title text of disabled buttons for later reactivation */
    private static final String DOMATTR_TITLE_BACKUP = "titleBkp";  //$NON-NLS-1$

    /** the position of this button */
    private CbPosition iPosition;

    /** index of the button face that was last clicked */
    private int iButtonFaceLastClicked = -1;



    /**
     * Constructor.
     * @param pPosition left or right pointing
     * @param pCaption button caption
     * @param pToolTip tooltip text
     */
    public CbNavigationButton(final CbPosition pPosition, final String pCaption,
        final String pToolTip)
    {
        InlineLabel lbl = buildTextButtonElement(pCaption, pToolTip);
        initWidget(buildButtonWidget(pPosition, pCaption, pToolTip, lbl));
    }



    /**
     * Constructor.
     * @param pPosition left or right pointing
     * @param pCaption button caption (used on MSIE)
     * @param pIcon icon shown on button face
     * @param pToolTip tooltip text
     */
    public CbNavigationButton(final CbPosition pPosition, final String pCaption,
        final ImageResource pIcon, final String pToolTip)
    {
        CbInlineFlowPanel ifp = buildIconButtonElement(pIcon, pToolTip);
        initWidget(buildButtonWidget(pPosition, pCaption, pToolTip, ifp));
    }



    private Widget buildButtonWidget(final CbPosition pPosition, final String pCaption,
        final String pToolTip, final Widget pInitialButtonFace)
    {
        iPosition = pPosition;

        if (CbUtil.isMSIE()) {
            Button btn = new Button(pCaption);
            btn.setTitle(pToolTip);
            DOM.setElementAttribute(btn.getElement(), CbConstants.DOMATTR_ID,
                CbConstants.CSS_NAVBUTTON + pPosition);
            return btn;
        }

        FlowPanel fp = new FlowPanel();
        fp.add(pInitialButtonFace);
        DOM.setElementAttribute(fp.getElement(), CbConstants.DOMATTR_ID,
            CbConstants.CSS_NAVBUTTON + pPosition);
        return fp;
    }



    private InlineLabel buildTextButtonElement(final String pCaption, final String pToolTip)
    {
        InlineLabel lbl = new InlineLabel(pCaption);
        lbl.setStyleName(CbConstants.CSS.cbNavButtonText());
        lbl.setTitle(pToolTip);
        return lbl;
    }



    private CbInlineFlowPanel buildIconButtonElement(final ImageResource pIcon,
        final String pToolTip)
    {
        CbInlineFlowPanel ifp = new CbInlineFlowPanel();
        Image img = new Image(pIcon);
        ifp.add(img);
        ifp.setStyleName(CbConstants.CSS.cbNavButtonText());
        ifp.setTitle(pToolTip);
        return ifp;
    }



    /**
     * Adds a button with a text caption. Adding buttons is not possible on MSIE
     * and will be ignored.
     * @param pCaption button caption
     * @param pToolTip tooltip text
     */
    public void addButton(final String pCaption, final String pToolTip)
    {
        if (!CbUtil.isMSIE()) {
            InlineLabel lbl = buildTextButtonElement(pCaption, pToolTip);
            add2Panel(lbl);
        }
    }



    /**
     * Adds a button with a text caption. Adding buttons is not possible on MSIE
     * and will be ignored.
     * @param pIcon icon shown on button face
     * @param pToolTip tooltip text
     */
    public void addButton(final ImageResource pIcon, final String pToolTip)
    {
        if (!CbUtil.isMSIE()) {
            CbInlineFlowPanel ifp = buildIconButtonElement(pIcon, pToolTip);
            add2Panel(ifp);
        }
    }



    private void add2Panel(final Widget pButtonWidget)
    {
        // the added button element points to the farther away location
        if (CbPosition.left == iPosition) {
            ((FlowPanel) getWidget()).insert(pButtonWidget, 0);
        } else {
            ((FlowPanel) getWidget()).add(pButtonWidget);
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
                    iButtonFaceLastClicked = 0;
                    if (CbUtil.isMSIE()) {
                        pHandler.onClick(pEvent);
                    }
                    else {
                        FlowPanel fp = (FlowPanel) getWidget();
                        for (int i = fp.getWidgetCount() - 1; i > 0; i--) {
                            if (CbUtil.isInside(fp.getWidget(i), pEvent)) {
                                iButtonFaceLastClicked = i;
                                break;
                            }
                        }
                        pHandler.onClick(pEvent);
                    }
                }
                else {
                    pEvent.stopPropagation();
                }
            }
        }, ClickEvent.getType());
    }



    @Override
    public boolean isEnabled()
    {
        boolean result = false;
        if (CbUtil.isMSIE()) {
            result = ((Button) getWidget()).isEnabled();
        } else {
            result = !DOM.getElementPropertyBoolean(getElement(), CbConstants.DOMATTR_DISABLED);
        }
        return result;
    }

    @Override
    public void setEnabled(final boolean pEnabled)
    {
        if (CbUtil.isMSIE()) {
            Button btn = (Button) getWidget();
            btn.setEnabled(pEnabled);
            if (pEnabled) {
                restoreTooltip(btn);
            } else {
                suspendTooltip(btn);
            }
        }
        else {
            // TODO Support individual disabling of the button fragments
            DOM.setElementPropertyBoolean(getElement(), CbConstants.DOMATTR_DISABLED, !pEnabled);
            FlowPanel fp = (FlowPanel) getWidget();
            for (Iterator<Widget> iter = fp.iterator(); iter.hasNext();) {
                Widget buttonFace = iter.next();
                if (pEnabled) {
                    restoreTooltip(buttonFace);
                    buttonFace.setStyleName(CbConstants.CSS.cbNavButtonText());
                } else {
                    suspendTooltip(buttonFace);
                    buttonFace.setStyleName(CbConstants.CSS.cbNavButtonTextDisabled());
                }
                DOM.setElementPropertyBoolean(buttonFace.getElement(),
                    CbConstants.DOMATTR_DISABLED, !pEnabled);
            }
        }
    }



    private void suspendTooltip(final Widget pWidget)
    {
        final String title = pWidget.getTitle();
        if (title != null) {
            DOM.setElementProperty(pWidget.getElement(), DOMATTR_TITLE_BACKUP, title);
        }
        pWidget.setTitle(null);
    }

    private void restoreTooltip(final Widget pWidget)
    {
        String bkp = DOM.getElementProperty(pWidget.getElement(), DOMATTR_TITLE_BACKUP);
        if (bkp != null) {
            pWidget.setTitle(bkp);
        }
    }



    public int getButtonFaceLastClicked()
    {
        return iButtonFaceLastClicked;
    }
}
