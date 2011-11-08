/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 09.01.2011
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
package com.tj.civ.client.widgets;

import com.google.gwt.user.client.ui.HTML;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.CbState;


/**
 * Widget showing the current and nominal cost of a card.
 *
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>.cc-CostIndicator
 *     <dd>the entire HTML element
 * <dt>.cc-CostNominal
 *     <dd>the nominal cost of the card
 * </dl>
 *
 * @author Thomas Jensen
 */
public class CbCardCostIndicator
    extends HTML
{
    /** nominal cost of the card */
    private int iCostNominal;

    /** current cost of the card */
    private int iCostCurrent;

    /** flag indicating if the current cost should be shown */
    private boolean iShowCurrentCost = true;



    /**
     * Constructor.
     * @param pCostNominal nominal cost of the card
     */
    public CbCardCostIndicator(final int pCostNominal)
    {
        super();
        iCostNominal = pCostNominal;
        iCostCurrent = pCostNominal;
        setHTML(buildHtml());
        setStyleName(CbConstants.CSS.ccCostIndicator());
    }



    private String buildHtml()
    {
        StringBuilder sb = new StringBuilder();
        if (iShowCurrentCost) {
            sb.append(iCostCurrent);
            sb.append("&nbsp;/&nbsp;"); //$NON-NLS-1$
        }
        sb.append("<span class=\"" //$NON-NLS-1$
            + CbConstants.CSS.ccCostNominal() + "\">"); //$NON-NLS-1$
        sb.append(iCostNominal);
        sb.append("</span>"); //$NON-NLS-1$
        return sb.toString();
    }



    /**
     * Update the indicator to show the new current card cost.
     * @param pCostCurrent the new value
     */
    public void setCurrentCost(final int pCostCurrent)
    {
        boolean changed = iCostCurrent != pCostCurrent;
        iCostCurrent = pCostCurrent;
        if (changed && iShowCurrentCost) {
            setHTML(buildHtml());
        }
    }



    /**
     * If the card is in state {@link CbState#Owned}, we don't display the current
     * cost.
     * @param pIsOwned <code>true</code> if Owned, or <code>false</code> if not
     */
    public void setOwned(final boolean pIsOwned)
    {
        boolean changed = iShowCurrentCost == pIsOwned;
        iShowCurrentCost = !pIsOwned;
        if (changed) {
            setHTML(buildHtml());
        }
    }
}
