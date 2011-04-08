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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tj.civ.client.event.CcFundsEvent;
import com.tj.civ.client.event.CcFundsHandlerIF;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.model.CcVariantConfig;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.views.CbCardsViewIF;


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

    /** the 'Cards' activity we're going to be associated to */
    private CbCardsViewIF.CcPresenterIF iPresenter;

    /** mapping cache */
    private static Integer[] Real2SpecialIdxCache = null; 
    
    /** the current card states in the order of the specially sorted array<br>
     *  This is a short-term cache for the duration of a single {@link #recalcAll}. */
    private static CcState[] StatesForSpecial = null;

    /** the game variant which is being played */
    private CcVariantConfig iVariant;
    
    /** number of points that the player must reach to win */
    private int iTargetPoints;

    /** the current enablement state of the funds tracking feature, kept current
     *  by means of {@link CcFundsEvent}s via the event bus */
    private boolean iFundsEnabled = false;

    /** the currently available funds, kept current  by means of
     *  {@link CcFundsEvent}s via the event bus */
    private int iFundsTotal = 0;



    /**
     * Constructor.
     * @param pActivity the 'Cards' activity we're going to be associated to
     * @param pVariant the game variant which is being played
     * @param pTargetPoints target points of this player's civilization. This value
     *          should be 0 (zero) if the game variant does not feature a card
     *          limit
     * @param pFundsEnabled initial value of the funds enabled flag, will be
     *          updated through Funds events
     * @param pFundsTotal initial value of the total funds, will be updated through
     *          Funds events
     */
    public CcCardStateManager(final CbCardsViewIF.CcPresenterIF pActivity,
        final CcVariantConfig pVariant, final int pTargetPoints,
        final boolean pFundsEnabled, final int pFundsTotal)
    {
        super();
        iPresenter = pActivity;
        iVariant = pVariant;
        iTargetPoints = pTargetPoints;
        iFundsEnabled = pFundsEnabled;
        iFundsTotal = pFundsTotal;

        pActivity.getEventBus().addHandler(CcFundsEvent.TYPE, new CcFundsHandlerIF() {
            @Override
            public void onFundsChanged(final CcFundsEvent pEvent)
            {
                CcCardStateManager.this.iFundsEnabled = pEvent.isFundsEnabled();
                CcCardStateManager.this.iFundsTotal = pEvent.getFunds();
            }
        });
    }



    /**
     * Recalculate the state of all cards.
     */
    public void recalcAll()
    {
        long debugTimeStart = 0L;
        if (LOG.isLoggable(Level.FINE)) {
            debugTimeStart = System.currentTimeMillis();
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("ENTER: recalcAll()"); //$NON-NLS-1$
        }
        final CcCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
        for (CcCardCurrent card : cardsCurrent)
        {
            final CcCardConfig cardConfig = card.getConfig();
            final CcState currentState = card.getState();
            CcState newState = null;
            String reason = null;
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("recalcAll(): Computing " + card.getConfig() //$NON-NLS-1$
                    + " (state=" + currentState + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (card.getMyIdx() == 1) {
                LOG.setLevel(Level.FINEST);  // TODO remove
            } else {
                LOG.setLevel(Level.FINER);
            }

            if (currentState.isAffectingCredit()) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("recalcAll(): - - - - - - - - - - - - - - - - - - - - - - - -> Skipping; Owned or Planned"); //$NON-NLS-1$
                }
                continue;   // Owned and Planned
            }

            if (cardConfig.hasPrereq()
                && !cardsCurrent[cardConfig.getPrereq()].getState().isAffectingCredit())
            {
                newState = CcState.PrereqFailed;
                String prn = cardsCurrent[cardConfig.getPrereq()].getConfig().getLocalizedName();
                reason = CcConstants.MESSAGES.prereqFailed(prn);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("recalcAll(): - - - - - - - - - - - - - - - - - - - - - - - -> Prereq Failed (" + prn //$NON-NLS-1$
                        + ")"); //$NON-NLS-1$
                }
            }
            else if (iFundsEnabled
                && (iFundsTotal - iPresenter.getPlannedInvestment() - card.getCostCurrent()) < 0)
            {
                newState = CcState.Unaffordable;
                reason = CcConstants.STRINGS.noFunds();
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("recalcAll(): - - - - - - - - - - - - - - - - - - - - - - - -> unaffordable"); //$NON-NLS-1$
                }
            }
            else {
                final int pointsAchievable = computePointsAchievableOld(card.getMyIdx());
                if (isDiscouraged(pointsAchievable)) {
                    newState = CcState.DiscouragedBuy;
                    reason = CcConstants.MESSAGES.discouraged(iTargetPoints - pointsAchievable);
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("recalcAll(): - - - - - - - - - - - - - - - - - - - - - - - -> discouraged"); //$NON-NLS-1$
                    }
                }
                else {
                    newState = CcState.Absent;
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("recalcAll(): - - - - - - - - - - - - - - - - - - - - - - - -> Absent"); //$NON-NLS-1$
                    }
                }
            }
            // TODO: erst alle states berechnen, dann anzeigen (generell behandeln)
            // TODO: falls alle verbleibenden karten rot sind, anders anzeigen
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("recalcAll(): currentState=" + currentState //$NON-NLS-1$
                    + "; newState=" + newState); //$NON-NLS-1$
            }
            if (currentState != newState) {
                iPresenter.setState(card, newState, reason);
            }
        }
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("EXIT: recalcAll()"); //$NON-NLS-1$
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


    
    private int computePointsAchievableOld(final int pRowIdx)
    {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ENTER: computePointsAchievable(" + pRowIdx //$NON-NLS-1$
                + ")"); //$NON-NLS-1$
            LOG.finest("========= Computing '" //$NON-NLS-1$
                + iPresenter.getCardsCurrent()[pRowIdx].getConfig().getLocalizedName()
                + "' ===================================="); //$NON-NLS-1$
        }
        int result = 0;
        if (iVariant.getNumCardsLimit() > 0) {
            final int remainingSteps = Math.max(0,
                iVariant.getNumCardsLimit() - iPresenter.getNumCardsAffectingCredit());
            final List<Integer> path = new ArrayList<Integer>();
            CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("speciallySorted = " + Arrays.deepToString(specialCards)); //$NON-NLS-1$
            }
            Integer startingPoint = null;
            for (int i = 0; i < specialCards.length; i++) {
                if (!getStateFromSpecial(iPresenter.getCardsCurrent(),
                        Integer.valueOf(i)).isAffectingCredit()
                    && isPrereqMet(iPresenter.getCardsCurrent(), specialCards[i], path))
                {
                    startingPoint = Integer.valueOf(i);
                    break;
                }
            }
            // FIXME: In einer Situation, wo wegen des Kartenlimits nur noch
            //        Democracy und Philosophy gekauft werden dürfen, wird irrtümlich
            //        Democracy als discouraged angezeigt, wenn man Philosophy zuerst
            //        wählt. Andersrum ist alles ok.
            // FIXME: 102 Funds, Pottery, Cloth, Eng Owned, D&P Planned -> Myst not discouraged!
            if (startingPoint != null) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Entering into isDiscouragedInternal() with:" //$NON-NLS-1$
                        +  "\n   - pSpecialIdx     = " + startingPoint     //$NON-NLS-1$
                        +  "\n   - pPath           = " + path              //$NON-NLS-1$
                        +  "\n   - pSum      = " + iPresenter.getNominalSumInclPlan() //$NON-NLS-1$
                        +  "\n   - pRemainingSteps = " + remainingSteps); //$NON-NLS-1$
                }
                result = isDiscouragedInternal(startingPoint, path,
                    iPresenter.getNominalSumInclPlan(), remainingSteps);
            }
        }
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Done. Result=" + result);  //$NON-NLS-1$
        }
        return result;
    }



    /**
     * Computes if there is a combination of cards which the player could buy in the
     * future, which would still make it possible to reach the required winning points.
     * @param pSpecialIdx index of the next step to take (which may be an invalid step)
     * @param pPath the list of steps previously taken
     * @param pSum the current sum
     * @param pRemainingSteps the number of steps which may still be taken
     * @return an arbitrary value &gt;= {@link #iTargetPoints}, or, if that was impossible,
     *      the highest score still achievable
     */
    private int isDiscouragedInternal(final Integer pSpecialIdx,
        final List<Integer> pPath, final int pSum, final int pRemainingSteps)
    {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("isDiscouragedInternal() - ENTER - pSpecialIdx=" //$NON-NLS-1$
                + pSpecialIdx + ", pPath=" + pPath                     //$NON-NLS-1$
                + ", pSum=" + pSum                                     //$NON-NLS-1$
                + ", pRemainingSteps=" + pRemainingSteps);             //$NON-NLS-1$
        }
        int result = pSum;
        final CcCardConfig[] specialCards = iVariant.getCardsSpeciallySorted();
        if (pRemainingSteps > 0 && result < iTargetPoints
            && pSpecialIdx.intValue() < specialCards.length)
        {
            final CcCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
            for (int i = pSpecialIdx.intValue(); i < specialCards.length; i++)
            {
                final Integer specialIdx = Integer.valueOf(i);
                final CcCardConfig card = specialCards[i];
                
                if (!getStateFromSpecial(cardsCurrent, specialIdx).isAffectingCredit()
                    && !pPath.contains(specialIdx)
                    && isPrereqMet(cardsCurrent, card, pPath))
                {
                    pPath.add(specialIdx);
                    final int newSum = isDiscouragedInternal(
                        Integer.valueOf(specialIdx.intValue() + 1),
                        pPath, pSum + card.getCostNominal(), pRemainingSteps - 1);
                    pPath.remove(pPath.size() - 1);
                    if (newSum >= iTargetPoints) {
                        result = newSum;
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("OK, path = " + pPath); //$NON-NLS-1$
                        }
                        break;  // great, we're ok
                    }
                    if (newSum > result) {
                        result = newSum;
                    }
                }
            }
        }
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("isDiscouragedInternal() - EXIT - result = " + result); //$NON-NLS-1$
        }
        LOG.setLevel(Level.FINER); // TODO remvece
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



    private boolean isDiscouragedNeu(final int pRowIdx)
    {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ENTER: isDiscouraged(" + pRowIdx //$NON-NLS-1$
                + ")"); //$NON-NLS-1$
            LOG.finest("========= Computing '" //$NON-NLS-1$
                + iPresenter.getCardsCurrent()[pRowIdx].getConfig().getLocalizedName()
                + "' ===================================="); //$NON-NLS-1$
        }
        boolean result = false;
        if (iVariant.getNumCardsLimit() > 0 && iPresenter.getNumCardsAffectingCredit() > 0)
        {
            int remainingSteps = Math.max(0,
                iVariant.getNumCardsLimit() - iPresenter.getNumCardsAffectingCredit());
            final int fixedNominal = iVariant.getCards()[pRowIdx].getCostNominal();
            if (remainingSteps == 1)
            {
                result = iPresenter.getNominalSumInclPlan() + fixedNominal < iTargetPoints;
            }
            else if (remainingSteps > 1)
            {
                final CcCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
                final CcCardConfig[] cardsSorted = iVariant.getCardsSortedInternal();
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.finer("cardsSorted = " + Arrays.deepToString(cardsSorted)); //$NON-NLS-1$
                }
    
                remainingSteps--;
                final int[] path = new int[remainingSteps];
                final int[] rest = new int[cardsSorted.length - (remainingSteps + 1)
                                           - iPresenter.getNumCardsAffectingCredit()];
                for (int i = 0, stepsTaken = 0;
                    i < cardsSorted.length && stepsTaken < remainingSteps; i++)
                {
                    int idx = cardsSorted[i].getMyIdx();
                    CcState state = cardsCurrent[idx].getState();
                    if (!state.isAffectingCredit() && idx != pRowIdx) {
                        if (stepsTaken < remainingSteps) {
                            path[stepsTaken] = i;
                        } else {
                            rest[stepsTaken - remainingSteps] = i;
                        }
                        stepsTaken++;
                    }
                }
                result = handlePrereqs(
                    iPresenter.getNominalSumInclPlan() + fixedNominal, path, rest);
            }
        }
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("EXIT - result = " + result);  //$NON-NLS-1$
        }
        return result;
    }



    /**
     * Determine if the required winning total can be reached
     *  given <tt>pPathSpecial</tt> is legal and if so, calculate
     * its value. The value returned must be the highest legal value (if goal is
     * met), or the highest, possible illegal, value (if goal is not met).
     * <p>In other words
     * <p>Cards which are already Owned or Planned are never contained in either
     * array. The values of the cards referenced in both arrays are in descending
     * order.
     * @param pStartingSum the sum of all cards already Owned or Planned, plus the
     *          value of the card whose Discouraged state we are calculating
     * @param pPathSpecial a potential path, built from the most valuable cards
     *          still available. Contains indexes into the sorted array of cards.
     *          Length is always equal to the number of steps the path may have,
     *          which is at least 1. This is certainly the path with the highest
     *          value, however it may be illegal due to failed prerequisites.
     * @param pRestSpecial the remaining card which could theoretically be used on
     *          the path, in case the path contained illegal entries. Contains
     *          indexes into the sorted array of cards.
     * @return <code>true</code> if discouraged, <code>false</code> if okay
     */
    private boolean handlePrereqs(final int pStartingSum, final int[] pPathSpecial,
        final int[] pRestSpecial)
    {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ENTER: handlePrereqs(" + pStartingSum //$NON-NLS-1$
                + ", " + pPathSpecial + ", " + pRestSpecial  //$NON-NLS-1$ //$NON-NLS-2$
                + ")"); //$NON-NLS-1$
        }
        boolean result = false;

        // TODO Auto-generated method stub HERE
        // TODO Falls ein Eintrag in path die prereqs nicht erfüllt, mit einem
        //      aus rest tauschen. Über Varianten mti mehreren rprereqs und
        //      transitiven prereqs nachdenken. Das muss dann aber nicht mehr effizient
        //      sein. die entscheidung über den initialen path muss sehr schnell
        //      erfolgen.
        // Mtheonds: wert-des-pfades, illegal-index

        return result;
    }
}
