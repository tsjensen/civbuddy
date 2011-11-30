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

import java.util.ArrayList;
import java.util.List;

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
        MSGS.setHeaderHint(CbConstants.STRINGS.viewGamesHeaderHint());
        MSGS.setBtnNewTooltip(CbConstants.STRINGS.viewGamesButtonNewTitle());
        MSGS.setBtnEditTooltip(CbConstants.STRINGS.viewGamesButtonRenameTitle());
        MSGS.setBtnRemoveTooltip(CbConstants.STRINGS.viewGamesButtonDeleteTitle());
        MSGS.setEmptyListMessage(CbConstants.STRINGS.viewGamesMessageEmptyList());
        MSGS.setSelectTooltip(CbConstants.STRINGS.viewGamesChooseTitle());
    }


    /**
     * Constructor.
     */
    public CbGamesView()
    {
        super(MSGS, true);
    }



    @Override
    public void addGame(final CbGameVO pGame)
    {
        CbVoListEntry<CbGameVO> widget = new CbVoListEntry<CbGameVO>(pGame);
        addDisplayWidget(widget);
    }



    @Override
    public void renameGame(final String pGameKey, final String pNewName)
    {
        CbVoListEntry<CbGameVO> widget = getItem(pGameKey);
        widget.setPrimaryText(pNewName);
    }



    @Override
    public void deleteGame(final String pName)
    {
        removeDisplayWidget(pName);
    }



    @Override
    public void setGames(final List<CbGameVO> pGameList)
    {
        List<CbVoListEntry<CbGameVO>> widgets = new ArrayList<CbVoListEntry<CbGameVO>>();
        for (CbGameVO vo : pGameList)
        {
            widgets.add(new CbVoListEntry<CbGameVO>(vo));
        }
        setDisplayWidgets(widgets);
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
