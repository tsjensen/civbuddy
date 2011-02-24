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
 * Decribes the 'Games' view interface.
 * 
 * @author Thomas Jensen
 */
public interface CcGamesViewIF
    extends IsWidget
{

    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CcPresenterIF pPresenter);



    /**
     * Set the entire list of games, potentially replacing a present list.
     * @param pNames game names
     * @param pVariants their corresponding variant names
     */
    void setGames(final List<String> pNames, final List<String> pVariants);



    /**
     * Setter.
     * @param pName the currently selected game
     */
    void setSelected(final String pName);



    /**
     * Add a row to the list of games.
     * @param pName game name
     * @param pVariant variant name
     */
    void addGame(final String pName, final String pVariant);



    /**
     * Rename a game in the list of games.
     * @param pOldName old name (for identification)
     * @param pNewName new name to set
     */
    void renameGame(final String pOldName, final String pNewName);



    /**
     * Delete a row from the list of games.
     * @param pName game name
     */
    void deleteGame(final String pName);



    /**
     * Describes the presenter of the 'Games' view.
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
         * The 'Rename' button was clicked.
         * @param pClickedGame game name
         */
        void onRenameClicked(final String pClickedGame);



        /**
         * The 'Delete' button was clicked.
         * @param pClickedGame game name
         */
        void onDeleteClicked(final String pClickedGame);
    }
}
