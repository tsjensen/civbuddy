/*
 * CivBuddy - A Civilization Tactics Guide
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbCardsPlace;


/**
 * Implementation of the 'Players' view.
 *
 * @author Thomas Jensen
 */
public class CbPlayersView
    extends CbAbstractListView<Label, CbPlayersViewIF.CbPresenterIF>
    implements CbPlayersViewIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbPlayersView.class);

    /** message texts used in this view */
    private static final CbMessages MSGS = new CbMessages();

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
    public CbPlayersView()
    {
        super(MSGS);
        LOG.touch(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void setPlayers(final Collection<String> pPlayerNames)
    {
        List<Label> widgets = new ArrayList<Label>();
        for (String playerName : pPlayerNames) {
            widgets.add(new Label(playerName));
        }
        setDisplayWidgets(widgets);
    }



    @Override
    public void addPlayer(final String pPlayerName)
    {
        addDisplayWidget(new Label(pPlayerName));
    }



    @Override
    public void renamePlayer(final String pOldName, final String pNewName)
    {
        getItem(pOldName).setText(pNewName);
    }



    @Override
    public void deletePlayer(final String pPlayerName)
    {
        removeDisplayWidget(pPlayerName);
    }



    @Override
    protected String getIdFromWidget(final Label pWidget)
    {
        return pWidget.getText();
    }



    @Override
    protected CbAbstractPlace getPreviousPlace()
    {
        return CbConstants.DEFAULT_PLACE;
    }



    @Override
    protected CbAbstractPlace getNextPlace(final String pPlayerName)
    {
        // TODO these should be moved to the presenter
        return new CbCardsPlace(getPresenter().getCurrentSituationKey());
    }



    @Override
    public void setMarked(final String pPlayerName)
    {
        super.setMarked(pPlayerName);
    }
}
