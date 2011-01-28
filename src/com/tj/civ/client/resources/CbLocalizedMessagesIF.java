/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-11
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

import com.google.gwt.i18n.client.Messages;


/**
 * Localized messages (with variable parts) used in the application.
 *
 * @author Thomas Jensen
 */
public interface CcLocalizedMessagesIF
    extends Messages
{
    /** Accessor method for a localized string message.
     *  @param pPrereqName localized card name of the prerequisite card
     *  @return the message text in the current locale */
    @DefaultMessage("''{0}'' required")
    String prereqFailed(@Example("Law") String pPrereqName);

    /** Accessor method for a localized string message.
     *  @param pDeficit number of points unachievable
     *  @return the message text in the current locale */
    @DefaultMessage("You''d end up {0,number} points short.")
    @PluralText({"one", "You''d end up 1 point short."})
    String discouraged(@Example("42") @PluralCount int pDeficit);
}
