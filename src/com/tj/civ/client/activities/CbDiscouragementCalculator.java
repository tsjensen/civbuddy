/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-06-29
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.activities;

import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.CbVariantConfig;


/**
 * Provides a means of calculating {@link CbState#DiscouragedBuy}. An instance of
 * this class can only be used for one calculation and must then be discarded.
 *
 * @author Thomas Jensen
 */
public class CbDiscouragementCalculator
{
    /** Logger for this class */
    private static final CbLogAdapter LOG =
        CbLogAdapter.getLogger(CbDiscouragementCalculator.class);

    /** current cards */
    private CbCardCurrent[] iCardsCurrent;

    /** card configurations sorted in descending order by nominal cost */
    private CbCardConfig[] iCardsSorted;

    /** the target points of this player's civilization */
    private int iPointsTarget;

    /** the number of cards we can still buy */
    private int iStepsMax;

    /** nominal sum of cards in states 'Planned' or 'Owned' at the beginning of the
     *  calculation */
    private int iBaseSum;

    /** the initial states in which the cards are assumed to be. In this calculation,
     *  we only consider cards in state 'Absent'. The state 'DiscouragedBuy' must
     *  not be set on any of the cards. */
    private CbState[] iInitialStates;

    /** maximum number of points achievable by civilization cards alone under the
     *  assumptions given in the constructor this lists the values for each
     *  discouraged card. The order of cards in the result array is the same as in
     *  {@link #iCardsCurrent}. */
    private int[] iResultingPoints;



