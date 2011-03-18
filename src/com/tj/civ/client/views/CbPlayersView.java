/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 21.02.2011
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

import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.places.CcGamesPlace;
import com.tj.civ.client.views.CcPlayersViewIF.CcPresenterIF;


/**
 * Implementation of the 'Players' view.
 *
 * @author Thomas Jensen
 */
public class CcPlayersView
    extends CcAbstractListView<Label, CcPresenterIF>
    implements CcPlayersViewIF
{
    /** message texts used in this view */
    private static final CcMessages MSGS = new CcMessages();

    static {
        MSGS.setViewTitle("Players");
        MSGS.setBtnBackCaption(SafeHtmlUtils.fromSafeConstant("&lt;&nbsp;Game"));
        MSGS.setBtnBackTooltip("Choose a different game");
        MSGS.setBtnNewCaption("Add");
        MSGS.setBtnNewTooltip("Add a new player");
        MSGS.setBtnEditCaption("Change");
        MSGS.setBtnEditTooltip("Change name and target points of a player");
        MSGS.setBtnRemoveCaption("Remove");
        MSGS.setBtnRemoveTooltip("Remove the selected player");
        MSGS.setEmptyListMessage("Add a player by pressing 'Add'.");
        MSGS.setSelectTooltip("Select this player");
    }


    /**
     * Constructor.
     */
    public CcPlayersView()
    {
        super(MSGS);
    }



    @Override
    public void setPlayers(final List<String> pPlayerNames)
    {
        getEntries().clear();
        for (String playerName : pPlayerNames) {
            getEntries().add(new Label(playerName));
        }
        updateGrid(getEntries().size() - getRowCount());
    }



    @Override
    public void addPlayer(final String pPlayerName)
    {
        getEntries().add(new Label(pPlayerName));
        updateGrid(1);
    }



    @Override
    public void renamePlayer(final String pOldName, final String pNewName)
    {
        getItem(pOldName).setText(pNewName);
        updateGrid(0);
    }



    @Override
    public void deletePlayer(final String pPlayerName)
    {
        removeItem(pPlayerName);
        updateGrid(-1);
    }



    @Override
    protected String getIdFromWidget(final Label pWidget)
    {
        return pWidget.getText();
    }



    @Override
    protected Place getPreviousPlace()
    {
        return new CcGamesPlace();
    }



    @Override
    protected Place getNextPlace(final String pItemId)
    {
        // TODO link with card view
        return null;
    }
}
