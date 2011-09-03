/*
 * CivBuddy - A Civilization Tactics Guide
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

import com.tj.civ.client.activities.CbDiscouragementCalculator;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.event.CbDesperationEvent;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.views.CbCardsViewIF;


/**
 * Manges state transitions of cards according to their definition in
 * {@link CbState}.
 *
 * @author Thomas Jensen
 */
public class CbCardStateManager
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbCardStateManager.class);

    /** the 'Cards' activity we're going to be associated to */
    private CbCardsViewIF.CbPresenterIF iPresenter;

    /** Desperation Mode: Activated once a discouraged card is planned or bought,
     *  this mode signifies that no further discourgedBuy state calculations should
     *  be made. The winning points are displayed as 'problematic'.
     *  <p>This mode can be deactivated by revising the owned cards or taking back
     *  a planned acquisition in such a way that winning by civilization card points
     *  along is again possible.
     *  <p>The reason for this mode is to avoid having to show all cards as
     *  discouraged for the remainder of the game.
     *  @see CbSituation */
    private boolean iIsDesperate = false;



    /**
     * Constructor.
     * @param pPresenter the 'Cards' activity we're going to be associated to
     */
    public CbCardStateManager(final CbCardsViewIF.CbPresenterIF pPresenter)
    {
        super();
        iPresenter = pPresenter;
    }



    /**
     * Recalculate the state of all cards based on the cards which are currently in
     * states 'Owned' or 'Planned'. Consequently, states 'Owned' and 'Planned' are
     * not recalculated.
     * @param pForceAll force all states (including 'Owned' and 'Planned') to be set
     *     anew, even if they appear to be unchanged (useful when recycling the view)
     */
    public void recalcAll(final boolean pForceAll)
    {
        LOG.enter("recalcAll"); //$NON-NLS-1$
        long debugTimeStart = 0L;
        if (LOG.isDebugEnabled()) {
            debugTimeStart = System.currentTimeMillis();
        }
        
        final boolean hasCardLimit = CbGlobal.getGame().getVariant().hasNumCardsLimit();
        final int targetPoints = CbGlobal.getCurrentSituation().getPlayer().getWinningTotal();
        final boolean fundsEnabled = CbGlobal.getCurrentFunds().isEnabled();
        final int fundsTotal = CbGlobal.getCurrentFunds().getTotalFunds();
        final CbCardCurrent[] cardsCurrent =
            CbGlobal.getCurrentSituation().getCardsCurrent();

        final CbState[] newStates = new CbState[cardsCurrent.length];
        final String[] stateReasons = new String[cardsCurrent.length];
        final int[] deltas = new int[cardsCurrent.length];

        for (int i = 0; i < cardsCurrent.length; i++)
        {
            final CbCardConfig cardConfig = cardsCurrent[i].getConfig();
            final CbState currentState = cardsCurrent[i].getState();
            deltas[i] = 0;

            if (currentState.isAffectingCredit()) {
                // Owned or Planned
                newStates[i] = currentState;
                stateReasons[i] = null;
            }
            else if (cardConfig.hasPrereq()
                && !cardsCurrent[cardConfig.getPrereq()].getState().isAffectingCredit())
            {
                newStates[i] = CbState.PrereqFailed;
                String prn = cardsCurrent[cardConfig.getPrereq()].getConfig().getLocalizedName();
                stateReasons[i] = CbConstants.MESSAGES.prereqFailed(prn);
            }
            else if (fundsEnabled && (fundsTotal - iPresenter.getPlannedInvestment()
                - cardsCurrent[i].getCostCurrent()) < 0)
            {
                newStates[i] = CbState.Unaffordable;
                stateReasons[i] = CbConstants.STRINGS.noFunds();
            }
            else {
                newStates[i] = CbState.Absent;
                stateReasons[i] = null;
            }
            // DiscouragedBuy omitted, will be handled in next step
        }
        
        // calculate DiscouragedBuy states
        if (hasCardLimit) {
            int[] dcResultPoints = new CbDiscouragementCalculator(newStates).execute();
            boolean desperate = true;
            for (int i = 0; i < dcResultPoints.length; i++)
            {
                if (desperate && !newStates[i].isAffectingCredit()
                    && dcResultPoints[i] > 0 && dcResultPoints[i] >= targetPoints)
                {
                    desperate = false;
                }
                if (newStates[i] == CbState.Absent) {
                    if (dcResultPoints[i] > 0 && dcResultPoints[i] < targetPoints) {
                        newStates[i] = CbState.DiscouragedBuy;
                        int delta = targetPoints - dcResultPoints[i];
                        stateReasons[i] = CbConstants.MESSAGES.cardsDiscouraged(delta);
                        deltas[i] = delta;
                    }
                }
            }
            setDesperate(desperate);
            // FIXME das ist falsch, weil es sein kann, dass gar keine desperation
            //       berechnet wird, wenn z.B. alle non-affecting auch unaffordable
            //       sind. explizite setzung + stillDesperate() überlegen; alternativ
            //       auf jeden fall desperation bei recalcAll() mit ausrechnen.

            // fire event informing on new desperation state
            int delta = 0;
            if (desperate) {
                int max = CbUtil.max(dcResultPoints);
                delta = targetPoints - max;
            }
            iPresenter.getEventBus().fireEvent(new CbDesperationEvent(desperate, delta));
        }

        // display results
        logRecalcAllResults(cardsCurrent, newStates);
        for (int i = 0; i < newStates.length; i++)
        {
            final CbState currentState = cardsCurrent[i].getState();
            if (pForceAll || currentState != newStates[i]) {
                iPresenter.setState(cardsCurrent[i], newStates[i], stateReasons[i], deltas[i]);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("recalcAll", "TIME: " //$NON-NLS-1$ //$NON-NLS-2$
                + (System.currentTimeMillis() - debugTimeStart) + " ms"); //$NON-NLS-1$
        }
        LOG.exit("recalcAll"); //$NON-NLS-1$
    }



    private void logRecalcAllResults(final CbCardCurrent[] pCardsCurrent,
        final CbState[] pNewStates)
    {
        if (!LOG.isDetailEnabled()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("RESULT = {"); //$NON-NLS-1$
        for (int i = 0; i < pCardsCurrent.length; i++)
        {
            sb.append(pCardsCurrent[i].getConfig().toString());
            sb.append("->"); //$NON-NLS-1$
            sb.append(pNewStates[i].toString());
            if (pNewStates[i] != pCardsCurrent[i].getState()) {
                sb.append("(!was:"); //$NON-NLS-1$
                sb.append(pCardsCurrent[i].getState().toString());
                sb.append(')');
            }
            if (i < pCardsCurrent.length - 1) {
                sb.append(", "); //$NON-NLS-1$
            }
        }
        sb.append('}');
        
        LOG.detail("recalcAll", sb.toString());  //$NON-NLS-1$
    }



    public boolean isDesperate()
    {
        return iIsDesperate;
    }

    /**
     * Setter.
     * @param pIsDesperate new value of {@link #iIsDesperate}
     */
    public void setDesperate(final boolean pIsDesperate)
    {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setDesperate", //$NON-NLS-1$
                "pIsDesperate = " + pIsDesperate); //$NON-NLS-1$
        }
        iIsDesperate = pIsDesperate;
    }



    /**
     * Determine if the current set of card states still justifies desperation mode
     * being active. Called after a card was set to 'Absent'.
     * <p>This is the case if even the most expensive card we could buy next would
     * still be discouraged.
     * @return <code>true</code> if yes
     * @deprecated whenever desperation state changes, we fire an event, so this
     *              method is obsolete
     */
    @Deprecated
    public boolean stillDesperate()
    {
        boolean result = false;
        if (isDesperate()) {
            result = CbDiscouragementCalculator.isStillDesperate();
        }
        return result;
    }
}
