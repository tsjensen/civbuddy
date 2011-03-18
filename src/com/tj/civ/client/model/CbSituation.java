/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-05
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

import java.util.HashSet;
import java.util.Set;

import com.tj.civ.client.model.jso.CcFundsJSO;
import com.tj.civ.client.model.jso.CcPlayerJSO;
import com.tj.civ.client.model.jso.CcSituationJSO;


/**
 * Describes the current situation of a single player.
 *
 * @author Thomas Jensen
 */
public class CcSituation
    extends CcIndependentlyPersistableObject<CcSituationJSO>
{
    /** reference to the game variant that this situation is based on */
    private CcVariantConfig iVariant;

    /** The current civilization card situation. The order of cards must be the same
     *  as in the variant config */
    private CcCardCurrent[] iCardsCurrent = null;

    /** Total funds available to the player at the moment, according to the
     *  'Commodities' panel */
    private int iFunds = 0;

    /** Funds remaining (equals total funds minus current costs of planned cards) */ 
    private int iFundsPlanned = 0;

    /** number of groups of which the player owns at least one card */
    private int iNumGroups = 0;

    /** {@link #iNumGroups}, but including planned cards */
    private int iNumGroupsPlanned = 0;

    /** current number of winning points from civilization cards */ 
    private int iWinningPoints = 0;

    /** {@link #iWinningPoints}, but including planned cards */
    private int iWinningPointsPlanned = 0;

    /** number of owned cards */
    private int iNumCards = 0;

    /** {@link #iNumCards}, but including planned cards */
    private int iNumCardsPlanned = 0;



    /**
     * Constructor.
     * @param pSituationJso the JSO wrapped by this class
     * @param pVariant the game variant that this situation is based on
     */
    public CcSituation(final CcSituationJSO pSituationJso, final CcVariantConfig pVariant)
    {
        super(pSituationJso);
        iVariant = pVariant;
    }



    /**
     * Getter.
     * @return {@link #iCardsCurrent}
     */
    public CcCardCurrent[] getCardsCurrent()
    {
        return iCardsCurrent;
    }

    /**
     * Setter.
     * @param pCardsCurrent the new value of {@link #iCardsCurrent}
     */
    public void setCardsCurrent(final CcCardCurrent[] pCardsCurrent)
    {
        iCardsCurrent = pCardsCurrent;
    }



    /**
     * Getter.
     * @return {@link #iFunds}
     */
    public int getFunds()
    {
        return iFunds;
    }

    /**
     * Setter.
     * @param pFunds the new value of {@link #iFunds}
     */
    public void setFunds(final int pFunds)
    {
        iFunds = pFunds;
    }



    /**
     * Getter.
     * @return {@link #iFundsPlanned}
     */
    public int getFundsPlanned()
    {
        return iFundsPlanned;
    }

    /**
     * Setter.
     * @param pFundsPlanned the new value of {@link #iFundsPlanned}
     */
    public void setFundsPlanned(final int pFundsPlanned)
    {
        iFundsPlanned = pFundsPlanned;
    }



    /**
     * Getter.
     * @return {@link #iNumGroups}
     */
    public int getNumGroups()
    {
        return iNumGroups;
    }

    /**
     * Setter.
     * @param pNumGroups the new value of {@link #iNumGroups}
     */
    public void setNumGroups(final int pNumGroups)
    {
        iNumGroups = pNumGroups;
    }



    /**
     * Getter.
     * @return {@link #iNumGroupsPlanned}
     */
    public int getNumGroupsPlanned()
    {
        return iNumGroupsPlanned;
    }

    /**
     * Setter.
     * @param pNumGroupsPlanned the new value of {@link #iNumGroupsPlanned}
     */
    public void setNumGroupsPlanned(final int pNumGroupsPlanned)
    {
        iNumGroupsPlanned = pNumGroupsPlanned;
    }



    /**
     * Getter.
     * @return {@link #iWinningPoints}
     */
    public int getWinningPoints()
    {
        return iWinningPoints;
    }

    /**
     * Setter.
     * @param pWinningPoints the new value of {@link #iWinningPoints}
     */
    public void setWinningPoints(final int pWinningPoints)
    {
        iWinningPoints = pWinningPoints;
    }



    /**
     * Getter.
     * @return {@link #iWinningPointsPlanned}
     */
    public int getWinningPointsPlanned()
    {
        return iWinningPointsPlanned;
    }

    /**
     * Setter.
     * @param pWinningPointsPlanned the new value of {@link #iWinningPointsPlanned}
     */
    public void setWinningPointsPlanned(final int pWinningPointsPlanned)
    {
        iWinningPointsPlanned = pWinningPointsPlanned;
    }



    /**
     * Getter.
     * @return {@link #iNumCards}
     */
    public int getNumCards()
    {
        return iNumCards;
    }

    /**
     * Setter.
     * @param pNumCards the new value of {@link #iNumCards}
     */
    public void setNumCards(final int pNumCards)
    {
        iNumCards = pNumCards;
    }



    /**
     * Getter.
     * @return {@link #iNumCardsPlanned}
     */
    public int getNumCardsPlanned()
    {
        return iNumCardsPlanned;
    }

    /**
     * Setter.
     * @param pNumCardsPlanned the new value of {@link #iNumCardsPlanned}
     */
    public void setNumCardsPlanned(final int pNumCardsPlanned)
    {
        iNumCardsPlanned = pNumCardsPlanned;
    }



    /**
     * Getter.
     * @return current commodity counts
     * @see CcFundsJSO#getCommodityCounts()
     */
    public int[] getCommoditiesCurrent()
    {
        return getJso().getFunds().getCommodityCounts();
    }

    /**
     * Setter. 
     * @param pIdx commodity index
     * @param pCount the current count for the given commodity
     */
    public void setCommodityCurrent(final int pIdx, final int pCount)
    {
        getJso().getFunds().setCommodityCount(pIdx, pCount);
    }



    public CcPlayerJSO getPlayer()
    {
        return getJso().getPlayer();
    }



    @Override
    public void evaluateJsoState(final CcSituationJSO pJso)
    {
        // TODO does it make sense at all?
    }



    private int calculateTotalFunds(final CcFundsJSO pFunds)
    {
        int result = 0;
        if (pFunds.isDetailed() && pFunds.getCommodityCounts() != null) {
            final int numCommos = pFunds.getCommodityCounts().length;
            for (int i = 0; i < numCommos; i++)
            {
                int num = pFunds.getCommodityCount(i);
                result += num * num * iVariant.getCommodities()[i].getBase();
            }
            result += pFunds.getTreasury();
            result += pFunds.getBonus();
        }
        else {
            result = pFunds.getTotalFunds();
        }
        return result;
    }



    private static void addGroups(final Set<CcGroup> pSet, final CcGroup[] pGroups)
    {
        for (CcGroup group : pGroups) {
            pSet.add(group);
        }
    }



    public CcVariantConfig getVariant()
    {
        return iVariant;
    }



    /**
     * Recalculate some values.
     */
    public void recalc()
    {
        CcSituationJSO jso = getJso();

        CcCardConfig[] cardConfigs = iVariant.getCards();
        final int numCards = cardConfigs.length;
        CcCardCurrent[] cards = new CcCardCurrent[cardConfigs.length];
        for (int i = 0; i < numCards; i++)
        {
            cards[i] = new CcCardCurrent(cards, cardConfigs[i]);
            cards[i].setState(jso.getState(i));
        }
        iCardsCurrent = cards;

        iNumCards = 0;
        iNumCardsPlanned = 0;
        iNumGroups = 0;
        iNumGroupsPlanned = 0;
        iWinningPoints = 0;
        iWinningPointsPlanned = 0;
        
        Set<CcGroup> iGroupsSet = new HashSet<CcGroup>();
        Set<CcGroup> iGroupsPlannedSet = new HashSet<CcGroup>();

        for (int i = 0; i < numCards; i++)
        {
            CcState state = jso.getState(i);
            CcCardConfig card = iVariant.getCards()[i];
            if (state == CcState.Owned) {
                iWinningPoints += card.getCostNominal();
                iNumCards++;
                addGroups(iGroupsSet, card.getGroups());
            } else if (state == CcState.Planned) {
                iWinningPointsPlanned += card.getCostNominal();
                iNumCardsPlanned++;
                addGroups(iGroupsPlannedSet, card.getGroups());
            }
        }

        iNumGroups = iGroupsSet.size();
        iNumGroupsPlanned = iGroupsPlannedSet.size();

        iFunds = calculateTotalFunds(jso.getFunds());
        // funds planned are not calculated here, because that would duplicate the
        // calculation logic from the card controller
    }
}
