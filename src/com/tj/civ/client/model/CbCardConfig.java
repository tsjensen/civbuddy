/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 25.12.2010
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

import com.google.gwt.i18n.client.LocaleInfo;
import com.tj.civ.client.resources.CcConstants;


/**
 * Describes a civilization card as configured in the game variant's config file.
 * This includes values from the config file and values that are directly deduced
 * from those. It does not cover dynamic information based on haves and plans.
 *
 * @author tsjensen
 */
public class CcCardConfig
{
    /** Card name in English */
    private String iNameEn = null;

    /** Card name in German */
    private String iNameDe = null;

    /** Group(s) this card belongs to */
    private CcGroup[] iGroups = null;

    /** Prerequsite card (index into the variant's card array) */
    private int iPrereq = -1;

    /** The nominal cost of this card as printed on the card */
    private int iCostNominal = -1;

    /** Credit given by this card to other cards. The index of the benefitting card
     *  in this array is the same as that card's index in the array of cards in the
     *  game variant. If a value is not present, the credit is 0. */
    private int[] iCreditGiven = null;

    /** Description of the card's effect as far as it doesn't pertain to calamities */
    private String iAttributes = null;

    /** Description of the card's effect on calamities */
    private String iClamityEffects = null;

    /* ---------- the following values are calculated, but fix ------------ */

    /** the minimum cost of this card if all possible credit was leveraged */
    private int iCostMinimum = -1;

    /** the sum of all credit given by this card (sum of {@link #iCreditGiven}) */
    private int iCreditGivenTotal = -1;

    /** the sum of all credit recevied by this card */
    private int iCreditReceivedTotal = -1;

    /** the cards which give credit to this card (array of indexes into the cards
     *  array of the gave variant, sorted by order in config file */
    private int[] iCreditFromCards = null;

    /* ---------- technical values ------------ */

    /** The array that this object is part of */
    private CcCardConfig[] iAllCardsConfig = null;

    /** The index that this object has in {@link #iAllCardsConfig} */
    private int iMyIdx = -1;



    /**
     * Constructor.
     * @param pMyIdx the index that this object has in <tt>pAllCardsConfig</tt>
     * @param pAllCardsConfig the array that this object is part of
     */
    public CcCardConfig(final int pMyIdx, final CcCardConfig[] pAllCardsConfig)
    {
        super();
        iAllCardsConfig = pAllCardsConfig;
        iMyIdx = pMyIdx;
    }



    /**
     * Getter.
     * @return {@link #iNameEn}
     */
    public String getNameEn()
    {
        return iNameEn;
    }

    /**
     * Setter.
     * @param pNameEn the new value of {@link #iNameEn}
     */
    public void setNameEn(final String pNameEn)
    {
        iNameEn = pNameEn;
    }



    /**
     * Getter.
     * @return {@link #iNameDe}
     */
    public String getNameDe()
    {
        return iNameDe;
    }

    /**
     * Setter.
     * @param pNameDe the new value of {@link #iNameDe}
     */
    public void setNameDe(final String pNameDe)
    {
        iNameDe = pNameDe;
    }



    /**
     * Calls {@link #getNameEn()} or {@link #getNameDe()} depending on the current
     * locale.
     * @return the localized card name
     */
    public String getLocalizedName()
    {
        String result = null;
        if (CcConstants.LOCALE_EN.equalsIgnoreCase(
            LocaleInfo.getCurrentLocale().getLocaleName()))
        {
            result = getNameEn();
        } else {
            result = getNameDe();
        }
        return result;
    }



    /**
     * Getter.
     * @return {@link #iGroups}
     */
    public CcGroup[] getGroups()
    {
        return iGroups;
    }

    /**
     * Setter.
     * @param pGroups the new value of {@link #iGroups}
     */
    public void setGroups(final CcGroup[] pGroups)
    {
        iGroups = pGroups;
    }



    /**
     * Getter.
     * @return {@link #iPrereq}
     */
    public int getPrereq()
    {
        return iPrereq;
    }

    /**
     * Setter.
     * @param pPrereq the new value of {@link #iPrereq}
     */
    public void setPrereq(final int pPrereq)
    {
        iPrereq = pPrereq;
    }

    /**
     * Getter.
     * @return <code>true</code> if {@link #iPrereq} is a valid index into
     *              {@link #iAllCardsConfig}
     */
    public boolean hasPrereq()
    {
        return iPrereq >= 0 && iPrereq < iAllCardsConfig.length;
    }



    /**
     * Getter.
     * @return {@link #iCostNominal}
     */
    public int getCostNominal()
    {
        return iCostNominal;
    }

    /**
     * Setter.
     * @param pCostNominal the new value of {@link #iCostNominal}
     */
    public void setCostNominal(final int pCostNominal)
    {
        iCostNominal = pCostNominal;
    }



    /**
     * Getter.
     * @return {@link #iCreditGiven}
     */
    public int[] getCreditGiven()
    {
        return iCreditGiven;
    }

    /**
     * Setter.
     * @param pCreditGiven the new value of {@link #iCreditGiven}
     */
    public void setCreditGiven(final int[] pCreditGiven)
    {
        iCreditGiven = pCreditGiven;
    }



    /**
     * Getter.
     * @return {@link #iAttributes}
     */
    public String getAttributes()
    {
        return iAttributes;
    }

    /**
     * Setter.
     * @param pAttributes the new value of {@link #iAttributes}
     */
    public void setAttributes(final String pAttributes)
    {
        iAttributes = pAttributes;
    }



    /**
     * Getter.
     * @return {@link #iClamityEffects}
     */
    public String getClamityEffects()
    {
        return iClamityEffects;
    }

    /**
     * Setter.
     * @param pCalamityEffects the new value of {@link #iClamityEffects}
     */
    public void setClamityEffects(final String pCalamityEffects)
    {
        iClamityEffects = pCalamityEffects;
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
    public CcCardConfig[] getAllCardsConfig()
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
        sb.append(iNameEn);
        sb.append('(');
        sb.append(iCostNominal);
        sb.append(')');
        return sb.toString();
    }
}
