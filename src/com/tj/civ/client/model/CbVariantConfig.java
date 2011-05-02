/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 26.12.2010
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
package com.tj.civ.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.model.jso.CbCommodityConfigJSO;
import com.tj.civ.client.model.jso.CbVariantConfigJSO;
import com.tj.civ.client.model.vo.CbHasViewObjectIF;
import com.tj.civ.client.model.vo.CbVariantVO;



/**
 * Describes a game variant as per the config file.
 *
 * @author tsjensen
 */
public class CbVariantConfig
    extends CbIndependentlyPersistableObject<CbVariantConfigJSO>
    implements CbHasViewObjectIF<CbVariantVO>
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbVariantConfig.class);

    /** civilization card configuration */
    private CbCardConfig[] iCards;

    /** sorted civilization card configuration based on
     *  {@link CbVariantConfigJSO#getCards()}. The sort is by nominal cost in
     *  descending order, no matter the prerequisite requirements.
     *  <p>CAUTION: This field is for calculation of the state 'DiscouragedBuy'
     *  only, and must not be used by any other part of the application. */
    private CbCardConfig[] iCardsByValueDesc;



    /**
     * Constructor.
     * @param pJso the JSO representing the object (must not be empty)
     */
    public CbVariantConfig(final CbVariantConfigJSO pJso)
    {
        super(pJso);
    }



    /**
     * Computes those civilization card properties which are not directly part of the
     * configuration file (variant), but can be directly deduced from the
     * configuration (variant) and do not change during the game.
     */
    private void calculateValues()
    {
        if (iCards == null || iCards.length < 1) {
            throw new IllegalArgumentException("iCards is emtpy");  //$NON-NLS-1$
        }

        for (int c = 0; c < iCards.length; c++)
        {
            // number of cards that give credit to this card
            int count = 0;
            for (CbCardConfig card : iCards) {
                if (card.getCreditGiven(c) > 0) {
                    count++;
                }
            }

            // total credit received by this card, and from which cards
            int[] creditReceivedFrom = new int[count];
            count = 0;
            int totalCreditReceived = 0;
            for (int i = 0; i < iCards.length; i++) {
                int cred = iCards[i].getCreditGiven(c);
                if (cred > 0) {
                    totalCreditReceived += cred;
                    creditReceivedFrom[count++] = i;
                }
            }
            iCards[c].setCreditReceivedTotal(totalCreditReceived);
            iCards[c].setCreditFromCards(creditReceivedFrom);

            // total credit given by this card to other cards
            int totalCreditGiven = 0;
            for (int cred : iCards[c].getCreditGiven()) {
                totalCreditGiven += cred;
            }
            iCards[c].setCreditGivenTotal(totalCreditGiven);

            // minimum cost of this card
            iCards[c].setCostMinimum(
                Math.max(0, iCards[c].getCostNominal() - totalCreditReceived));
        }
    }



    /**
     * Sort the cards in {@link #iCards} by nominal cost in descending order and
     * store the result in {@link #iCardsByValueDesc}.
     */
    private void calculateSpecialSort()
    {
        List<CbCardConfig> temp = new ArrayList<CbCardConfig>(Arrays.asList(iCards));
        Collections.sort(temp, new Comparator<CbCardConfig>() {
            @Override
            public int compare(final CbCardConfig pCard1, final CbCardConfig pCard2)
            {
                int v1 = pCard1 != null ? pCard1.getCostNominal() : -1;
                int v2 = pCard2 != null ? pCard2.getCostNominal() : -1;
                return v1 > v2 ? -1 : (v1 < v2 ? 1 : 0);
            }
        });
        iCardsByValueDesc = temp.toArray(new CbCardConfig[iCards.length]);

        // log the result of the sort if debugging
        if (LOG.isDetailEnabled()) {
            LOG.detail("calculateSpecialSort", //$NON-NLS-1$
                "iCardsByValueDesc = " + CbToString.obj2str(iCardsByValueDesc)); //$NON-NLS-1$
        }
    }



    public String getVariantId()
    {
        return getJso().getVariantId();
    }



    public String getLocalizedDisplayName()
    {
        return getJso().getLocalizedDisplayName();
    }



    public int getVersion()
    {
        return getJso().getVariantVersion();
    }



    public String getUrl()
    {
        return getJso().getUrl();
    }



    /**
     * Getter.
     * @return {@link #iCards}
     */
    public CbCardConfig[] getCards()
    {
        return iCards;
    }



    /**
     * Getter.
     * @return {@link #iCardsByValueDesc}
     */
    public CbCardConfig[] getCardsSortedInternal()
    {
        return iCardsByValueDesc;
    }



    public CbCommodityConfigJSO[] getCommodities()
    {
        return getJso().getCommodities();
    }



    public int getNumCardsLimit()
    {
        return getJso().getNumCardsLimit();
    }



    /**
     * Getter.
     * @return <code>true</code> if this variant specifies a civilization card limit
     */
    public boolean hasNumCardsLimit()
    {
        return getJso().getNumCardsLimit() > 0;
    }



    public SortedSet<Integer> getTargetOptions()
    {
        return getJso().getTargetOptions();
    }



    @Override
    public void evaluateJsoState(final CbVariantConfigJSO pJso)
    {
        CbCardConfig[] cards = new CbCardConfig[pJso.getCards().length];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CbCardConfig(pJso.getCard(i), i, cards);
        }
        iCards = cards;

        calculateValues();
        calculateSpecialSort();
    }



    @Override
    public CbVariantVO getViewObject()
    {
        return new CbVariantVO(getVariantId(), getLocalizedDisplayName());
    }
}
