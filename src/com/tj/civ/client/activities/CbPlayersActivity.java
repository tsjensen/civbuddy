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
package com.tj.civ.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CcClientFactoryIF;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.places.CcPlayersPlace;
import com.tj.civ.client.views.CcPlayersViewIF;


/**
 * Presenter of the 'Players' view.
 *
 * @author Thomas Jensen
 */
public class CcPlayersActivity
    extends AbstractActivity
    implements CcPlayersViewIF.CcPresenterIF
{
    /** our client factory */
    private CcClientFactoryIF iClientFactory;

    /** the selected game */
    private CcGame iGame;

    /** name of the currently marked player */
    private String iMarkedPlayer;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CcPlayersActivity(final CcPlayersPlace pPlace, final CcClientFactoryIF pClientFactory)
    {
        super();
        iClientFactory = pClientFactory;
        //iGameName = pPlace.getMarkedGame(); // TODO
        iMarkedPlayer = pPlace.getMarkedPlayer();
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        CcPlayersViewIF view = iClientFactory.getPlayersView();
        view.setPresenter(this);
        view.setSelected(iMarkedPlayer);
        pContainerWidget.setWidget(view.asWidget());
    }



    @Override
    public void goTo(final Place pPlace)
    {
        // TODO Auto-generated method stub
    }



    @Override
    public void onNewClicked()
    {
        // TODO Auto-generated method stub
    }



    @Override
    public void onDeleteClicked(final String pClickedPlayer)
    {
        // TODO Auto-generated method stub
    }
}
