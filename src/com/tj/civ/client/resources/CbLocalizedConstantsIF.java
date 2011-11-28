/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-04
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

import com.google.gwt.i18n.client.Constants;


/**
 * Localized String constants used in the application.
 *
 * @author Thomas Jensen
 */
public interface CbLocalizedConstantsIF
    extends Constants
{
    @DefaultStringValue("Treasury")
    String treasury();

    @DefaultStringValue("OK")
    String ok();

    @DefaultStringValue("Cancel")
    String cancel();

    @DefaultStringValue("Insufficient funds")
    String noFunds();

    @DefaultStringValue("This may prevent you from winning.")
    String askDiscouraged();

    @DefaultStringValue("You may not be able to afford this.")
    String askUnaffordable();

    @DefaultStringValue("Are you sure?")
    String askAreYouSure();

    @DefaultStringValue("Points")
    String statsPoints();

    @DefaultStringValue("Cards")
    String statsCards();

    @DefaultStringValue("Funds")
    String statsFunds();

    @DefaultStringValue("Groups")
    String statsGroups();

    @DefaultStringValue("Clear")
    String clearFunds();

    @DefaultStringValue("Set all funds to zero")
    String clearFundsDesc();

    @DefaultStringValue("This will reset all funds data.")
    String askClearFunds();

    @DefaultStringValue("Enable funds tracking")
    String enableFunds();

    @DefaultStringValue("Disable funds tracking")
    String disableFunds();

    @DefaultStringValue("Rules")
    String rules();

    @DefaultStringValue("Enter a game by pressing the '+' icon.")
    String gamesEmptyListMsg();

    @DefaultStringValue("Games")
    String gamesViewTitle();

    @DefaultStringValue("Start a new game")
    String gamesBtnNewTip();

    @DefaultStringValue("Change the name of the selected game")
    String gamesBtnRenameTip();

    @DefaultStringValue("Delete the selected game")
    String gamesBtnDeleteTip();

    @DefaultStringValue("Select this game")
    String gamesChoseTip();

    @DefaultStringValue("Rename game:")
    String gamesAskRename();

    @DefaultStringValue("Name your game:")
    String gamesAskNewName();

    @DefaultStringValue("Bonus")
    String fundsBonus();

    @DefaultStringValue("Arbitrary points you want added to your funds")
    String fundsBonusTitle();

    @DefaultStringValue("Detailed Input")
    String fundsDetailed();

    @DefaultStringValue("Funds Available")
    String fundsTotalLabel();

    @DefaultStringValue("Commodities")
    String fundsCommodities();

    @DefaultStringValue("No active game selected")
    String noGame();

    @DefaultStringValue("Error:")
    String error();

    @DefaultStringValue("Notice")
    String notice();

    @DefaultStringValue("(unknown)")
    String unknown();

    @DefaultStringValue("Add Player")
    String playersDlgTitleAdd();

    @DefaultStringValue("Edit Player")
    String playersDlgTitleEdit();

    @DefaultStringValue("Cards")
    String fundsBtnBack();   // same as viewDetailButtonBack()

    @DefaultStringValue("Go back to the civilization cards")
    String fundsBtnBackTitle();   // same as viewDetailButtonBackTitle()


    /*
     * --------- Card States -------------------------------------------------------
     */

    @Key("state.detail.Owned")
    @DefaultStringValue("You own this card.")
    String stateDetailOwned();

    @Key("state.detail.Planned")
    @DefaultStringValue("You are planning to buy this card.")
    String stateDetailPlanned();

    @Key("state.detail.Absent")
    @DefaultStringValue("You don't own this card yet.")
    String stateDetailAbsent();

    @Key("state.detail.Unaffordable")
    @DefaultStringValue("This card is currently too expensive.")
    String stateDetailUnaffordable();


    /*
     * --------- 'Cards' View ------------------------------------------------------
     */

    @Key("view.cards.heading")
    @DefaultStringValue("Cards")
    String viewCardsHeading();

    @Key("view.cards.button.back")
    @DefaultStringValue("Player")
    String viewCardsButtonBack();

    @Key("view.cards.button.back.title")
    @DefaultStringValue("Switch to another player")
    String viewCardsButtonBackTitle();

    @Key("view.cards.button.forward")
    @DefaultStringValue("Funds")
    String viewCardsButtonForward();

    @Key("view.cards.button.forward.title")
    @DefaultStringValue("Update funds")
    String viewCardsButtonForwardTitle();

    @Key("view.cards.details.title")
    @DefaultStringValue("Card Details")
    String viewCardsDetailsTitle();

    @Key("view.cards.button.revise.title")
    @DefaultStringValue("Correct the cards already owned")
    String viewCardsButtonReviseTitle();

    @Key("view.cards.ask.clearplans")
    @DefaultStringValue("This will clear your plans.")
    String viewCardsAskClearPlans();

    @Key("view.cards.msg.revising")
    @DefaultStringValue("Revising ...")
    String viewCardsMsgRevising();

    @Key("view.cards.button.revisedone")
    @DefaultStringValue("Done")
    String viewCardsButtonReviseDone();

    @Key("view.cards.button.revisedone.title")
    @DefaultStringValue("Exit Revise Mode")
    String viewCardsButtonReviseDoneTitle();

    @Key("view.cards.button.buy.title")
    @DefaultStringValue("Buy the cards flagged as 'planned'")
    String viewCardsButtonBuyTitle();


    /*
     * --------- 'Funds' View ------------------------------------------------------
     */

    @Key("view.funds.checkbox.mainswitch")
    @DefaultStringValue("Enable Funds")
    String viewFundsCheckboxMain();


    /*
     * --------- 'Detail' View -----------------------------------------------------
     */

    @Key("view.detail.heading")
    @DefaultStringValue("Details")
    String viewDetailHeading();

    @Key("view.detail.sectionheading.supports")
    @DefaultStringValue("Supports")
    String viewDetailSectionHeadingSupports();

    @Key("view.detail.sectionheading.credit")
    @DefaultStringValue("Credit")
    String viewDetailSectionHeadingCredit();

    @Key("view.detail.sectionheading.attributes")
    @DefaultStringValue("Attributes")
    String viewDetailSectionHeadingAttributes();

    @Key("view.detail.sectionheading.calamityEffects")
    @DefaultStringValue("Calamity Effects")
    String viewDetailSectionHeadingCalamityEffects();

    @Key("view.detail.button.back")
    @DefaultStringValue("Cards")
    String viewDetailButtonBack();   // same as fundsBtnBack()

    @Key("view.detail.button.back.title")
    @DefaultStringValue("Go back to the civilization cards")
    String viewDetailButtonBackTitle();   // same as fundsBtnBackTitle()

    @Key("view.detail.message.supports.none")
    @DefaultStringValue("None")
    String viewDetailMessageSupportsNone();

    @Key("view.detail.button.prev")
    @DefaultStringValue("Prev.")
    String viewDetailButtonPrev();

    @Key("view.detail.button.next")
    @DefaultStringValue("Next")
    String viewDetailButtonNext();
}
