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
    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Civilization Cards")
    String civCards();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Funds")
    String funds();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Treasury")
    String treasury();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Card Details")
    String cardDetails();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("< Player")
    String changeUser();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Revise")
    String revise();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Done")
    String reviseDone();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Buy Cards")
    String commit();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("OK")
    String ok();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Cancel")
    String cancel();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("This will clear your plans.")
    String askClearPlans();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Switch to another player")
    String btnTitleChangeUser();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Correct the cards already owned")
    String btnTitleRevise();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Buy the cards flagged as 'planned'")
    String btnTitleBuyCards();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Buy all planned cards?")
    String askCommitBuy();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Insufficient funds")
    String noFunds();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("This may prevent you from winning.")
    String askDiscouraged();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("You may not be able to afford this.")
    String askUnaffordable();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Are you sure?")
    String askAreYouSure();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Points")
    String statsPoints();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Cards")
    String statsCards();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Funds")
    String statsFunds();

    /** accessor method for a localized string constant.
     *  @return the constant value in the current locale */
    @DefaultStringValue("Groups")
    String statsGroups();
}
