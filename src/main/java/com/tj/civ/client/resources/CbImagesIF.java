/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-31
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * Image bundle of all the icons used by this application's custom code.
 *
 * @author Thomas Jensen
 */
public interface CbImagesIF
    extends ClientBundle
{
    /*
     *  -------------------- BARS ---------------------
     */

    /** bar for cards we don't have and don't plan to buy.
     *  @return ImageResource */
    @Source("images/bar_Absent.png")
    ImageResource barAbsent();

    /** bar for cards we already own.
     *  @return ImageResource */
    @Source("images/bar_Owned.png")
    ImageResource barOwned();

    /** bar for cards we plan to buy in this turn.
     *  @return ImageResource */
    @Source("images/bar_Planned.png")
    ImageResource barPlanned();


    /*
     *  -------------- BUTTON BAR ICONS ---------------
     */

    /** the 'Add' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_add.png")
    ImageResource iconAdd();

    /** the 'Edit' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_edit.png")
    ImageResource iconEdit();

    /** the 'Trash Can' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_trash.png")
    ImageResource iconDelete();

    /** the 'Buy' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_buy.png")
    ImageResource iconBuy();

    /** the 'Revise' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_revise.png")
    ImageResource iconRevise();

    /** the 'Next' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_next.png")
    ImageResource iconNext();

    /** the 'Previous' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_prev.png")
    ImageResource iconPrevious();

    /** the 'Clear' button icon in the bottom bar.
     *  @return ImageResource */
    @Source("images/icon_clear.png")
    ImageResource iconClear();


    /*
     *  ---------------- OTHER ICONS ------------------
     */

    /** 'checkmark' icon for cards we already own.
     *  @return ImageResource */
    @Source("images/state_Owned.png")
    ImageResource stateOwned();

    /** 'question mark' icon for cards we are planning to buy.
     *  @return ImageResource */
    @Source("images/state_Planned.png")
    ImageResource statePlanned();

    /** the 'ring' bullet shown before each entry in the abstract list view.
     *  @return ImageResource */
    @Source("images/marker_passive.png")
    ImageResource markerPassive();

    /** the 'ring' bullet shown before each entry in the abstract list view,
     *  with a cross through its middle.
     *  @return ImageResource */
    @Source("images/marker_active.png")
    ImageResource markerActive();

    /** the 'Players' icon for the left navigation button.
     *  @return ImageResource */
    @Source("images/icon_players.png")
    ImageResource navIconPlayers();


    /*
     *  -------------------- GROUPS -------------------
     */

    /** symbol of the 'arts' group of cards.
     *  @return ImageResource */
    @Source("images/grp_Arts.png")
    ImageResource groupArts();

    /** symbol of the 'civics' group of cards.
     *  @return ImageResource */
    @Source("images/grp_Civics.png")
    ImageResource groupCivics();

    /** symbol of the 'crafts' group of cards.
     *  @return ImageResource */
    @Source("images/grp_Crafts.png")
    ImageResource groupCrafts();

    /** symbol of the 'religion' group of cards.
     *  @return ImageResource */
    @Source("images/grp_Religion.png")
    ImageResource groupReligion();

    /** symbol of the 'sciences' group of cards.
     *  @return ImageResource */
    @Source("images/grp_Sciences.png")
    ImageResource groupSciences();
}