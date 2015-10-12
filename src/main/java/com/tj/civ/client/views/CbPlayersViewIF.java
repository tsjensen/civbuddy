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
package com.tj.civ.client.views;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.tj.civ.client.activities.CbListPresenterIF;


/**
 * Decribes the 'Players' view interface.
 * 
 * @author Thomas Jensen
 */
public interface CbPlayersViewIF
    extends IsWidget
{
    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CbPresenterIF pPresenter);



    /**
     * Set the entire list of players, replacing a present list.
     * @param pNames player names
     */
    void setPlayers(final Collection<String> pNames);



    /**
     * Getter.
     * @return the ID of the marked entry
     */
    String getMarkedID();



    /**
     * Setter.
     * @param pName the currently selected player
     */
    void setMarked(final String pName);



    /**
     * Add a row to the list of players.
     * @param pName player name
     */
    void addPlayer(final String pName);



    /**
     * Rename a player in the list of players.
     * @param pOldName old name (for identification)
     * @param pNewName new name to set
     */
    void renamePlayer(final String pOldName, final String pNewName);



    /**
     * Delete a player from the list of players.
     * @param pName player name
     */
    void deletePlayer(final String pName);



    /**
     * Describes the presenter of the 'Players' view.
     * 
     * @author Thomas Jensen
     */
    public interface CbPresenterIF
        extends CbListPresenterIF
    {
        /**
         * Return the persistence key of the currently selected game.
         * @return just that
         */
        String getGameKey();



        /**
         * Getter.
         * @return the persistence key of the currently selected situation
         */
        String getCurrentSituationKey();
    }
}