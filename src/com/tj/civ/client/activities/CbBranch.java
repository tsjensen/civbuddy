/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-07-03
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

import java.util.HashMap;
import java.util.Map;

import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.common.CbUtil;


/**
 * Represents the state of one branch in the calculation of 'DiscouragedBuy' states
 * in {@link CbDiscouragementCalculator}.
 *
 * @author Thomas Jensen
 */
public class CbBranch
{
    /** original index of the card we're assuming (so figure out what would happen
     *  if that card was bought) */
    private int iAssumption;

    /** number if steps taken on this branch */
    private int iNumStepsTaken;

    /** which steps were taken (indexes into original card array) */
    private int[] iStepsTaken;

    /** <em>total</em> sum reached so far (including base sum) */
    private int iSumReached;

    /** decisions made on prereqs on this path<br>
     *  key = prereq original index, value = buy (<code>true</code>) or leave
     *  (<code>false</code>) */
    private Map<Integer, Boolean> iDecisionsMade = new HashMap<Integer, Boolean>();

    /** index into the specially sorted list of cards that we are currently
     *  processing. Used only in {@link #getNextListPos()}. */ 
    private int iListPosition;



    /**
     * Constructor for a new branch. Normally, this is called only for the root.
     * @param pAssumption original index of the card we're assuming (so this branch
     *          exists to figure out what would happen if that card was bought)
     * @param pMaxSteps maximum number of steps we can still take
     * @param pCurrentSum base sum to which our steps will add
     */
    public CbBranch(final int pAssumption, final int pMaxSteps, final int pCurrentSum)
    {
        iAssumption = pAssumption;
        iNumStepsTaken = 0;
        iStepsTaken = new int[pMaxSteps];
        iSumReached = pCurrentSum;
        iDecisionsMade = new HashMap<Integer, Boolean>();
        iListPosition = -1;
    }



    /**
     * Copy constructor.
     * @param pBranch the originating branch, all of whose values are copied
     */
    public CbBranch(final CbBranch pBranch)
    {
        iAssumption = pBranch.iAssumption;
        iStepsTaken = new int[pBranch.iStepsTaken.length];
        for (int i = 0; i < pBranch.iStepsTaken.length; i++) {
            iStepsTaken[i] = pBranch.iStepsTaken[i];
        }
        iNumStepsTaken = pBranch.iNumStepsTaken;
        iSumReached = pBranch.iSumReached;
        iDecisionsMade = new HashMap<Integer, Boolean>();
        iDecisionsMade.putAll(pBranch.iDecisionsMade);
        iListPosition = pBranch.iListPosition;
    }



    /**
     * Takes the given step on this branch.
     * @param pRowIdx step as index into original card array
     * @param pCardNominalCost nominal value of that card which we add to the sum
     */
    public void takeStep(final int pRowIdx, final int pCardNominalCost)
    {
        iStepsTaken[iNumStepsTaken] = pRowIdx;
        iSumReached += pCardNominalCost;
        iNumStepsTaken++;
    }



    /**
     * Records a decision on a prerequisite card, which is buy it or leave it.
     * @param pPrereqIdx card index of the prerequisite card into the current cards
     *              (original order)
     * @param pDecision <code>true</code> if the card is bought, <code>false</code>
     *              otherwise
     */
    public void decideOnPrereq(final int pPrereqIdx, final boolean pDecision)
    {
        iDecisionsMade.put(Integer.valueOf(pPrereqIdx), Boolean.valueOf(pDecision));
    }



    /**
     * Determine if the given prerequisite card is present in {@link #iDecisionsMade},
     * which means that a decision on the prerequisite card was made.
     * @param pPrereqIdx card index of the prerequisite card into the current cards
     *              (original order)
     * @return <code>true</code> if a decision was made, regardless of what it was
     */
    public boolean isPrereqDecided(final int pPrereqIdx)
    {
        return iDecisionsMade.get(Integer.valueOf(pPrereqIdx)) != null;
    }



    /**
     * Determine what decision was made on the given prerequisite card. If no
     * decision was made yet, <code>false</code> is returned.
     * @param pPrereqIdx card index of the prerequisite card into the current cards
     *              (original order)
     * @return recorded decision
     */
    public boolean isPrereqSatisfied(final int pPrereqIdx)
    {
        Boolean decision = iDecisionsMade.get(Integer.valueOf(pPrereqIdx));
        return decision != null ? decision.booleanValue() : false;
    }



    /**
     * Check whether the given card is already one of the steps we've taken on this
     * branch.
     * @param pRowIdx card index of the card to check into the current cards
     *              (original order)
     * @return <code>true</code> if present
     */
    public boolean isStepPresentOnPath(final int pRowIdx)
    {
        boolean result = false;
        for (int i = 0; i < iNumStepsTaken; i++) {
            if (iStepsTaken[i] == pRowIdx) {
                result = true;
                break;
            }
        }
        return result;
    }



    public int getAssumption()
    {
        return iAssumption;
    }



    public int getNumStepsTaken()
    {
        return iNumStepsTaken;
    }



    public int getSumReached()
    {
        return iSumReached;
    }



    /**
     * Pseudo-Iterator.
     * @return next index into the sorted(!) cards, returns a higher index every time
     */
    public int getNextListPos()
    {
        iListPosition++;
        return iListPosition;
    }



    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(CbUtil.simpleName(getClass()));
        sb.append('{');
        sb.append("ID="); //$NON-NLS-1$
        sb.append(System.identityHashCode(this));
        sb.append(", iAssumption="); //$NON-NLS-1$
        sb.append(iAssumption);
        sb.append(", iNumStepsTaken="); //$NON-NLS-1$
        sb.append(iNumStepsTaken);
        sb.append(", iStepsTaken="); //$NON-NLS-1$
        CbToString.obj2str(sb, iStepsTaken);
        sb.append(", iSumReached="); //$NON-NLS-1$
        sb.append(iSumReached);
        sb.append(", iDecisionsMade="); //$NON-NLS-1$
        CbToString.obj2str(sb, iDecisionsMade);
        sb.append(", iListPosition="); //$NON-NLS-1$
        sb.append(iListPosition);
        sb.append('}');
        return sb.toString();
    }
}
