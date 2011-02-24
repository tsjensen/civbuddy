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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;


/**
 * Implementation of the 'Players' view.
 *
 * @author Thomas Jensen
 */
public class CcPlayersView
    extends Composite
    implements CcPlayersViewIF
{
    /** our current presenter */
    private CcPresenterIF iPresenter = null;



    /**
     * Constructor.
     */
    public CcPlayersView()
    {
        // TODO Auto-generated method stub
        initWidget(new Label("not implemented"));
    }



    @Override
    public void setPresenter(final CcPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public void setPlayers(final List<String> pPlayerNames)
    {
        // TODO Auto-generated method stub

    }



    @Override
    public void setSelected(final String pPlayerName)
    {
        // TODO Auto-generated method stub

    }



    @Override
    public void addPlayer(final String pPlayerName)
    {
        // TODO Auto-generated method stub

    }



    @Override
    public void deletePlayer(final String pPlayerName)
    {
        // TODO Auto-generated method stub

    }
}
