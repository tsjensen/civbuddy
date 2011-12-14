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
    @DefaultStringValue("OK")
    String ok();

    @DefaultStringValue("Cancel")
    String cancel();

    @DefaultStringValue("Insufficient funds")
    String noFunds();

    @DefaultStringValue("Are you sure?")
    String askAreYouSure();

    @DefaultStringValue("Rules")
    String rules();

    @DefaultStringValue("No active game selected")
    String noGame();

    @DefaultStringValue("Error:")
    String error();

    @DefaultStringValue("Notice")
    String notice();

    @DefaultStringValue("(unknown)")
    String unknown();


    /*
     * --------- Card States -------------------------------------------------------
     */

    @Key("group.C.name")
    @DefaultStringValue("Crafts")
    String groupNameC();

    @Key("group.S.name")
    @DefaultStringValue("Sciences")
    String groupNameS();

    @Key("group.A.name")
    @DefaultStringValue("Arts")
    String groupNameA();

    @Key("group.G.name")
    @DefaultStringValue("Civics")
    String groupNameG();

    @Key("group.R.name")
    @DefaultStringValue("Religion")
    String groupNameR();
    

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
     * --------- 'Games' View ------------------------------------------------------
     */

    @Key("view.games.headerhint")
    @DefaultStringValue("Select game:")
    String viewGamesHeaderHint();

    @Key("view.games.button.new.title")
    @DefaultStringValue("Start a new game")
    String viewGamesButtonNewTitle();

    @Key("view.games.button.rename.title")
    @DefaultStringValue("Change the name of the selected game")
    String viewGamesButtonRenameTitle();

    @Key("view.games.button.remove.title")
    @DefaultStringValue("Remove the selected game")
    String viewGamesButtonRemoveTitle();

    @Key("view.games.message.emptylist")
    @DefaultStringValue("Enter a game by pressing the '+' icon.")
    String viewGamesMessageEmptyList();

    @Key("view.games.choose.title")
    @DefaultStringValue("Select this game")
    String viewGamesChooseTitle();

    @Key("view.games.ask.rename")
    @DefaultStringValue("Rename game:")
    String viewGamesAskRename();

    @Key("view.games.ask.newname")
    @DefaultStringValue("Name your game:")
    String viewGamesAskNewName();

    @Key("view.games.message.unknownvariant")
    @DefaultStringValue("Unknown variant.\nCannot create game.")
    String viewGamesMessageUnknownVariant();


    /*
     * --------- 'Players' View ----------------------------------------------------
     */

    @Key("view.players.heading")
    @DefaultStringValue("Players")
    String viewPlayersHeading();

    @Key("view.players.headerhint")
    @DefaultStringValue("Select player:")
    String viewPlayersHeaderHint();

    @Key("view.players.navbutton.back")
    @DefaultStringValue("Game")
    String viewPlayersNavbuttonBack();

    @Key("view.players.navbutton.back.title")
    @DefaultStringValue("Choose a different game")
    String viewPlayersNavbuttonBackTitle();

    @Key("view.players.button.new.title")
    @DefaultStringValue("Add a new player")
    String viewPlayersButtonNewTitle();

    @Key("view.players.button.edit.title")
    @DefaultStringValue("Change name and target points of a player")
    String viewPlayersButtonEditTitle();

    @Key("view.players.button.remove.title")
    @DefaultStringValue("Remove the selected player")
    String viewPlayersButtonRemoveTitle();

    @Key("view.players.message.emptylist")
    @DefaultStringValue("Add a player by pressing the '+' icon.")
    String viewPlayersMessageEmptyList();

    @Key("view.players.choose.title")
    @DefaultStringValue("Select this player")
    String viewPlayersChooseTitle();

    @Key("view.players.dialog.heading.add")
    @DefaultStringValue("Add Player")
    String viewPlayersDlgHeadingAdd();

    @Key("view.players.dialog.heading.edit")
    @DefaultStringValue("Edit Player")
    String viewPlayersDlgHeadingEdit();


    /*
     * --------- 'Variants' View ---------------------------------------------------
     */

    @Key("view.variants.heading")
    @DefaultStringValue("Variants")
    String viewVariantsHeading();

    @Key("view.variants.headerhint")
    @DefaultStringValue("Pick a game variant:")
    String viewVariantsHeaderHint();

    @Key("view.variants.navbutton.back")
    @DefaultStringValue("Cancel")
    String viewVariantsNavbuttonBack();

    @Key("view.variants.navbutton.back.title")
    @DefaultStringValue("Return to the list of games")
    String viewVariantsNavbuttonBackTitle();

    @Key("view.variants.button.new.title")
    @DefaultStringValue("Define a new variant")
    String viewVariantsButtonNewTitle();

    @Key("view.variants.button.remove.title")
    @DefaultStringValue("Remove the selected variant")
    String viewVariantsButtonRemoveTitle();

    @Key("view.variants.message.emptylist")
    @DefaultStringValue("Define a new game variant by pressing the '+' icon.")
    String viewVariantsMessageEmptyList();

    @Key("view.variants.choose.title")
    @DefaultStringValue("Select this variant")
    String viewVariantsChooseTitle();


    /*
     * --------- 'Cards' View ------------------------------------------------------
     */

    @Key("view.cards.heading")
    @DefaultStringValue("Cards")
    String viewCardsHeading();

    @Key("view.cards.navbutton.back")
    @DefaultStringValue("Player")
    String viewCardsNavbuttonBack();

    @Key("view.cards.navbutton.back.title")
    @DefaultStringValue("Switch to another player")
    String viewCardsNavbuttonBackTitle();

    @Key("view.cards.navbutton.forward")
    @DefaultStringValue("Funds")
    String viewCardsNavbuttonForward();

    @Key("view.cards.navbutton.forward.title")
    @DefaultStringValue("Update funds")
    String viewCardsNavbuttonForwardTitle();

    @Key("view.cards.stats.score")
    @DefaultStringValue("Score")
    String viewCardsStatsScore();

    @Key("view.cards.stats.cards")
    @DefaultStringValue("Cards")
    String viewCardsStatsCards();

    @Key("view.cards.stats.groups")
    @DefaultStringValue("Groups")
    String viewCardsStatsGroups();

    @Key("view.cards.choose.title")
    @DefaultStringValue("Card Details")
    String viewCardsChooseTitle();

    @Key("view.cards.button.revise.title")
    @DefaultStringValue("Correct the cards already owned")
    String viewCardsButtonReviseTitle();

    @Key("view.cards.ask.clearplans")
    @DefaultStringValue("This will clear your plans.")
    String viewCardsAskClearPlans();

    @Key("view.cards.ask.discouraged")
    @DefaultStringValue("This may prevent you from winning.")
    String viewCardsAskDiscouraged();

    @Key("view.cards.ask.unaffordable")
    @DefaultStringValue("You may not be able to afford this.")
    String viewCardsAskUnaffordable();

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

    @Key("view.funds.navbutton.back")
    @DefaultStringValue("Cards")
    String viewFundsNavbuttonBack();

    @Key("view.funds.navbutton.back.title")
    @DefaultStringValue("Go back to the civilization cards")
    String viewFundsNavbuttonBackTitle();

    @Key("view.funds.stats.totalfunds")
    @DefaultStringValue("Funds")
    String viewFundsStatsTotalFunds();

    @Key("view.funds.stats.commodities")
    @DefaultStringValue("Commodities")
    String viewFundsStatsCommodities();

    @Key("view.funds.checkbox.mainswitch")
    @DefaultStringValue("Enable Funds")
    String viewFundsCheckboxMain();

    @Key("view.funds.checkbox.detailed")
    @DefaultStringValue("Detailed Input")
    String viewFundsCheckboxDetailed();

    @Key("view.funds.input.treasury")
    @DefaultStringValue("Treasury")
    String viewFundsInputTreasury();

    @Key("view.funds.label.miningyield")
    @DefaultStringValue("Mining Yield")
    String viewFundsLabelMiningYield();

    @Key("view.funds.input.bonus")
    @DefaultStringValue("Bonus")
    String viewFundsInputBonus();

    @Key("view.funds.input.bonus.title")
    @DefaultStringValue("Arbitrary points you want added to your funds")
    String viewFundsInputBonusTitle();

    @Key("view.funds.input.totalfunds")
    @DefaultStringValue("Funds Available")
    String viewFundsInputTotalFunds();

    @Key("view.funds.button.clear.title")
    @DefaultStringValue("Set all funds to zero")
    String viewFundsButtonClearTitle();

    @Key("view.funds.ask.clear")
    @DefaultStringValue("This will reset all funds data.")
    String viewFundsAskClear();


    /*
     * --------- 'Detail' View -----------------------------------------------------
     */

    @Key("view.detail.heading")
    @DefaultStringValue("Details")
    String viewDetailHeading();

    @Key("view.detail.sectionheading.currentcost")
    @DefaultStringValue("Current cost")
    String viewDetailSectionHeadingCurrentCost();

    @Key("view.detail.sectionheading.credit")
    @DefaultStringValue("Credit")
    String viewDetailSectionHeadingCredit();

    @Key("view.detail.sectionheading.attributes")
    @DefaultStringValue("Attributes")
    String viewDetailSectionHeadingAttributes();

    @Key("view.detail.sectionheading.calamityEffects")
    @DefaultStringValue("Calamity Effects")
    String viewDetailSectionHeadingCalamityEffects();

    @Key("view.detail.sectionheading.supports")
    @DefaultStringValue("Supports")
    String viewDetailSectionHeadingSupports();

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
