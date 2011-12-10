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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbToString;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.event.CbFundsEvent;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.CbVariantConfig;
import com.tj.civ.client.model.jso.CbCommodityConfigJSO;
import com.tj.civ.client.model.jso.CbFundsJSO;
import com.tj.civ.client.places.CbAbstractPlace;
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
            CbStorage.ensureGameLoadedWithSitKey(pPlace.getSituationKey(),
                pClientFactory.getEventBus());
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
        final CbSituation sit = CbGlobal.getCurrentSituation();
        final CbVariantConfig variant = CbGlobal.getGame().getVariant();
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        if (fundsJso == null) {
            // no situation loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }

        CbFundsViewIF view = getView();
        view.setPresenter(this);
        iNumberOfCommodityCards = countCommodities();
        view.initializeSituation(fundsJso, iNumberOfCommodityCards);
        boolean ownsMining = ownsMining();
        recalcTotalFunds(ownsMining);
        if (variant.getMiningIdx() >= 0) {
            view.setMiningEnabled(ownsMining);
        }

        // Adjust browser and view title
        CbUtil.setBrowserTitle(sit.getPlayer().getName() + " - " //$NON-NLS-1$
            + sit.getGame().getName());
        view.setTitleHeading(sit.getPlayer().getName());

        pContainer.setWidget(view.asWidget());
        LOG.exit("start"); //$NON-NLS-1$
    }



    private boolean ownsMining()
    {
        final CbSituation sit = CbGlobal.getCurrentSituation();
        final CbVariantConfig variant = CbGlobal.getGame().getVariant();
        boolean result = false;
        if (variant.getMiningIdx() >= 0) {
            result = sit.getCardsCurrent()[variant.getMiningIdx()].getState() == CbState.Owned;
        }
        return result;
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



    private void recalcTotalFunds(final boolean pOwnsMining)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("recalcTotalFunds", new String[]{"pOwnsMining"}, //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Boolean.valueOf(pOwnsMining)});
        }
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();

        int sum = 0;
        List<Integer> miningYields = new ArrayList<Integer>();
        if (fundsJso.isDetailed()) {
            sum += fundsJso.getTreasury();
            sum += fundsJso.getBonus();
            
            int wine = 0;
            int wineCount = 0;
    
            CbCommodityConfigJSO[] commodities =
                CbGlobal.getCurrentSituation().getVariant().getCommodities();
            for (int i = 0; i < commodities.length; i++)
            {
                final CbCommodityConfigJSO commo = commodities[i];
                final int n = fundsJso.getCommodityCount(i);
                if (commo.isWineSpecial()) {
                    wine += n * commo.getBase();
                    wineCount += n;
                }
                else {
                    sum += n * n * commo.getBase();
                }
                if (pOwnsMining && n > 0 && commo.isMineable() && n < commo.getMaxCount()) {
                    int current = n * n * commo.getBase();
                    int my = ((n + 1) * (n + 1) * commo.getBase()) - current;
                    miningYields.add(Integer.valueOf(my));
                }
            }
            sum += wine * wineCount;
        }
        else {
            sum = fundsJso.getTotalFunds();
        }
        int maxMiningYield = 0;
        if (miningYields.size() > 0) {
            maxMiningYield = Collections.max(miningYields).intValue();
            sum += maxMiningYield;
        }
        
        if (sum < 0) {
            sum = 0;
        }

        setTotalFunds(sum);
        getView().setMiningYield(maxMiningYield);
        CbGlobal.getCurrentSituation().setMiningYields(miningYields);

        LOG.exit("recalcTotalFunds", Integer.valueOf(sum)); //$NON-NLS-1$
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
    public void onCommodityChange(final int pIdx, final int pNewNumber)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("onCommodityChange",  //$NON-NLS-1$
                new String[]{"pIdx", "pNewNumber"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Integer.valueOf(pIdx), Integer.valueOf(pNewNumber)});
        }

        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        final CbCommodityConfigJSO[] configJSOs =
            CbGlobal.getCurrentSituation().getVariant().getCommodities();
        final CbCommodityConfigJSO commodity = configJSOs[pIdx];
        final int oldCount = fundsJso.getCommodityCount(pIdx);

        // update card count
        final int cardsDelta = pNewNumber - oldCount;
        fundsJso.setCommodityCount(pIdx, pNewNumber);
        iNumberOfCommodityCards += cardsDelta;
        getView().setNumCommodities(iNumberOfCommodityCards);

        // update points
        int pointsDelta = 0;
        if (commodity.isWineSpecial())
        {
            int oldWine = 0;
            int oldWineCount = 0;
            int newWine = 0;
            int newWineCount = 0;
    
            for (int i = 0; i < configJSOs.length; i++) {
                if (configJSOs[i].isWineSpecial()) {
                    if (i != pIdx) {
                        int n = fundsJso.getCommodityCount(i);
                        oldWine += n * configJSOs[i].getBase();
                        oldWineCount += n;
                        newWine += n * configJSOs[i].getBase();
                        newWineCount += n;
                    } else {
                        oldWine += oldCount * configJSOs[i].getBase();
                        oldWineCount += oldCount;
                        newWine += pNewNumber * configJSOs[i].getBase();
                        newWineCount += pNewNumber;
                    }
                }
            }
            pointsDelta = newWine * newWineCount - oldWine * oldWineCount;
        }
        else {
            pointsDelta = commodity.getBase() * pNewNumber * pNewNumber
                - commodity.getBase() * oldCount * oldCount;
        }

        // update mining yield
        if (commodity.isMineable() && ownsMining()) {
            CbSituation sit = CbGlobal.getCurrentSituation();
            final int oldMiningYield = sit.getHighestMiningYield();
            int currentYield = 0;
            if (oldCount > 0 && oldCount < commodity.getMaxCount()) {
                currentYield = (commodity.getBase() * (oldCount + 1) * (oldCount + 1))
                    - (commodity.getBase() * oldCount * oldCount);
            }
            int newYield = 0;
            if (pNewNumber > 0 && pNewNumber < commodity.getMaxCount()) {
                newYield = (commodity.getBase() * (pNewNumber + 1) * (pNewNumber + 1))
                    - (commodity.getBase() * pNewNumber * pNewNumber);
            }
            for (Iterator<Integer> iter = sit.getMiningYields().iterator(); iter.hasNext();) {
                Integer my = iter.next();
                if (currentYield == my.intValue()) {
                    iter.remove();
                    break;
                }
            }
            if (newYield > 0) {
                sit.getMiningYields().add(Integer.valueOf(newYield));
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("onCommodityChange", //$NON-NLS-1$
                    "new mining yields: " + CbToString.obj2str(sit.getMiningYields())); //$NON-NLS-1$
            }
            int newMiningYield = sit.getHighestMiningYield();
            pointsDelta += newMiningYield - oldMiningYield;
            getView().setMiningYield(newMiningYield);
        }

        setTotalFunds(fundsJso.getTotalFunds() + pointsDelta);

        CbStorage.saveSituation();

        LOG.exit("onCommodityChange"); //$NON-NLS-1$
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
        final CbFundsJSO fundsJso = CbGlobal.getCurrentFunds();
        fundsJso.setEnabled(pEnabled);
        getView().setEnabled(pEnabled, true);
        if (!pEnabled) {
            // when funds tracking is turned off, we also turn off detail tracking
            fundsJso.setDetailed(false);
            getView().setDetailTracking(false, true);
        }
        CbStorage.saveSituation();
    }



    @Override
    public void onDetailToggled(final boolean pDetailed)
    {
        CbGlobal.getCurrentFunds().setDetailed(pDetailed);
        getView().setDetailTracking(pDetailed, true);
        if (pDetailed) {
            recalcTotalFunds(ownsMining());
        }
        CbStorage.saveSituation();
    }



    @Override
    public com.google.web.bindery.event.shared.EventBus getEventBus()
    {
        return getEventBus();
    }
}