    /**
     * Constructor.
     * @param pInitialStates the initial states in which the cards are assumed to be
     */
    public CbDiscouragementCalculator(final CbState[] pInitialStates)
    {
        LOG.enter(CbLogAdapter.CONSTRUCTOR);
        final CbVariantConfig variant = CbGlobal.getGame().getVariant();
        final CbSituation sit = CbGlobal.getGame().getCurrentSituation();
        final CbCardCurrent[] cardsCurrent = sit.getCardsCurrent();

        iCardsCurrent = cardsCurrent;
        iCardsSorted = variant.getCardsSortedInternal();
        iPointsTarget = sit.getPlayer().getWinningTotal();
        iStepsMax = variant.getJso().getNumCardsLimit()
            - getNumCardsAffectingCredit(pInitialStates);
        iBaseSum = getNominalSumInclPlan(cardsCurrent, pInitialStates);
        iInitialStates = pInitialStates;
        iResultingPoints = new int[cardsCurrent.length];

        if (LOG.isDetailEnabled())
        {
            LOG.detail(CbLogAdapter.CONSTRUCTOR, "iPointsTarget = " + iPointsTarget); //$NON-NLS-1$
            LOG.detail(CbLogAdapter.CONSTRUCTOR, "iStepsMax = " + iStepsMax); //$NON-NLS-1$
            LOG.detail(CbLogAdapter.CONSTRUCTOR, "iBaseSum = " + iBaseSum); //$NON-NLS-1$
            LOG.detail(CbLogAdapter.CONSTRUCTOR, "iCardsSorted = "   //$NON-NLS-1$
                + CbToString.obj2str(iCardsSorted));
            LOG.detail(CbLogAdapter.CONSTRUCTOR, "iInitialStates = "   //$NON-NLS-1$
                + CbToString.obj2str(iInitialStates));
        }

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    private int getNumCardsAffectingCredit(final CbState[] pInitialStates)
    {
        int result = 0;
        for (CbState state : pInitialStates) {
            if (state.isAffectingCredit()) {
                result++;
            }
        }
        return result;
    }



    private int getNominalSumInclPlan(final CbCardCurrent[] pCardsCurrent,
        final CbState[] pInitialStates)
    {
        int result = 0;
        for (int i = 0; i < pCardsCurrent.length; i++) {
            if (pInitialStates[i].isAffectingCredit()) {
                result += pCardsCurrent[i].getConfig().getCostNominal();
            }
        }
        return result;
    }



    /**
     * Is the most expensive card we could buy still Discouraged?
     * @return <code>true</code> if yes
     */
    public static boolean isStillDesperate()
    {
        LOG.enter("isStillDesperate"); //$NON-NLS-1$

        final CbSituation sit = CbGlobal.getGame().getCurrentSituation();
        final CbCardCurrent[] cardsCurrent = sit.getCardsCurrent();
        CbState[] initialStates = new CbState[cardsCurrent.length];
        for (int i = 0; i < initialStates.length; i++) {
            initialStates[i] = cardsCurrent[i].getState();
        }
        
        CbDiscouragementCalculator dc = new CbDiscouragementCalculator(initialStates);
        int maxIdx = -1;
        for (int i = 0; i < dc.iCardsSorted.length; i++)
        {
            final int idx = dc.iCardsSorted[i].getMyIdx();
            CbState state = initialStates[idx];
            if (!state.isAffectingCredit() && state != CbState.PrereqFailed) {
                maxIdx = idx;
                break;
            }
        }
        
        boolean result = false;
        if (maxIdx >= 0) {
            int pointsPossible = dc.executeWithAssumption(maxIdx);
            result = pointsPossible < dc.iPointsTarget;
        }
        LOG.exit("isStillDesperate", Boolean.valueOf(result)); //$NON-NLS-1$
        return result;
    }



    /**
     * Execute the calculation.
     * @return maximum number of points achievable by civilization cards alone
     *      under the assumptions given in the constructor this lists the values
     *      for each discouraged card. The order of cards in the result array is
     *      the same as in {@link #iCardsCurrent}.
     */
    public int[] execute()
    {
        LOG.enter("execute"); //$NON-NLS-1$
        boolean allClear = false;    // points target reached once already?
        
        if (iStepsMax <= 0 || iBaseSum >= iPointsTarget) {
            // skip invalid calls (should not happen)
            LOG.exit("execute", iResultingPoints); //$NON-NLS-1$
            return iResultingPoints;
        }

        // from least to most valuable
        for (int i = iCardsSorted.length - 1; i >= 0; i--)
        {
            int rowIdx = iCardsSorted[i].getMyIdx();
            CbState state = iInitialStates[rowIdx];
            iResultingPoints[rowIdx] = 0;
            if (allClear || state != CbState.Absent) { // DiscouragedBuy is never set
                continue;
            }

            executeWithAssumption(rowIdx);
            if (iResultingPoints[rowIdx] >= iPointsTarget) {
                // since all remaining cards are more expensive, they must also be
                // okay to buy
                LOG.detail("execute", "allClear = true;"); //$NON-NLS-1$ //$NON-NLS-2$
                allClear = true;
            }
        }
        
        LOG.exit("execute", iResultingPoints); //$NON-NLS-1$
        return iResultingPoints;
    }



    private int executeWithAssumption(final int pRowIdx)
    {
        LOG.enter("executeWithAssumption"); //$NON-NLS-1$
        if (LOG.isDetailEnabled()) {
            LOG.detail("executeWithAssumption", //$NON-NLS-1$
                "Assuming " + iCardsCurrent[pRowIdx].getConfig().getLocalizedName()); //$NON-NLS-1$
        }

        CbBranch root = new CbBranch(pRowIdx, iStepsMax, iBaseSum);
        processBranch(root, pRowIdx);

        int result = iResultingPoints[pRowIdx];
        LOG.exit("executeWithAssumption", Integer.valueOf(result)); //$NON-NLS-1$
        return result;
    }



    private void processBranch(final CbBranch pBranch, final int pRowIdx)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("processBranch",  //$NON-NLS-1$
                new String[]{"pRowIdx", "pBranch"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Integer.valueOf(pRowIdx), pBranch});
        }

        if (pRowIdx < 0 || !isBranchAlive(pBranch)) {
            // no need to note the result
            LOG.exit("processBranch"); //$NON-NLS-1$
            return;
        }

        tryAddingCard(pBranch, pRowIdx);

