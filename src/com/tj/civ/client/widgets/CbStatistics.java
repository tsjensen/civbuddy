/*
 * CivBuddy - A Civilization Tactics Guide
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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CbAllStatesEvent;
import com.tj.civ.client.event.CbAllStatesHandlerIF;
import com.tj.civ.client.event.CbDesperationEvent;
import com.tj.civ.client.event.CbDesperationHandlerIF;
import com.tj.civ.client.event.CbFundsEvent;
import com.tj.civ.client.event.CbFundsHandlerIF;
import com.tj.civ.client.event.CbStateEvent;
import com.tj.civ.client.event.CbStateHandlerIF;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbGroup;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.views.CbCardsViewIF;


/**
 * GWT Widget displaying the current game statistics for a player based on his or
 * her {@link CbSituation}.
 *
 * @author Thomas Jensen
 */
public class CbStatistics
    extends Composite
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbStatistics.class);

    /** indicator for winning points */
    private CbStatsIndicator iPoints;

    /** indicator for number of cards */
    private CbStatsIndicator iCards;

    /** indicator for funds */
    private CbStatsIndicator iFunds;

    /** indicator for number of groups of cards */
    private CbStatsIndicator iGroups;

    /** the groups of which cards are currently owned */
    private Set<CbGroup> iGroupsSet = new HashSet<CbGroup>();

    /** the groups of which cards are currently owned or planned */
    private Set<CbGroup> iGroupsSetInclPlan = new HashSet<CbGroup>();

    /** flag indicating whether {@link #addEventHandlers} was called */
    private boolean iHandlersAdded = false;

    // TODO add an indicator for desperation mode which shows how far off we are exactly


    /**
     * Constructor.
     * @param pWinningTotal total number of points the player must reach to win
     * @param pNumCardsLimit card limit imposed by the variant, or 0 if none
     */
    public CbStatistics(final int pWinningTotal, final int pNumCardsLimit)
    {
        super();

        iPoints = new CbStatsIndicator(CbConstants.STRINGS.statsPoints(),
            Integer.valueOf(pWinningTotal), true);
        iPoints.addStyleName(CbConstants.CSS.cbExtraBarNorthWest());

        iGroups = new CbStatsIndicator(CbConstants.STRINGS.statsGroups(), null, false);
        iGroups.addStyleName(CbConstants.CSS.cbExtraBarNorthEast());

        iFunds = new CbStatsIndicator(CbConstants.STRINGS.statsFunds(), null, false);
        iFunds.setEnabled(false);
        iFunds.addStyleName(CbConstants.CSS.cbExtraBarSouthWest());

        iCards = new CbStatsIndicator(CbConstants.STRINGS.statsCards(),
            pNumCardsLimit > 0 ? Integer.valueOf(pNumCardsLimit) : null, false);
        iCards.addStyleName(CbConstants.CSS.cbExtraBarSouthEast());

        FlowPanel extraBar = new FlowPanel();
        extraBar.add(iPoints);
        extraBar.add(iGroups);
        extraBar.add(iFunds);
        extraBar.add(iCards);
        extraBar.setStyleName(CbConstants.CSS.cbExtraBar());
        extraBar.addStyleName(CbConstants.CSS_EXTRABAR_GRADIENT);

        FlowPanel headPanelIeWrapper = new FlowPanel();
        headPanelIeWrapper.setStyleName(CbConstants.CSS.cbExtraBarIeWrapper());
        headPanelIeWrapper.add(extraBar);
        initWidget(headPanelIeWrapper);
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

        pEventBus.addHandler(CbStateEvent.TYPE, new CbStateHandlerIF() {
            @Override
            public void onStateChanged(final CbStateEvent pEvent)
            {
                CbStatistics.this.onStateChanged(pEvent);
            }
        });
        pEventBus.addHandler(CbAllStatesEvent.TYPE, new CbAllStatesHandlerIF() {
            @Override
            public void onAllStatesChanged(final CbAllStatesEvent pEvent)
            {
                CbStatistics.this.onAllStatesChanged(pEvent);
            }
        });
        pEventBus.addHandler(CbFundsEvent.TYPE, new CbFundsHandlerIF() {
            @Override
            public void onFundsChanged(final CbFundsEvent pEvent)
            {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("onFundsChanged", //$NON-NLS-1$
                        pEvent.isFundsEnabled() + "; " + pEvent.getFunds()); //$NON-NLS-1$
                }
                updateFunds(pEvent.getFunds(), pEvent.isFundsEnabled());
            }
        });
        pEventBus.addHandler(CbDesperationEvent.TYPE, new CbDesperationHandlerIF() {
            @Override
            public void onDesperationCalculated(final CbDesperationEvent pEvent)
            {
                setDesperate(pEvent.isDesperate());
                // TODO use delta value for tooltip or something
            }
        });

        if (LOG.isDebugEnabled()) {
            LOG.debug("addEventHandlers", //$NON-NLS-1$
                "Added 4 event handlers"); //$NON-NLS-1$
        }
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



    private void onStateChanged(final CbStateEvent pEvent)
    {
        if (!(pEvent.getSource() instanceof CbCardsViewIF.CbPresenterIF)) {
            // do nothing if this event didn't originate with the cards activity
            return;
        }
        CbCardsViewIF.CbPresenterIF cardCtrl = (CbCardsViewIF.CbPresenterIF) pEvent.getSource();

        final CbCardCurrent[] cardsCurrent =
            CbGlobal.getGame().getCurrentSituation().getCardsCurrent();
        final CbCardCurrent card = cardsCurrent[pEvent.getRowIdx()];
        final CbCardConfig config = card.getConfig();
        if (cardCtrl.getView().isRevising()) {
            if (card.getState() == CbState.Owned) {
                iCards.setValue(iCards.getValue() + 1);
                iPoints.setValue(iPoints.getValue() + config.getCostNominal());
                addGroups(config.getGroups(), false);
                iGroups.setValue(iGroupsSet.size());
            } else {
                iCards.setValue(iCards.getValue() - 1);
                iPoints.setValue(iPoints.getValue() - config.getCostNominal());
                removeGroups(cardsCurrent, config.getGroups(), false);
                iGroups.setValue(iGroupsSet.size());
            }
        } else {
            if (card.getState() == CbState.Planned) {
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
                removeGroups(cardsCurrent, config.getGroups(), true);
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



    private void addGroups(final CbGroup[] pGroups, final boolean pPlanOnly)
    {
        for (CbGroup group : pGroups) {
            if (!pPlanOnly) {
                iGroupsSet.add(group);
            }
            iGroupsSetInclPlan.add(group);
        }
    }



    private void removeGroups(final CbCardCurrent[] pCards, final CbGroup[] pGroups,
        final boolean pPlanOnly)
    {
        for (CbGroup group : pGroups) {   // 1 or 2 iterations
            boolean found = false;
            for (CbCardCurrent card : pCards) {   // up to number-of-cards iterations
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



    private boolean arrayContains(final CbGroup[] pArray, final CbGroup pGroup)
    {
        boolean result = false;
        for (CbGroup grp : pArray) {
            if (grp == pGroup) {
                result = true;
                break;
            }
        }
        return result;
    }



    private void incrementPlanBy(final CbStatsIndicator pIndicator, final int pIncrement)
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



    private void onAllStatesChanged(final CbAllStatesEvent pEvent)
    {
        if (!(pEvent.getSource() instanceof CbCardsViewIF.CbPresenterIF)) {
            // do nothing if this event didn't originate with the cards activity
            return;
        }
        handleAllStatesChanged();
    }



    /**
     * Handle the fact that the states of all cards has just changed, so we must
     * update all statistical values.
     */
    public void handleAllStatesChanged()
    {
        int points = 0;
        int pointsPlanned = 0;
        int cards = 0;
        int cardsPlanned = 0;
        int expensesPlanned = 0;
        Set<CbGroup> grps = new HashSet<CbGroup>();
        Set<CbGroup> grpsPlanned = new HashSet<CbGroup>();
        final CbCardCurrent[] cardsCurrent =
            CbGlobal.getGame().getCurrentSituation().getCardsCurrent();
        for (CbCardCurrent card : cardsCurrent)
        {
            CbState state = card.getState();
            CbCardConfig config = card.getConfig();
            if (state == CbState.Owned) {
                points += config.getCostNominal();
                cards++;
                for (CbGroup grp : config.getGroups()) {
                    grps.add(grp);
                    grpsPlanned.add(grp);
                }
            }
            else if (state == CbState.Planned) {
                pointsPlanned += config.getCostNominal();
                expensesPlanned += card.getCostCurrent();
                cardsPlanned++;
                for (CbGroup grp : config.getGroups()) {
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
