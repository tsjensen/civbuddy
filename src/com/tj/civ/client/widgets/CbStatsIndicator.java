/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 16.01.2011
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
import com.google.gwt.user.client.ui.HasEnabled;
import com.tj.civ.client.resources.CcConstants;


/**
 * Widget showing a stats item in the main stats on the card panel.
 *
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>.cc-StatsPlanned
 *     <dd>the planned value (if present)
 * <dt>.cc-StatsProblem
 *     <dd>the main value, if its value is flagged as problematic
 * </dl>
 *
 * @author Thomas Jensen
 */
public class CcStatsIndicator
    extends HTML
    implements HasEnabled
{
    /** a maximum or target value (optional) */
    private Integer iMax;

    /** label of the stats value */
    private String iLabel;

    /** the main value */
    private int iValue = 0;

    /** the main value including planned cards */
    private int iPlanned = -1;

    /** if set, the main value will be highlighted as problematic */
    private boolean iProblem = false;

    /** <code>true</code> to show the value in bold font */
    private boolean iBoldValue = false;

    /** flag indicating if the widget is shown as 'enabled' */
    private boolean iEnabled = false;  // DIFFERENT from the actual contructor value!



    /**
     * Constructor.
     * @param pLabel label of the stats value
     * @param pMax the immutable displayed maximum/target value
     * @param pBoldValue <code>true</code> to show the value in bold font
     */
    public CcStatsIndicator(final String pLabel, final Integer pMax,
        final boolean pBoldValue)
    {
        super();
        iMax = pMax;
        iLabel = pLabel;
        iBoldValue = pBoldValue;
        setHTML(buildHtml());
        setWordWrap(false);
        setEnabled(true);  // must be DIFFERENT from the above initialization!
    }



    private String buildHtml()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<span class=\"" //$NON-NLS-1$
            + CcConstants.CSS.ccStatsLabel() + "\">"); //$NON-NLS-1$
        sb.append(iLabel);
        sb.append(":</span>&nbsp;"); //$NON-NLS-1$
        if (iProblem) {
            sb.append("<span class=\"" //$NON-NLS-1$
                + CcConstants.CSS.ccStatsProblem() + "\">"); //$NON-NLS-1$
        }
        if (iBoldValue) {
            sb.append("<b>"); //$NON-NLS-1$
        }
        sb.append(iValue);
        if (iBoldValue) {
            sb.append("</b>"); //$NON-NLS-1$
        }
        if (iProblem) {
            sb.append("</span>"); //$NON-NLS-1$
        }
        if (iPlanned >= 0) {
            sb.append("&nbsp;<span class=\"" //$NON-NLS-1$
            + CcConstants.CSS.ccStatsPlanned() + "\">("); //$NON-NLS-1$
            sb.append(iPlanned);
            sb.append(")</span>"); //$NON-NLS-1$
        }
        if (iMax != null) {
            sb.append("&nbsp;/&nbsp;"); //$NON-NLS-1$
            sb.append(iMax.intValue());
        }
        return sb.toString();
    }



    /**
     * Getter.
     * @return {@link #iValue}
     */
    public int getValue()
    {
        return iValue;
    }

    /**
     * Sets the main value. Will clear the planned value.
     * @param pNewValue the new value, should be &gt;= 0
     */
    public void setValue(final int pNewValue)
    {
        iValue = pNewValue;
        iPlanned = -1;
        setHTML(buildHtml());
    }



    /**
     * Getter.
     * @return {@link #iPlanned}
     */
    public int getPlan()
    {
        return iPlanned;
    }

    /**
     * Sets the planned value. Will keep the main value.
     * @param pNewValue the new value, or -1 if it should no longer be shown
     */
    public void setPlan(final int pNewValue)
    {
        iPlanned = pNewValue;
        setHTML(buildHtml());
    }



    /**
     * Sets the planned value. Will keep the main value.
     * @param pValue the new value, should be &gt;= 0
     * @param pPlan the new value, or -1 if it should no longer be shown
     */
    public void setValueAndPlan(final int pValue, final int pPlan)
    {
        iValue = pValue;
        iPlanned = pPlan;
        setHTML(buildHtml());
    }



    /**
     * Setter.
     * @param pFlag <code>true</code> if the main value should be highlighted as
     *              problematic
     */
    public void setProblem(final boolean pFlag)
    {
        if (iEnabled || !pFlag) {   // see setEnabled() -> disabled means no problems
            if (iProblem != pFlag) {
                iProblem = pFlag;
                setHTML(buildHtml());
            }
        }
    }



    @Override
    public boolean isEnabled()
    {
        return iEnabled;
    }



    @Override
    public void setEnabled(final boolean pEnabled)
    {
        if (iEnabled != pEnabled) {
            iEnabled = pEnabled;
            if (pEnabled) {
                setStyleName(CcConstants.CSS.ccStatsIndicator());
            } else {
                setProblem(false);
                setStyleName(CcConstants.CSS.ccStatsIndicatorDisabled());
            }
        }
    }
}
