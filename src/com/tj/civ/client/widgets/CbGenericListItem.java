/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 23.10.2011
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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.views.CbAbstractListView;


/**
 * Widget encapsulating a selection marker, the display widget, and the 'More'
 * arrow in a <tt>&lt;div&gt;</tt>.
 * 
 * @param <W> the type of widget displaying our main view object in the list
 * @author Thomas Jensen
 */
public class CbGeneralListItem<W extends Widget>
    extends Composite
    implements HasClickHandlers
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbGeneralListItem.class);

    /** position of the selector element in the panel */
    private static final int COL_SELECTOR = 0;

    /** position of the displa widget in the panel */
    private static final int COL_DISPLAY_WIDGET = 1;

    /** HTML text to use for the blank in an unselected marker */
    private static final SafeHtml SAFE_BLANK =
        SafeHtmlUtils.fromSafeConstant("&nbsp;"); //$NON-NLS-1$

    /** the display widget to show */
    private W iDisplayWidget;

    /** the row index within the {@link CbAbstractListView}s list */
    private int iRowIdx;



    public interface CbSelectorCallbackIF<W extends Widget>
    {
        void onItemSelected(final CbGeneralListItem<W> pSource);
    }



    public interface CbMoreArrowCallbackIF<W extends Widget>
    {
        void onMoreArrowClicked(final CbGeneralListItem<W> pSource);



        String getTooltipText();
    }



    /**
     * Constructor.
     * 
     * @param pRowIdx the index of this  list item in the list, starting from zero
     * @param pSelectorCallback called when the selector is clicked. Must <b>not</b>
     *             be <code>null</code>.
     * @param pMoreArrowCallback called when the 'More' arrow is clicked. Must
     *             <b>not</b> be <code>null</code>.
     */
    public CbGeneralListItem(final int pRowIdx,
        final CbSelectorCallbackIF<W> pSelectorCallback,
        final CbMoreArrowCallbackIF<W> pMoreArrowCallback)
    {
        // TODO show a nice icon instead of just the 'X'
        HTML marker = new HTML(SAFE_BLANK);
        marker.setStyleName(CbConstants.CSS.ccColMarker());

        CbMoreArrow moreArrow = new CbMoreArrow(pMoreArrowCallback.getTooltipText());

        iRowIdx = pRowIdx;
        iDisplayWidget = null;

        // TODO adjust styles mit AbstractListView
        //      Die Styles sollen wie im test pageitem etc. sein, also nicht an die
        //      konkreten Widgets gebunden.
        final FlowPanel fp = new FlowPanel();
        fp.setStyleName(CbConstants.CSS.cbGeneralListItem());
        fp.add(marker);
        fp.add(new Label("<empty>")); //$NON-NLS-1$ // dummy, until a display widget is set
        fp.add(moreArrow);
        
        final ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (isInside(fp.getWidget(COL_SELECTOR), pEvent))
                {
                    pSelectorCallback.onItemSelected(CbGeneralListItem.this);
                }
                else {
                    pMoreArrowCallback.onMoreArrowClicked(CbGeneralListItem.this);
                }
            }
        };
        addClickHandler(clickHandler);
        initWidget(fp);
    }



    public W getDisplayWidget()
    {
        return iDisplayWidget;
    }

    /**
     * Setter.
     * @param pWidget the display widget to show
     */
    public void setDisplayWidget(final W pWidget)
    {
        FlowPanel fp = (FlowPanel) getWidget();
        fp.remove(COL_DISPLAY_WIDGET);
        fp.insert(pWidget, COL_DISPLAY_WIDGET);
        iDisplayWidget = pWidget;
    }



    /**
     * Getter.
     * @return <code>true</code> if the selector (marker) is currently visible
     */
    public boolean getMarkerVisible()
    {
        FlowPanel fp = (FlowPanel) getWidget();
        return fp.getWidget(COL_SELECTOR).isVisible();
    }

    /**
     * Setter.
     * @param pVisible <code>true</code> if the selector (marker) should be visisble
     */
    public void setMarkerVisible(final boolean pVisible)
    {
        FlowPanel fp = (FlowPanel) getWidget();
        if (pVisible) {
            ((HTML) fp.getWidget(COL_SELECTOR)).setText("X"); //$NON-NLS-1$
        } else {
            ((HTML) fp.getWidget(COL_SELECTOR)).setHTML(SAFE_BLANK);
        }
    }



    private static boolean isInside(final Widget pWidget, final ClickEvent pClickEvent)
    {
        boolean result = false;
        final int cx = pClickEvent.getClientX();
        final int cy = pClickEvent.getClientY();
        final int wleft = pWidget.getAbsoluteLeft();
        final int wtop = pWidget.getAbsoluteTop();

        if (LOG.isDetailEnabled()) {
            LOG.detail("isInside", //$NON-NLS-1$
                "Click at (" + cx + ',' + cy //$NON-NLS-1$
                + "), widget pos (" + wleft + ',' + wtop //$NON-NLS-1$
                + "), widget dims [" + pWidget.getOffsetWidth() + ',' //$NON-NLS-1$
                + pWidget.getOffsetHeight() + ']');
        }
        if (cx >= wleft && cy >= wtop
            && cx < wleft + pWidget.getOffsetWidth() && cy < wtop + pWidget.getOffsetHeight())
        {
            result = true;
        }
        return result;
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
