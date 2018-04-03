/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-14
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.views;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import org.civbuddy.client.activities.CbListPresenterIF;
import org.civbuddy.client.model.vo.CbGameVO;


/**
 * Decribes the 'Games' view interface.
 * 
 * @author Thomas Jensen
 */
public interface CbGamesViewIF
    extends IsWidget
{

    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CbPresenterIF pPresenter);



    /**
     * Set the entire list of games, potentially replacing a present list.
     * @param pGameList games
     */
    void setGames(final List<CbGameVO> pGameList);



    /**
     * Add a row to the list of games.
     * @param pGame gameVO
     */
    void addGame(final CbGameVO pGame);



    /**
     * Rename a game in the list of games.
     * @param pGameKey game key (for identification)
     * @param pNewName new name to set
     */
    void renameGame(final String pGameKey, final String pNewName);



    /**
     * Delete a row from the list of games.
     * @param pName game name
     */
    void deleteGame(final String pName);



    /**
     * Mark the game with the given ID.
     * @param pGameName game ID
     */
    void setMarked(final String pGameName);



    /**
     * Describes the presenter of the 'Games' view.
     * 
     * @author Thomas Jensen
     */
    public interface CbPresenterIF
        extends CbListPresenterIF
    {
        // adds nothing
    }
}