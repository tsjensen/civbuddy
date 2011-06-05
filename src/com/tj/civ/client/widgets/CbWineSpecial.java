/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 21.05.2011
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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CbCommSpinnerPayload;
import com.tj.civ.client.model.jso.CbCommodityConfigJSO;


/**
 * A widget for handling the special commodity 'Wine' from the Western Expansion.
 *
 * @author Thomas Jensen
 */
public class CbWineSpecial
    extends Composite
    implements HasEnabled, HasValueChangeHandlers<CbCommSpinnerPayload>
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbWineSpecial.class);

    /** the spinners for each kind of wine */
    private CbCommoditySpinner[] iSpinners;

    /** total number of commodity cards (wine only, of course) */
    private int iNumber;

    /** Label showing the title and total points */
    private CbLabel iLblPoints;

    /** localized name of the wine commodity */
    private String iName;



    /**
     * Constructor.
     * @param pCommIDx the index values into the commodities config
     * @param pConfig the commodity configs (array of same length as <tt>pCommIdx</tt>)
     * @param pCounts current commodity counts (array of same length as <tt>pCommIdx</tt>)
     */
    public CbWineSpecial(final int[] pCommIDx, final CbCommodityConfigJSO[] pConfig,
        final int[] pCounts)
    {
        final ValueChangeHandler<CbCommSpinnerPayload> vch =
            new ValueChangeHandler<CbCommSpinnerPayload>() {
                @Override
                public void onValueChange(
                    final ValueChangeEvent<CbCommSpinnerPayload> pEvent)
                {
                    onInnerSpinnerChanged(pEvent.getValue());
                }
        };

        iName = pConfig[0].getLocalizedName();
        // same title style as in CbCommoditySpinner
        iLblPoints = new CbLabel(CbConstants.CSS.ccWCommoditySpinnerLabelTitle(),
            CbConstants.CSS.ccWCommoditySpinnerLabelTitleDisabled());

        Panel hPanel = new HorizontalPanel();
        iSpinners = new CbCommoditySpinner[pConfig.length];
        int count = 0;
        for (int i = 0; i < pConfig.length; i++) {
            iSpinners[i] = new CbCommoditySpinner(pCommIDx[i], pConfig[i]);
            iSpinners[i].setNumber(pCounts[i]);
            iSpinners[i].addValueChangeHandler(vch);
            count += pCounts[i];
            hPanel.add(iSpinners[i]);
        }
        //hPanel.setStyleName("TODO"); // TODO
        
        setNumber(count);

        Panel vPanel = new VerticalPanel();
        vPanel.add(iLblPoints);
        vPanel.add(hPanel);

        initWidget(vPanel);
    }



    private void onInnerSpinnerChanged(final CbCommSpinnerPayload pValue)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("onInnerSpinnerChanged",  //$NON-NLS-1$
                new String[]{"pValue"}, new Object[]{pValue}); //$NON-NLS-1$
        }

        int oldPoints = getPoints(pValue);
        setNumber(iNumber + pValue.getDeltaNumber());
        int deltaPoints = getPoints() - oldPoints;

        CbCommSpinnerPayload agg = new CbCommSpinnerPayload(pValue.getCommIdx(),
            pValue.getDeltaNumber(), deltaPoints);
        ValueChangeEvent.fire(this, agg);

        LOG.exit("onInnerSpinnerChanged"); //$NON-NLS-1$
    }



    private void setNumber(final int pNumber)
    {
        iNumber = pNumber;
        iLblPoints.setText(iName + " (" + getPoints() + ')'); //$NON-NLS-1$
    }



    /**
     * Reset all values to 0 (zero).
     */
    public void reset()
    {
        if (iSpinners != null) {
            for (CbCommoditySpinner spinner : iSpinners) {
                if (spinner != null) {
                    spinner.setNumber(0);
                }
            }
            setNumber(0);
        }
    }



    public int getNumber()
    {
        return iNumber;
    }



    /**
     * Computes the current total point value of the special 'wine' commodity by
     * calculating sum(base * number for each spinner) * total number of cards.
     * @return total point value of the special 'wine' commodity
     */
    public int getPoints()
    {
        return getPoints(null);
    }



    private int getPoints(final CbCommSpinnerPayload pLatestChange)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("getPoints",  //$NON-NLS-1$
                new String[]{"pLatestChange"}, new Object[]{pLatestChange}); //$NON-NLS-1$
        }

        int result = 0;
        if (iSpinners != null) {
            for (CbCommoditySpinner spinner : iSpinners) {
                if (spinner != null)
                {
                    result += spinner.getPoints();

                    if (pLatestChange != null
                        && spinner.getCommIDx() == pLatestChange.getCommIdx())
                    {
                        result -= pLatestChange.getDeltaPoints();
                    }
                }
            }
            result = result * iNumber;
        }

        LOG.exit("getPoints", Integer.valueOf(result)); //$NON-NLS-1$
        return result;
    }



    @Override
    public HandlerRegistration addValueChangeHandler(
        final ValueChangeHandler<CbCommSpinnerPayload> pHandler)
    {
        return addHandler(pHandler, ValueChangeEvent.getType());
    }



    @Override
    public boolean isEnabled()
    {
        return iSpinners != null && iSpinners[0] != null && iSpinners[0].isEnabled();
    }

    @Override
    public void setEnabled(final boolean pEnabled)
    {
        if (iSpinners != null) {
            for (CbCommoditySpinner spinner : iSpinners) {
                if (spinner != null) {
                    spinner.setEnabled(pEnabled);
                }
            }
        }
        iLblPoints.setEnabled(pEnabled);
    }
}
