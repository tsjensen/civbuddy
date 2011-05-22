/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 13.03.2011
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
package com.tj.civ.client.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.activities.CbListPresenterIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.widgets.CbMoreArrow;


/**
 * Common superclass of our views which simply show a list of simple objects.
 *
 * @author Thomas Jensen
 * @param <W> the type of widget displaying our main view object in the list
 * @param <P> the presenter type used for the view instance
 */
public abstract class CbAbstractListView<W extends Widget, P extends CbListPresenterIF>
    extends Composite
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbAbstractListView.class);

    /** our current presenter */
    private P iPresenter;

    /** number of columns in {@link #iGrid} */
    private static final int NUM_COLS = 3;

    /** the list we're displaying */
    private List<W> iEntries = new ArrayList<W>();

    /** index of the currently marked item (-1 == nothing marked) */
    private int iMarkedIdx = -1;

    /** label shown when the list is empty */
    private Label iEmpty;

    /** the grid showing the list */
    private Grid iGrid;

    /** the top panel of this view */
    private VerticalPanel iPanel;

    /** button 'Edit' */
    private Button iBtnEditItem;

    /** button 'Delete' */
    private Button iBtnDeleteItem;

    /** tooltip under the 'more' arrows */
    private String iSelectTooltip;



    /**
     * Message texts used in {@link CbAbstractListView}.
     * @author Thomas Jensen
     */
    protected static class CbMessages
    {
        /** main view heading */
        private String iViewTitle = null;

        /** 'Back' button caption (text on the button itself) */
        private SafeHtml iBtnBackCaption = null;

        /** 'Back' button tooltip text */
        private String iBtnBackTooltip = null;

        /** 'New' button caption (text on the button itself) */
        private String iBtnNewCaption = null;

        /** 'New' button tooltip text */
        private String iBtnNewTooltip = null;

        /** 'Edit' button caption (text on the button itself) */
        private String iBtnEditCaption = null;

        /** 'Edit' button tooltip text */
        private String iBtnEditTooltip = null;

        /** 'Remove' button caption (text on the button itself) */
        private String iBtnRemoveCaption = null;

        /** 'Remove' button tooltip text */
        private String iBtnRemoveTooltip = null;

        /** message shown when there are no items in the list */
        private String iEmptyListMessage = null;

        /** 'More' arrow tooltip text */
        private String iSelectTooltip = null;



        protected void setViewTitle(final String pViewTitle)
        {
            iViewTitle = pViewTitle;
        }

        public void setBtnBackCaption(final SafeHtml pBtnBackCaption)
        {
            iBtnBackCaption = pBtnBackCaption;
        }

        public void setBtnBackTooltip(final String pBtnBackTooltip)
        {
            iBtnBackTooltip = pBtnBackTooltip;
        }

        protected void setBtnNewCaption(final String pBtnNewCaption)
        {
            iBtnNewCaption = pBtnNewCaption;
        }

        protected void setBtnNewTooltip(final String pBtnNewTooltip)
        {
            iBtnNewTooltip = pBtnNewTooltip;
        }

        protected void setBtnEditCaption(final String pBtnEditCaption)
        {
            iBtnEditCaption = pBtnEditCaption;
        }

        protected void setBtnEditTooltip(final String pBtnEditTooltip)
        {
            iBtnEditTooltip = pBtnEditTooltip;
        }

        protected void setBtnRemoveCaption(final String pBtnRemoveCaption)
        {
            iBtnRemoveCaption = pBtnRemoveCaption;
        }

        protected void setBtnRemoveTooltip(final String pBtnRemoveTooltip)
        {
            iBtnRemoveTooltip = pBtnRemoveTooltip;
        }

        protected void setEmptyListMessage(final String pEmptyListMessage)
        {
            iEmptyListMessage = pEmptyListMessage;
        }

        protected void setSelectTooltip(final String pSelectTooltip)
        {
            iSelectTooltip = pSelectTooltip;
        }
    }



    /**
     * Constructor.
     * @param pMsgs message texts used in this view
     */
    protected CbAbstractListView(final CbMessages pMsgs)
    {
        Button btnNewItem = new Button(pMsgs.iBtnNewCaption);
        btnNewItem.setStyleName(CbConstants.CSS.ccButton());
        btnNewItem.setTitle(pMsgs.iBtnNewTooltip);
        btnNewItem.setEnabled(true);
        btnNewItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.onNewClicked();
            }
        });

        iBtnEditItem = null;
        if (pMsgs.iBtnEditCaption != null) {
            iBtnEditItem = new Button(pMsgs.iBtnEditCaption);
            iBtnEditItem.setStyleName(CbConstants.CSS.ccButton());
            iBtnEditItem.setTitle(pMsgs.iBtnEditTooltip);
            iBtnEditItem.setEnabled(false);
            iBtnEditItem.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent pEvent)
                {
                    if (iMarkedIdx >= 0 && iMarkedIdx < iEntries.size()) {
                        iPresenter.onChangeClicked(
                            getIdFromWidget(iEntries.get(iMarkedIdx)));
                    }
                }
            });
        }

        iBtnDeleteItem = new Button(pMsgs.iBtnRemoveCaption);
        iBtnDeleteItem.setStyleName(CbConstants.CSS.ccButton());
        iBtnDeleteItem.setTitle(pMsgs.iBtnRemoveTooltip);
        iBtnDeleteItem.setEnabled(false);
        iBtnDeleteItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (iMarkedIdx >= 0 && iMarkedIdx < iEntries.size()) {
                    iPresenter.onRemoveClicked(
                        getIdFromWidget(iEntries.get(iMarkedIdx)));
                }
            }
        });

        HorizontalPanel headPanel = new HorizontalPanel();
        Label heading = new Label(pMsgs.iViewTitle);
        heading.setStyleName(CbConstants.CSS.ccHeading());
        final CbAbstractPlace backPlace = getPreviousPlace();
        if (backPlace != null) {
            Button btnBack = new Button(pMsgs.iBtnBackCaption);
            btnBack.setStyleName(CbConstants.CSS.ccButton());
            btnBack.setTitle(pMsgs.iBtnBackTooltip);
            btnBack.setEnabled(true);
            btnBack.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent pEvent)
                {
                    iPresenter.goTo(backPlace);
                }
            });
            headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
            headPanel.add(btnBack);
            headPanel.setCellWidth(btnBack, "12%"); //$NON-NLS-1$
        }
        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        headPanel.add(heading);
        headPanel.setStyleName(CbConstants.CSS.ccButtonPanel());
        headPanel.addStyleName(CbConstants.CSS_BLUEGRADIENT);
        
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        buttonPanel.add(btnNewItem);
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        if (iBtnEditItem != null) {
            buttonPanel.add(iBtnEditItem);
        } else {
            buttonPanel.add(new HTML("&nbsp;")); //$NON-NLS-1$
        }
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttonPanel.add(iBtnDeleteItem);
        buttonPanel.setStyleName(CbConstants.CSS.ccButtonPanel());
        buttonPanel.addStyleName(CbConstants.CSS_BLUEGRADIENT);

        iEmpty = new Label(pMsgs.iEmptyListMessage);
        iEmpty.setStyleName(CbConstants.CSS.ccEmptyListLabel());

        iGrid = new Grid(0, NUM_COLS);
        iGrid.setStyleName(CbConstants.CSS.ccGrid());
        ColumnFormatter cf = iGrid.getColumnFormatter();
        cf.setWidth(0, "30px");
        cf.setWidth(1, "260px");
        cf.setWidth(2, "28px");
        iGrid.setVisible(false);
        iGrid.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                Cell cell = ((Grid) pEvent.getSource()).getCellForEvent(pEvent);
                int rowIdx = cell.getRowIndex();
                if (cell.getCellIndex() > 0) {
                    @SuppressWarnings("unchecked")
                    String itemId = getIdFromWidget((W) iGrid.getWidget(rowIdx, 1));
                    iPresenter.goTo(getNextPlace(itemId));
                } else {
                    setMarked(rowIdx);
                }
            }
        });
        iSelectTooltip = pMsgs.iSelectTooltip;

        // TODO Put the click handler onto some extra widget instead of the grid,
        //      because Safari highlights the entire widget on click. This causes
        //      the whole list to flash confusingly, even when only a single entry
        //      is clicked.
        iPanel = new VerticalPanel();
        iPanel.setWidth("100%"); //$NON-NLS-1$
        iPanel.add(headPanel);
        iPanel.add(buttonPanel);
        iPanel.add(iEmpty);
        initWidget(iPanel);
    }



    /**
     * Get the identifying attribute of the item from the display widget.
     * @param pWidget the widget
     * @return the ID String (e.g. game or player name)
     */
    protected abstract String getIdFromWidget(final W pWidget);



    /**
     * Create the place object of the previous place in the click chain.
     * @return the previous place
     */
    protected abstract CbAbstractPlace getPreviousPlace();



    /**
     * Create the place object of the place navigated to upon selection of an item.
     * @param pItemId the selected item's ID
     * @return the next place
     */
    protected abstract CbAbstractPlace getNextPlace(final String pItemId);



    /**
     * Update the grid with the current state in {@link #iEntries}.
     * @param pRowDiff difference in number of entries to the last time this was called
     */
    protected void updateGrid(final int pRowDiff)
    {
        boolean emptyShown = iPanel.getWidget(iPanel.getWidgetCount() - 1) instanceof Label;
        if (iEntries.size() == 0) {
            if (!emptyShown) {
                iPanel.remove(iPanel.getWidgetCount() - 1);   // remove grid
                iPanel.add(iEmpty);                           // add 'empty' label
                iEmpty.setVisible(true);
            }
            return;
        }

        if (pRowDiff != 0) {
            iGrid.resize(iEntries.size(), NUM_COLS);
        }
        if (pRowDiff > 0) {
            for (int i = 0; i < pRowDiff; i++) {
                int gIdx = iGrid.getRowCount() - pRowDiff + i;
                Label marker = new Label("X"); //$NON-NLS-1$
                marker.setStyleName(CbConstants.CSS.ccListMarker());
                iGrid.setWidget(gIdx, 0, marker);
                marker.setVisible(false);
                iGrid.getCellFormatter().setStyleName(gIdx, 0, CbConstants.CSS.ccColMarker());
                iGrid.setWidget(gIdx, 2, new CbMoreArrow(iSelectTooltip));
                iGrid.getRowFormatter().setStyleName(gIdx, CbConstants.CSS.ccRow());
                iGrid.getCellFormatter().setStyleName(gIdx, 2, CbConstants.CSS.ccColMore());
            }
        }

        int i = 0;
        for (W w : iEntries) {
            iGrid.setWidget(i, 1, w);
            i++;
        }
        if (emptyShown) {
            iPanel.remove(iPanel.getWidgetCount() - 1);
            iPanel.add(iGrid);
            iGrid.setVisible(true);
        }
    }



    private void clearMarker()
    {
        if (iMarkedIdx >= 0) {
            iGrid.getWidget(iMarkedIdx, 0).setVisible(false);
            iMarkedIdx = -1;
            iBtnDeleteItem.setEnabled(false);
            if (iBtnEditItem != null) {
                iBtnEditItem.setEnabled(false);
            }
        }
    }



    /**
     * Gets the widget with the given item ID from {@link #iEntries}.
     * @param pItemId the widget's item ID
     * @return the widget itself
     */
    protected W getItem(final String pItemId)
    {
        W result = null;
        for (W w : iEntries) {
            if (pItemId.equals(getIdFromWidget(w))) {
                result = w;
                break;
            }
        }
        return result;
    }



    /**
     * Removes the widget with the given ID from {@link #iEntries}.
     * @param pItemId the widget's item ID
     * @return the widget that was removed, or <code>null</code> if the item was
     *              not found
     */
    protected W removeItem(final String pItemId)
    {
        W result = null;
        for (Iterator<W> iter = iEntries.iterator(); iter.hasNext();) {
            W item = iter.next();
            if (pItemId.equals(getIdFromWidget(item))) {
                result = item;
                iter.remove();
                break;
            }
        }
        return result;
    }



    /**
     * Mark the list item with the given ID.
     * @param pItemId item ID
     */
    public void setMarked(final String pItemId)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setMarked",  //$NON-NLS-1$
                new String[]{"pItemId"}, new Object[]{pItemId}); //$NON-NLS-1$
        }

        boolean clear = pItemId == null
            || pItemId.equals(getIdFromWidget(iEntries.get(iMarkedIdx)));
        clearMarker();
        if (pItemId != null && !clear) {
            int idx = 0;
            for (Iterator<W> iter = iEntries.iterator(); iter.hasNext(); idx++) {
                if (pItemId.equals(getIdFromWidget(iter.next()))) {
                    setMarked(idx);
                    break;
                }
            }
        }

        LOG.exit("setMarked"); //$NON-NLS-1$
    }



    /**
     * Mark the list item at the given position.
     * @param pRowIdx item index
     */
    private void setMarked(final int pRowIdx)
    {
        boolean clear = pRowIdx < 0 || pRowIdx >= iEntries.size()
            || pRowIdx == iMarkedIdx;
        clearMarker();
        if (!clear) {
            iGrid.getWidget(pRowIdx, 0).setVisible(true);
            iMarkedIdx = pRowIdx;
            iBtnDeleteItem.setEnabled(true);
            if (iBtnEditItem != null) {
                iBtnEditItem.setEnabled(true);
            }
        }
    }



    protected P getPresenter()
    {
        return iPresenter;
    }

    /**
     * Setter.
     * @param pPresenter the new presenter
     */
    public void setPresenter(final P pPresenter)
    {
        if (LOG.isDetailEnabled()) {
            LOG.detail("setPresenter", //$NON-NLS-1$
                "pPresenter = " + pPresenter); //$NON-NLS-1$
        }
        iPresenter = pPresenter;
    }



    protected List<W> getEntries()
    {
        return iEntries;
    }



    protected int getRowCount()
    {
        return iGrid.getRowCount();
    }
}
