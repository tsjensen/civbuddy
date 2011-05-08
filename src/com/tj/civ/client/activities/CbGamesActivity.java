/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 15.02.2011
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.model.CbGame;
import com.tj.civ.client.model.CbVariantConfig;
import com.tj.civ.client.model.vo.CbGameVO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbGamesPlace;
import com.tj.civ.client.places.CbVariantsPlace;
import com.tj.civ.client.views.CbGamesViewIF;


/**
 * Presenter of the 'Games' view.
 *
 * @author Thomas Jensen
 */
public class CbGamesActivity
    extends CbAbstractActivity
    implements CbGamesViewIF.CbPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbGamesActivity.class);

    /** the games in our list */
    private Map<String, CbGameVO> iGames = new HashMap<String, CbGameVO>();

    /** the place object that got us here, may be <code>null</code> */
    private CbGamesPlace iPlace;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbGamesActivity(final CbGamesPlace pPlace, final CbClientFactoryIF pClientFactory)
    {
        super(CbConstants.DEFAULT_PLACE, pClientFactory);
        if (LOG.isTraceEnabled()) {
            LOG.enter(CbLogAdapter.CONSTRUCTOR,
                new String[]{"pPlace"}, new Object[]{pPlace});  //$NON-NLS-1$
        }

        if (pPlace != null && pPlace.getGameName() != null && pPlace.getVariantKey() != null) {
            iPlace = pPlace;
        } else {
            iPlace = null;
        }

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void goTo(final CbAbstractPlace pPlace)
    {
        getClientFactory().getGamesView().setMarked(null);
        super.goTo(pPlace);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$

        CbGamesViewIF view = getClientFactory().getGamesView();
        view.setPresenter(this);
        iGames.clear();
        List<CbGameVO> gameList = CbStorage.loadGameList();
        for (CbGameVO game : gameList)
        {
            iGames.put(game.getPersistenceKey(), game);
        }
        view.setGames(gameList);
        view.setMarked(null);
        CbUtil.setBrowserTitle(null);
        pContainerWidget.setWidget(view.asWidget());

        if (iPlace != null) {
            createNewGame();
        }

        LOG.exit("start"); //$NON-NLS-1$
    }



    private void createNewGame()
    {
        LOG.enter("createNewGame"); //$NON-NLS-1$
        if (iPlace == null) {
            LOG.exit("createNewGame"); //$NON-NLS-1$
            return;
        }

        String gameName = iPlace.getGameName() != null ? iPlace.getGameName().trim() : null;
        String variantKey = iPlace.getVariantKey() != null ? iPlace.getVariantKey().trim() : null;
        CbVariantConfig variant = CbStorage.loadVariant(variantKey);
        if (variant == null) {
            Window.alert("Unknown variant.\nCannot create game.");
            LOG.exit("createNewGame"); //$NON-NLS-1$
            return;
        }
        if (!isNewNameValid(gameName)) {
            Window.alert("Invalid game name '" + gameName + "'.\nCannot create game.");
            LOG.exit("createNewGame"); //$NON-NLS-1$
            return;
        }

        CbGameVO gameVO = new CbGameVO(null, gameName, variant.getLocalizedDisplayName());
        String key = CbStorage.saveNewGame(gameVO, variant.getPersistenceKey());
        gameVO.setPersistenceKey(key);
        iGames.put(key, gameVO);
        getClientFactory().getGamesView().addGame(gameVO);
        iPlace = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("createNewGame", //$NON-NLS-1$
                "Successfully created new game '" + key + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        LOG.exit("createNewGame"); //$NON-NLS-1$
    }



    @Override
    public void onNewClicked()
    {
        String name = null;
        do {
            name = Window.prompt(CbConstants.STRINGS.gamesAskNewName(),
                CbConstants.DATE_FORMAT.format(new Date()));
        } while (!isNewNameValid(name)); 
        if (name == null) {
            return;  // 'Cancel' was pressed
        }
        getClientFactory().getGamesView().setMarked(null);
        goTo(new CbVariantsPlace(name));
    }



    private boolean isNewNameValid(final String pNewGameName)
    {
        boolean result = true;
        if (pNewGameName != null) {
            String name = pNewGameName.trim();
            if (name.length() == 0) {
                result = false;
            }
            else {
                for (CbGameVO game : iGames.values()) {
                    if (name.equalsIgnoreCase(game.getGameName())) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }



    @Override
    public void onChangeClicked(final String pClickedGameKey)
    {
        CbGameVO gameVO = iGames.get(pClickedGameKey);
        String newName = null;
        do {
            newName = Window.prompt(CbConstants.STRINGS.gamesAskRename(),
                gameVO.getGameName());
            if (newName != null) {
                newName = newName.trim();
            }
        } while (!isNewNameValid(newName));

        if (newName != null) {   // null means 'Cancel'
            gameVO.setGameName(newName);
            getClientFactory().getGamesView().setMarked(null);
            getClientFactory().getGamesView().renameGame(pClickedGameKey, newName);
            CbStorage.renameGame(gameVO);
        }
    }



    @Override
    public void onRemoveClicked(final String pClickedGameKey)
    {
        if (Window.confirm(CbConstants.MESSAGES.gamesAskDelete(
            iGames.get(pClickedGameKey).getGameName())))
        {
            // delete from view and presenter
            CbGameVO deletedGame = iGames.remove(pClickedGameKey);
            getClientFactory().getGamesView().setMarked(null);
            getClientFactory().getGamesView().deleteGame(pClickedGameKey);

            // delete from HTML5 storage
            CbGame game = CbStorage.loadGame(pClickedGameKey);
            CbStorage.deleteGameCascading(deletedGame.getPersistenceKey(), game.getJso());
        }
    }
}
