/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-31
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
package com.tj.civ.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * Image bundle of all the icons used by this application's custom code.
 *
 * @author Thomas Jensen
 */
public interface CcImagesIF
    extends ClientBundle
{
    /*
     *  -------------------- BARS -------------------- 
     */

    /** bar for cards we don't have and don't plan to buy.
     *  @return ImageResource */
    @Source(value = "bar_Absent.png")
    ImageResource barAbsent();

    /** bar for cards we already own.
     *  @return ImageResource */
    @Source(value = "bar_Owned.png")
    ImageResource barOwned();

    /** bar for cards we plan to buy in this turn.
     *  @return ImageResource */
    @Source(value = "bar_Planned.png")
    ImageResource barPlanned();

    /** seperator between bars.
     *  @return ImageResource */
    @Source(value = "bar_sep.png")
    ImageResource barSeparator();

    /*
     *  -------------------- ICONS -------------------- 
     */

    /** 'checkmark' icon for cards we already own.
     *  @return ImageResource */
    @Source(value = "state_Owned.png")
    ImageResource stateOwned();


    /*
     *  -------------------- GROUPS -------------------- 
     */

    /** symbol of the 'arts' group of cards.
     *  @return ImageResource */
    @Source(value = "grp_Arts.png")
    ImageResource groupArts();

    /** symbol of the 'civics' group of cards.
     *  @return ImageResource */
    @Source(value = "grp_Civics.png")
    ImageResource groupCivics();

    /** symbol of the 'crafts' group of cards.
     *  @return ImageResource */
    @Source(value = "grp_Crafts.png")
    ImageResource groupCrafts();

    /** symbol of the 'religion' group of cards.
     *  @return ImageResource */
    @Source(value = "grp_Religion.png")
    ImageResource groupReligion();

    /** symbol of the 'sciences' group of cards.
     *  @return ImageResource */
    @Source(value = "grp_Sciences.png")
    ImageResource groupSciences();
}
