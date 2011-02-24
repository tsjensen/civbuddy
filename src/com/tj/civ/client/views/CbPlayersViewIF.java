/*
 * CivCounsel - A Civilization Tactics Guide Copyright (c) 2011 Thomas Jensen $Id$
 * Date created: 2011-02-14 This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License Version 2 as
 * published by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.tj.civ.client.views;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;


/**
 * Decribes the 'Players' view interface.
 * 
 * @author Thomas Jensen
 */
public interface CcPlayersViewIF
    extends IsWidget
{

    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CcPresenterIF pPresenter);



    /**
     * Set the entire list of players, potentially replacing a present list.
     * @param pNames player names
     */
    void setPlayers(final List<String> pNames);



    /**
     * Setter.
     * @param pName the currently selected player
     */
    void setSelected(final String pName);



    /**
     * Add a row to the list of players.
     * @param pName player name
     */
    void addPlayer(final String pName);



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
    public interface CcPresenterIF
        extends CcCanGoPlacesIF
    {
        /**
         * The 'New' button was clicked.
         */
        void onNewClicked();



        /**
         * The 'Delete' button was clicked.
         * @param pClickedPlayer player name
         */
        void onDeleteClicked(final String pClickedPlayer);
    }
}
