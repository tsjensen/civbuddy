/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 14.02.2011
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
package com.tj.civ.client.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.widgets.CcGameListEntry;
import com.tj.civ.client.widgets.CcMoreArrow;


/**
 * Column definitions of the Games view.
 *
 * @author Thomas Jensen
 */
public class CcGamesViewColumnDefinitions
{
    /** the instance of this Singleton */
    public static final CcGamesViewColumnDefinitions INSTANCE = new CcGamesViewColumnDefinitions();

    /** column definitions of the Games view */
    private List<CcColumnDefinition<CcGame>> iColumnDefinitions;



    /**
     * Constructor.
     */
    protected CcGamesViewColumnDefinitions()
    {
        iColumnDefinitions = new ArrayList<CcColumnDefinition<CcGame>>();
        iColumnDefinitions.add(new CcColumnDefinition<CcGame>() {
            @Override
            public Widget render(final CcGame pGame)
            {
                return new HTML("&nbsp;"); //$NON-NLS-1$
            }
        });
        iColumnDefinitions.add(new CcColumnDefinition<CcGame>() {
            @Override
            public Widget render(final CcGame pGame)
            {
                return new CcGameListEntry(pGame.getViewObject());
            }
        });
        iColumnDefinitions.add(new CcColumnDefinition<CcGame>() {
            @Override
            public Widget render(final CcGame pGame)
            {
                return new CcMoreArrow("Select this game");
            }
        });
    }
}
