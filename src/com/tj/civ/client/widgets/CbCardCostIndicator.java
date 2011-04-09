/*
 * CivCounsel - A Civilization Tactics Guide
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
public class CcCardCostIndicator
    extends HTML
{
    /** nominal cost of the card */
    private int iCostNominal;



    /**
     * Constructor.
     * @param pCostNominal nominal cost of the card
     */
    public CcCardCostIndicator(final int pCostNominal)
    {
        super();
        iCostNominal = pCostNominal;
        setHTML(buildHtml(pCostNominal));
        setWordWrap(false);
        setStyleName(CbConstants.CSS.ccCostIndicator());
    }



    private String buildHtml(final int pCostCurrent)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(pCostCurrent);
        sb.append("&nbsp;/&nbsp;<span class=\"" //$NON-NLS-1$
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
        setHTML(buildHtml(pCostCurrent));
    }
}
