/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-08
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbState;


/**
 * GWT Widget displaying the credit received by a card.
 * 
 * <p>Fragments are ordered by state as in {@link CbState}.
 *
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>TODO panel style
 *     <dd>the entire panel
 * <dt>.cc-imgCreditBar
 *     <dd>each fragment of the credit bar, including separators
 * </dl>
 *
 * @author Thomas Jensen
 */
public final class CbCreditBar
    extends FlowPanel
{
    /** the card to which this credit bar belongs */
    private CbCardCurrent iCard;

    /** sort order of the credit bar fragments. Each element of the array represents
     *  a fragment. The value is the index of the giving card in the model */
    private List<Integer> iSortOrder;



    /**
     * Constructor.
     * @param pCard card to which this credit bar belongs
     */
    private CbCreditBar(final CbCardCurrent pCard)
    {
        super();
        iCard = pCard;

        final int[] potCredIdx = iCard.getConfig().getCreditFromCards();
        final int len = potCredIdx != null ? potCredIdx.length : 0;
        iSortOrder = new ArrayList<Integer>();
        for (int i = 0; i < len; i++) {
            iSortOrder.add(Integer.valueOf(potCredIdx[i]));
        }
    }



    /**
     * Factory method.
     * @param pCard card to which the new credit bar will belong
     * @return a credit bar
     */
    public static CbCreditBar create(final CbCardCurrent pCard)
    {
        CbCreditBar result = new CbCreditBar(pCard);
        result.init();
        return result;
    }



    private Image createFragmentSeparator()
    {
        Image result = new Image(CbConstants.IMG_BUNDLE.barSeparator());
        result.setStyleName(CbConstants.CSS.ccImgCreditBar());
        return result;
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

        result.setAltText(pGivingCardConfig.getLocalizedName());
        result.setTitle(pGivingCardConfig.getLocalizedName());

        result.setWidth(((int) (pCreditGiven * CbConstants.BAR_PIXEL_POINT_RATIO))
            + CbConstants.UNIT_PIXEL);
        result.setStyleName(CbConstants.CSS.ccImgCreditBar());

        return result;
    }



    private void init()
    {
        CbCardConfig card = iCard.getConfig();
        final int[] potCredIdx = card.getCreditFromCards();

        add(createFragmentSeparator());
        for (int i = 0; i < potCredIdx.length; i++)
        {
            CbCardConfig givingCardConfig = card.getAllCardsConfig()[potCredIdx[i]];
            int credGiven = givingCardConfig.getCreditGiven(card.getMyIdx());
            add(createFragment(givingCardConfig, CbState.Absent, credGiven));
            add(createFragmentSeparator());
        }
    }



    /**
     * Updates the credit bar in such a way that the fragment representing the
     * given card changes its color to the color representing the new state of the
     * giving card. Also, the order of fragments may change.
     * @param pGivingCardIdx index of the giving card which has changes state
     */
    public void update(final int pGivingCardIdx)
    {
        if (iSortOrder == null || iSortOrder.size() == 0) {
            return;
        }
        final int len = iSortOrder.size();
        final Integer givingCardIdx = Integer.valueOf(pGivingCardIdx);

        final CbCardCurrent[] allCards = iCard.getAllCardsCurrent();
        final CbCardConfig givingCardConfig = allCards[pGivingCardIdx].getConfig();
        final int credGiven = givingCardConfig.getCreditGiven(iCard.getMyIdx());
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
        remove((2 * oldSortIdx) + 1);  // fragment
        remove(2 * oldSortIdx);  // separator
        insert(fragment, 2 * newSortIdx);
        insert(createFragmentSeparator(), 2 * newSortIdx);
    }
}
