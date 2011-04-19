/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 15.01.2011
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
package com.tj.civ.client.widgets;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CcAllStatesEvent;
import com.tj.civ.client.event.CcAllStatesHandlerIF;
import com.tj.civ.client.event.CcFundsEvent;
import com.tj.civ.client.event.CcFundsHandlerIF;
import com.tj.civ.client.event.CcStateEvent;
import com.tj.civ.client.event.CcStateHandlerIF;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcGroup;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.views.CbCardsViewIF;


/**
 * GWT Widget displaying the current game statistics for a player based on his or
 * her {@link CcSituation}.
 *
 * @author Thomas Jensen
 */
public class CcStatistics
    extends VerticalPanel
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CcStatistics.class);

    /** indicator for winning points */
    private CcStatsIndicator iPoints;

    /** indicator for number of cards */
    private CcStatsIndicator iCards;

    /** indicator for funds */
    private CcStatsIndicator iFunds;

    /** indicator for number of groups of cards */
    private CcStatsIndicator iGroups;

    /** the groups of which cards are currently owned */
    private Set<CcGroup> iGroupsSet = new HashSet<CcGroup>();

    /** the groups of which cards are currently owned or planned */
    private Set<CcGroup> iGroupsSetInclPlan = new HashSet<CcGroup>();

    /** flag indicating whether {@link #addEventHandlers} was called */
    private boolean iHandlersAdded = false;



    /**
     * Constructor.
     * @param pWinningTotal total number of points the player must reach to win
     * @param pNumCardsLimit card limit imposed by the variant, or 0 if none
     */
    public CcStatistics(final int pWinningTotal, final int pNumCardsLimit)
    {
        super();
        addStyleName(CbConstants.CSS.ccStats());

        HorizontalPanel hp = new HorizontalPanel();
        hp.setStyleName(CbConstants.CSS.ccStatsInner() + " " //$NON-NLS-1$
            + CbConstants.CSS_BLUEGRADIENT);

        iPoints = new CcStatsIndicator(CbConstants.STRINGS.statsPoints(),
            Integer.valueOf(pWinningTotal), true);
        hp.setHorizontalAlignment(ALIGN_LEFT);
        hp.add(iPoints);
        iGroups = new CcStatsIndicator(CbConstants.STRINGS.statsGroups(), null, false);
        hp.setHorizontalAlignment(ALIGN_RIGHT);
        hp.add(iGroups);
        add(hp);

        hp = new HorizontalPanel();
        hp.setStyleName(CbConstants.CSS.ccStatsInner() + " " //$NON-NLS-1$
            + CbConstants.CSS_BLUEGRADIENT);

        iFunds = new CcStatsIndicator(CbConstants.STRINGS.statsFunds(), null, false);
        iFunds.setEnabled(false);
        hp.setHorizontalAlignment(ALIGN_LEFT);
        hp.add(iFunds);
        iCards = new CcStatsIndicator(CbConstants.STRINGS.statsCards(),
            pNumCardsLimit > 0 ? Integer.valueOf(pNumCardsLimit) : null, false);
        hp.setHorizontalAlignment(ALIGN_RIGHT);
        hp.add(iCards);
        add(hp);
    }



    /**
     * Registers the event handlers required by this widget with the event bus. 
     * @param pEventBus the event bus
     */
    public void addEventHandlers(final EventBus pEventBus)
    {
        LOG.enter("addEventHandlers"); //$NON-NLS-1$
        if (LOG.isDetailEnabled()) {
            LOG.detail("addEventHandlers", //$NON-NLS-1$
                "iHandlersAdded = " + iHandlersAdded); //$NON-NLS-1$
        }
        if (iHandlersAdded) {
            LOG.exit("addEventHandlers"); //$NON-NLS-1$
            return;
        }
        iHandlersAdded = true;

        pEventBus.addHandler(CcStateEvent.TYPE, new CcStateHandlerIF() {
            @Override
            public void onStateChanged(final CcStateEvent pEvent)
            {
                CcStatistics.this.onStateChanged(pEvent);
            }
        });
        pEventBus.addHandler(CcAllStatesEvent.TYPE, new CcAllStatesHandlerIF() {
            @Override
            public void onAllStatesChanged(final CcAllStatesEvent pEvent)
            {
                CcStatistics.this.onAllStatesChanged(pEvent);
            }
        });
        pEventBus.addHandler(CcFundsEvent.TYPE, new CcFundsHandlerIF() {
            @Override
            public void onFundsChanged(final CcFundsEvent pEvent)
            {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("onFundsChanged", //$NON-NLS-1$
                        pEvent.isFundsEnabled() + "; " + pEvent.getFunds()); //$NON-NLS-1$
                }
                updateFunds(pEvent.getFunds(), pEvent.isFundsEnabled());
            }
        });
        LOG.exit("addEventHandlers"); //$NON-NLS-1$
    }



    /**
     * Update the funds display.
     * @param pTotalFunds new value of total funds
     * @param pEnabled whether funds tracking is generally enabled or not
     */
    public void updateFunds(final int pTotalFunds, final boolean pEnabled)
    {
        iFunds.setValueAndPlan(pTotalFunds, iFunds.getPlan());
        iFunds.setEnabled(pEnabled);
        if (pEnabled) {
            iFunds.setProblem(iFunds.getPlan() > pTotalFunds);
        }
    }



    private void onStateChanged(final CcStateEvent pEvent)
    {
        if (!(pEvent.getSource() instanceof CbCardsViewIF.CcPresenterIF)) {
            // do nothing if this event didn't originate with the cards activity
            return;
        }
        CbCardsViewIF.CcPresenterIF cardCtrl = (CbCardsViewIF.CcPresenterIF) pEvent.getSource();

        final CcCardCurrent[] cards = cardCtrl.getCardsCurrent();
        final CcCardCurrent card = cards[pEvent.getRowIdx()];
        final CcCardConfig config = card.getConfig();
        if (cardCtrl.getView().isRevising()) {
            if (card.getState() == CcState.Owned) {
                iCards.setValue(iCards.getValue() + 1);
                iPoints.setValue(iPoints.getValue() + config.getCostNominal());
                addGroups(config.getGroups(), false);
                iGroups.setValue(iGroupsSet.size());
            } else {
                iCards.setValue(iCards.getValue() - 1);
                iPoints.setValue(iPoints.getValue() - config.getCostNominal());
                removeGroups(cards, config.getGroups(), false);
                iGroups.setValue(iGroupsSet.size());
            }
        } else {
            if (card.getState() == CcState.Planned) {
                incrementPlanBy(iCards, +1);
                incrementPlanBy(iPoints, config.getCostNominal());
                addGroups(config.getGroups(), true);
                if (iGroupsSetInclPlan.size() > iGroups.getValue()) {
                    iGroups.setPlan(iGroupsSetInclPlan.size());
                }
                int expensesPlanned = card.getCostCurrent();
                if (iFunds.getPlan() > 0) {
                    expensesPlanned += iFunds.getPlan();
                }
                iFunds.setPlan(expensesPlanned);
                checkExpenses();
            } else {
                incrementPlanBy(iCards, -1);
                incrementPlanBy(iPoints, -config.getCostNominal());
                removeGroups(cards, config.getGroups(), true);
                if (iGroupsSetInclPlan.size() > iGroups.getValue()) {
                    iGroups.setPlan(iGroupsSetInclPlan.size());
                } else {
                    iGroups.setPlan(-1);
                }
                int expensesPlanned = iFunds.getPlan() - card.getCostCurrent();
                if (expensesPlanned <= 0) {
                    expensesPlanned = -1;
                }
                iFunds.setPlan(expensesPlanned);
                checkExpenses();
            }
        }
    }



    private void addGroups(final CcGroup[] pGroups, final boolean pPlanOnly)
    {
        for (CcGroup group : pGroups) {
            if (!pPlanOnly) {
                iGroupsSet.add(group);
            }
            iGroupsSetInclPlan.add(group);
        }
    }



    private void removeGroups(final CcCardCurrent[] pCards, final CcGroup[] pGroups,
        final boolean pPlanOnly)
    {
        for (CcGroup group : pGroups) {   // 1 or 2 iterations
            boolean found = false;
            for (CcCardCurrent card : pCards) {   // up to number-of-cards iterations
                if (card.getState().isAffectingCredit()) {
                    if (arrayContains(card.getConfig().getGroups(), group)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                if (!pPlanOnly) {
                    iGroupsSet.remove(group);
                }
                iGroupsSetInclPlan.remove(group);
            }
        }
    }



    private boolean arrayContains(final CcGroup[] pArray, final CcGroup pGroup)
    {
        boolean result = false;
        for (CcGroup grp : pArray) {
            if (grp == pGroup) {
                result = true;
                break;
            }
        }
        return result;
    }



    private void incrementPlanBy(final CcStatsIndicator pIndicator, final int pIncrement)
    {
        int p = pIndicator.getPlan();
        if (p < 0) {
            p = pIndicator.getValue();
        }
        if (pIncrement < 0) {
            p += pIncrement;
            if (p <= pIndicator.getValue()) {
                p = -1;
            }
        }
        else {
            p += pIncrement;
        }
        pIndicator.setPlan(p);
    }



    private void onAllStatesChanged(final CcAllStatesEvent pEvent)
    {
        if (!(pEvent.getSource() instanceof CbCardsViewIF.CcPresenterIF)) {
            // do nothing if this event didn't originate with the cards activity
            return;
        }
        final CbCardsViewIF.CcPresenterIF cardCtrl =
            (CbCardsViewIF.CcPresenterIF) pEvent.getSource();
        if (cardCtrl != null) {
            handleAllStatesChanged(cardCtrl);
        }
    }


    /**
     * Handle the fact that the states of all cards has just changed, so we must
     * update all statistical values.
     * @param pCardCtrl the presenter
     */
    public void handleAllStatesChanged(final CbCardsViewIF.CcPresenterIF pCardCtrl)
    {
        int points = 0;
        int pointsPlanned = 0;
        int cards = 0;
        int cardsPlanned = 0;
        int expensesPlanned = 0;
        Set<CcGroup> grps = new HashSet<CcGroup>();
        Set<CcGroup> grpsPlanned = new HashSet<CcGroup>();
        for (CcCardCurrent card : pCardCtrl.getCardsCurrent())
        {
            CcState state = card.getState();
            CcCardConfig config = card.getConfig();
            if (state == CcState.Owned) {
                points += config.getCostNominal();
                cards++;
                for (CcGroup grp : config.getGroups()) {
                    grps.add(grp);
                    grpsPlanned.add(grp);
                }
            }
            else if (state == CcState.Planned) {
                pointsPlanned += config.getCostNominal();
                expensesPlanned += card.getCostCurrent();
                cardsPlanned++;
                for (CcGroup grp : config.getGroups()) {
                    grpsPlanned.add(grp);
                }
            }
        }

        if (pointsPlanned == 0) {
            pointsPlanned = -1;
        } else {
            pointsPlanned += points;
        }
        if (cardsPlanned == 0) {
            cardsPlanned = -1;
        } else {
            cardsPlanned += cards;
        }
        if (expensesPlanned == 0) {
            expensesPlanned = -1;
        } // no else
        int grpsPlannedInt = grpsPlanned.size();
        if (grpsPlannedInt <= grps.size()) {
            grpsPlannedInt = -1;
        }

        iPoints.setValueAndPlan(points, pointsPlanned);
        iCards.setValueAndPlan(cards, cardsPlanned);
        iGroupsSet = grps;
        iGroupsSetInclPlan = grpsPlanned;
        iGroups.setValueAndPlan(grps.size(), grpsPlannedInt);
        iFunds.setPlan(expensesPlanned);
        checkExpenses();
    }



    private void checkExpenses()
    {
        if (iFunds.isEnabled()) {
            iFunds.setProblem(iFunds.getPlan() > iFunds.getValue());
        }
    }



    /**
     * Setter.
     * @param pIsDesperate Set or clear the 'problematic' flag on the total points
     */
    public void setDesperate(final boolean pIsDesperate)
    {
        iPoints.setProblem(pIsDesperate);
    }



    /**
     * Set the limit values. 
     * @param pPointsTarget the current points target of the player's civilization
     * @param pNumCardsLimit maximum number of cards allowed by the variant
     */
    public void setLimits(final int pPointsTarget, final Integer pNumCardsLimit)
    {
        iPoints.setMax(Integer.valueOf(pPointsTarget));
        iCards.setMax(pNumCardsLimit);
    }
}
