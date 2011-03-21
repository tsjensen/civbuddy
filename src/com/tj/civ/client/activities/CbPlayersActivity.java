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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CcClientFactoryIF;
import com.tj.civ.client.common.CcStorage;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.jso.CcPlayerJSO;
import com.tj.civ.client.model.jso.CcSituationJSO;
import com.tj.civ.client.places.CcCardsPlace;
import com.tj.civ.client.places.CcPlayersPlace;
import com.tj.civ.client.views.CcPlayersViewIF;
import com.tj.civ.client.widgets.CcPlayerSettingsBox;
import com.tj.civ.client.widgets.CcPlayerSettingsBox.CcPlayerResultCallbackIF;


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



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CcPlayersActivity(final CcPlayersPlace pPlace, final CcClientFactoryIF pClientFactory)
    {
        super();
        iClientFactory = pClientFactory;
        iGame = CcStorage.loadGame(pPlace.getMarkedGameKey());
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        CcPlayersViewIF view = iClientFactory.getPlayersView();
        view.setPresenter(this);
        view.setMarked(null);
        if (iGame != null && iGame.getSituations() != null) {
            view.setPlayers(iGame.getSituations().keySet());
        }
        pContainerWidget.setWidget(view.asWidget());
    }



    @Override
    public void goTo(final Place pPlace)
    {
        iClientFactory.getPlayersView().setMarked(null);
        iClientFactory.getPlaceController().goTo(pPlace);
    }



    @Override
    public void onNewClicked()
    {
        CcPlayerSettingsBox.showPlayerSettings("Add Player",
            iGame.getVariant().getTargetOptions(), null,
            new CcPlayerResultCallbackIF()
        {
            @Override
            public void onResultAvailable(final boolean pOkPressed,
                final String pPlayerName, final int pTargetPoints)
            {
                String name = pPlayerName != null ? pPlayerName.trim() : ""; //$NON-NLS-1$
                if (pOkPressed && name.length() > 0) {
                    if (validateName(name)) {
                        addPlayer(name, pTargetPoints);
                    }
                    else {
                        Window.alert("Cannot add '" + name + "'");
                        onNewClicked(); // TODO deferred command?
                    }
                }
            }
        });
    }



    private boolean validateName(final String pPlayerName)
    {
        boolean result = true;
        String name = pPlayerName != null ? pPlayerName.trim() : ""; //$NON-NLS-1$
        if (name.length() == 0 || name.length() > CcPlayerJSO.PLAYER_NAME_MAXLEN
            || name.indexOf(CcCardsPlace.SEP) >= 0)
        {
            result = false;
        }
        if (result && iGame.getSituations() != null) {
            result = !iGame.getSituations().keySet().contains(name);
        }
        return result;
    }



    @Override
    public void onChangeClicked(final String pClickedPlayerName)
    {
        final CcPlayerJSO playerJso = iGame.getSituations().get(pClickedPlayerName).getPlayer();
        CcPlayerSettingsBox.showPlayerSettings("Edit Player",
            pClickedPlayerName, playerJso.getWinningTotal(),
            iGame.getVariant().getTargetOptions(), null,
            new CcPlayerResultCallbackIF()
        {
            @Override
            public void onResultAvailable(final boolean pOkPressed,
                final String pPlayerName, final int pTargetPoints)
            {
                String name = pPlayerName != null ? pPlayerName.trim() : ""; //$NON-NLS-1$
                if (pOkPressed && name.length() > 0) {
                    if (pClickedPlayerName.equals(name) || validateName(name)) {
                        changePlayer(playerJso, name, pTargetPoints);
                    }
                    else {
                        Window.alert("Cannot change name to '" + name + "'");
                        onChangeClicked(pClickedPlayerName); // TODO deferred command?
                    }
                }
            }
        });
    }



    private void addPlayer(final String pPlayerName, final int pTargetPoints)
    {
        CcPlayerJSO playerJso = CcPlayerJSO.create();
        playerJso.setName(pPlayerName);
        playerJso.setWinningTotal(pTargetPoints);
        CcSituationJSO sitJso = CcSituationJSO.create(playerJso,
            iGame.getVariant().getCards().length);
        CcSituation sit = new CcSituation(sitJso, iGame.getVariant());
        CcStorage.saveSituation(sit);  // save sit *before* calling game.addPlayer()
        iGame.addPlayer(sit);
        iClientFactory.getPlayersView().addPlayer(pPlayerName);
        CcStorage.saveGame(iGame);
    }



    private void changePlayer(final CcPlayerJSO pPlayerJso, final String pPlayerName,
        final int pTargetPoints)
    {
        String oldName = pPlayerJso.getName();
        CcSituation sit = iGame.getSituations().get(oldName);
        iGame.removePlayer(sit);
        pPlayerJso.setName(pPlayerName);
        pPlayerJso.setWinningTotal(pTargetPoints);
        iGame.addPlayer(sit);
        iClientFactory.getPlayersView().renamePlayer(oldName, pPlayerName);
        CcStorage.saveSituation(sit);
        CcStorage.saveGame(iGame);
    }



    @Override
    public void onRemoveClicked(final String pPlayerName)
    {
        CcSituation sit = iGame.getSituations().get(pPlayerName);
        iGame.removePlayer(sit);
        iClientFactory.getPlayersView().deletePlayer(pPlayerName);
        CcStorage.saveGame(iGame);
        CcStorage.deleteItem(sit.getPersistenceKey());
    }



    @Override
    public String getGameKey()
    {
        return iGame.getPersistenceKey();
    }
}
