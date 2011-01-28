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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Describes a game variant as per the config file.
 *
 * @author tsjensen
 */
public class CcVariantConfig
{
    /** logger for this class */
    private static final Logger LOG = Logger.getLogger(CcVariantConfig.class.getName());

    /** name of the game variant */
    private String iName = null;

    /** version of this variant */
    private int iVersion = -1;

    /** URL where to get updates of this variant */
    private String iUrl = null;

    /** civilization card configuration */
    private CcCardConfig[] iCards = null;

    /** civilization card configuration. copy of {@link #iCards}, sorted by nominal
     *  cost in descending order. Cards which are prerequisites of other cards are
     *  put in front of the most expensive card for which they are prerequisite.
     *  <p>CAUTION: This field is for calculation of the state 'DiscouragedBuy'
     *  only, and must not be used by any other part of the application. */
    private CcCardConfig[] iCardsSorted = null;

    /** commodity card configuration */
    private CcCommodityConfig[] iCommodities = null;

    /** limit to the number of civilization cards that a player may buy during a
     *  game. A value of 0 (zero) indicates that there is no such limit. */
    private int iNumCardsLimit = 0;



    /**
     * Computes those civilization card properties which are not directly part of the
     * configuration file (variant), but can be directly deduced from the
     * configuration (variant) and do not change during the game.
     */
    public void calculateValues()
    {
        if (iCards == null || iCards.length < 1) {
            throw new IllegalArgumentException("iCards is emtpy");  //$NON-NLS-1$
        }

        for (int c = 0; c < iCards.length; c++)
        {
            // number of cards that give credit to this card
            int count = 0;
            for (CcCardConfig card : iCards) {
                if (card.getCreditGiven()[c] > 0) {
                    count++;
                }
            }

            // total credit received by this card, and from which cards
            int[] creditReceivedFrom = new int[count];
            count = 0;
            int totalCreditReceived = 0;
            for (int i = 0; i < iCards.length; i++) {
                int cred = iCards[i].getCreditGiven()[c];
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
            iCards[c].setCostMinimum(iCards[c].getCostNominal() - totalCreditReceived);
        }

        calculateSpecialSort();
    }



    private void calculateSpecialSort()
    {
        final int len = iCards.length;
        final List<CcCardConfig> temp = new ArrayList<CcCardConfig>(len + 1);

        // sort by nominal cost descending
        temp.add(iCards[0]);
        for (int i = 1; i < len; i++) {
            int s = 0;
            int ts = temp.size();
            for (s = 0; s < ts; s++) {
                if (temp.get(s).getCostNominal() < iCards[i].getCostNominal()) {
                    temp.add(s, iCards[i]);
                    break;
                }
            }
            if (s == ts) {
                temp.add(iCards[i]);
            }
        }

        // put prerequisite cards first
        // TODO: handle transitive dependencies, detect cyclic dependencies
        for (int s = 0; s < len; s++)
        {
            int prereq = temp.get(s).getPrereq();
            if (prereq >= 0) {
                // the card at position s in temp has a prereq
                for (int p = s + 1; p < len; p++) {
                    if (temp.get(p).getMyIdx() == prereq) {
                        // the prereq is less expensive than the card itself
                        CcCardConfig prCard = temp.remove(p);
                        temp.add(s, prCard);
                        break;
                    }
                }
            }
        }
        iCardsSorted = temp.toArray(new CcCardConfig[len]);

        // log the result of the sort if debugging
        if (LOG.isLoggable(Level.FINER)) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < len; i++) {
                sb.append(iCardsSorted[i].toString());
                if (i < len - 1) {
                    sb.append(", "); //$NON-NLS-1$
                }
            }
            sb.append(']');
            LOG.finer("iCardsSorted = " + sb.toString()); //$NON-NLS-1$
        }
    }



    /**
     * Getter.
     * @return {@link #iName}
     */
    public String getName()
    {
        return iName;
    }

    /**
     * Setter.
     * @param pName the new value of {@link #iName}
     */
    public void setName(final String pName)
    {
        iName = pName;
    }



    /**
     * Getter.
     * @return {@link #iVersion}
     */
    public int getVersion()
    {
        return iVersion;
    }

    /**
     * Setter.
     * @param pVersion the new value of {@link #iVersion}
     */
    public void setVersion(final int pVersion)
    {
        iVersion = pVersion;
    }



    /**
     * Getter.
     * @return {@link #iUrl}
     */
    public String getUrl()
    {
        return iUrl;
    }

    /**
     * Setter.
     * @param pUrl the new value of {@link #iUrl}
     */
    public void setUrl(final String pUrl)
    {
        iUrl = pUrl;
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
     * Setter.
     * @param pCards the new value of {@link #iCards}
     */
    public void setCards(final CcCardConfig[] pCards)
    {
        iCards = pCards;
    }

    /**
     * Getter.
     * @return {@link #iCardsSorted}
     */
    public CcCardConfig[] getCardsSpeciallySorted()
    {
        return iCardsSorted;
    }



    /**
     * Getter.
     * @return {@link #iCommodities}
     */
    public CcCommodityConfig[] getCommodities()
    {
        return iCommodities;
    }

    /**
     * Setter.
     * @param pCommodities the new value of {@link #iCommodities}
     */
    public void setCommodities(final CcCommodityConfig[] pCommodities)
    {
        iCommodities = pCommodities;
    }



    /**
     * Getter.
     * @return {@link #iNumCardsLimit}
     */
    public int getNumCardsLimit()
    {
        return iNumCardsLimit;
    }

    /**
     * Setter.
     * @param pNumCardsLimit the new value of {@link #iNumCardsLimit}
     */
    public void setNumCardsLimit(final int pNumCardsLimit)
    {
        iNumCardsLimit = pNumCardsLimit;
    }
}
