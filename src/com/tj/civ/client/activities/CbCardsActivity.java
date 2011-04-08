/*
 * CivCounsel - A Civilization Tactics Guide
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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CcCardStateManager;
import com.tj.civ.client.CcClientFactoryIF;
import com.tj.civ.client.common.CcStorage;
import com.tj.civ.client.event.CcAllStatesEvent;
import com.tj.civ.client.event.CcStateEvent;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.model.CcVariantConfig;
import com.tj.civ.client.model.jso.CcFundsJSO;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CcCardsPlace;
import com.tj.civ.client.places.CcPlayersPlace;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.views.CbCardsViewIF;
import com.tj.civ.client.widgets.CcMessageBox;
import com.tj.civ.client.widgets.CcMessageBox.CcResultCallbackIF;


/**
 * The presenter of the 'Cards' view.
 *
 * @author Thomas Jensen
 */
public class CbCardsActivity
    extends AbstractActivity
    implements CbCardsViewIF.CcPresenterIF
{
    /** logger for this class */
    private static final Logger LOG = Logger.getLogger(CbCardsActivity.class.getName());

    /** our client factory */
    private CcClientFactoryIF iClientFactory;

    /** the selected situation */
    private CcSituation iSituation;

    /** reference to the array of current card states */
    private CcCardCurrent[] iCardsCurrent;

    /** sum of current costs of the currently planned cards */
    private int iPlannedInvestment = 0;

    /** the sum of the nominal costs of all planned cards, plus the sum of the
     *  nominal costs of all cards already owned */
    private int iNominalSumInclPlan = 0;

    /** the state manager passed upon construction */
    private CcCardStateManager iStateCtrl;

    /** number of cards in {@link CcState#Planned} */
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
    public CbCardsActivity(final CcCardsPlace pPlace, final CcClientFactoryIF pClientFactory)
    {
        super();
        iClientFactory = pClientFactory;
        iSituation = null;
        iCardsCurrent = null;
        iNeedsViewInit = true;
        if (pPlace != null)
        {
            CcSituation givenSit = pPlace.getSituation();
            if (givenSit != null) {
                // TODO bullshit, nur wenn sich die variante geändert hat!
                //iNeedsViewInit = givenSit != iSituation;
                iNeedsViewInit = true;
                iSituation = givenSit;
                iCardsCurrent = givenSit.getCardsCurrent();
            }
            else if (pPlace.getSituationKey() != null) {
                try {
                    CcVariantConfig variant = CcStorage.loadVariantForSituation(
                        pPlace.getSituationKey());
                    iSituation = CcStorage.loadSituation(pPlace.getSituationKey(), variant);
                    if (iSituation != null) {
                        iCardsCurrent = iSituation.getCardsCurrent();
                    }
                }
                catch (Throwable t) {
                    Window.alert(CcConstants.STRINGS.error() + ' ' + t.getMessage());
                }
            }
        }
        if (iCardsCurrent == null) {
            Window.alert(CcConstants.STRINGS.noGame());
        }
    }



    @Override
    public void goTo(final Place pPlace)
    {
        iClientFactory.getPlaceController().goTo(pPlace);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        if (iCardsCurrent == null) {
            // no situation loaded, so redirect to game selection
            goTo(CcConstants.DEFAULT_PLACE);
            return;
        }
        iNumCardsPlanned = recalcNumberOfPlannedCards();

        CbCardsViewIF view = getView();
        view.setPresenter(this);
        if (iNeedsViewInit) {
            view.initializeGridContents(iCardsCurrent);
        }

        CcFundsJSO fundsJso = iSituation.getJso().getFunds();
        iStateCtrl = new CcCardStateManager(this, iSituation.getVariant(),
            iSituation.getPlayer().getWinningTotal(), fundsJso.isEnabled(),
            fundsJso.getTotalFunds());
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("start() - funds: " + fundsJso.isEnabled() //$NON-NLS-1$
                + "/" + fundsJso.getTotalFunds()); //$NON-NLS-1$
        }
        iStateCtrl.recalcAll();

        pContainerWidget.setWidget(view.asWidget());

        if (fundsJso.isEnabled() && iSituation.getFunds() < iPlannedInvestment)
        {
            CcMessageBox.showAsyncMessage(CcConstants.STRINGS.notice(),
                SafeHtmlUtils.fromString(CcConstants.STRINGS.noFunds()), null);
        }

        view.updateFunds(fundsJso.getTotalFunds(), fundsJso.isEnabled());
        iClientFactory.getEventBus().fireEvent(new CcAllStatesEvent());
    }



    /**
     * Determine the number of cards in the state 'Planned' by counting. 
     * @return just that
     */
    private int recalcNumberOfPlannedCards()
    {
        int result = 0;
        for (CcCardCurrent card : iCardsCurrent) {
            if (card.getState() == CcState.Planned) {
                result++;
            }
        }
        return result;
    }



    @Override
    public CcPlayersPlace getPlayersPlace()
    {
        return new CcPlayersPlace(iSituation.getGame().getPersistenceKey());
    }



    @Override
    public void enterReviseMode()
    {
        for (CcCardCurrent card : iCardsCurrent)
        {
            final CcState previous = card.getState();
            if (previous != CcState.Owned) {
                setState(card, CcState.Absent, null);
            }
        }
        iPlannedInvestment = 0;
        iNumCardsPlanned = 0;
        iClientFactory.getEventBus().fireEventFromSource(new CcAllStatesEvent(), this);
    }



    private void updateCreditBars(final CbCardsViewIF pView, final CcCardConfig pCard)
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
        iStateCtrl.recalcAll();
        iClientFactory.getEventBus().fireEventFromSource(new CcAllStatesEvent(), this);
    }



    /**
     * Enable/disable commit button.
     * @param pCard if an individual card was changed, that card; else if it's a
     *              global update, just <code>null</code>
     */
    private void updateCommitButton(final CcCardCurrent pCard)
    {
        CbCardsViewIF view = getView();
        if (view.isCommitButtonEnabled()) {
            if (!hasAnyPlans()) {
                view.setCommitButtonEnabled(false);
            }
        } else {
            if (pCard != null) {
                if (pCard.getState() == CcState.Planned) {
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
        for (CcCardCurrent card : iCardsCurrent)
        {
            if (card.getState() == CcState.Planned) {
                setState(card, CcState.Owned, null);
                updateCostIndicators(getView(), card);
            }
            if (card.getState() == CcState.Owned) {
                nominalSum += card.getConfig().getCostNominal();
            }
        }
        iPlannedInvestment = 0;
        iNumCardsPlanned = 0;
        iNominalSumInclPlan = nominalSum;

        iStateCtrl.recalcAll();
        getView().setCommitButtonEnabled(false);
        iClientFactory.getEventBus().fireEventFromSource(new CcAllStatesEvent(), this);
    }



    @Override
    public void onMoreClicked(final int pRowIdx)
    {
        // TODO Auto-generated method stub
    }



    private SafeHtml getPlanMsg(final int pRowIdx, final CcState pState)
    {
        SafeHtml result = null;
        final CbCardsViewIF view = getView();
        if (pState == CcState.DiscouragedBuy) {
            result = SafeHtmlUtils.fromSafeConstant(CcConstants.STRINGS.askDiscouraged()
                + "<br/>" + view.getStateReason(pRowIdx)); //$NON-NLS-1$
        } else if (pState == CcState.Unaffordable) {
            result = SafeHtmlUtils.fromSafeConstant(CcConstants.STRINGS.askUnaffordable());
        } else {
            result = SafeHtmlUtils.fromString("(programming error)"); //$NON-NLS-1$
        }
        return result;
    }



    @Override
    public void onStateClicked(final int pRowIdx)
    {
        final CbCardsViewIF view = getView();
        final CcCardCurrent card = iCardsCurrent[pRowIdx];
        final CcState oldState = card.getState();

        if (view.isRevising()) {
            if (oldState != CcState.Owned) {
                card.setState(CcState.Owned);
                iNominalSumInclPlan += card.getConfig().getCostNominal();
            } else {
                card.setState(CcState.Absent);
                iNominalSumInclPlan -= card.getConfig().getCostNominal();
            }
            handleGridClick2(card);
        }
        else {
            if (oldState != CcState.Owned && oldState != CcState.PrereqFailed) {
                if (oldState == CcState.Unaffordable || oldState == CcState.DiscouragedBuy) {
                    // FIXME: This click always results in a stack overflow HERE
                    CcMessageBox.showOkCancel(CcConstants.STRINGS.askAreYouSure(),
                        getPlanMsg(pRowIdx, oldState), view.getWidget(),
                        new CcResultCallbackIF() {
                            @Override
                            public void onResultAvailable(final boolean pOkPressed)
                            {
                                if (pOkPressed) {
                                    // TODO: Wenn DiscouragedBuy einmal trotzdem durchgeführt
                                    //       wird, die Funktion deaktivieren, da sonst
                                    //       immer alles rot wäre
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
    private void handleGridClick1(final CcCardCurrent pCard, final CcState pOldState)
    {
        if (pOldState != CcState.Planned) {
            pCard.setState(CcState.Planned);
            iPlannedInvestment += pCard.getCostCurrent();
            iNominalSumInclPlan += pCard.getCostCurrent();
            iNumCardsPlanned++;
        } else {
            pCard.setState(CcState.Absent);
            iPlannedInvestment -= pCard.getCostCurrent();
            iNominalSumInclPlan -= pCard.getCostCurrent();
            iNumCardsPlanned--;
        }
        handleGridClick2(pCard);
    }



    private void handleGridClick2(final CcCardCurrent pCard)
    {
        // Update card state indicator
        final CbCardsViewIF view = getView();
        view.setState(pCard.getMyIdx(), pCard.getState(), null);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                // TODO: konsolidieren mit setState() (achtung state widget)
                updateCreditBars(view, pCard.getConfig());
                if (view.isRevising()) {
                    updateCostIndicators(view, pCard);
                } else {
                    iStateCtrl.recalcAll();
                }
                if (iNumCardsPlanned == 1) {
                    view.setCommitButtonEnabled(true);
                } else if (iNumCardsPlanned == 0) {
                    view.setCommitButtonEnabled(false);
                }
                iClientFactory.getEventBus().fireEventFromSource(
                    new CcStateEvent(pCard.getMyIdx(), pCard.getState()), CbCardsActivity.this);
            }
        });
    }



    private void updateCostIndicators(final CbCardsViewIF pView, final CcCardCurrent pCard)
    {
        final CcState state = pCard.getState();
        int[] creditGiven = pCard.getConfig().getCreditGiven();
        for (int rowIdx = 0; rowIdx < creditGiven.length; rowIdx++)
        {
            if (creditGiven[rowIdx] > 0) {
                final CcCardCurrent card = iCardsCurrent[rowIdx];
                if (state == CcState.Owned) {
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
        return new CbFundsPlace(iSituation);
    }



    @Override
    public CcCardCurrent[] getCardsCurrent()
    {
        return iCardsCurrent;
    }



    @Override
    public int getPlannedInvestment()
    {
        return iPlannedInvestment;
    }



    @Override
    public void setState(final CcCardCurrent pCard, final CcState pNewState,
        final String pStateReason)
    {
        final CbCardsViewIF view = getView();
        final CcState oldState = pCard.getState();
        if (oldState != pNewState)
        {
            pCard.setState(pNewState);
            view.setState(pCard.getMyIdx(), pNewState, null);
            updateCreditBars(view, pCard.getConfig());
            updateCommitButton(pCard);

            if (pNewState == CcState.Planned) {
                iNumCardsPlanned++;
            } else if (oldState == CcState.Planned) {
                iNumCardsPlanned--;
            }
            // TODO persist state
        }
    }



    @Override
    public int getNumCardsAffectingCredit()
    {
        int result = 0;
        for (CcCardCurrent card : iCardsCurrent) {
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
        return iClientFactory.getCardsView();
    }



    @Override
    public EventBus getEventBus()
    {
        return iClientFactory.getEventBus();
    }
}
