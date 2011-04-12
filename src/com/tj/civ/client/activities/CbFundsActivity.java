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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CcClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CcStorage;
import com.tj.civ.client.event.CcCommSpinnerPayload;
import com.tj.civ.client.event.CcFundsEvent;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.jso.CcCommodityConfigJSO;
import com.tj.civ.client.model.jso.CcFundsJSO;
import com.tj.civ.client.places.CbFundsPlace;
import com.tj.civ.client.places.CcCardsPlace;
import com.tj.civ.client.views.CbFundsViewIF;


/**
 * The presenter of the 'Funds' view.
 *
 * @author Thomas Jensen
 */
public class CbFundsActivity
    extends AbstractActivity
    implements CbFundsViewIF.CbPresenterIF
{
    /** our client factory */
    private CcClientFactoryIF iClientFactory;

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
    public CbFundsActivity(final CbFundsPlace pPlace, final CcClientFactoryIF pClientFactory)
    {
        super();
        iClientFactory = pClientFactory;
        iSituation = null;
        iFundsJso = null;
        if (pPlace != null)
        {
            if (pPlace.getSituation() != null) {
                iSituation = pPlace.getSituation();
                iFundsJso = pPlace.getSituation().getJso().getFunds();
                iSituation.getGame().setCurrentSituation(iSituation);
            }
            else if (pPlace.getSituationKey() != null) {
                try {
                    CcGame game = CcStorage.loadGameForSituation(pPlace.getSituationKey());
                    if (game != null) {
                        iSituation = game.getSituationByKey(pPlace.getSituationKey());
                        if (iSituation != null) {
                            game.setCurrentSituation(iSituation);
                            iFundsJso = iSituation.getJso().getFunds();
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
    }



    @Override
    public void goTo(final Place pPlace)
    {
        if (iFundsJso != null) {
            iClientFactory.getEventBus().fireEventFromSource(
                new CcFundsEvent(iFundsJso.getTotalFunds(), iFundsJso.isEnabled()), this);
        }
        iClientFactory.getPlaceController().goTo(pPlace);
    }



    @Override
    public void start(final AcceptsOneWidget pContainer, final EventBus pEventBus)
    {
        if (iFundsJso == null) {
            // no situation loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            return;
        }

        CbFundsViewIF view = getView();
        view.setPresenter(this);
        view.initialize(iSituation.getVariant().getCommodities(), iFundsJso);
        iNumberOfCommodityCards = countCommodities();
        view.setNumCommodities(iNumberOfCommodityCards);
        recalcTotalFunds();

        pContainer.setWidget(view.asWidget());
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
        return iClientFactory.getFundsView();
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
        CcStorage.saveSituation(iSituation);
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
            CcStorage.saveSituation(iSituation);
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
    public void onSpinnerChanged(final CcCommSpinnerPayload pValue)
    {
        setTotalFunds(iFundsJso.getTotalFunds() + pValue.getDeltaPoints());
        iNumberOfCommodityCards += pValue.getDeltaNumber();
        getView().setNumCommodities(iNumberOfCommodityCards);
        int individualCount = iFundsJso.getCommodityCount(pValue.getCommIdx())
            + pValue.getDeltaNumber();
        iFundsJso.setCommodityCount(pValue.getCommIdx(), individualCount);
        CcStorage.saveSituation(iSituation);
    }



    @Override
    public void onBonusChanged(final Integer pNewValue)
    {
        if (isIntBetween(pNewValue, 0, CcFundsJSO.MAX_BONUS)) {
            int newValue = pNewValue.intValue();
            setTotalFunds(iFundsJso.getTotalFunds() + newValue - iFundsJso.getBonus());
            iFundsJso.setBonus(newValue);
            CcStorage.saveSituation(iSituation);
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
        CcStorage.saveSituation(iSituation);
    }



    @Override
    public void onDetailToggled(final boolean pDetailed)
    {
        iFundsJso.setDetailed(pDetailed);
        getView().setDetailTracking(pDetailed);
        if (pDetailed) {
            recalcTotalFunds();
        }
        CcStorage.saveSituation(iSituation);
    }



    @Override
    public void goBack()
    {
        goTo(new CcCardsPlace(iSituation, CbFundsPlace.class));
    }
}
