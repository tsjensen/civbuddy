/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 07.01.2011
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
package com.tj.civ.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.model.CcVariantConfig;
import com.tj.civ.client.resources.CcConstants;


/**
 * Manges state transitions of cards according to their definition in
 * {@link CcState}.
 *
 * @author Thomas Jensen
 */
public class CcCardStateManager
{
    /** logger for this class */
    private static final Logger LOG = Logger.getLogger(CcCardStateManager.class.getName());

    /** mapping cache */
    private static Integer[] Real2SpecialIdxCache = null; 
    
    /** the current card states in the order of the specially sorted array<br>
     *  This is a short-term cache for the duration of a single {@link #recalcAll}. */
    private static CcState[] StatesForSpecial = null;

    /** the funds controller */
    private CcFundsController iFundsCtrl;

    /** the game variant which is being played */
    private CcVariantConfig iVariant;
    
    /** number of points that the player must reach to win */
    private int iTargetPoints;



    /**
     * Constructor.
     * @param pVariant the game variant which is being played
     * @param pFundsCtrl the funds controller
     * @param pTargetPoints target points of this player's civilization. This value
     *          should be 0 (zero) if the game variant does not feature a card
     *          limit
     */
    public CcCardStateManager(final CcVariantConfig pVariant,
        final CcFundsController pFundsCtrl, final int pTargetPoints)
    {
        super();
        iVariant = pVariant;
        iFundsCtrl = pFundsCtrl;
        iTargetPoints = pTargetPoints;
    }



