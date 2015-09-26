/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-05-23
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

import com.google.gwt.i18n.client.Constants;


/**
 * Accessor interface for version information from the properties file.
 *
 * @author Thomas Jensen
 */
public interface CbVersionIF
    extends Constants
{
    /**
     * Getter.
     * @return the major version number from <tt>CbVersionIF.properties</tt>
     */
    String major();



    /**
     * Getter.
     * @return the minor version number from <tt>CbVersionIF.properties</tt>
     */
    String minor();
}
