/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-08
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import org.civbuddy.client.common.CbConstants;
import org.civbuddy.client.common.CbGlobal;
import org.civbuddy.client.model.CbCardConfig;
import org.civbuddy.client.model.CbCardCurrent;
import org.civbuddy.client.model.CbState;


/**
 * GWT Widget displaying the credit received by a card.
 * 
 * <p>Fragments are ordered by state as in {@link CbState}.
 *
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt><tt>.cb-cw-creditBar</tt>
 *     <dd>the entire panel
 * <dt><tt>.cb-cw-creditBar-img</tt>
 *     <dd>each <tt>&lt;img&gt;</tt> fragment of the credit bar
 * </dl>
 *
 * @author Thomas Jensen
 */
public final class CbCreditBar
    extends Composite
{
    /** represents the credit bar */
    private FlowPanel iPanel;

    /** index of this card in the model */
    private int iMyIdx;

    /** sort order of the credit bar fragments. Each element of the array represents
     *  a fragment. The value is the index of the giving card in the model */
    private List<Integer> iSortOrder;



    /**
     * Constructor.
     * @param pCard card to which this credit bar belongs
     */
    public CbCreditBar(final CbCardCurrent pCard)
    {
        super();

        iMyIdx = pCard.getMyIdx();

        CbCardConfig[] configs = CbGlobal.getGame().getVariant().getCards();
        final int[] potCredIdx = pCard.getConfig().getCreditFromCards();
        final int len = potCredIdx != null ? potCredIdx.length : 0;

        iPanel = new FlowPanel();
        iSortOrder = new ArrayList<Integer>();
        for (int i = 0; i < len; i++) {
            iSortOrder.add(Integer.valueOf(potCredIdx[i]));
            CbCardConfig givingCardConfig = configs[potCredIdx[i]];
            int credGiven = givingCardConfig.getCreditGiven(pCard.getMyIdx());
            iPanel.add(createFragment(givingCardConfig, CbState.Absent, credGiven));
        }

        iPanel.setStyleName(CbConstants.CSS.cbCwCreditBar());
        initWidget(iPanel);
    }



    /**
     * Factory method.
     * @param pCard card to which the new credit bar will belong
     * @return a credit bar
     * @deprecated use constructor directly
     */
    @Deprecated
    public static CbCreditBar create(final CbCardCurrent pCard)
    {
        return new CbCreditBar(pCard);
    }



    private Image createFragment(final CbCardConfig pGivingCardConfig,
        final CbState pState, final int pCreditGiven)
    {
        Image result = null;
        if (pState == CbState.Owned) {
            result = new Image(CbConstants.IMG_BUNDLE.barOwned());
        } else if (pState == CbState.Planned) {
            result = new Image(CbConstants.IMG_BUNDLE.barPlanned());
        } else {
            result = new Image(CbConstants.IMG_BUNDLE.barAbsent());
        }

        result.setAltText(pGivingCardConfig.getLocalizedName() + " ("  //$NON-NLS-1$
            + pCreditGiven + ')');
        result.setTitle(pGivingCardConfig.getLocalizedName() + " ("  //$NON-NLS-1$
            + pCreditGiven + ')');

        result.setWidth(((int) (pCreditGiven * CbConstants.BAR_PIXEL_POINT_RATIO))
            + CbConstants.UNIT_PIXEL);
        result.setStyleName(CbConstants.CSS.cbCwCreditBarImg());

        return result;
    }



    /**
     * Updates the credit bar in such a way that the fragment representing the
     * given card changes its color to the color representing the new state of the
     * giving card. Also, the order of fragments may change.
     * @param pGivingCardIdx index of the giving card which has changed state
     */
    public void update(final int pGivingCardIdx)
    {
        if (iSortOrder == null || iSortOrder.size() == 0) {
            return;
        }
        final int len = iSortOrder.size();
        final Integer givingCardIdx = Integer.valueOf(pGivingCardIdx);

        final CbCardCurrent[] allCards = CbGlobal.getCardsCurrent();
        final CbCardConfig givingCardConfig = allCards[pGivingCardIdx].getConfig();
        final int credGiven = givingCardConfig.getCreditGiven(iMyIdx);
        final CbState state = allCards[pGivingCardIdx].getState();
        final Image fragment = createFragment(givingCardConfig, state, credGiven);

        final int oldSortIdx = iSortOrder.indexOf(givingCardIdx);
        int newSortIdx = len - 1;
        for (int i = 0; i < len; i++)
        {
            if (allCards[iSortOrder.get(i).intValue()].getState().ordinal() > state.ordinal()) {
                newSortIdx = i;
                if (newSortIdx > oldSortIdx) {
                    newSortIdx--;
                }
                break;
            }
        }

        // adjust our memory of the sort order
        iSortOrder.remove(oldSortIdx);
        iSortOrder.add(newSortIdx, givingCardIdx);
        
        // adjust the flow panel itself
        iPanel.remove(oldSortIdx);  // separator
        iPanel.insert(fragment, newSortIdx);
    }
}