    /**
     * Recalculate the state of all cards.
     * @param pCardCtrl the current card controller
     */
    public void recalcAll(final CcCardController pCardCtrl)
    {
        long debugTimeStart = 0L;
        if (LOG.isLoggable(Level.FINE)) {
            debugTimeStart = System.currentTimeMillis();
        }
        StatesForSpecial = null;
        final CcCardCurrent[] cardsCurrent = pCardCtrl.getCardsCurrent();
        for (CcCardCurrent card : cardsCurrent)
        {
            final CcCardConfig cardConfig = card.getConfig();
            final CcState currentState = card.getState();
            CcState newState = null;
            String reason = ""; //$NON-NLS-1$

            if (currentState.isAffectingCredit()) {
                continue;   // Owned and Planned
            }

            if (cardConfig.hasPrereq()
                && !cardsCurrent[cardConfig.getPrereq()].getState().isAffectingCredit())
            {
                newState = CcState.PrereqFailed;
                String prn = cardsCurrent[cardConfig.getPrereq()].getConfig().getLocalizedName();
                reason = CcConstants.MESSAGES.prereqFailed(prn);
            }
            else if (iFundsCtrl.isEnabled()
                &&
                 (iFundsCtrl.getFunds() - pCardCtrl.getPlannedInvestment()
                    - card.getCostCurrent()) < 0)
                {
                newState = CcState.Unaffordable;
                reason = CcConstants.STRINGS.noFunds();
            }
            else {
                final int pointsAchievable = computePointsAchievable(pCardCtrl, card.getMyIdx());
                if (isDiscouraged(pointsAchievable)) {
                    newState = CcState.DiscouragedBuy;
                    reason = CcConstants.MESSAGES.discouraged(iTargetPoints - pointsAchievable);
                }
                else {
                    newState = CcState.Absent;
                }
            }
            // TODO: erst alle states berechnen, dann anzeigen
            // TODO: falls alle verbleibenden karten rot sind, anders anzeigen
            if (currentState != newState) {
                card.setState(newState);
                pCardCtrl.updateStyle(card.getMyIdx());
                pCardCtrl.setStateReason(card.getMyIdx(), reason);
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("recalcAll() - TIME: " //$NON-NLS-1$
                + (System.currentTimeMillis() - debugTimeStart) + " ms"); //$NON-NLS-1$
        }
    }



    private boolean isDiscouraged(final int pPointsAchievable)
    {
        boolean result = false;
        if (iVariant.getNumCardsLimit() > 0) {
            result = pPointsAchievable < iTargetPoints;
        }
        return result;
    }



    /**
     * Converts the real index of a card into that card's index in the specially
     * sorted array (which is used on the path).
     * @param pRealIdx real index
     * @return index into the specially sorted array, never <code>null</code>
     */
    private Integer realIdx2SpecialIdx(final int pRealIdx)
    {
        if (Real2SpecialIdxCache == null) {
            final CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
            Real2SpecialIdxCache = new Integer[specialCards.length];
            for (int i = 0; i < specialCards.length; i++) {
                Real2SpecialIdxCache[specialCards[i].getMyIdx()] = Integer.valueOf(i);
            }
            
            if (LOG.isLoggable(Level.FINER)) {
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int i = 0; i < Real2SpecialIdxCache.length; i++) {
                    sb.append(Real2SpecialIdxCache[i].intValue());
                    if (i < Real2SpecialIdxCache.length - 1) {
                        sb.append(", "); //$NON-NLS-1$
                    }
                }
                sb.append(']');
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.finer("realIdx2SpecialIdx = " + sb.toString());  //$NON-NLS-1$
                }
            }
        }
        return Real2SpecialIdxCache[pRealIdx];
    }


    
    private CcState getStateFromSpecial(final CcCardCurrent[] pCards4State,
        final Integer pSecialIdx)
    {
        if (StatesForSpecial == null) {
            final CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
            StatesForSpecial = new CcState[specialCards.length];
            for (int i = 0; i < specialCards.length; i++)
            {
                StatesForSpecial[i] = pCards4State[specialCards[i].getMyIdx()].getState();
            }

        }
        return StatesForSpecial[pSecialIdx.intValue()];
    }


    
    private int computePointsAchievable(final CcCardController pCardCtrl, final int pRowIdx)
    {
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("========= Computing '" + pCardCtrl.getCardsCurrent()[pRowIdx]  //$NON-NLS-1$
                .getConfig().getNameEn() + "' ===================================="); //$NON-NLS-1$
        }
        int result = 0;
        if (iVariant.getNumCardsLimit() > 0) {
            final int remainingSteps = Math.max(0,
                iVariant.getNumCardsLimit() - pCardCtrl.getNumCardsAffectingCredit());
            final List<Integer> path = new ArrayList<Integer>();
            path.add(realIdx2SpecialIdx(pRowIdx));
            CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
            Integer startingPoint = null;
            for (int i = 0; i < specialCards.length; i++) {
                if (!getStateFromSpecial(pCardCtrl.getCardsCurrent(),
                        Integer.valueOf(i)).isAffectingCredit()
                    && specialCards[i].getMyIdx() != pRowIdx
                    && isPrereqMet(pCardCtrl.getCardsCurrent(), specialCards[i], path))
                {
                    startingPoint = Integer.valueOf(i);
                    break;
                }
            }
            if (startingPoint != null) {
                result = isDiscouragedInternal(pCardCtrl, startingPoint, path,
                    pCardCtrl.getNominalSumInclPlan()
                    + iVariant.getCards()[pRowIdx].getCostNominal(),
                    remainingSteps - 1);
            }
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Done. Result=" + result);  //$NON-NLS-1$
        }
        return result;
    }



    /**
     * Computes if there is a combination of cards which the player could buy in the
     * future, which would still make it possible to reach the required winning points.
     * @param pCardCtrl the current card controller
     * @param pSpecialIdx index of the next step to take (which may be an invalid step)
     * @param pPath the list of steps previously taken
     * @param pSum the current sum
     * @param pRemainingSteps the number of steps which may still be taken
     * @return an arbitrary value &gt;= {@link #iTargetPoints}, or, if that was impossible,
     *      the highest score still achievable
     */
    private int isDiscouragedInternal(final CcCardController pCardCtrl,
        final Integer pSpecialIdx, final List<Integer> pPath, final int pSum,
        final int pRemainingSteps)
    {
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("isDiscouragedInternal() - ENTER - pSpecialIdx=" //$NON-NLS-1$
                + pSpecialIdx + ", pPath=" + pPath                     //$NON-NLS-1$
                + ", pSum=" + pSum                                     //$NON-NLS-1$
                + ", pRemainingSteps=" + pRemainingSteps);             //$NON-NLS-1$
        }
        int result = pSum;
        final CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
        if (pRemainingSteps > 0 && result < iTargetPoints
            && pSpecialIdx.intValue() < specialCards.length)
        {
            final CcCardCurrent[] cardsCurrent = pCardCtrl.getCardsCurrent();
            for (int i = pSpecialIdx.intValue(); i < specialCards.length; i++)
            {
                final Integer specialIdx = Integer.valueOf(i);
                final CcCardConfig card = specialCards[i];
                
                if (!getStateFromSpecial(cardsCurrent, specialIdx).isAffectingCredit()
                    && !pPath.contains(specialIdx)
                    && isPrereqMet(cardsCurrent, card, pPath))
                {
                    pPath.add(specialIdx);
                    final int newSum = isDiscouragedInternal(pCardCtrl,
                        Integer.valueOf(specialIdx.intValue() + 1),
                        pPath, pSum + card.getCostNominal(), pRemainingSteps - 1);
                    pPath.remove(pPath.size() - 1);
                    if (newSum >= iTargetPoints) {
                        result = newSum;
                        if (LOG.isLoggable(Level.FINER)) {
                            LOG.finer("OK, path = " + pPath); //$NON-NLS-1$
                        }
                        break;  // great, we're ok
                    }
                    if (newSum > result) {
                        result = newSum;
                    }
                }
            }
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("isDiscouragedInternal() - EXIT - result = " + result); //$NON-NLS-1$
        }
        return result;
    }
    
    
    
    private boolean isPrereqMet(final CcCardCurrent[] pCardsCurrent, final CcCardConfig pCard,
        final List<Integer> pPath)
    {
        boolean result = true;
        if (pCard.hasPrereq()) {
            final CcCardConfig[] cards = iVariant.getCardsSpeciallySorted();
            boolean foundOnPath = false;
            for (Iterator<Integer> iter = pPath.iterator(); iter.hasNext();) {
                if (cards[iter.next().intValue()].getMyIdx() == pCard.getPrereq()) {
                    foundOnPath = true;
                    break;
                }
            }
            if (!foundOnPath) {
                // not on path, so look in cards owned/planned
                CcCardCurrent prereqCard = pCardsCurrent[pCard.getPrereq()];
                if (!prereqCard.getState().isAffectingCredit()) {
                    result = false;
                }
            }
        }
        return result;
    }
}
