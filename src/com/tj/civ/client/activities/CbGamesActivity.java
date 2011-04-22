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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.model.CcVariantConfigMock;
import com.tj.civ.client.model.vo.CcGameVO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.views.CcGamesViewIF;


/**
 * Presenter of the 'Games' view.
 *
 * @author Thomas Jensen
 */
public class CbGamesActivity
    extends CbAbstractActivity
    implements CcGamesViewIF.CcPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbGamesActivity.class);

    /** the persistence key of the currently selected game */
    private String iGameKey;

    /** the games in our list */
    private Set<CcGameVO> iGames = new HashSet<CcGameVO>();



    /**
     * Constructor.
     * @param pClientFactory our client factory
     */
    public CbGamesActivity(final CbClientFactoryIF pClientFactory)
    {
        super(CbConstants.DEFAULT_PLACE, pClientFactory);
        LOG.touch(CbLogAdapter.CONSTRUCTOR);
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
        CcGamesViewIF view = getClientFactory().getGamesView();
        view.setPresenter(this);
        List<CcGameVO> gameList = CbStorage.loadGameList();
        iGames = new HashSet<CcGameVO>(gameList);
        view.setGames(gameList);
        view.setMarked(iGameKey);
        CbUtil.setBrowserTitle(null);
        pContainerWidget.setWidget(view.asWidget());
        LOG.exit("start"); //$NON-NLS-1$
    }



    @Override
    public void onNewClicked()
    {
        String name = null;
        do {
            name = Window.prompt(CbConstants.STRINGS.gamesAskNewName(),
                CbConstants.DATE_FORMAT.format(new Date()) + " - "); //$NON-NLS-1$
        } while (!isNewNameValid(name)); 
        if (name == null) {
            return;  // 'Cancel' was pressed
        }
        // TODO Variante wählen / Verzweigung zur Variantenverwaltung
        CcVariantConfigMock variant = new CcVariantConfigMock();
        CcGameVO gameVO = new CcGameVO(null, name.trim(), variant.getLocalizedDisplayName());
        String key = CbStorage.saveNewGame(gameVO, variant.getVariantId());
        gameVO.setPersistenceKey(key);
        iGames.add(gameVO);
        getClientFactory().getGamesView().setMarked(null);
        getClientFactory().getGamesView().addGame(gameVO);
    }



    private boolean isNewNameValid(final String pNewGameName)
    {
        boolean result = true;
        if (pNewGameName != null) {
            String name = pNewGameName.trim();
            if (name.length() == 0 || iGames.contains(new CcGameVO(null, name, null))) {
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
    public void onChangeClicked(final String pClickedGame)
    {
        String newName = null;
        do {
            newName = Window.prompt(CbConstants.STRINGS.gamesAskRename(), pClickedGame);
            if (newName != null) {
                newName = newName.trim();
            }
        } while (!isNewNameValid(newName));
        if (newName != null) {   // null means 'Cancel'
            CcGameVO gameVO = getGameByName(pClickedGame);
            iGames.remove(gameVO);
            gameVO.setGameName(newName);
            iGames.add(gameVO);
            getClientFactory().getGamesView().setMarked(null);
            getClientFactory().getGamesView().renameGame(pClickedGame, newName);
            CbStorage.saveGame(gameVO);
        }
    }



    @Override
    public void onRemoveClicked(final String pClickedGame)
    {
        if (Window.confirm(CbConstants.MESSAGES.gamesAskDelete(pClickedGame)))
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
            getClientFactory().getGamesView().setMarked(null);
            getClientFactory().getGamesView().deleteGame(pClickedGame);
            CbStorage.deleteItem(deletedGame.getPersistenceKey());
        }
    }
}
