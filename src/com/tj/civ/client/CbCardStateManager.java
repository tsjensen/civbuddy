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

import java.util.Arrays;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CbFundsEvent;
import com.tj.civ.client.event.CbFundsHandlerIF;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.model.CcVariantConfig;
import com.tj.civ.client.views.CbCardsViewIF;


/**
 * Manges state transitions of cards according to their definition in
 * {@link CcState}.
 *
 * @author Thomas Jensen
 */
public class CbCardStateManager
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbCardStateManager.class);

    /** the 'Cards' activity we're going to be associated to */
    private CbCardsViewIF.CbPresenterIF iPresenter;

    /** the game variant which is being played */
    private CcVariantConfig iVariant;
    
    /** number of points that the player must reach to win */
    private int iTargetPoints;

    /** the current enablement state of the funds tracking feature, kept current
     *  by means of {@link CbFundsEvent}s via the event bus */
    private boolean iFundsEnabled = false;

    /** the currently available funds, kept current  by means of
     *  {@link CbFundsEvent}s via the event bus */
    private int iFundsTotal = 0;

    /** Desperation Mode: Activated once a discouraged card is planned or bought,
     *  this mode signifies that no further discourgedBuy state calculations should
     *  be made. The winning points are displayed as 'problematic'.
     *  <p>This mode can be deactivated by revising the owned cards or taking back
     *  a planned acquisition in such a way that winning by civilization card points
     *  along is again possible.
     *  <p>The reason for this mode is to avoid having to show all cards as
     *  discouraged for the remainder of the game.
     *  @see CcSituation */
    private boolean iIsDesperate = false;



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
    public CbCardStateManager(final CbCardsViewIF.CbPresenterIF pActivity,
        final CcVariantConfig pVariant, final int pTargetPoints,
        final boolean pFundsEnabled, final int pFundsTotal)
    {
        super();
        iPresenter = pActivity;
        iVariant = pVariant;
        iTargetPoints = pTargetPoints;
        iFundsEnabled = pFundsEnabled;
        iFundsTotal = pFundsTotal;

        pActivity.getEventBus().addHandler(CbFundsEvent.TYPE, new CbFundsHandlerIF() {
            @Override
            public void onFundsChanged(final CbFundsEvent pEvent)
            {
                CbCardStateManager.this.iFundsEnabled = pEvent.isFundsEnabled();
                CbCardStateManager.this.iFundsTotal = pEvent.getFunds();
            }
        });
    }



    /**
     * Recalculate the state of all cards.
     * @param pForceAll force all states to be set anew, even if the appear to be
     *          unchanged (useful when recycling the view)
     */
    public void recalcAll(final boolean pForceAll)
    {
        LOG.enter("recalcAll"); //$NON-NLS-1$
        long debugTimeStart = 0L;
        if (LOG.isDetailEnabled()) {
            debugTimeStart = System.currentTimeMillis();
        }

        final CcCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
        for (CcCardCurrent card : cardsCurrent)
        {
            final CcCardConfig cardConfig = card.getConfig();
            final CcState currentState = card.getState();
            CcState newState = null;
            String reason = null;
            if (LOG.isDebugEnabled()) {
                LOG.debug("recalcAll", //$NON-NLS-1$
                    "Computing " + card.getConfig() //$NON-NLS-1$
                    + " (state=" + currentState + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (currentState.isAffectingCredit()) {
                // Owned or Planned
                newState = currentState;
                if (!pForceAll) {
                    continue;
                }
            }
            else if (cardConfig.hasPrereq()
                && !cardsCurrent[cardConfig.getPrereq()].getState().isAffectingCredit())
            {
                newState = CcState.PrereqFailed;
                String prn = cardsCurrent[cardConfig.getPrereq()].getConfig().getLocalizedName();
                reason = CbConstants.MESSAGES.prereqFailed(prn);
            }
            else if (iFundsEnabled
                && (iFundsTotal - iPresenter.getPlannedInvestment() - card.getCostCurrent()) < 0)
            {
                newState = CcState.Unaffordable;
                reason = CbConstants.STRINGS.noFunds();
            }
            else if (!iIsDesperate && isDiscouraged(card.getMyIdx()))
            {
                newState = CcState.DiscouragedBuy;
                reason = CbConstants.STRINGS.cardsDiscouraged();
            }
            else {
                newState = CcState.Absent;
            }
            // TODO: erst alle states berechnen, dann anzeigen (generell behandeln)
            if (pForceAll || currentState != newState) {
                //LOG.fine("recalcAll() - \t\t\t\t\tSetting state of '"
                //    + card.getConfig().getLocalizedName() + "' to '" + newState + "'");
                iPresenter.setState(card, newState, reason);
            }
        }

        if (LOG.isDetailEnabled()) {
            LOG.detail("recalcAll", "TIME: " //$NON-NLS-1$ //$NON-NLS-2$
                + (System.currentTimeMillis() - debugTimeStart) + " ms"); //$NON-NLS-1$
        }
        LOG.exit("recalcAll"); //$NON-NLS-1$
    }


    
    private boolean isDiscouraged(final int pRowIdx)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("isDiscouraged",  //$NON-NLS-1$
                new String[]{"pRowIdx"},  //$NON-NLS-1$
                new Object[]{Integer.valueOf(pRowIdx)});
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("isDiscouraged", //$NON-NLS-1$
                "========= Computing '" //$NON-NLS-1$
                + iPresenter.getCardsCurrent()[pRowIdx].getConfig().getLocalizedName()
                + "' ===================================="); //$NON-NLS-1$
        }

        boolean result = false;
        if (iVariant.hasNumCardsLimit() && iPresenter.getNumCardsAffectingCredit() > 0)
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
                if (LOG.isDetailEnabled()) {
                    LOG.detail("isDiscouraged", //$NON-NLS-1$
                        "cardsSorted = " + Arrays.deepToString(cardsSorted)); //$NON-NLS-1$
                }
    
                remainingSteps--;
                final int[] path = new int[remainingSteps];
                final int[] rest = new int[cardsSorted.length - (remainingSteps + 1)
                                           - iPresenter.getNumCardsAffectingCredit()];
                for (int i = 0, stepsTaken = 0; i < cardsSorted.length; i++)
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
                result = handlePrereqs(pRowIdx,
                    iPresenter.getNominalSumInclPlan() + fixedNominal, path, rest);
            }
        }

        LOG.exit("isDiscouraged", Boolean.valueOf(result)); //$NON-NLS-1$
        return result;
    }



    /**
     * Determine if the required winning total can be reached, by looking at whether
     * the given <tt>pPathSpecial</tt> is legal and if so, calculating
     * its value.
     * <p>Cards which are already Owned or Planned are never contained in either
     * array. The values of the cards referenced in both arrays are in descending
     * order.
     * @param pRowIdx the card whose state we are calculating, in the form of an
     *          index into cardsCurrent
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
    private boolean handlePrereqs(final int pRowIdx, final int pStartingSum,
        final int[] pPathSpecial, final int[] pRestSpecial)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("handlePrereqs",  //$NON-NLS-1$
                new String[]{"pRowIdx", "pStartingSum", //$NON-NLS-1$ //$NON-NLS-2$
                    "pPathSpecial", "pRestSpecial"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Integer.valueOf(pRowIdx), Integer.valueOf(pStartingSum),
                    pPathSpecial, pRestSpecial});
        }
        boolean result = false;

        final CcCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
        final CcCardConfig[] cardsSorted = iVariant.getCardsSortedInternal();

        int sum = pStartingSum;
        int npf = 0;     // number of times we've had to do a swap
        for (int p = 0; p < pPathSpecial.length; p++)
        {
            CcCardConfig card = cardsSorted[pPathSpecial[p]];
            int prIdx = card.getPrereq();
            if (prIdx < 0 || prIdx == pRowIdx
                || prereqOkay(prIdx, pPathSpecial, cardsCurrent))
            {
                sum += card.getCostNominal();
            }
            else {
                swapInPrereqp(prIdx, pPathSpecial, npf, pRestSpecial);
                npf++;
            }
        }

        result = sum < iTargetPoints;
        // TODO think about game variants with multiple prereqs and transitive
        //      dependencies
        // TODO also, this does not work if the prereq card is cheaper than the
        //      most expensive card still in rest
        // TODO A whole different idea still for this algorithm is to somehow know
        //      the minimum value that the next card bought must have, then show
        //      all less valuable cards as discouraged. This can possibly be achieved
        //      by pre-calculating the most expensive path up front, then taking
        //      steps out sequentially.

        LOG.exit("handlePrereqs", Boolean.valueOf(result)); //$NON-NLS-1$
        return result;
    }



    /**
     * Swap the least valuable card on the path with the prerequiste card in rest.
     * @param pPrereqIdx index into cardsCurrent of the required prereq card
     * @param pPathSpecial the current path of indexes into cardsSorted
     * @param pSwapCount the number of times we've done this before
     * @param pRestSpecial the cards not on the path, in no particular order
     */
    private void swapInPrereqp(final int pPrereqIdx, final int[] pPathSpecial,
        final int pSwapCount, final int[] pRestSpecial)
    {
        final int p = pPathSpecial.length - 1 - pSwapCount;
        final int temp = pPathSpecial[p];
        
        final CcCardConfig[] cardsSorted = iVariant.getCardsSortedInternal();
        int r = 0;
        while (r < pRestSpecial.length) {
            if (cardsSorted[pRestSpecial[r]].getMyIdx() == pPrereqIdx) {
                break;
            }
            r++;
        }
        pPathSpecial[p] = pRestSpecial[r];
        pRestSpecial[r] = temp;
    }



    private boolean prereqOkay(final int pPrereqIdx, final int[] pPathSpecial,
        final CcCardCurrent[] pCardsCurrent)
    {
        boolean result = false;
        
        if (pCardsCurrent[pPrereqIdx].getState().isAffectingCredit()) {
            result = true;
        } else {
            final CcCardConfig[] cardsSorted = iVariant.getCardsSortedInternal();
            for (int p = 0; p < pPathSpecial.length; p++) {
                if (cardsSorted[pPathSpecial[p]].getMyIdx() == pPrereqIdx) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }



    public boolean isDesperate()
    {
        return iIsDesperate;
    }

    public void setDesperate(final boolean pIsDesperate)
    {
        iIsDesperate = pIsDesperate;
    }



    /**
     * Determine if the current set of card states still justifies desperation mode
     * being active. Called after a card was set to 'Absent'.
     * <p>This is the case if even the most expensive card we could buy next would
     * still be discouraged.
     * @return <code>true</code> if yes
     */
    public boolean stillDesperate()
    {
        boolean result = false;
        if (iIsDesperate) {
            CcCardCurrent max = null;
            for (CcCardCurrent card : iPresenter.getCardsCurrent()) {
                CcState state = card.getState();
                if (state.isAffectingCredit() || state == CcState.PrereqFailed) {
                    continue;
                }
                if (max == null
                    || card.getConfig().getCostNominal() > max.getConfig().getCostNominal())
                {
                    max = card;
                }
            }
            result = max != null && isDiscouraged(max.getMyIdx());
        }
        return result;
    }
}
