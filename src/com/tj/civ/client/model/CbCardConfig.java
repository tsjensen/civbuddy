/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-01
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.model;

import com.tj.civ.client.model.jso.CbCardConfigJSO;


/**
 * Augments {@link CbCardConfigJSO} with calculated and with technical fields. This
 * must be done in a separate class because JSOs cannot have instance fields.
 * <p>Throughout the application, this class is used in place of
 * <tt>CbCardConfigJSO</tt>.
 *
 * @author Thomas Jensen
 */
public class CbCardConfig
{
    /** Reference to the card configuration JSO from the game variant definition */
    private CbCardConfigJSO iConfig = null;

    /* ---------- the following values are calculated, but fix ------------ */

    /** the minimum cost of this card if all possible credit was leveraged */
    private int iCostMinimum = -1;

    /** the sum of all credit given by this card (sum of
     *  {@link CbCardConfigJSO#getCreditGiven()}) */
    private int iCreditGivenTotal = -1;

    /** the sum of all credit recevied by this card */
    private int iCreditReceivedTotal = -1;

    /** the cards which give credit to this card (array of indexes into the cards
     *  array of the gave variant, sorted by order in config file */
    private int[] iCreditFromCards = null;

    /* ---------- technical values ------------ */

    /** The array that this object is part of */
    private CbCardConfig[] iAllCardsConfig = null;

    /** The index that this object has in {@link #iAllCardsConfig} */
    private int iMyIdx = -1;



    /**
     * Constructor.
     * @param pConfig the card configuration JSO from the game variant definition
     * @param pMyIdx the index that this object has in <tt>pAllCardsConfig</tt>
     * @param pAllCardsConfig the array that this object is part of
     */
    public CbCardConfig(final CbCardConfigJSO pConfig, final int pMyIdx,
        final CbCardConfig[] pAllCardsConfig)
    {
        super();
        iConfig = pConfig;
        iMyIdx = pMyIdx;
        iAllCardsConfig = pAllCardsConfig;
    }



    /**
     * Getter.
     * @return {@link #iCostMinimum}
     */
    public int getCostMinimum()
    {
        return iCostMinimum;
    }

    /**
     * Setter.
     * @param pCostMinimum the new value of {@link #iCostMinimum}
     */
    public void setCostMinimum(final int pCostMinimum)
    {
        iCostMinimum = pCostMinimum;
    }



    /**
     * Getter.
     * @return {@link #iCreditGivenTotal}
     */
    public int getCreditGivenTotal()
    {
        return iCreditGivenTotal;
    }

    /**
     * Setter.
     * @param pCreditGivenTotal the new value of {@link #iCreditGivenTotal}
     */
    public void setCreditGivenTotal(final int pCreditGivenTotal)
    {
        iCreditGivenTotal = pCreditGivenTotal;
    }



    /**
     * Getter.
     * @return {@link #iCreditReceivedTotal}
     */
    public int getCreditReceivedTotal()
    {
        return iCreditReceivedTotal;
    }

    /**
     * Setter.
     * @param pCreditReceivedTotal the new value of {@link #iCreditReceivedTotal}
     */
    public void setCreditReceivedTotal(final int pCreditReceivedTotal)
    {
        iCreditReceivedTotal = pCreditReceivedTotal;
    }



    /**
     * Getter.
     * @return {@link #iCreditFromCards}
     */
    public int[] getCreditFromCards()
    {
        return iCreditFromCards;
    }

    /**
     * Setter.
     * @param pCreditFromCards the new value of {@link #iCreditFromCards}
     */
    public void setCreditFromCards(final int[] pCreditFromCards)
    {
        iCreditFromCards = pCreditFromCards;
    }



    /**
     * Getter.
     * @return {@link #iAllCardsConfig}
     */
    public CbCardConfig[] getAllCardsConfig()
    {
        return iAllCardsConfig;
    }



    /**
     * Getter.
     * @return {@link #iMyIdx}
     */
    public int getMyIdx()
    {
        return iMyIdx;
    }



    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(iMyIdx);
        sb.append(':');
        sb.append(iConfig.getNames().getDefaultEn());
        sb.append('(');
        sb.append(iConfig.getCostNominal());
        sb.append(')');
        return sb.toString();
    }



    public String getLocalizedName()
    {
        return iConfig.getLocalizedName();
    }



    public String getLocalizedAttributes()
    {
        return iConfig.getLocalizedAttributes();
    }



    public String getLocalizedCalamityEffects()
    {
        return iConfig.getLocalizedCalamityEffects();
    }



    public int getCostNominal()
    {
        return iConfig.getCostNominal();
    }



    public int[] getCreditGiven()
    {
        return iConfig.getCreditGiven();
    }



    /**
     * Get credit given by this card to one particular other card. The index of the
     * benefitting card in this array is the same as that card's index in the array
     * of cards in the game variant. If a value is not present, the credit is 0.
     * <p>Calling this method is just a faster way of calling
     * <code>getCreditGiven()[pIdx]</code>.
     * @param pIdx index into the creditGiven array field
     * @return the credit given in points
     * @see CbCardConfigJSO#getCreditGiven(int)
     */
    public int getCreditGiven(final int pIdx)
    {
        return iConfig.getCreditGiven(pIdx);
    }



    public CbGroup[] getGroups()
    {
        return iConfig.getGroups();
    }



    public int getPrereq()
    {
        return iConfig.getPrereq();
    }



    /**
     * Determine if a prerequisite card is defined for this card.
     * @return <code>true</code> if yes
     */
    public boolean hasPrereq()
    {
        return iConfig.hasPrereq();
    }
}
