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
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CbFundsEvent;
import com.tj.civ.client.event.CbFundsHandlerIF;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.CbVariantConfig;
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

    /** the game variant which is being played */
    private CbVariantConfig iVariant;
    
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
     *  @see CbSituation */
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
        final CbVariantConfig pVariant, final int pTargetPoints,
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
     * @param pForceAll force all states to be set anew, even if they appear to be
     *          unchanged (useful when recycling the view)
     */
    public void recalcAll(final boolean pForceAll)
    {
        LOG.enter("recalcAll"); //$NON-NLS-1$
        long debugTimeStart = 0L;
        if (LOG.isDetailEnabled()) {
            debugTimeStart = System.currentTimeMillis();
        }

        final CbCardCurrent[] cardsCurrent = iPresenter.getCardsCurrent();
        CbState[] newStates = new CbState[cardsCurrent.length];
        String[] stateReasons = new String[cardsCurrent.length];

        for (int i = 0; i < cardsCurrent.length; i++)
        {
            final CbCardConfig cardConfig = cardsCurrent[i].getConfig();
            final CbState currentState = cardsCurrent[i].getState();

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
            else if (iFundsEnabled && (iFundsTotal - iPresenter.getPlannedInvestment()
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
        if (!iIsDesperate && iVariant.hasNumCardsLimit()) {
            int[] dcResultPoints = new CbDiscouragementCalculator(newStates).execute();
            for (int i = 0; i < dcResultPoints.length; i++)
            {
                if (newStates[i] == CbState.Absent) {
                    if (dcResultPoints[i] > 0 && dcResultPoints[i] < iTargetPoints) {
                        newStates[i] = CbState.DiscouragedBuy;
                        int delta = iTargetPoints - dcResultPoints[i];
                        stateReasons[i] = CbConstants.MESSAGES.cardsDiscouraged(delta);
                    }
                }
            }
            // TODO notify global display: still desperate / if yes how much exactly (max)
        }

        // display results
        logRecalcAllResults(cardsCurrent, newStates);
        for (int i = 0; i < newStates.length; i++)
        {
            final CbState currentState = cardsCurrent[i].getState();
            if (pForceAll || currentState != newStates[i]) {
                iPresenter.setState(cardsCurrent[i], newStates[i], stateReasons[i]);
            }
        }

        if (LOG.isDetailEnabled()) {
            LOG.detail("recalcAll", "TIME: " //$NON-NLS-1$ //$NON-NLS-2$
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
            result = CbDiscouragementCalculator.isStillDesperate();
        }
        return result;
    }
}
