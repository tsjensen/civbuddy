/*
 * CivBuddy - A Civilization Tactics Guide
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

import java.util.List;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.vo.CbGameVO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbPlayersPlace;
import com.tj.civ.client.views.CbGamesViewIF.CbPresenterIF;
import com.tj.civ.client.widgets.CbGameListEntry;


/**
 * Implementation of the 'Games' view.
 *
 * @author Thomas Jensen
 */
public class CbGamesView
    extends CbAbstractListView<CbGameListEntry, CbPresenterIF>
    implements CbGamesViewIF
{
    /** message texts used in this view */
    private static final CbMessages MSGS = new CbMessages();

    static {
        MSGS.setViewTitle(CbConstants.STRINGS.gamesViewTitle());
        MSGS.setBtnNewCaption(CbConstants.STRINGS.gamesBtnNew());
        MSGS.setBtnNewTooltip(CbConstants.STRINGS.gamesBtnNewTip());
        MSGS.setBtnEditCaption(CbConstants.STRINGS.gamesBtnRename());
        MSGS.setBtnEditTooltip(CbConstants.STRINGS.gamesBtnRenameTip());
        MSGS.setBtnRemoveCaption(CbConstants.STRINGS.gamesBtnDelete());
        MSGS.setBtnRemoveTooltip(CbConstants.STRINGS.gamesBtnDeleteTip());
        MSGS.setEmptyListMessage(CbConstants.STRINGS.emptyGamesListMsg());
        MSGS.setSelectTooltip(CbConstants.STRINGS.gamesChoseTip());
    }


    /**
     * Constructor.
     */
    public CbGamesView()
    {
        super(MSGS);
    }



    @Override
    public void addGame(final CbGameVO pGame)
    {
        CbGameListEntry widget = new CbGameListEntry(pGame);
        getEntries().add(widget);
        updateGrid(1);
    }



    @Override
    public void renameGame(final String pOldName, final String pNewName)
    {
        CbGameListEntry widget = getItem(pOldName);
        widget.setName(pNewName);
        updateGrid(0);
    }



    @Override
    public void deleteGame(final String pName)
    {
        removeItem(pName);
        updateGrid(-1);
    }



    @Override
    public void setGames(final List<CbGameVO> pGameList)
    {
        getEntries().clear();
        for (CbGameVO vo : pGameList)
        {
            CbGameListEntry widget = new CbGameListEntry(vo);
            getEntries().add(widget);
        }
        updateGrid(getEntries().size() - getRowCount());
    }



    @Override
    protected String getIdFromWidget(final CbGameListEntry pWidget)
    {
        return pWidget.getGameVO().getPersistenceKey();
    }



    @Override
    protected CbAbstractPlace getPreviousPlace()
    {
        return null;  // there is none
    }



    @Override
    protected CbAbstractPlace getNextPlace(final String pGameKey)
    {
        return new CbPlayersPlace(pGameKey);
    }
}
