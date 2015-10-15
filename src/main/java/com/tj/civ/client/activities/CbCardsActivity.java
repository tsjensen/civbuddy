/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * File: $Id$
 * Date created: 2011-03-22
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import com.tj.civ.client.CbCardStateManager;
import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.event.CbAllStatesEvent;
import com.tj.civ.client.event.CbStateEvent;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.jso.CbFundsJSO;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbDetailPlace;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CbPlayersPlace;
import com.tj.civ.client.views.CbCardsViewIF;
import com.tj.civ.client.widgets.CbMessageBox;
import com.tj.civ.client.widgets.CbMessageBox.CbResultCallbackIF;


/**
 * The presenter of the 'Cards' view.
 *
 * @author Thomas Jensen
 */
public class CbCardsActivity
    extends CbAbstractActivity
    implements CbCardsViewIF.CbPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbCardsActivity.class);

    /** sum of current costs of the currently planned cards */
    private int iPlannedInvestment = 0;

    /** the sum of the nominal costs of all planned cards, plus the sum of the
     *  nominal costs of all cards already owned */
    private int iNominalSumInclPlan = 0;

    /** the state manager passed upon construction */
    private CbCardStateManager iStateCtrl;

    /** number of cards in {@link CbState#Planned} */
    private int iNumCardsPlanned = 0;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbCardsActivity(final CbCardsPlace pPlace, final CbClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);

        LOG.enter(CbLogAdapter.CONSTRUCTOR);
        if (LOG.isDetailEnabled()) {
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "pPlace.getSituationKey() = " //$NON-NLS-1$
                + (pPlace != null ? CbToString.obj2str(pPlace.getSituationKey()) : null));
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "CbGlobal.isSet() = " + CbGlobal.isGameSet()); //$NON-NLS-1$
        }

        if (pPlace != null) {
            CbStorage.ensureGameLoadedWithSitKey(pPlace.getSituationKey(),
                pClientFactory.getEventBus());
        }
        if (!CbGlobal.isSituationSet()) {
            Window.alert(CbConstants.STRINGS.noGame());
        }

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget,
        final com.google.gwt.event.shared.EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$
        if (!CbGlobal.isSituationSet()) {
            // no situation loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }
        final CbSituation sit = CbGlobal.getCurrentSituation();

        // Register this presenter (which is always new) with the (recycled) view
        CbCardsViewIF view = getView();
        view.setPresenter(this);
        pContainerWidget.setWidget(view.asWidget());

        // Create a new card state manager for this activity
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        iStateCtrl = new CbCardStateManager(this);

        // Update funds display
        view.updateFunds(fundsJso.getTotalFunds(), fundsJso.isEnabled());
        if (LOG.isDebugEnabled()) {
            LOG.debug("start", //$NON-NLS-1$
                "funds: " + fundsJso.isEnabled() //$NON-NLS-1$
                + "/" + fundsJso.getTotalFunds()); //$NON-NLS-1$
        }

        // If necessary, rebuild the entire grid to match a new game variant
        if (view.getLastVariantId() == null
            || !view.getLastVariantId().equals(sit.getVariant().getVariantId()))
        {
            view.initializeGridContents(CbGlobal.getCardsCurrent(),
                sit.getVariant().getVariantId());
        }

        // Recalculate state and stats display
        recalcInternalSums();
        iStateCtrl.recalcAll(true);
        setDesperate(iStateCtrl.isDesperate());
        int cardsLimit = sit.getVariant().getNumCardsLimit();
        view.updateStats(sit.getPlayer().getWinningTotal(),
            cardsLimit > 0 ? Integer.valueOf(cardsLimit) : null);

        // Adjust browser title
        CbUtil.setBrowserTitle(sit.getPlayer().getName() + " - " //$NON-NLS-1$
            + sit.getGame().getName());
        view.setTitleHeading(sit.getPlayer().getName());

        // Check if plans can be funded and show a warning if not
        checkFundsSufficient(fundsJso.isEnabled(), fundsJso.getTotalFunds());

        LOG.exit("start"); //$NON-NLS-1$
    }



    private void checkFundsSufficient(final boolean pEnabled, final int pTotalFunds)
    {
        if (pEnabled && pTotalFunds < iPlannedInvestment)
        {
            if (LOG.isDebugEnabled()) {
                // FIXME pTotalFunds wrongly reported as zero when just turned on
                LOG.debug("checkFundsSufficient", //$NON-NLS-1$
                    "Funds of " + pTotalFunds //$NON-NLS-1$
                    + " insufficient for plans of " + iPlannedInvestment //$NON-NLS-1$
                    + ". Showing alert."); //$NON-NLS-1$
            }
            CbMessageBox.showAsyncMessage(CbConstants.STRINGS.notice(),
                SafeHtmlUtils.fromString(CbConstants.STRINGS.noFunds()), null);
        }
    }



    private void setDesperate(final boolean pIsDesperate)
    {
        final CbSituation sit = CbGlobal.getCurrentSituation();
        sit.setDesperate(pIsDesperate);
        iStateCtrl.setDesperate(pIsDesperate);
        getView().setDesperate(pIsDesperate);
    }



    /**
     * Recalculate the internal sums kept by this presenter. This is necessary when
     * the activity is started.
     */
    private void recalcInternalSums()
    {
        int nominalSumInclPlan = 0;
        int numCardsPlanned = 0;
        final CbCardCurrent[] cardsCurrent = CbGlobal.getCardsCurrent();

        // reset current cost, and calculate numCardsPlanned and nominalSumInclPlan
        for (CbCardCurrent card : cardsCurrent) {
            CbState state = card.getState();
            CbCardConfig config = card.getConfig();

            card.setCostCurrent(config.getCostNominal());
            if (state.isAffectingCredit()) {
                if (state == CbState.Planned) {
                    numCardsPlanned++;
                }
                nominalSumInclPlan += config.getCostNominal();
            }
        }

        // calculate current costs based on owned cards
        for (CbCardCurrent card : cardsCurrent) {
            if (card.getState() == CbState.Owned) {
                int[] creditFrom = card.getConfig().getCreditGiven();
                for (int i = 0; i < creditFrom.length; i++) {
                    if (creditFrom[i] > 0) {
                        cardsCurrent[i].setCostCurrent(
                            cardsCurrent[i].getCostCurrent() - creditFrom[i]);
                    }
                }
            }
        }

        // update current costs in the view
        final CbCardsViewIF view = getView();
        for (int i = 0; i < cardsCurrent.length; i++) {
            view.setCostDisplay(i, cardsCurrent[i].getCostCurrent());
        }

        // calculate plannedInvestment
        int plannedInvestment = 0;
        for (CbCardCurrent card : cardsCurrent) {
            if (card.getState() == CbState.Planned) {
                plannedInvestment += card.getCostCurrent();
            }
        }

        iNominalSumInclPlan = nominalSumInclPlan;
        iNumCardsPlanned = numCardsPlanned;
        iPlannedInvestment = plannedInvestment;
    }



    @Override
    public CbPlayersPlace getPlayersPlace()
    {
        return new CbPlayersPlace(CbGlobal.getGame().getPersistenceKey());
    }



    @Override
    public void enterReviseMode()
    {
        for (CbCardCurrent card : CbGlobal.getCardsCurrent())
        {
            final CbState previous = card.getState();
            if (previous != CbState.Owned && previous != CbState.Absent) {
                setState(card, CbState.Absent, null, 0);
            }
        }
        iNominalSumInclPlan -= iPlannedInvestment;
        iPlannedInvestment = 0;
        iNumCardsPlanned = 0;

        // persist state change
        CbStorage.saveSituation();

        getEventBus().fireEventFromSource(new CbAllStatesEvent(), this);
    }



    private void updateCreditBars(final CbCardsViewIF pView, final CbCardConfig pCard)
    {
        // FIXME Zustand möglich, wo generell keine grünen Elemente mehr erscheinen
        int[] creditGiven = pCard.getCreditGiven();
        for (int row = 0; row < creditGiven.length; row++) {
            if (creditGiven[row] > 0) {
                pView.updateCreditBar(row, pCard.getMyIdx());
            }
        }
    }



    @Override
    public void leaveReviseMode()
    {
        iStateCtrl.recalcAll(false);
        getEventBus().fireEventFromSource(new CbAllStatesEvent(), this);
    }



    /**
     * Enable/disable commit button.
     * @param pCard if an individual card was changed, that card; else if it's a
     *              global update, just <code>null</code>
     */
    private void updateCommitButton(final CbCardCurrent pCard)
    {
        CbCardsViewIF view = getView();
        if (view.isCommitButtonEnabled()) {
            if (!hasAnyPlans()) {
                view.setCommitButtonEnabled(false);
            }
        } else {
            if (pCard != null) {
                if (pCard.getState() == CbState.Planned) {
                    view.setCommitButtonEnabled(true);
                }
            } else if (hasAnyPlans()) {
                view.setCommitButtonEnabled(true);
            }
        }
    }



    @Override
    public boolean hasAnyPlans()
    {
        return iNumCardsPlanned > 0;
    }



    @Override
    public void commit()
    {
        int nominalSum = 0;
        for (CbCardCurrent card : CbGlobal.getCardsCurrent())
        {
            if (card.getState() == CbState.Planned) {
                setState(card, CbState.Owned, null, 0);
                updateCostIndicators(getView(), card);
            }
            if (card.getState() == CbState.Owned) {
                nominalSum += card.getConfig().getCostNominal();
            }
        }
        iPlannedInvestment = 0;
        iNumCardsPlanned = 0;
        iNominalSumInclPlan = nominalSum;

        iStateCtrl.recalcAll(false);
        getView().setCommitButtonEnabled(false);

        // persist state change
        CbStorage.saveSituation();

        getEventBus().fireEventFromSource(new CbAllStatesEvent(), this);
    }



    @Override
    public void onMoreClicked(final int pRowIdx)
    {
        goTo(new CbDetailPlace(CbGlobal.getCurrentSituation().getPersistenceKey(), pRowIdx));
    }



    private SafeHtml getPlanMsg(final int pRowIdx, final CbState pState)
    {
        SafeHtml result = null;
        final CbCardsViewIF view = getView();
        if (pState == CbState.DiscouragedBuy) {
            result = SafeHtmlUtils.fromSafeConstant(CbConstants.STRINGS.viewCardsAskDiscouraged()
                + "<br/>" + view.getStateReason(pRowIdx)); //$NON-NLS-1$
        } else if (pState == CbState.Unaffordable) {
            result = SafeHtmlUtils.fromSafeConstant(CbConstants.STRINGS.viewCardsAskUnaffordable());
        } else {
            result = SafeHtmlUtils.fromString("(programming error)"); //$NON-NLS-1$
        }
        return result;
    }



    @Override
    public void onStateClicked(final int pRowIdx)
    {
        final CbCardsViewIF view = getView();
        final CbSituation sit = CbGlobal.getGame().getCurrentSituation();
        final CbCardCurrent card = CbGlobal.getCardsCurrent()[pRowIdx];
        final CbState oldState = card.getState();

        if (view.isRevising()) {
            if (oldState != CbState.Owned) {
                sit.setCardState(card.getMyIdx(), CbState.Owned, 0);
                iNominalSumInclPlan += card.getConfig().getCostNominal();
                // TODO warn if card limit would be exceeded
            } else {
                sit.setCardState(card.getMyIdx(), CbState.Absent, 0);
                iNominalSumInclPlan -= card.getConfig().getCostNominal();
            }
            handleGridClick2(card);
        }
        else {
            // TODO hard limit at 1000 points in some variants
            if (oldState != CbState.Owned && oldState != CbState.PrereqFailed) {
                if (oldState == CbState.Unaffordable || oldState == CbState.DiscouragedBuy) {
                    CbMessageBox.showOkCancel(CbConstants.STRINGS.askAreYouSure(),
                        getPlanMsg(pRowIdx, oldState), view.getWidget(),
                        new CbResultCallbackIF() {
                            @Override
                            public void onResultAvailable(final boolean pOkPressed)
                            {
                                if (pOkPressed) {
                                    setDesperate(true);
                                    handleGridClick1(card, oldState);
                                }
                            }
                        }
                    );
                }
                else {
                    handleGridClick1(card, oldState);
                }
            }
        }
    }



    /**
     * This part of the handleGridClick() flow is reached only when a card is
     * clicked in planning mode and the user has confirmed the decision (if asked).
     * @param pCard the card that was clicked
     * @param pOldState the old state that the card was in
     */
    private void handleGridClick1(final CbCardCurrent pCard, final CbState pOldState)
    {
        final CbSituation sit = CbGlobal.getGame().getCurrentSituation();
        if (pOldState != CbState.Planned) {
            sit.setCardState(pCard.getMyIdx(), CbState.Planned, 0);
            iPlannedInvestment += pCard.getCostCurrent();
            iNominalSumInclPlan += pCard.getCostCurrent();
            iNumCardsPlanned++;
        } else {
            sit.setCardState(pCard.getMyIdx(), CbState.Absent, 0);
            iPlannedInvestment -= pCard.getCostCurrent();
            iNominalSumInclPlan -= pCard.getCostCurrent();
            iNumCardsPlanned--;
        }
        handleGridClick2(pCard);
    }



    private void handleGridClick2(final CbCardCurrent pCard)
    {
        // Update card state indicator
        final CbCardsViewIF view = getView();
        view.setState(pCard.getMyIdx(), pCard.getState(), null);

        // FIXME wenn eine prereq mit ihrer karte zusammen geplant sind, kann man die
        //       prereq wieder abwählen, der plan-marker auf der karte bleibt aber
        //       bestehen (sollte aber mit entfernt werden).
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                // TODO konsolidieren mit setState() (achtung state widget)
                updateCreditBars(view, pCard.getConfig());

                // deactivate desperation mode if applicable
                if (iStateCtrl.isDesperate() && pCard.getState() == CbState.Absent) {
                    // FIXME geht manchmal auf rot, nachdem Funds ausgeschaltet wurde
                    if (!iStateCtrl.stillDesperate()) {
                        setDesperate(false);
                    }
                }

                if (view.isRevising()) {
                    updateCostIndicators(view, pCard);
                } else {
                    iStateCtrl.recalcAll(false);
                }

                if (iNumCardsPlanned == 1) {
                    view.setCommitButtonEnabled(true);
                } else if (iNumCardsPlanned == 0) {
                    view.setCommitButtonEnabled(false);
                }

                // persist state change
                CbStorage.saveSituation();

                // fire event
                getEventBus().fireEventFromSource(
                    new CbStateEvent(pCard.getMyIdx(), pCard.getState()), CbCardsActivity.this);
            }
        });
    }



    private void updateCostIndicators(final CbCardsViewIF pView, final CbCardCurrent pCard)
    {
        final CbState state = pCard.getState();
        int[] creditGiven = pCard.getConfig().getCreditGiven();
        for (int rowIdx = 0; rowIdx < creditGiven.length; rowIdx++)
        {
            if (creditGiven[rowIdx] > 0) {
                final CbCardCurrent card = CbGlobal.getCardsCurrent()[rowIdx];
                if (state == CbState.Owned) {
                    card.setCostCurrent(card.getCostCurrent() - creditGiven[rowIdx]);
                } else {
                    card.setCostCurrent(card.getCostCurrent() + creditGiven[rowIdx]);
                }
                pView.setCostDisplay(rowIdx, card.getCostCurrent());
            }
        }
    }



    @Override
    public CbFundsPlace getFundsPlace()
    {
        return new CbFundsPlace(CbGlobal.getGame().getCurrentSituation().getPersistenceKey());
    }



    @Override
    public int getPlannedInvestment()
    {
        return iPlannedInvestment;
    }



    @Override
    public void setState(final CbCardCurrent pCard, final CbState pNewState,
        final String pStateReason, final int pPointsDelta)
    {
        final CbCardsViewIF view = getView();
        final CbState oldState = pCard.getState();

        CbGlobal.getGame().getCurrentSituation().setCardState(
            pCard.getMyIdx(), pNewState, pPointsDelta);
        view.setState(pCard.getMyIdx(), pNewState, pStateReason);
        updateCreditBars(view, pCard.getConfig());
        updateCommitButton(pCard);

        if (pNewState == CbState.Planned) {
            iNumCardsPlanned++;
        } else if (oldState == CbState.Planned) {
            iNumCardsPlanned--;
        }

        // do not persist state here, this is done elsewhere
    }



    @Override
    public int getNumCardsAffectingCredit()
    {
        int result = 0;
        for (CbCardCurrent card : CbGlobal.getCardsCurrent()) {
            if (card.getState().isAffectingCredit()) {
                result++;
            }
        }
        return result;
    }



    @Override
    public int getNominalSumInclPlan()
    {
        return iNominalSumInclPlan;
    }



    @Override
    public CbCardsViewIF getView()
    {
        return getClientFactory().getCardsView();
    }



    @Override
    public EventBus getEventBus()
    {
        return getClientFactory().getEventBus();
    }
}
