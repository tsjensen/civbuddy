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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CcClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.common.CcStorage;
import com.tj.civ.client.common.CcUtil;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.jso.CcPlayerJSO;
import com.tj.civ.client.model.jso.CcSituationJSO;
import com.tj.civ.client.places.CbAbstractPlace;
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
    extends CbAbstractActivity
    implements CcPlayersViewIF.CcPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CcPlayersActivity.class);

    /** the selected game */
    private CcGame iGame;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CcPlayersActivity(final CcPlayersPlace pPlace, final CcClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);
        LOG.enter(CbLogAdapter.CONSTRUCTOR);

        iGame = null;

        if (LOG.isDetailEnabled()) {
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "pPlace.getGameKey() = " //$NON-NLS-1$
                + (pPlace != null ? CbToString.obj2str(pPlace.getGameKey()) : null));
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "CbGlobal.getGame().getPersistenceKey() = " //$NON-NLS-1$
                + (CbGlobal.getGame() != null ? CbToString.obj2str(
                    CbGlobal.getGame().getPersistenceKey()) : null));
        }
        if (pPlace != null && pPlace.getGameKey() != null)
        {
            if (CbGlobal.isSet()
                && pPlace.getGameKey().equals(CbGlobal.getGame().getPersistenceKey()))
            {
                // it's the game we already have
                LOG.debug(CbLogAdapter.CONSTRUCTOR,
                    "Using globally present game"); //$NON-NLS-1$
                iGame = CbGlobal.getGame();
                iGame.setCurrentSituation(null);
            }
            else {
                // it's a different game which we must load first
                LOG.debug(CbLogAdapter.CONSTRUCTOR,
                    "Loading game from DOM storage"); //$NON-NLS-1$
                try {
                    iGame = CcStorage.loadGame(pPlace.getGameKey());
                    if (iGame != null) {
                        iGame.setBackrefs();
                        iGame.setCurrentSituation(null);
                        CbGlobal.setGame(iGame);
                    }
                }
                catch (Throwable t) {
                    LOG.error(CbLogAdapter.CONSTRUCTOR + ": " //$NON-NLS-1$
                        + t.getClass().getName() + ": " + t.getMessage(), t); //$NON-NLS-1$
                    Window.alert(CbConstants.STRINGS.error() + ' ' + t.getMessage());
                }
            }
        }
        if (iGame == null) {
            Window.alert(CbConstants.STRINGS.noGame());
        }
        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$

        CcPlayersViewIF view = getClientFactory().getPlayersView();
        view.setPresenter(this);
        view.setMarked(null);

        if (iGame == null) {
            // no game loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }

        if (iGame.getSituations() != null) {
            view.setPlayers(iGame.getSituations().keySet());
        }
        CcUtil.setBrowserTitle(iGame.getName());
        pContainerWidget.setWidget(view.asWidget());

        LOG.exit("start"); //$NON-NLS-1$
    }



    @Override
    public void goTo(final CbAbstractPlace pPlace)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("goTo",  //$NON-NLS-1$
                new String[]{"pPlace"}, new Object[]{pPlace}); //$NON-NLS-1$
        }
        getClientFactory().getPlayersView().setMarked(null);
        super.goTo(pPlace);
        LOG.exit("goTo"); //$NON-NLS-1$
    }



    @Override
    public void onNewClicked()
    {
        CcPlayerSettingsBox.showPlayerSettings(CbConstants.STRINGS.playersDlgTitleAdd(),
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
                        Window.alert(CbConstants.MESSAGES.playersDlgAddError(name));
                        onNewClicked(); // TODO deferred command? no recursion?
                    }
                }
            }
        });
    }



    private boolean validateName(final String pPlayerName)
    {
        boolean result = true;
        String name = pPlayerName != null ? pPlayerName.trim() : ""; //$NON-NLS-1$
        if (name.length() == 0 || name.length() > CcPlayerJSO.PLAYER_NAME_MAXLEN) {
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
        CcPlayerSettingsBox.showPlayerSettings(CbConstants.STRINGS.playersDlgTitleEdit(),
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
                        Window.alert(CbConstants.MESSAGES.playersDlgEditError(name));
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
            iGame.getVariant().getCards().length,
            iGame.getVariant().getCommodities().length);
        CcSituation sit = new CcSituation(sitJso, iGame.getVariant());
        CcStorage.saveSituation(sit);  // save sit *before* calling game.addPlayer()
        iGame.addPlayer(sit);
        getClientFactory().getPlayersView().addPlayer(pPlayerName);
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
        getClientFactory().getPlayersView().renamePlayer(oldName, pPlayerName);
        getClientFactory().getPlayersView().setMarked(null);
        CcStorage.saveSituation(sit);
        CcStorage.saveGame(iGame);
    }



    @Override
    public void onRemoveClicked(final String pPlayerName)
    {
        CcSituation sit = iGame.getSituations().get(pPlayerName);
        iGame.removePlayer(sit);
        getClientFactory().getPlayersView().deletePlayer(pPlayerName);
        getClientFactory().getPlayersView().setMarked(null);
        CcStorage.saveGame(iGame);
        CcStorage.deleteItem(sit.getPersistenceKey());
    }



    @Override
    public String getGameKey()
    {
        String result = null;
        if (iGame != null) {
            result = iGame.getPersistenceKey();
        }
        return result;
    }



    @Override
    public CcSituation getCurrentSituation()
    {
        CcSituation result = null;
        if (iGame != null) {
            result = iGame.getCurrentSituation();
        }
        return result;
    }

    @Override
    public void setCurrentSituation(final String pPlayerName)
    {
        if (iGame != null) {
            CcSituation sit = null;
            if (pPlayerName != null) {
                sit = iGame.getSituations().get(pPlayerName);
            }
            iGame.setCurrentSituation(sit);
        }
    }
}
