/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-10-23
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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.views.CbAbstractListView;


/**
 * Widget encapsulating a selection marker, the display widget, and the 'More'
 * arrow in a <tt>&lt;div&gt;</tt>.
 * 
 * @param <W> the type of widget displaying our main view object in the list
 * @author Thomas Jensen
 */
public class CbGenericListItem<W extends Widget>
    extends Composite
    implements HasClickHandlers
{
    /** position of the selector element in the panel */
    private static final int COL_SELECTOR = 0;

    /** <tt>&lt;div&gt;</tt> around the display widget */
    private FlowPanel iDisplayWidgetWrapper;

    /** the row index within the {@link CbAbstractListView}s list */
    private int iRowIdx = -1;



    /**
     * Callback invoked when the general list item is selected, which means that
     * it was marked.
     *
     * @author Thomas Jensen
     *
     * @param <W> the type of widget displaying our main view object in the list
     */
    public interface CbSelectorCallbackIF<W extends Widget>
    {
        /**
         * Callback invoked when the general list item is selected, which means that
         * it was marked.
         * @param pSource the general list item that was marked
         */
        void onItemSelected(final CbGenericListItem<W> pSource);
    }



    /**
     * Callback invoked when the general list item's 'More' arrow was clicked.
     *
     * @author Thomas Jensen
     *
     * @param <W> the type of widget displaying our main view object in the list
     */
    public interface CbMoreArrowCallbackIF<W extends Widget>
    {
        /**
         * Callback invoked when the general list item's 'More' arrow was clicked.
         * @param pSource the general list item whose 'More' arrow was clicked
         */
        void onMoreArrowClicked(final CbGenericListItem<W> pSource);



        /**
         * The tooltip text to display on the 'More' arrow. This shouldn't really
         * go here, but we are lazy today.
         * @return the tooltip text as plain text (no HTML)
         */
        String getTooltipText();
    }



    /**
     * Constructor.
     * 
     * @param pSelectorCallback called when the selector is clicked. Must <b>not</b>
     *             be <code>null</code>.
     * @param pMoreArrowCallback called when the 'More' arrow is clicked. Must
     *             <b>not</b> be <code>null</code>.
     */
    public CbGenericListItem(final CbSelectorCallbackIF<W> pSelectorCallback,
        final CbMoreArrowCallbackIF<W> pMoreArrowCallback)
    {
        // dummy widget we put on the panel until the display widget is set in setDisplayWidget()
        Label dummy = new Label("<dummy>"); //$NON-NLS-1$

        // image shown when the marker is active
        Image markerActive = new Image(CbConstants.IMG_BUNDLE.markerActive());
        markerActive.setStyleName(CbConstants.CSS.ccColMarker());
        markerActive.setVisible(false);
        Image markerPassive = new Image(CbConstants.IMG_BUNDLE.markerPassive());
        markerPassive.setStyleName(CbConstants.CSS.ccColMarker());

        CbMoreArrow moreArrow = new CbMoreArrow(pMoreArrowCallback.getTooltipText());
        moreArrow.addStyleName(CbConstants.CSS.cbMoreArrowLabelSmaller());

        iDisplayWidgetWrapper = new FlowPanel();
        iDisplayWidgetWrapper.add(dummy);
        iDisplayWidgetWrapper.setStyleName(CbConstants.CSS.cbDisplayWidgetWrapper());

        final FlowPanel fp = new FlowPanel();
        fp.setStyleName(CbConstants.CSS.cbGeneralListItem());
        fp.add(markerActive);
        fp.add(markerPassive);
        fp.add(iDisplayWidgetWrapper);
        fp.add(moreArrow);
        
        final ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                int pos = COL_SELECTOR;
                if (!fp.getWidget(pos).isVisible()) {
                    pos++;
                }
                if (CbUtil.isInside(fp.getWidget(pos), pEvent)) {
                    pSelectorCallback.onItemSelected(CbGenericListItem.this);
                } else {
                    pMoreArrowCallback.onMoreArrowClicked(CbGenericListItem.this);
                }
            }
        };
        addClickHandler(clickHandler);
        initWidget(fp);
    }



    @SuppressWarnings("unchecked")
    public W getDisplayWidget()
    {
        return (W) iDisplayWidgetWrapper.getWidget(0);
    }

    /**
     * Setter.
     * @param pWidget the display widget to show
     */
    public void setDisplayWidget(final W pWidget)
    {
        iDisplayWidgetWrapper.clear();
        iDisplayWidgetWrapper.add(pWidget);
    }



    /**
     * Setter.
     * @param pVisible <code>true</code> if the selector (marker) should be visisble
     */
    public void setMarkerVisible(final boolean pVisible)
    {
        FlowPanel fp = (FlowPanel) getWidget();
        fp.getWidget(COL_SELECTOR).setVisible(pVisible);
        fp.getWidget(COL_SELECTOR + 1).setVisible(!pVisible);
    }



    @Override
    public HandlerRegistration addClickHandler(final ClickHandler pHandler)
    {
        return addDomHandler(pHandler, ClickEvent.getType());
    }



    public int getRowIdx()
    {
        return iRowIdx;
    }

    public void setRowIdx(final int pRowIdx)
    {
        iRowIdx = pRowIdx;
    }
}
