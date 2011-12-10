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
package com.tj.civ.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbVariantConfig;
import com.tj.civ.client.model.jso.CbPlayerJSO;
import com.tj.civ.client.model.jso.CbSituationJSO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbPlayersPlace;
import com.tj.civ.client.views.CbPlayersViewIF;
import com.tj.civ.client.widgets.CbPlayerSettingsBox;
import com.tj.civ.client.widgets.CbPlayerSettingsBox.CbPlayerResultCallbackIF;


/**
 * Presenter of the 'Players' view.
 *
 * @author Thomas Jensen
 */
public class CbPlayersActivity
    extends CbAbstractActivity
    implements CbPlayersViewIF.CbPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbPlayersActivity.class);



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbPlayersActivity(final CbPlayersPlace pPlace, final CbClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);
        LOG.enter(CbLogAdapter.CONSTRUCTOR);

        if (LOG.isDetailEnabled()) {
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "pPlace.getGameKey() = " //$NON-NLS-1$
                + (pPlace != null ? CbToString.obj2str(pPlace.getGameKey()) : null));
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "CbGlobal.getGame().getPersistenceKey() = " //$NON-NLS-1$
                + (CbGlobal.isGameSet() ? CbToString.obj2str(
                    CbGlobal.getGame().getPersistenceKey()) : null));
        }

        CbStorage.ensureGameLoadedWithGameKey(pPlace.getGameKey(), pClientFactory.getEventBus());

        if (!CbGlobal.isGameSet()) {
            Window.alert(CbConstants.STRINGS.noGame());
        }
        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$

        CbPlayersViewIF view = getClientFactory().getPlayersView();
        view.setPresenter(this);
        view.setMarked(null);

        if (!CbGlobal.isGameSet()) {
            // no game loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }

        if (CbGlobal.getGame().getSituations() != null) {
            view.setPlayers(CbGlobal.getGame().getSituations().keySet());
        }
        CbUtil.setBrowserTitle(CbGlobal.getGame().getName());
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
        CbPlayerSettingsBox.showPlayerSettings(CbConstants.STRINGS.viewPlayersDlgHeadingAdd(),
            CbGlobal.getGame().getVariant().getTargetOptions(), null,
            new CbPlayerResultCallbackIF()
        {
            // TODO bestehende Markierung entfernen!
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
        if (name.length() == 0 || name.length() > CbPlayerJSO.PLAYER_NAME_MAXLEN) {
            result = false;
        }
        if (result && CbGlobal.getGame().getSituations() != null) {
            result = !CbGlobal.getGame().getSituations().keySet().contains(name);
        }
        return result;
    }



    @Override
    public void onChangeClicked(final String pClickedPlayerName)
    {
        final CbPlayerJSO playerJso =
            CbGlobal.getGame().getSituations().get(pClickedPlayerName).getPlayer();
        CbPlayerSettingsBox.showPlayerSettings(CbConstants.STRINGS.viewPlayersDlgHeadingEdit(),
            pClickedPlayerName, playerJso.getWinningTotal(),
            CbGlobal.getGame().getVariant().getTargetOptions(), null,
            new CbPlayerResultCallbackIF()
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
        if (LOG.isTraceEnabled()) {
            LOG.enter("addPlayer",  //$NON-NLS-1$
                new String[]{"pPlayerName", "pTargetPoints"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{pPlayerName, Integer.valueOf(pTargetPoints)});
        }
        CbPlayerJSO playerJso = CbPlayerJSO.create();
        playerJso.setName(pPlayerName);
        playerJso.setWinningTotal(pTargetPoints);
        final CbVariantConfig variant = CbGlobal.getGame().getVariant();
        CbSituationJSO sitJso = CbSituationJSO.create(playerJso,
            variant.getCards().length,
            variant.getCommodities().length);
        CbSituation sit = new CbSituation(sitJso, variant);
        sit.setGame(CbGlobal.getGame());
        sit.evaluateJsoState(sitJso); // again ... TODO does not seem to work in constr.
        CbStorage.saveNewSituation(sit);  // save sit *before* calling game.addPlayer()
        CbGlobal.getGame().addPlayer(sit);
        getClientFactory().getPlayersView().addPlayer(pPlayerName);
        CbStorage.saveGame();
        LOG.exit("addPlayer"); //$NON-NLS-1$
    }



    private void changePlayer(final CbPlayerJSO pPlayerJso, final String pPlayerName,
        final int pTargetPoints)
    {
        String oldName = pPlayerJso.getName();
        CbSituation sit = CbGlobal.getGame().getSituations().get(oldName);
        CbGlobal.getGame().setCurrentSituation(null);
        CbGlobal.getGame().removePlayer(sit);
        pPlayerJso.setName(pPlayerName);
        pPlayerJso.setWinningTotal(pTargetPoints);
        CbGlobal.getGame().addPlayer(sit);
        getClientFactory().getPlayersView().renamePlayer(oldName, pPlayerName);
        getClientFactory().getPlayersView().setMarked(null);
        CbGlobal.getGame().setCurrentSituation(sit);
        CbStorage.saveSituation();
        CbStorage.saveGame();
    }



    @Override
    public void onRemoveClicked(final String pPlayerName)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("onRemoveClicked",  //$NON-NLS-1$
                new String[]{"pPlayerName"},  //$NON-NLS-1$
                new Object[]{pPlayerName});
        }
        CbSituation sit = CbGlobal.getGame().getSituations().get(pPlayerName);
        CbGlobal.getGame().removePlayer(sit);
        getClientFactory().getPlayersView().deletePlayer(pPlayerName);
        getClientFactory().getPlayersView().setMarked(null);
        CbStorage.saveGame();
        CbStorage.deleteItem(sit.getPersistenceKey());
        LOG.exit("onRemoveClicked"); //$NON-NLS-1$
    }



    @Override
    public String getGameKey()
    {
        String result = null;
        if (CbGlobal.isGameSet()) {
            result = CbGlobal.getGame().getPersistenceKey();
        }
        return result;
    }



    @Override
    public String getCurrentSituationKey()
    {
        LOG.enter("getCurrentSituationKey"); //$NON-NLS-1$

        String result = null;
        if (CbGlobal.isGameSet()) {
            String playerName = getClientFactory().getPlayersView().getMarkedID();
            if (LOG.isDetailEnabled()) {
                LOG.detail("getCurrentSituationKey", //$NON-NLS-1$
                    "playerName = " + playerName); //$NON-NLS-1$
            }
            if (playerName != null) {
                CbSituation sit = CbGlobal.getGame().getSituations().get(playerName);
                result = sit.getPersistenceKey();
            }
        }

        LOG.exit("getCurrentSituationKey", result); //$NON-NLS-1$
        return result;
    }
}