        for (int nextIdx = next(pBranch); nextIdx >= 0 && nextIdx < iCardsSorted.length;
            nextIdx = next(pBranch))
        {
            if (!isBranchAlive(pBranch)) {
                // branch must end because target reached or max steps taken
                break;
            }
            tryAddingCard(pBranch, nextIdx);
        }

        noteBranchResult(pBranch);

        LOG.exit("processBranch"); //$NON-NLS-1$
    }



    /**
     * Check if the card can theoretically be added to the branch.
     * <p>This is the case if:<ul>
     * <li>The card state was not Owned or Planned when the calculation initially
     *     started,
     * <li>it's not the card that was the assumption card of the branch,
     * <li>the card is not listed in the branch's decision map, and<br>
     *     (Note that checking this is sufficient. It is not required to check the
     *     entire path, because cards are always added in the same order. The add
     *     order is only broken for prereq cards, which are then present in the
     *     decision map.)
     * <li>the card's prereq card (if present) was not already decided against.
     * </ul>
     * @param pBranch the branch
     * @param pRowIdx the card as original index
     * @return <code>true</code> if yes
     */
    private boolean isAddableOnPath(final CbBranch pBranch, final int pRowIdx)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("isAddableOnPath",  //$NON-NLS-1$
                new String[]{"pRowIdx", "pBranch"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Integer.valueOf(pRowIdx), pBranch});
        }

        boolean result = true;
        if (iInitialStates[pRowIdx].isAffectingCredit()) {
            // we already bought the card before the calculation started
            result = false;
        }
        else if (pBranch.isPrereqDecided(pRowIdx)) {
            // the card itself was a prereq earlier
            result = false;
        }
        else if (pBranch.getAssumption() == pRowIdx && pBranch.getNumStepsTaken() > 0) {
            result = false;
        }
        else {
            final int prereqIdx = iCardsCurrent[pRowIdx].getConfig().getPrereq();
            if (prereqIdx >= 0 && pBranch.isPrereqDecided(prereqIdx)
                && !pBranch.isPrereqSatisfied(prereqIdx))
            {
                // the card has a prereq which we already decided against
                result = false;
            }
        }

        LOG.exit("isAddableOnPath", Boolean.valueOf(result)); //$NON-NLS-1$
        return result;
    }



    private void tryAddingCard(final CbBranch pBranch, final int pRowIdx)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("tryAddingCard",  //$NON-NLS-1$
                new String[]{"pRowIdx", "pBranch"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Integer.valueOf(pRowIdx), pBranch});
        }

        if (isAddableOnPath(pBranch, pRowIdx)) {
            if (isBlockedByPrereq(pRowIdx, pBranch)) {
                // unsatisfied prereq
                final int prereqIdx = iCardsCurrent[pRowIdx].getConfig().getPrereq();
                
                // spawn a positive branch: take prereq, then take card
                CbBranch posBranch = new CbBranch(pBranch);
                posBranch.decideOnPrereq(prereqIdx, true);
                addStep(posBranch, prereqIdx);
                if (isBranchAlive(posBranch)) {
                    processBranch(posBranch, pRowIdx);
                }

                // original branch: decide against prereq and skip
                pBranch.decideOnPrereq(prereqIdx, false);
            }
            else {
                // no prereq or prereq ok, just take it
                addStep(pBranch, pRowIdx);
            }
        }

        LOG.exit("tryAddingCard"); //$NON-NLS-1$
    }



    /**
     * Records the result of a finished branch.
     * <p>If our result arrays do not contain a better result yet, this branch's
     * result is recorded. Else it is discarded. This way, in the end, the best
     * possible result, or a result that exceeds the target, is found in the
     * result arrays.
     * @param pBranch the branch
     */
    private void noteBranchResult(final CbBranch pBranch)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("noteBranchResult",  //$NON-NLS-1$
                new String[]{"pBranch"}, new Object[]{pBranch}); //$NON-NLS-1$
        }

        final int a = pBranch.getAssumption();
        if (LOG.isDetailEnabled()) {
            LOG.detail("noteBranchResult", //$NON-NLS-1$
                "found points=" + iResultingPoints[a]); //$NON-NLS-1$
        }

        if (iResultingPoints[a] < pBranch.getSumReached())
        {
            iResultingPoints[a] = pBranch.getSumReached();
            if (LOG.isDetailEnabled()) {
                LOG.detail("noteBranchResult", //$NON-NLS-1$
                    "recorded points=" + iResultingPoints[a]); //$NON-NLS-1$
            }
        }
        else if (LOG.isDetailEnabled()) {
            LOG.detail("noteBranchResult", //$NON-NLS-1$
                pBranch.getSumReached() + " not good enough, discarding"); //$NON-NLS-1$
        }

        LOG.exit("noteBranchResult"); //$NON-NLS-1$
    }



    private int next(final CbBranch pBranch)
    {
        int result = -1;
        int n = pBranch.getNextListPos();
        if (n >= 0 && n < iCardsSorted.length) {
            result = iCardsSorted[n].getMyIdx();
        }
        return result;
    }



    /**
     * Determine if the branch is still alive, which means that at least one more
     * step is allowed on the branch, and the points target has not been reached yet.
     * @param pBranch the branch
     * @return boolean
     */
    private boolean isBranchAlive(final CbBranch pBranch)
    {
        if (iResultingPoints[pBranch.getAssumption()] > iPointsTarget) {
            if (LOG.isDetailEnabled()) {
                LOG.detail("isBranchAlive", //$NON-NLS-1$
                    "A different branch of the same card calculation has " //$NON-NLS-1$
                    + "already reached the points target. Aborting branch " //$NON-NLS-1$
                    + System.identityHashCode(pBranch));
            }
            return false;
        }
        return pBranch.getSumReached() < iPointsTarget
            && pBranch.getNumStepsTaken() < iStepsMax;
    }



    /**
     * Determine if the given card is currently blocked by a non-satisfied
     * prerequisite.
     * <p>This is the case if the card
     * <ul>
     * <li>has a prerequisite card defined,
     * <li>the prerequisite card is not 'Owned' or 'Planned', and<ul>
     * <li>the prerequisite card has already been decided against, or
     * <li>the prerequisite card is not on the branch yet, and no decision was made
     *     until now.</ul>
     * </ul>
     * @param pRowIdx the index of the card to examine into the current cards
     *              (original order), <b>not</b> the index of the prerequisite card
     * @param pBranch the current branch
     * @return <code>true</code> if blocked, <code>false</code> otherwise
     */
    private boolean isBlockedByPrereq(final int pRowIdx, final CbBranch pBranch)
    {
        boolean result = false;
        final int prereqIdx = iCardsCurrent[pRowIdx].getConfig().getPrereq();
        if (prereqIdx >= 0) {
            CbState prereqState = iInitialStates[prereqIdx];
            if (!prereqState.isAffectingCredit()) {
                if (pBranch.isPrereqDecided(prereqIdx)) {
                    result = !pBranch.isPrereqSatisfied(prereqIdx);
                } else {
                    result = !pBranch.isStepPresentOnPath(prereqIdx);
                }
            }
        }
        return result;
    }



    /**
     * Takes the given step on the given branch.
     * <p>Prereqs are all taken recursively without creating new branches.<br>
     * No decisions on prereqs are noted.<br>
     * The first card added is the deepest prereq encountered which is not blocked
     * by another prereq. So if the number of steps is limited too far, it is
     * possible that the card given by the first call does not really get added
     * (should that happen, the branch is dead of course, so it must end).
     * @param pBranch the branch
     * @param pRowIdx the card to add as index into {@link #iCardsCurrent}
     */
    private void addStep(final CbBranch pBranch, final int pRowIdx)
    {
        if (isBlockedByPrereq(pRowIdx, pBranch)) {
            // recursive prereq
            int prereqIdx = iCardsCurrent[pRowIdx].getConfig().getPrereq();
            pBranch.decideOnPrereq(prereqIdx, true);
            addStep(pBranch, prereqIdx);
        }
        if (isBranchAlive(pBranch)) {
            pBranch.takeStep(pRowIdx, iCardsCurrent[pRowIdx].getConfig().getCostNominal());
        }
    }
}
