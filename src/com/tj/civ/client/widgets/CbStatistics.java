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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tj.civ.client.CcCardController;
import com.tj.civ.client.event.CcAllStatesEvent;
import com.tj.civ.client.event.CcAllStatesHandler;
import com.tj.civ.client.event.CcEventBus;
import com.tj.civ.client.event.CcFundsEvent;
import com.tj.civ.client.event.CcFundsHandler;
import com.tj.civ.client.event.CcStateEvent;
import com.tj.civ.client.event.CcStateHandler;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcGroup;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.resources.CcConstants;


/**
 * GWT Widget displaying the current game statistics for a player based on his or
 * her {@link CcSituation}.
 *
 * @author Thomas Jensen
 */
public class CcStatistics
    extends VerticalPanel
{
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



    /**
     * Constructor.
     * @param pSituation reference to the player's current situation
     */
    public CcStatistics(final CcSituation pSituation)
    {
        super();
        addStyleName(CcConstants.CSS.ccStats());

        HorizontalPanel hp = new HorizontalPanel();
        hp.setStyleName(CcConstants.CSS.ccStatsInner() + " " //$NON-NLS-1$
            + CcConstants.CSS_BLUEGRADIENT);

        final int target = pSituation.getPlayer().getWinningTotal();
        iPoints = new CcStatsIndicator(CcConstants.STRINGS.statsPoints(),
            Integer.valueOf(target), true);
        hp.setHorizontalAlignment(ALIGN_LEFT);
        hp.add(iPoints);
        iGroups = new CcStatsIndicator(CcConstants.STRINGS.statsGroups(), null, false);
        hp.setHorizontalAlignment(ALIGN_RIGHT);
        hp.add(iGroups);
        add(hp);

        hp = new HorizontalPanel();
        hp.setStyleName(CcConstants.CSS.ccStatsInner() + " " //$NON-NLS-1$
            + CcConstants.CSS_BLUEGRADIENT);

        iFunds = new CcStatsIndicator(CcConstants.STRINGS.statsFunds(), null, false);
        iFunds.setEnabled(false);
        hp.setHorizontalAlignment(ALIGN_LEFT);
        hp.add(iFunds);
        final int limit = pSituation.getVariant().getNumCardsLimit();
        iCards = new CcStatsIndicator(CcConstants.STRINGS.statsCards(),
            limit > 0 ? Integer.valueOf(limit) : null, false);
        hp.setHorizontalAlignment(ALIGN_RIGHT);
        hp.add(iCards);
        add(hp);

        CcEventBus.INSTANCE.addHandler(CcStateEvent.TYPE, new CcStateHandler() {
            @Override
            public void onStateChanged(final CcStateEvent pEvent)
            {
                CcStatistics.this.onStateChanged(pEvent);
            }
        });
        CcEventBus.INSTANCE.addHandler(CcAllStatesEvent.TYPE, new CcAllStatesHandler() {
            @Override
            public void onAllStatesChanged(final CcAllStatesEvent pEvent)
            {
                CcStatistics.this.onAllStatesChanged(pEvent);
            }
        });
        CcEventBus.INSTANCE.addHandler(CcFundsEvent.TYPE, new CcFundsHandler() {
            @Override
            public void onFundsChanged(final CcFundsEvent pEvent)
            {
                iFunds.setValueAndPlan(pEvent.getFunds(), iFunds.getPlan());
                iFunds.setEnabled(pEvent.isFundsEnabled());
                if (pEvent.isFundsEnabled()) {
                    iFunds.setProblem(iFunds.getPlan() > pEvent.getFunds());
                }
            }
        });
    }


    private void onStateChanged(final CcStateEvent pEvent)
    {
        CcCardController cardCtrl = null;
        if (pEvent.getSource() instanceof CcCardController) {
            cardCtrl = (CcCardController) pEvent.getSource();
        }
        if (cardCtrl != null) {
            final CcCardCurrent[] cards = pEvent.getSituation().getCardsCurrent();
            final CcCardCurrent card = cards[pEvent.getRowIdx()];
            final CcCardConfig config = card.getConfig();
            if (cardCtrl.isRevising()) {
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
        if (!(pEvent.getSource() instanceof CcCardController)) {
            // do nothing if this event didn't originate with the card controller
            return;
        }
        final CcCardController cardCtrl = (CcCardController) pEvent.getSource();

        int points = 0;
        int pointsPlanned = 0;
        int cards = 0;
        int cardsPlanned = 0;
        int expensesPlanned = 0;
        Set<CcGroup> grps = new HashSet<CcGroup>();
        Set<CcGroup> grpsPlanned = new HashSet<CcGroup>();
        for (CcCardCurrent card : cardCtrl.getCardsCurrent())
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
}
