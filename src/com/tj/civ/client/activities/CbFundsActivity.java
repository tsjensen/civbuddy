/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-31
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
import com.tj.civ.client.model.jso.CbCommodityConfigJSO;
import com.tj.civ.client.model.jso.CbFundsJSO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbFundsPlace;
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

        if (pPlace != null) {
            CbStorage.ensureGameLoadedWithSitKey(pPlace.getSituationKey());
        }
        if (CbGlobal.getCurrentFunds() == null) {
            Window.alert(CbConstants.STRINGS.noGame());
        }

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void goTo(final CbAbstractPlace pPlace)
    {
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        if (fundsJso != null) {
            getClientFactory().getEventBus().fireEventFromSource(
                new CbFundsEvent(fundsJso.getTotalFunds(), fundsJso.isEnabled()), this);
        }
        super.goTo(pPlace);
    }



    @Override
    public void start(final AcceptsOneWidget pContainer, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        if (fundsJso == null) {
            // no situation loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }

        CbFundsViewIF view = getView();
        view.setPresenter(this);
        view.initialize(CbGlobal.getCurrentSituation().getVariant().getCommodities(),
            CbGlobal.getCurrentSituation().getVariant().getNumWineSpecials(), fundsJso);
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
        final int[] commodityCounts = CbGlobal.getCurrentFunds().getCommodityCounts();
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
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        fundsJso.setBonus(0);
        fundsJso.setTreasury(0);
        fundsJso.setTotalFunds(0);
        final int commCount = CbGlobal.getCurrentSituation().getVariant().getCommodities().length;
        for (int i = 0; i < commCount; i++) {
            fundsJso.setCommodityCount(i, 0);
        }
        iNumberOfCommodityCards = 0;
        CbStorage.saveSituation();
    }



    private void recalcTotalFunds()
    {
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        int sum = 0;
        sum += fundsJso.getTreasury();
        sum += fundsJso.getBonus();
        
        int wine = 0;
        int wineCount = 0;

        CbCommodityConfigJSO[] commodities =
            CbGlobal.getCurrentSituation().getVariant().getCommodities();
        for (int i = 0; i < commodities.length; i++)
        {
            int n = fundsJso.getCommodityCount(i);
            if (commodities[i].isWineSpecial()) {
                wine += n * commodities[i].getBase();
                wineCount += n;
            }
            else {
                sum += n * n * commodities[i].getBase();
            }
        }
        sum += wine * wineCount;

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
        if (isIntBetween(pNewValue, 0, CbFundsJSO.MAX_TOTAL_FUNDS)) {
            int newValue = pNewValue.intValue();
            setTotalFunds(newValue);
            CbStorage.saveSituation();
        }
        else {
            getView().setTotalFundsBoxOnly(CbGlobal.getCurrentFunds().getTotalFunds());
        }
    }



    @Override
    public void onTreasuryBoxChanged(final Integer pNewValue)
    {
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        Integer temp = pNewValue != null ? pNewValue : Integer.valueOf(0);
        if (isIntBetween(temp, CbFundsJSO.TREASURY_MIN, CbFundsJSO.TREASURY_MAX)) {
            final int newValue = temp.intValue();
            final int oldValue = fundsJso.getTreasury();
            fundsJso.setTreasury(newValue);
            getView().setTreasury(newValue);
            setTotalFunds(fundsJso.getTotalFunds() - oldValue + newValue);
            CbStorage.saveSituation();
        }
        else {
            getView().setTreasury(fundsJso.getTreasury());
        }
    }



    private void setTotalFunds(final int pNewValue)
    {
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        fundsJso.setTotalFunds(pNewValue);
        getView().setTotalFunds(pNewValue);
    }



    @Override
    public void onSpinnerChanged(final CbCommSpinnerPayload pValue)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("onSpinnerChanged",  //$NON-NLS-1$
                new String[]{"pValue"}, new Object[]{pValue});  //$NON-NLS-1$
        }
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        setTotalFunds(fundsJso.getTotalFunds() + pValue.getDeltaPoints());
        iNumberOfCommodityCards += pValue.getDeltaNumber();
        getView().setNumCommodities(iNumberOfCommodityCards);
        int individualCount = fundsJso.getCommodityCount(pValue.getCommIdx())
            + pValue.getDeltaNumber();
        fundsJso.setCommodityCount(pValue.getCommIdx(), individualCount);
        CbStorage.saveSituation();
        LOG.exit("onSpinnerChanged"); //$NON-NLS-1$
    }



    @Override
    public void onBonusChanged(final Integer pNewValue)
    {
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        Integer temp = pNewValue != null ? pNewValue : Integer.valueOf(0);
        if (isIntBetween(temp, 0, CbFundsJSO.MAX_BONUS)) {
            int newValue = temp.intValue();
            setTotalFunds(fundsJso.getTotalFunds() + newValue - fundsJso.getBonus());
            fundsJso.setBonus(newValue);
            getView().setBonusBoxOnly(newValue);
            CbStorage.saveSituation();
        }
        else {
            getView().setBonusBoxOnly(fundsJso.getBonus());
        }
    }



    @Override
    public void onEnableToggled(final boolean pEnabled)
    {
        CbGlobal.getCurrentFunds().setEnabled(pEnabled);
        getView().setEnabled(pEnabled);
        CbStorage.saveSituation();
    }



    @Override
    public void onDetailToggled(final boolean pDetailed)
    {
        CbGlobal.getCurrentFunds().setDetailed(pDetailed);
        getView().setDetailTracking(pDetailed);
        if (pDetailed) {
            recalcTotalFunds();
        }
        CbStorage.saveSituation();
    }



    @Override
    public void goBack()
    {
        goTo(new CbCardsPlace(CbGlobal.getCurrentSituation().getPersistenceKey()));
    }
}
