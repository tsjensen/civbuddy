/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 22.03.2011
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
package com.tj.civ.client.activities;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbCardStateManager;
import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.event.CbAllStatesEvent;
import com.tj.civ.client.event.CbStateEvent;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbGame;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.jso.CbFundsJSO;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CbCardsPlace;
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

    /** the selected situation */
    private CbSituation iSituation;

    /** reference to the array of current card states */
    private CbCardCurrent[] iCardsCurrent;

    /** sum of current costs of the currently planned cards */
    private int iPlannedInvestment = 0;

    /** the sum of the nominal costs of all planned cards, plus the sum of the
     *  nominal costs of all cards already owned */
    private int iNominalSumInclPlan = 0;

    /** the state manager passed upon construction */
    private CbCardStateManager iStateCtrl;

    /** number of cards in {@link CbState#Planned} */
    private int iNumCardsPlanned = 0;

    /** Flag set by the constructor to tell the {@link #start} method whether it
     *  needs to perform a view initialization. This is not necessary if we just
     *  returned from a visit to the 'Funds' view. */
    private boolean iNeedsViewInit;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbCardsActivity(final CbCardsPlace pPlace, final CbClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);

        LOG.enter(CbLogAdapter.CONSTRUCTOR);
        iSituation = null;
        iCardsCurrent = null;
        iNeedsViewInit = true;

        if (LOG.isDetailEnabled()) {
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "pPlace.getSituationKey() = " //$NON-NLS-1$
                + (pPlace != null ? CbToString.obj2str(pPlace.getSituationKey()) : null));
            LOG.detail(CbLogAdapter.CONSTRUCTOR,
                "CbGlobal.isSet() = " + CbGlobal.isSet()); //$NON-NLS-1$
        }
        if (pPlace != null && pPlace.getSituationKey() != null)
        {
            CbSituation sit = null;
            if (CbGlobal.isSet()) {
                sit = CbGlobal.getGame().getSituationByKey(pPlace.getSituationKey());
            }
            if (sit != null) {
                // it's the game we already have
                LOG.debug(CbLogAdapter.CONSTRUCTOR,
                    "Using globally present game"); //$NON-NLS-1$
                iSituation = sit;
                iCardsCurrent = sit.getCardsCurrent();
                iSituation.getGame().setCurrentSituation(iSituation);
                iNeedsViewInit = !(CbGlobal.getPreviousPlace() instanceof CbFundsPlace);
            }
            else {
                // it's a different game which we must load first
                LOG.debug(CbLogAdapter.CONSTRUCTOR,
                    "Loading game from DOM storage"); //$NON-NLS-1$
                try {
                    CbGame game = CbStorage.loadGameForSituation(pPlace.getSituationKey());
                    if (game != null) {
                        iSituation = game.getSituationByKey(pPlace.getSituationKey());
                        if (iSituation != null) {
                            game.setCurrentSituation(iSituation);
                            iCardsCurrent = iSituation.getCardsCurrent();
                            CbGlobal.setGame(game);
                        }
                    }
                }
                catch (Throwable t) {
                    LOG.error(CbLogAdapter.CONSTRUCTOR + ": " //$NON-NLS-1$
                        + t.getClass().getName() + ": " + t.getMessage(), t); //$NON-NLS-1$
                    Window.alert(CbConstants.STRINGS.error() + ' ' + t.getMessage());
                }
            }
        }
        if (iCardsCurrent == null) {
            Window.alert(CbConstants.STRINGS.noGame());
        }
        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$
        if (iCardsCurrent == null) {
            // no situation loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }
        if (LOG.isDetailEnabled()) {
            LOG.detail("start", //$NON-NLS-1$
                "iNeedsViewInit=" + iNeedsViewInit); //$NON-NLS-1$
        }

        // Register this presenter (which is always new) with the (recycled) view
        CbCardsViewIF view = getView();
        view.setPresenter(this);
        pContainerWidget.setWidget(view.asWidget());

        // Create a new card state manager for this activity
        CbFundsJSO fundsJso = iSituation.getJso().getFunds();
        iStateCtrl = new CbCardStateManager(this, iSituation.getVariant(),
            iSituation.getPlayer().getWinningTotal(), fundsJso.isEnabled(),
            fundsJso.getTotalFunds());

        // Update funds display
        view.updateFunds(fundsJso.getTotalFunds(), fundsJso.isEnabled());
        if (LOG.isDebugEnabled()) {
            LOG.debug("start", //$NON-NLS-1$
                "funds: " + fundsJso.isEnabled() //$NON-NLS-1$
                + "/" + fundsJso.getTotalFunds()); //$NON-NLS-1$
        }

        // We came from the funds view, and this view is already ok, take a shortcut
        if (!iNeedsViewInit && view.getLastVariantId() != null
            && view.getLastVariantId().equals(iSituation.getVariant().getVariantId()))
        {
            LOG.detail("start", "shortcut"); //$NON-NLS-1$ //$NON-NLS-2$
            setDesperate(iSituation.isDesperate());
            iStateCtrl.recalcAll(false);
            checkFundsSufficient(fundsJso.isEnabled(), iSituation.getFunds());
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }
        
        // If necessary, rebuild the entire grid to match a new game variant
        if (view.getLastVariantId() == null
            || !view.getLastVariantId().equals(iSituation.getVariant().getVariantId()))
        {
            view.initializeGridContents(iCardsCurrent, iSituation.getVariant().getVariantId());
        }

        // Recalculate state and stats display
        recalcInternalSums();
        iStateCtrl.setDesperate(true); // temporary, state manager only
        iStateCtrl.recalcAll(true);  // to set all states except 'Discouraged'
        boolean desperate = iStateCtrl.stillDesperate();
        setDesperate(desperate);
        if (!desperate && iSituation.getVariant().hasNumCardsLimit()) {
            iStateCtrl.recalcAll(false);  // again to set 'Discouraged' states
        }
        int cardsLimit = iSituation.getVariant().getNumCardsLimit();
        view.updateStats(iSituation.getPlayer().getWinningTotal(),
            cardsLimit > 0 ? Integer.valueOf(cardsLimit) : null);

        // Adjust browser title
        CbUtil.setBrowserTitle(iSituation.getPlayer().getName() + " - " //$NON-NLS-1$
            + iSituation.getGame().getName());
        // TODO set view title to player name instead of 'Cards', limit length of name

        // Check if plans can be funded and show a warning if not
        checkFundsSufficient(fundsJso.isEnabled(), iSituation.getFunds());
        
        LOG.exit("start"); //$NON-NLS-1$
    }



    private void checkFundsSufficient(final boolean pEnabled, final int pTotalFunds)
    {
        if (pEnabled && pTotalFunds < iPlannedInvestment)
        {
            CbMessageBox.showAsyncMessage(CbConstants.STRINGS.notice(),
                SafeHtmlUtils.fromString(CbConstants.STRINGS.noFunds()), null);
        }
    }



    private void setDesperate(final boolean pIsDesperate)
    {
        iSituation.setDesperate(pIsDesperate);
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

        // reset current cost, and calculate numCardsPlanned and nominalSumInclPlan
        for (CbCardCurrent card : iCardsCurrent) {
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
        for (CbCardCurrent card : iCardsCurrent) {
            if (card.getState() == CbState.Owned) {
                int[] creditFrom = card.getConfig().getCreditGiven();
                for (int i = 0; i < creditFrom.length; i++) {
                    if (creditFrom[i] > 0) {
                        iCardsCurrent[i].setCostCurrent(
                            iCardsCurrent[i].getCostCurrent() - creditFrom[i]);
                    }
                }
            }
        }
        
        // update current costs in the view
        final CbCardsViewIF view = getView();
        for (int i = 0; i < iCardsCurrent.length; i++) {
            view.setCostDisplay(i, iCardsCurrent[i].getCostCurrent());
        }

        // calculate plannedInvestment
        int plannedInvestment = 0;
        for (CbCardCurrent card : iCardsCurrent) {
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
        return new CbPlayersPlace(iSituation.getGame().getPersistenceKey());
    }



    @Override
    public void enterReviseMode()
    {
        for (CbCardCurrent card : iCardsCurrent)
        {
            final CbState previous = card.getState();
            if (previous != CbState.Owned && previous != CbState.Absent) {
                setState(card, CbState.Absent, null);
            }
        }
        iNominalSumInclPlan -= iPlannedInvestment;
        iPlannedInvestment = 0;
        iNumCardsPlanned = 0;
        
        // persist state change
        CbStorage.saveSituation(iSituation);

        getEventBus().fireEventFromSource(new CbAllStatesEvent(), this);
    }



    private void updateCreditBars(final CbCardsViewIF pView, final CbCardConfig pCard)
    {
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
        for (CbCardCurrent card : iCardsCurrent)
        {
            if (card.getState() == CbState.Planned) {
                setState(card, CbState.Owned, null);
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
        CbStorage.saveSituation(iSituation);

        getEventBus().fireEventFromSource(new CbAllStatesEvent(), this);
    }



    @Override
    public void onMoreClicked(final int pRowIdx)
    {
        // TODO implement onMoreClicked()
    }



    private SafeHtml getPlanMsg(final int pRowIdx, final CbState pState)
    {
        SafeHtml result = null;
        final CbCardsViewIF view = getView();
        if (pState == CbState.DiscouragedBuy) {
            result = SafeHtmlUtils.fromSafeConstant(CbConstants.STRINGS.askDiscouraged()
                + "<br/>" + view.getStateReason(pRowIdx)); //$NON-NLS-1$
        } else if (pState == CbState.Unaffordable) {
            result = SafeHtmlUtils.fromSafeConstant(CbConstants.STRINGS.askUnaffordable());
        } else {
            result = SafeHtmlUtils.fromString("(programming error)"); //$NON-NLS-1$
        }
        return result;
    }



    @Override
    public void onStateClicked(final int pRowIdx)
    {
        final CbCardsViewIF view = getView();
        final CbCardCurrent card = iCardsCurrent[pRowIdx];
        final CbState oldState = card.getState();

        if (view.isRevising()) {
            if (oldState != CbState.Owned) {
                iSituation.setCardState(card.getMyIdx(), CbState.Owned);
                iNominalSumInclPlan += card.getConfig().getCostNominal();
                // TODO warn if card limit would be exceeded
            } else {
                iSituation.setCardState(card.getMyIdx(), CbState.Absent);
                iNominalSumInclPlan -= card.getConfig().getCostNominal();
            }
            handleGridClick2(card);
        }
        else {
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
            else {
                // TODO show "forbidden" sign as long as mouse key remains pressed
                //      SliderBar can already do this and has the cursor
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
        if (pOldState != CbState.Planned) {
            iSituation.setCardState(pCard.getMyIdx(), CbState.Planned);
            iPlannedInvestment += pCard.getCostCurrent();
            iNominalSumInclPlan += pCard.getCostCurrent();
            iNumCardsPlanned++;
        } else {
            iSituation.setCardState(pCard.getMyIdx(), CbState.Absent);
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

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                // TODO konsolidieren mit setState() (achtung state widget)
                updateCreditBars(view, pCard.getConfig());

                // deactivate desperation mode if applicable
                if (iStateCtrl.isDesperate() && pCard.getState() == CbState.Absent) {
                    // FIXME Ergün Pottery, Metal, D&P -> all discouraged!
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
                CbStorage.saveSituation(iSituation);
                
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
                final CbCardCurrent card = iCardsCurrent[rowIdx];
                if (state == CbState.Owned) {
                    card.setCostCurrent(Math.max(0, card.getCostCurrent() - creditGiven[rowIdx]));
                } else {
                    card.setCostCurrent(Math.max(0, card.getCostCurrent() + creditGiven[rowIdx]));
                }
                pView.setCostDisplay(rowIdx, card.getCostCurrent());
            }
        }
    }



    @Override
    public CbFundsPlace getFundsPlace()
    {
        return new CbFundsPlace(iSituation.getPersistenceKey());
    }



    @Override
    public CbCardCurrent[] getCardsCurrent()
    {
        return iCardsCurrent;
    }



    @Override
    public int getPlannedInvestment()
    {
        return iPlannedInvestment;
    }



    @Override
    public void setState(final CbCardCurrent pCard, final CbState pNewState,
        final String pStateReason)
    {
        final CbCardsViewIF view = getView();
        final CbState oldState = pCard.getState();

        iSituation.setCardState(pCard.getMyIdx(), pNewState);
        view.setState(pCard.getMyIdx(), pNewState, null);
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
        for (CbCardCurrent card : iCardsCurrent) {
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
