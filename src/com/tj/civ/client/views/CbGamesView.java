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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.vo.CbGameVO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbPlayersPlace;
import com.tj.civ.client.views.CbGamesViewIF.CbPresenterIF;
import com.tj.civ.client.widgets.CbVoListEntry;


/**
 * Implementation of the 'Games' view.
 *
 * @author Thomas Jensen
 */
public class CbGamesView
    extends CbAbstractListView<CbVoListEntry<CbGameVO>, CbPresenterIF>
    implements CbGamesViewIF
{
    /** message texts used in this view */
    private static final CbMessages MSGS = new CbMessages();

    static {
        MSGS.setViewTitle(CbConstants.APPNAME);
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

        // add version info to corner of screen
        final String version = 'v' + CbConstants.VERSION.major() + '.'
            + CbConstants.VERSION.minor() + " (build "    //$NON-NLS-1$
            + CbConstants.BUILD_NUM.buildNumber() + ')';
        HTML versionInfo = new HTML(version);
        versionInfo.setStyleName(CbConstants.CSS.ccGamesVersionInfo());
        ((VerticalPanel) getWidget()).insert(versionInfo, 2);
    }



    @Override
    public void addGame(final CbGameVO pGame)
    {
        CbVoListEntry<CbGameVO> widget = new CbVoListEntry<CbGameVO>(pGame);
        getEntries().add(widget);
        updateGrid(1);
    }



    @Override
    public void renameGame(final String pGameKey, final String pNewName)
    {
        CbVoListEntry<CbGameVO> widget = getItem(pGameKey);
        widget.setPrimaryText(pNewName);
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
            CbVoListEntry<CbGameVO> widget = new CbVoListEntry<CbGameVO>(vo);
            getEntries().add(widget);
        }
        updateGrid(getEntries().size() - getRowCount());
    }



    @Override
    protected String getIdFromWidget(final CbVoListEntry<CbGameVO> pWidget)
    {
        return pWidget.getViewObject().getPersistenceKey();
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
