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

    /** Accessor method for a localized string message.
     *  @param pGameName name of the corrupt game
     *  @return the message text in the current locale */
    @DefaultMessage("The game ''{0}''\nis corrupt, because it is based on"
        + "\na non-existing variant.\nGame will be deleted.")
    String gameCorruptNoVariant(@Example("2011-05-06") String pGameName);

    /** Accessor method for a localized string message.
     *  @param pPointsDelta number of points missing to the target
     *  @return the message text in the current locale */
    @DefaultMessage("You would miss your target by {0} points.")
    String cardsDiscouraged(@Example("40") int pPointsDelta);

    /** Accessor method for a localized string message.
     *  @param pPrereqName localized card name of the prerequisite card
     *  @return the message text in the current locale */
    @Key("state.detail.PrereqFailed")
    @DefaultMessage("This card requires ''{0}'' to buy.")
    String stateDetailPrereqFailed(@Example("Law") String pPrereqName);

    /** Accessor method for a localized string message.
     *  @param pPointsDelta number of points missing to the target
     *  @return the message text in the current locale */
    @Key("state.detail.DiscouragedBuy")
    @DefaultMessage("If you bought this card, you would miss your points target by {0} points.")
    String stateDetailDiscouragedBuy(@Example("40") int pPointsDelta);

    /** Accessor method for a localized string message.
     *  @param pGameName (invalid) name of the game
     *  @return the message text in the current locale */
    @Key("view.games.message.invalidname")
    @DefaultMessage("Invalid game name ''{0}''.\nCannot create game.")
    String viewGamesMessageInvalidGame(@Example("2011-05-06") String pGameName);

    /** Accessor method for a localized string message.
     *  @param pNumber number of cards of a commodity
     *  @param pPoints points value of that commodity
     *  @return the message text in the current locale */
    @Key("view.funds.commodity.option")
    @DefaultMessage("{0,number} ({1,number} pts)")
    @AlternateMessage({"=1", "{0,number} ({1,number} pt)"})
    String fundsCommodityOption(@Example("6") final int pNumber,
        @PluralCount @Example("108") final int pPoints);
}
