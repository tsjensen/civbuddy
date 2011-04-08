/*
 * CivCounsel - A Civilization Tactics Guide
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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tj.civ.client.model.jso.CcCommodityConfigJSO;
import com.tj.civ.client.model.jso.CcVariantConfigJSO;
import com.tj.civ.client.model.vo.CcHasViewObjectIF;
import com.tj.civ.client.model.vo.CcVariantVO;



/**
 * Describes a game variant as per the config file.
 *
 * @author tsjensen
 */
public class CcVariantConfig
    extends CcIndependentlyPersistableObject<CcVariantConfigJSO>
    implements CcHasViewObjectIF<CcVariantVO>
{
    /** logger for this class */
    private static final Logger LOG = Logger.getLogger(CcVariantConfig.class.getName());

    /** civilization card configuration */
    private CcCardConfig[] iCards;

    /** sorted civilization card configuration based on
     *  {@link CcVariantConfigJSO#getCards()}. The sort is by nominal cost in
     *  descending order, no matter the prerequisite requirements.
     *  <p>CAUTION: This field is for calculation of the state 'DiscouragedBuy'
     *  only, and must not be used by any other part of the application. */
    private CcCardConfig[] iCardsByValueDesc;



    /**
     * Constructor.
     * @param pJso the JSO representing the object (must not be empty)
     */
    public CcVariantConfig(final CcVariantConfigJSO pJso)
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
            for (CcCardConfig card : iCards) {
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
        List<CcCardConfig> temp = new ArrayList<CcCardConfig>(Arrays.asList(iCards));
        Collections.sort(temp, new Comparator<CcCardConfig>() {
            @Override
            public int compare(final CcCardConfig pCard1, final CcCardConfig pCard2)
            {
                int v1 = pCard1 != null ? pCard1.getCostNominal() : -1;
                int v2 = pCard2 != null ? pCard2.getCostNominal() : -1;
                return v1 > v2 ? -1 : (v1 < v2 ? 1 : 0);
            }
        });
        iCardsByValueDesc = temp.toArray(new CcCardConfig[iCards.length]);

        // log the result of the sort if debugging
        if (LOG.isLoggable(Level.FINER)) {
            final int len = iCardsByValueDesc.length;
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < len; i++) {
                sb.append(iCardsByValueDesc[i].toString());
                if (i < len - 1) {
                    sb.append(", "); //$NON-NLS-1$
                }
            }
            sb.append(']');
            LOG.finer("iCardsByValueDesc = " + sb.toString()); //$NON-NLS-1$
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
        return getJso().getVersion();
    }



    public String getUrl()
    {
        return getJso().getUrl();
    }



    /**
     * Getter.
     * @return {@link #iCards}
     */
    public CcCardConfig[] getCards()
    {
        return iCards;
    }



    /**
     * Getter.
     * @return {@link #iCardsByValueDesc}
     */
    public CcCardConfig[] getCardsSortedInternal()
    {
        return iCardsByValueDesc;
    }



    public CcCommodityConfigJSO[] getCommodities()
    {
        return getJso().getCommodities();
    }



    public int getNumCardsLimit()
    {
        return getJso().getNumCardsLimit();
    }


    public SortedSet<Integer> getTargetOptions()
    {
        return getJso().getTargetOptions();
    }



    @Override
    public void evaluateJsoState(final CcVariantConfigJSO pJso)
    {
        CcCardConfig[] cards = new CcCardConfig[pJso.getCards().length];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CcCardConfig(pJso.getCard(i), i, cards);
        }
        iCards = cards;

        calculateValues();
        calculateSpecialSort();
    }



    @Override
    public CcVariantVO getViewObject()
    {
        return new CcVariantVO(getVariantId(), getLocalizedDisplayName());
    }
}
