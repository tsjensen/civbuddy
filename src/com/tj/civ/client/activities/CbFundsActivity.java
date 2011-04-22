/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 31.03.2011
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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.event.CbCommSpinnerPayload;
import com.tj.civ.client.event.CbFundsEvent;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.jso.CcCommodityConfigJSO;
import com.tj.civ.client.model.jso.CcFundsJSO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.views.CbFundsViewIF;


/**
 * The presenter of the 'Funds' view.
 *
 * @author Thomas Jensen
 */
public class CbFundsActivity
    extends CbAbstractActivity
    implements CbFundsViewIF.CbPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbFundsActivity.class);

    /** the selected situation */
    private CcSituation iSituation;

    /** reference to the funds object of the situation */
    private CcFundsJSO iFundsJso;

    /** the current number of commodity cards held by the player */
    private int iNumberOfCommodityCards = 0;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbFundsActivity(final CbFundsPlace pPlace, final CbClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);
        LOG.enter(CbLogAdapter.CONSTRUCTOR);
        iSituation = null;
        iFundsJso = null;

        if (pPlace != null && pPlace.getSituationKey() != null)
        {
            CcSituation sit = null;
            if (CbGlobal.isSet()) {
                sit = CbGlobal.getGame().getSituationByKey(pPlace.getSituationKey());
            }
            if (sit != null) {
                // it's the game we already have
                LOG.debug(CbLogAdapter.CONSTRUCTOR,
                    "Using globally present game"); //$NON-NLS-1$
                iSituation = sit;
                iFundsJso = sit.getJso().getFunds();
                iSituation.getGame().setCurrentSituation(sit);
            }
            else {
                // it's a different game which we must load first
                LOG.debug(CbLogAdapter.CONSTRUCTOR,
                    "Loading game from DOM storage"); //$NON-NLS-1$
                try {
                    CcGame game = CbStorage.loadGameForSituation(pPlace.getSituationKey());
                    if (game != null) {
                        iSituation = game.getSituationByKey(pPlace.getSituationKey());
                        if (iSituation != null) {
                            game.setCurrentSituation(iSituation);
                            iFundsJso = iSituation.getJso().getFunds();
                            CbGlobal.setGame(game);
                        }
                    }
                }
                catch (Throwable t) {
                    Window.alert(CbConstants.STRINGS.error() + ' ' + t.getMessage());
                }
            }
        }
        if (iFundsJso == null) {
            Window.alert(CbConstants.STRINGS.noGame());
        }
        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void goTo(final CbAbstractPlace pPlace)
    {
        if (iFundsJso != null) {
            getClientFactory().getEventBus().fireEventFromSource(
                new CbFundsEvent(iFundsJso.getTotalFunds(), iFundsJso.isEnabled()), this);
        }
        super.goTo(pPlace);
    }



    @Override
    public void start(final AcceptsOneWidget pContainer, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$
        if (iFundsJso == null) {
            // no situation loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }

        CbFundsViewIF view = getView();
        view.setPresenter(this);
        view.initialize(iSituation.getVariant().getCommodities(), iFundsJso);
        iNumberOfCommodityCards = countCommodities();
        view.setNumCommodities(iNumberOfCommodityCards);
        recalcTotalFunds();

        pContainer.setWidget(view.asWidget());
        LOG.exit("start"); //$NON-NLS-1$
    }



    /**
     * Counts the currently held commodity cards.
     * @return their number
     */
    private int countCommodities()
    {
        int result = 0;
        final int[] commodityCounts = iFundsJso.getCommodityCounts();
        if (commodityCounts != null) {
            for (int c : commodityCounts) {
                if (c > 0) {
                    result += c;
                }
            }
        }
        return result;
    }



    private CbFundsViewIF getView()
    {
        return getClientFactory().getFundsView();
    }



    @Override
    public void reset()
    {
        iFundsJso.setBonus(0);
        iFundsJso.setTreasury(0);
        iFundsJso.setTotalFunds(0);
        final int commCount = iFundsJso.getCommodityCounts().length;
        for (int i = 0; i < commCount; i++) {
            iFundsJso.setCommodityCount(i, 0);
        }
        CbStorage.saveSituation(iSituation);
    }



    private void recalcTotalFunds()
    {
        int sum = 0;
        sum += iFundsJso.getTreasury();
        sum += iFundsJso.getBonus();
        
        CcCommodityConfigJSO[] commodities = iSituation.getVariant().getCommodities();
        for (int i = 0; i < commodities.length; i++)
        {
            int n = iFundsJso.getCommodityCount(i);
            sum += n * n * commodities[i].getBase();
        }

        setTotalFunds(sum);
    }



    private boolean isIntBetween(final Integer pNewValue, final int pMin, final int pMax)
    {
        boolean result = false;
        if (pNewValue != null) {
            int newValue = pNewValue.intValue();
            if (newValue >= pMin && newValue <= pMax) {
                result = true;
            }
        }
        return result;
    }



    @Override
    public void onTotalFundsBoxChanged(final Integer pNewValue)
    {
        if (isIntBetween(pNewValue, 0, CcFundsJSO.MAX_TOTAL_FUNDS)) {
            int newValue = pNewValue.intValue();
            setTotalFunds(newValue);
            CbStorage.saveSituation(iSituation);
        }
        else {
            getView().setTotalFundsBoxOnly(iFundsJso.getTotalFunds());
        }
    }



    private void setTotalFunds(final int pNewValue)
    {
        iFundsJso.setTotalFunds(pNewValue);
        getView().setTotalFunds(pNewValue);
    }



    @Override
    public void onSpinnerChanged(final CbCommSpinnerPayload pValue)
    {
        setTotalFunds(iFundsJso.getTotalFunds() + pValue.getDeltaPoints());
        iNumberOfCommodityCards += pValue.getDeltaNumber();
        getView().setNumCommodities(iNumberOfCommodityCards);
        int individualCount = iFundsJso.getCommodityCount(pValue.getCommIdx())
            + pValue.getDeltaNumber();
        iFundsJso.setCommodityCount(pValue.getCommIdx(), individualCount);
        CbStorage.saveSituation(iSituation);
    }



    @Override
    public void onBonusChanged(final Integer pNewValue)
    {
        if (isIntBetween(pNewValue, 0, CcFundsJSO.MAX_BONUS)) {
            int newValue = pNewValue.intValue();
            setTotalFunds(iFundsJso.getTotalFunds() + newValue - iFundsJso.getBonus());
            iFundsJso.setBonus(newValue);
            CbStorage.saveSituation(iSituation);
        }
        else {
            getView().setBonusBoxOnly(iFundsJso.getBonus());
        }
    }



    @Override
    public void onEnableToggled(final boolean pEnabled)
    {
        iFundsJso.setEnabled(pEnabled);
        getView().setEnabled(pEnabled);
        CbStorage.saveSituation(iSituation);
    }



    @Override
    public void onDetailToggled(final boolean pDetailed)
    {
        iFundsJso.setDetailed(pDetailed);
        getView().setDetailTracking(pDetailed);
        if (pDetailed) {
            recalcTotalFunds();
        }
        CbStorage.saveSituation(iSituation);
    }



    @Override
    public void goBack()
    {
        goTo(new CbCardsPlace(iSituation.getPersistenceKey()));
    }
}
