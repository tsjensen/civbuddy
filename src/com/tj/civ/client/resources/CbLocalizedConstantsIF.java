/*
 * CivCounsel - A Civilization Tactics Guide
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
public interface CcLocalizedConstantsIF
    extends Constants
{
    @DefaultStringValue("Civilization Cards")
    String civCards();

    @DefaultStringValue("Funds")
    String funds();

    @DefaultStringValue("Treasury")
    String treasury();

    @DefaultStringValue("Card Details")
    String cardDetails();

    @DefaultStringValue("< Player")
    String changeUser();

    @DefaultStringValue("Revise")
    String revise();

    @DefaultStringValue("Done")
    String reviseDone();

    @DefaultStringValue("Buy Cards")
    String commit();

    @DefaultStringValue("OK")
    String ok();

    @DefaultStringValue("Cancel")
    String cancel();

    @DefaultStringValue("This will clear your plans.")
    String askClearPlans();

    @DefaultStringValue("Switch to another player")
    String btnTitleChangeUser();

    @DefaultStringValue("Correct the cards already owned")
    String btnTitleRevise();

    @DefaultStringValue("Buy the cards flagged as 'planned'")
    String btnTitleBuyCards();

    @DefaultStringValue("Buy all planned cards?")
    String askCommitBuy();

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

    @DefaultStringValue("On")
    String on();

    @DefaultStringValue("Off")
    String off();

    @DefaultStringValue("Enable funds tracking")
    String enableFunds();

    @DefaultStringValue("Disable funds tracking")
    String disableFunds();
}
