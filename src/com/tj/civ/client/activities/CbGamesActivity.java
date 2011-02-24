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
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.tj.civ.client.CcClientFactoryIF;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.model.CcVariantConfigMock;
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
    private Set<CcGame> iGames = new TreeSet<CcGame>();



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
        iGames.add(new CcGame(name.trim()));
        iClientFactory.getGamesView().setSelected(null);
        iClientFactory.getGamesView().addGame(name, CcVariantConfigMock.VARIANT_NAME);
        // TODO save as JSON to local storage
    }



    private boolean isNewNameValid(final String pNewGameName)
    {
        boolean result = true;
        if (pNewGameName != null) {
            String name = pNewGameName.trim();
            if (name.length() == 0 || iGames.contains(new CcGame(name))
                || name.indexOf(CcPlayersPlace.SEP) >= 0) {
                result = false;
            }
        }
        return result;
    }



    private CcGame getGameByName(final String pName)
    {
        CcGame result = null;
        for (CcGame g : iGames) {
            if (g.getName().equals(pName)) {
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
            CcGame game = getGameByName(pClickedGame);
            iGames.remove(game);
            game.setName(newName);
            iGames.add(game);
            iClientFactory.getGamesView().setSelected(null);
            iClientFactory.getGamesView().renameGame(pClickedGame, newName);
        }
    }



    @Override
    public void onDeleteClicked(final String pClickedGame)
    {
        if (Window.confirm(CcConstants.MESSAGES.gamesAskDelete(pClickedGame)))
        {
            for (Iterator<CcGame> iter = iGames.iterator(); iter.hasNext();)
            {
                CcGame game = iter.next();
                if (pClickedGame.equalsIgnoreCase(game.getName())) {
                    iter.remove();
                    break;
                }
            }
            iClientFactory.getGamesView().setSelected(null);
            iClientFactory.getGamesView().deleteGame(pClickedGame);
        }
    }
}
