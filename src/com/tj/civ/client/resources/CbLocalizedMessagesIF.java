/*
 * CivBuddy - A Civilization Tactics Guide
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
public interface CbLocalizedMessagesIF
    extends Messages
{
    /** Accessor method for a localized string message.
     *  @param pPrereqName localized card name of the prerequisite card
     *  @return the message text in the current locale */
    @DefaultMessage("''{0}'' required")
    String prereqFailed(@Example("Law") String pPrereqName);

    /** Accessor method for a localized string message.
     *  @param pGameName name of the game to be deleted
     *  @return the message text in the current locale */
    @DefaultMessage("Permanently delete game\n''{0}'' ?")
    String gamesAskDelete(@Example("2011-02-21 - Bei Ingo") String pGameName);

    /** Accessor method for a localized string message.
     *  @param pPlayerName player name which could not be added
     *  @return the message text in the current locale */
    @DefaultMessage("Cannot add ''{0}''")
    String playersDlgAddError(@Example("Thomas") String pPlayerName);

    /** Accessor method for a localized string message.
     *  @param pPlayerName player name which could not be chosen
     *  @return the message text in the current locale */
    @DefaultMessage("Cannot change name to ''{0}''")
    String playersDlgEditError(@Example("Thomas") String pPlayerName);
}
