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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.activities.CbListPresenterIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.widgets.CbGenericListItem;
import com.tj.civ.client.widgets.CbIconButton;
import com.tj.civ.client.widgets.CbNavigationButton;


/**
 * Common superclass of our views which simply shows a list of objects.
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

    /** index of the currently marked item (-1 == nothing marked) */
    private int iMarkedIdx = -1;

    /** label shown when the list is empty */
    private Label iEmpty;

    /** the display widgets, keys are item IDs, always in sync with {@link #iGuiList} */
    private Map<String, W> iEntryMap = new HashMap<String, W>();

    /** the list of widgets, always in sync with {@link #iEntryMap} */
    private FlowPanel iGuiList;

    /** the top panel of this view */
    private FlowPanel iPanel;

    /** button 'Edit' */
    private CbIconButton iBtnEditItem;

    /** button 'Delete' */
    private CbIconButton iBtnDeleteItem;

    /** tooltip under the 'more' arrows */
    private String iSelectTooltip;

    /** called when an item is selected */
    private CbGenericListItem.CbSelectorCallbackIF<W> iSelectorCallback;

    /** called when the 'More' arrow or the display widget are clicked */
    private CbGenericListItem.CbMoreArrowCallbackIF<W> iMoreArrowCallback;



    /**
     * Message texts used in {@link CbAbstractListView}.
     * @author Thomas Jensen
     */
    protected static class CbMessages
    {
        /** main view heading */
        private String iViewTitle = null;

        /** 'Back' button caption (text on the button itself) */
        private String iBtnBackCaption = null;

        /** 'Back' button tooltip text */
        private String iBtnBackTooltip = null;

        /** 'New' button tooltip text */
        private String iBtnNewTooltip = null;

        /** 'Edit' button tooltip text */
        private String iBtnEditTooltip = null;

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

        public void setBtnBackCaption(final String pBtnBackCaption)
        {
            iBtnBackCaption = pBtnBackCaption;
        }

        public void setBtnBackTooltip(final String pBtnBackTooltip)
        {
            iBtnBackTooltip = pBtnBackTooltip;
        }

        protected void setBtnNewTooltip(final String pBtnNewTooltip)
        {
            iBtnNewTooltip = pBtnNewTooltip;
        }

        protected void setBtnEditTooltip(final String pBtnEditTooltip)
        {
            iBtnEditTooltip = pBtnEditTooltip;
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
        CbIconButton btnNewItem = new CbIconButton(CbIconButton.CbPosition.left,
            CbConstants.IMG_BUNDLE.iconAdd());
        btnNewItem.setTitle(pMsgs.iBtnNewTooltip);
        btnNewItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.onNewClicked();
            }
        });

        iBtnEditItem = null;
        if (pMsgs.iBtnEditTooltip != null) {
            iBtnEditItem = new CbIconButton(CbIconButton.CbPosition.center,
                CbConstants.IMG_BUNDLE.iconEdit());
            iBtnEditItem.setTitle(pMsgs.iBtnEditTooltip);
            iBtnEditItem.setEnabled(false);
            iBtnEditItem.addClickHandler(new ClickHandler() {
                @Override
                @SuppressWarnings("unchecked")
                public void onClick(final ClickEvent pEvent)
                {
                    if (iMarkedIdx >= 0 && iMarkedIdx < iGuiList.getWidgetCount()) {
                        iPresenter.onChangeClicked(getIdFromWidget(((CbGenericListItem<W>)
                            iGuiList.getWidget(iMarkedIdx)).getDisplayWidget()));
                    }
                }
            });
        }

        iBtnDeleteItem = new CbIconButton(CbIconButton.CbPosition.right,
            CbConstants.IMG_BUNDLE.iconDelete());
        iBtnDeleteItem.setTitle(pMsgs.iBtnRemoveTooltip);
        iBtnDeleteItem.setEnabled(false);
        iBtnDeleteItem.addClickHandler(new ClickHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (iMarkedIdx >= 0 && iMarkedIdx < iGuiList.getWidgetCount()) {
                    iPresenter.onRemoveClicked(getIdFromWidget(((CbGenericListItem<W>)
                        iGuiList.getWidget(iMarkedIdx)).getDisplayWidget()));
                }
            }
        });

        Panel headPanel = new FlowPanel();
        Label heading = new InlineLabel(pMsgs.iViewTitle);
        final CbAbstractPlace backPlace = getPreviousPlace();
        if (backPlace != null) {
            CbNavigationButton btnBack = new CbNavigationButton(
                CbNavigationButton.CbPosition.left, pMsgs.iBtnBackCaption, pMsgs.iBtnBackTooltip);
            btnBack.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent pEvent)
                {
                    iPresenter.goTo(backPlace);
                }
            });
            headPanel.add(btnBack);
        }
        headPanel.add(heading);
        headPanel.setStyleName(CbConstants.CSS.cbTitleBar());
        headPanel.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        headPanel.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());
        
        Panel buttonPanel = new FlowPanel();
        buttonPanel.add(btnNewItem);
        if (iBtnEditItem != null) {
            buttonPanel.add(iBtnEditItem);
        } else {
            buttonPanel.add(new HTML("&nbsp;")); //$NON-NLS-1$
        }
        buttonPanel.add(iBtnDeleteItem);
        buttonPanel.setStyleName(CbConstants.CSS.cbBottomBar());
        buttonPanel.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        buttonPanel.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());

        iEmpty = new Label(pMsgs.iEmptyListMessage);
        iEmpty.setStyleName(CbConstants.CSS.cbBackgroundText());

        iGuiList = new FlowPanel();
        iGuiList.setStyleName(CbConstants.CSS.cbPageItem());

        iSelectTooltip = pMsgs.iSelectTooltip;

        // TODO Put the click handler onto some extra widget instead of the grid,
        //      because Safari highlights the entire widget on click. This causes
        //      the whole list to flash confusingly, even when only a single entry
        //      is clicked. Mobile Safari calls this feature 'tap highlighting'.
        iPanel = new FlowPanel();
        iPanel.add(headPanel);
        iPanel.add(buttonPanel);
        iPanel.add(iEmpty);
        iPanel.setStyleName(CbConstants.CSS.cbAbstractListViewMargin());
        initWidget(iPanel);
    }



    private CbGenericListItem.CbSelectorCallbackIF<W> getSelectorCallback()
    {
        if (iSelectorCallback == null) {
            iSelectorCallback = new CbGenericListItem.CbSelectorCallbackIF<W>() {
                @Override
                public void onItemSelected(final CbGenericListItem<W> pSource)
                {
                    setMarked(pSource.getRowIdx());
                }
            };
        }
        return iSelectorCallback;
    }



    private CbGenericListItem.CbMoreArrowCallbackIF<W> getMoreArrowCallback()
    {
        if (iMoreArrowCallback == null) {
            iMoreArrowCallback = new CbGenericListItem.CbMoreArrowCallbackIF<W>() {
                @Override
                public void onMoreArrowClicked(final CbGenericListItem<W> pSource)
                {
                    String itemId = getIdFromWidget(pSource.getDisplayWidget());
                    iPresenter.goTo(getNextPlace(itemId));
                }
    
                @Override
                public String getTooltipText()
                {
                    return iSelectTooltip;
                }
            };
        }
        return iMoreArrowCallback;
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



    @SuppressWarnings("unchecked")
    private void clearMarker()
    {
        if (iMarkedIdx >= 0) {
            ((CbGenericListItem<W>) iGuiList.getWidget(iMarkedIdx)).setMarkerVisible(false);
            iMarkedIdx = -1;
            iBtnDeleteItem.setEnabled(false);
            if (iBtnEditItem != null) {
                iBtnEditItem.setEnabled(false);
            }
        }
    }



    /**
     * Gets the display widget with the given item ID from {@link #iGuiList}.
     * @param pItemId the widget's item ID
     * @return the widget itself
     */
    protected W getItem(final String pItemId)
    {
        W result = null;
        for (Iterator<Widget> iter = iGuiList.iterator(); iter.hasNext();)
        {
            @SuppressWarnings("unchecked")
            CbGenericListItem<W> gli = (CbGenericListItem<W>) iter.next();
            W w = gli.getDisplayWidget();
            if (pItemId.equals(getIdFromWidget(w))) {
                result = w;
                break;
            }
        }
        return result;
    }



    /**
     * Mark the list item with the given ID.
     * @param pItemId item ID
     */
    @SuppressWarnings("unchecked")
    public void setMarked(final String pItemId)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setMarked",  //$NON-NLS-1$
                new String[]{"pItemId"}, new Object[]{pItemId}); //$NON-NLS-1$
        }

        boolean clear = pItemId == null
            || pItemId.equals(getIdFromWidget(((CbGenericListItem<W>)
                iGuiList.getWidget(iMarkedIdx)).getDisplayWidget()));
        clearMarker();
        if (pItemId != null && !clear) {
            int idx = 0;
            for (Iterator<Widget> iter = iGuiList.iterator(); iter.hasNext(); idx++)
            {
                CbGenericListItem<W> gli = (CbGenericListItem<W>) iter.next();
                W w = gli.getDisplayWidget();
                if (pItemId.equals(getIdFromWidget(w))) {
                    setMarked(idx);
                    break;
                }
            }
        }

        LOG.exit("setMarked"); //$NON-NLS-1$
    }



    /**
     * Getter.
     * @return the ID of the marked entry
     */
    @SuppressWarnings("unchecked")
    public String getMarkedID()
    {
        LOG.enter("getMarkedID"); //$NON-NLS-1$
        if (LOG.isDetailEnabled()) {
            LOG.detail("getMarkedID", //$NON-NLS-1$
                "iMarkedIdx = " + iMarkedIdx); //$NON-NLS-1$
        }

        String result = null;
        if (iMarkedIdx >= 0 && iMarkedIdx < iGuiList.getWidgetCount()) {
            result = getIdFromWidget(((CbGenericListItem<W>)
                iGuiList.getWidget(iMarkedIdx)).getDisplayWidget());
        }

        LOG.exit("getMarkedID", result); //$NON-NLS-1$
        return result;
    }



    /**
     * Mark the list item at the given position.
     * @param pRowIdx item index
     */
    @SuppressWarnings("unchecked")
    private void setMarked(final int pRowIdx)
    {
        boolean clear = pRowIdx < 0 || pRowIdx >= iGuiList.getWidgetCount()
            || pRowIdx == iMarkedIdx;
        clearMarker();
        if (!clear) {
            ((CbGenericListItem<W>) iGuiList.getWidget(pRowIdx)).setMarkerVisible(true);
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



    /**
     * Add an item to the list.
     * @param pNewWidget the new display widget
     */
    protected void addDisplayWidget(final W pNewWidget)
    {
        String itemId = getIdFromWidget(pNewWidget);
        iEntryMap.put(itemId, pNewWidget);
        iGuiList.add(new CbGenericListItem<W>(iGuiList.getWidgetCount(),
            getSelectorCallback(), getMoreArrowCallback()));
        syncLists();
    }



    /**
     * Replace the list displayed with the given one.
     * @param pDisplayWidgets the new list of items to display
     */
    protected void setDisplayWidgets(final List<W> pDisplayWidgets)
    {
        iEntryMap.clear();
        iGuiList.clear();
        for (W w : pDisplayWidgets)
        {
            iEntryMap.put(getIdFromWidget(w), w);
            iGuiList.add(new CbGenericListItem<W>(iGuiList.getWidgetCount(),
                getSelectorCallback(), getMoreArrowCallback()));
        }
        syncLists();
    }



    /**
     * Removes the entry with the given item ID.
     * @param pItemId the unique item ID
     */
    protected void removeDisplayWidget(final String pItemId)
    {
        // FIXME after deleting a game, the more arrow of the bottom-most game diappears
        iEntryMap.remove(pItemId);
        iGuiList.remove(iGuiList.getWidgetCount() - 1);
        syncLists();
    }



    private void syncLists()
    {
        boolean emptyShown = iPanel.getWidget(iPanel.getWidgetCount() - 1) instanceof Label;
        if (iGuiList.getWidgetCount() == 0) {
            if (!emptyShown) {
                iPanel.remove(iPanel.getWidgetCount() - 1);   // remove FlowPanel
                iPanel.add(iEmpty);                           // add 'empty' label
            }
            return;
        }

        // TODO sorting
        int i = 0;
        Iterator<Widget> iter = iGuiList.iterator();
        for (W w : iEntryMap.values()) {
            @SuppressWarnings("unchecked")
            CbGenericListItem<W> gli = (CbGenericListItem<W>) iter.next();
            gli.setDisplayWidget(w);
            gli.setRowIdx(i);
            i++;
        }

        if (emptyShown) {
            iPanel.remove(iPanel.getWidgetCount() - 1);
            iPanel.add(iGuiList);
        }
    }
}
