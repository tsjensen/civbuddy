/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-15
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.model.vo.CcGameVO;
import com.tj.civ.client.places.CcPlayersPlace;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.widgets.CcGameListEntry;
import com.tj.civ.client.widgets.CcMoreArrow;


/**
 * Implementation of the 'Games' view.
 *
 * @author Thomas Jensen
 */
public class CcGamesView
    extends Composite
    implements CcGamesViewIF
{
    /** our current presenter */
    private CcPresenterIF iPresenter = null;

    /** number of columns in {@link #iGrid} */
    private static final int NUM_COLS = 3;

    /** the list we're displaying */
    private Map<String, CcGameListEntry> iEntries =
        new TreeMap<String, CcGameListEntry>(String.CASE_INSENSITIVE_ORDER);

    /** name of the currently marked game */
    private String iMarked = null;

    /** label shown when the list of games is empty */
    private Label iEmpty;

    /** the grid showing the list of games */
    private Grid iGrid;

    /** the top panel of this view */
    private VerticalPanel iPanel;

    /** button 'Rename' */
    private Button iBtnEditName;

    /** button 'Delete' */
    private Button iBtnDeleteGame;



    /**
     * Constructor.
     */
    public CcGamesView()
    {
        Button btnNewGame = new Button(CcConstants.STRINGS.gamesBtnNew());
        btnNewGame.setStyleName(CcConstants.CSS.ccButton());
        btnNewGame.setTitle(CcConstants.STRINGS.gamesBtnNewTip());
        btnNewGame.setEnabled(true);
        btnNewGame.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.onNewClicked();
            }
        });

        iBtnEditName = new Button(CcConstants.STRINGS.gamesBtnRename());
        iBtnEditName.setStyleName(CcConstants.CSS.ccButton());
        iBtnEditName.setTitle(CcConstants.STRINGS.gamesBtnRenameTip());
        iBtnEditName.setEnabled(false);
        iBtnEditName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (iMarked != null) {
                    iPresenter.onRenameClicked(iMarked);
                }
            }
        });

        iBtnDeleteGame = new Button(CcConstants.STRINGS.gamesBtnDelete());
        iBtnDeleteGame.setStyleName(CcConstants.CSS.ccButton());
        iBtnDeleteGame.setTitle(CcConstants.STRINGS.gamesBtnDeleteTip());
        iBtnDeleteGame.setEnabled(false);
        iBtnDeleteGame.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (iMarked != null) {
                    iPresenter.onDeleteClicked(iMarked);
                }
            }
        });

        HorizontalPanel headPanel = new HorizontalPanel();
        Label heading = new Label(CcConstants.STRINGS.gamesViewTitle());
        heading.setStyleName(CcConstants.CSS.ccHeading());
        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        headPanel.add(heading);
        headPanel.setStyleName(CcConstants.CSS.ccButtonPanel());
        headPanel.addStyleName(CcConstants.CSS_BLUEGRADIENT);
        
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        buttonPanel.add(btnNewGame);
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        buttonPanel.add(iBtnEditName);
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttonPanel.add(iBtnDeleteGame);
        buttonPanel.setStyleName(CcConstants.CSS.ccButtonPanel());
        buttonPanel.addStyleName(CcConstants.CSS_BLUEGRADIENT);

        iEmpty = new Label(CcConstants.STRINGS.emptyGamesListMsg());
        iEmpty.setStyleName(CcConstants.CSS.ccEmptyListLabel());

        iGrid = new Grid(0, NUM_COLS);
        iGrid.setStyleName(CcConstants.CSS.ccGrid());
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
                if (cell.getCellIndex() == NUM_COLS - 1) {
                    String game = ((CcGameListEntry) iGrid.getWidget(rowIdx, 1)).getName();
                    iPresenter.goTo(new CcPlayersPlace(game));
                } else {
                    setSelected(rowIdx);
                }
            }
        });

        iPanel = new VerticalPanel();
        iPanel.setWidth("100%"); //$NON-NLS-1$
        iPanel.add(headPanel);
        iPanel.add(buttonPanel);
        iPanel.add(iEmpty);
        initWidget(iPanel);
    }



    @Override
    public void setPresenter(final CcPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public void addGame(final CcGameVO pGame)
    {
        CcGameListEntry widget = new CcGameListEntry(pGame.getGameName(),
            pGame.getVariantNameLocalized());
        iEntries.put(pGame.getGameName(), widget);
        updateGrid(1);
    }



    @Override
    public void renameGame(final String pOldName, final String pNewName)
    {
        CcGameListEntry widget = iEntries.get(pOldName);
        widget.setName(pNewName);
        iEntries.remove(pOldName);
        iEntries.put(pNewName, widget);
        updateGrid(0);
    }



    @Override
    public void deleteGame(final String pName)
    {
        iEntries.remove(pName);
        updateGrid(-1);
    }



    private void updateGrid(final int pRowDiff)
    {
        boolean emptyShown = iPanel.getWidget(iPanel.getWidgetCount() - 1) instanceof Label;
        if (iEntries.size() == 0) {
            if (!emptyShown) {
                iPanel.remove(iPanel.getWidgetCount() - 1);
                iPanel.add(iEmpty);
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
                marker.setStyleName(CcConstants.CSS.ccListMarker());
                iGrid.setWidget(gIdx, 0, marker);
                marker.setVisible(false);
                iGrid.getCellFormatter().setStyleName(gIdx, 0, CcConstants.CSS.ccColMarker());
                iGrid.setWidget(gIdx, 2, new CcMoreArrow(CcConstants.STRINGS.gamesChoseTip()));
                iGrid.getRowFormatter().setStyleName(gIdx, CcConstants.CSS.ccRow());
                iGrid.getCellFormatter().setStyleName(gIdx, 2, CcConstants.CSS.ccColMore());
            }
        }

        Collection<CcGameListEntry> vals = iEntries.values();
        int i = 0;
        for (CcGameListEntry entry : vals) {
            iGrid.setWidget(i, 1, entry);
            entry.setRowIdx(i);
            i++;
        }
        if (emptyShown) {
            iPanel.remove(iPanel.getWidgetCount() - 1);
            iPanel.add(iGrid);
            iGrid.setVisible(true);
        }
    }



    @Override
    public void setGames(final List<CcGameVO> pGameList)
    {
        for (CcGameVO vo : pGameList)
        {
            CcGameListEntry widget = new CcGameListEntry(vo.getGameName(),
                vo.getVariantNameLocalized());
            iEntries.put(vo.getGameName(), widget);
        }
        updateGrid(iEntries.size() - iGrid.getRowCount());
    }



    private void clearMarker()
    {
        if (iMarked != null) {
            CcGameListEntry entry = iEntries.get(iMarked);
            ((Label) iGrid.getWidget(entry.getRowIdx(), 0)).setVisible(false);
            iMarked = null;
            iBtnDeleteGame.setEnabled(false);
            iBtnEditName.setEnabled(false);
        }
    }



    @Override
    public void setSelected(final String pName)
    {
        boolean clear = pName == null || pName.equals(iMarked);
        clearMarker();
        if (pName != null && !clear) {
            CcGameListEntry entry = iEntries.get(pName);
            setSelected(entry.getRowIdx());
        } 
    }



    private void setSelected(final int pRowIdx)
    {
        String newName = ((CcGameListEntry) iGrid.getWidget(pRowIdx, 1)).getName();
        boolean clear = newName == null || newName.equals(iMarked);
        clearMarker();
        if (!clear) {
            ((Label) iGrid.getWidget(pRowIdx, 0)).setVisible(true);
            iMarked = newName;
            iBtnDeleteGame.setEnabled(true);
            iBtnEditName.setEnabled(true);
        }
    }
}
