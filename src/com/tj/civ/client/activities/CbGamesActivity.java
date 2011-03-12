/*
 * CivCounsel - A Civilization Tactics Guide
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CcClientFactoryIF;
import com.tj.civ.client.common.CcStorage;
import com.tj.civ.client.model.CcVariantConfigMock;
import com.tj.civ.client.model.vo.CcGameVO;
import com.tj.civ.client.places.CcGamesPlace;
import com.tj.civ.client.places.CcPlayersPlace;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.views.CcGamesViewIF;


/**
 * Presenter of the 'Games' view.
 *
 * @author Thomas Jensen
 */
public class CcGamesActivity
    extends AbstractActivity
    implements CcGamesViewIF.CcPresenterIF
{
    /** our client factory */
    private CcClientFactoryIF iClientFactory;

    /** the name of the currently selected game */
    private String iMarkedGame;

    /** the games in our list */
    private Set<CcGameVO> iGames = new HashSet<CcGameVO>();



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CcGamesActivity(final CcGamesPlace pPlace, final CcClientFactoryIF pClientFactory)
    {
        super();
        iClientFactory = pClientFactory;
        iMarkedGame = pPlace.getMarkedGame();
    }



    @Override
    public void goTo(final Place pPlace)
    {
        iClientFactory.getGamesView().setSelected(null);
        iClientFactory.getPlaceController().goTo(pPlace);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        CcGamesViewIF view = iClientFactory.getGamesView();
        view.setPresenter(this);
        List<CcGameVO> gameList = CcStorage.loadGameList();
        iGames = new HashSet<CcGameVO>(gameList);
        view.setGames(gameList);
        view.setSelected(iMarkedGame);
        pContainerWidget.setWidget(view.asWidget());
    }



    @Override
    public void onNewClicked()
    {
        String name = null;
        do {
            name = Window.prompt(CcConstants.STRINGS.gamesAskNewName(),
                CcConstants.DATE_FORMAT.format(new Date()) + " - "); //$NON-NLS-1$
        } while (!isNewNameValid(name)); 
        if (name == null) {
            return;  // 'Cancel' was pressed
        }
        // TODO Variante wählen / Verzweigung zur Variantenverwaltung
        CcVariantConfigMock variant = new CcVariantConfigMock();
        CcGameVO gameVO = new CcGameVO(null, name.trim(), variant.getLocalizedDisplayName());
        iGames.add(gameVO);
        iClientFactory.getGamesView().setSelected(null);
        iClientFactory.getGamesView().addGame(gameVO);
        CcStorage.saveNewGame(gameVO, variant.getVariantId());
    }



    private boolean isNewNameValid(final String pNewGameName)
    {
        boolean result = true;
        if (pNewGameName != null) {
            String name = pNewGameName.trim();
            if (name.length() == 0 || iGames.contains(new CcGameVO(null, name, null))
                || name.indexOf(CcPlayersPlace.SEP) >= 0) {
                result = false;
            }
        }
        return result;
    }



    private CcGameVO getGameByName(final String pName)
    {
        CcGameVO result = null;
        for (CcGameVO g : iGames) {
            if (g.getGameName().equals(pName)) {
                result = g;
                break;
            }
        }
        return result;
    }



    @Override
    public void onRenameClicked(final String pClickedGame)
    {
        String newName = null;
        do {
            newName = Window.prompt(CcConstants.STRINGS.gamesAskRename(), pClickedGame);
            if (newName != null) {
                newName = newName.trim();
            }
        } while (!isNewNameValid(newName));
        if (newName != null) {   // null means 'Cancel'
            CcGameVO gameVO = getGameByName(pClickedGame);
            iGames.remove(gameVO);
            gameVO.setGameName(newName);
            iGames.add(gameVO);
            iClientFactory.getGamesView().setSelected(null);
            iClientFactory.getGamesView().renameGame(pClickedGame, newName);
            CcStorage.saveGame(gameVO);
        }
    }



    @Override
    public void onDeleteClicked(final String pClickedGame)
    {
        if (Window.confirm(CcConstants.MESSAGES.gamesAskDelete(pClickedGame)))
        {
            CcGameVO deletedGame = null;
            for (Iterator<CcGameVO> iter = iGames.iterator(); iter.hasNext();)
            {
                deletedGame = iter.next();
                if (pClickedGame.equalsIgnoreCase(deletedGame.getGameName())) {
                    iter.remove();
                    break;
                }
            }
            iClientFactory.getGamesView().setSelected(null);
            iClientFactory.getGamesView().deleteGame(pClickedGame);
            CcStorage.deleteItem(deletedGame.getPersistenceKey());
        }
    }
}
