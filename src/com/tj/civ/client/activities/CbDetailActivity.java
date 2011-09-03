/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-07-20
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
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.tj.civ.client.CbClientFactoryIF;
import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbSituation;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.vo.CbDetailVO;
import com.tj.civ.client.model.vo.CbDetailVO.CbCardEntry;
import com.tj.civ.client.places.CbDetailPlace;
import com.tj.civ.client.views.CbDetailViewIF;


/**
 * Presenter of the 'Detail' view.
 *
 * @author Thomas Jensen
 */
public class CbDetailActivity
    extends CbAbstractActivity
    implements CbDetailViewIF.CbPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbDetailActivity.class);

    /** the number 100 */
    private static final int ONE_HUNDRED = 100;

    /** index number of the card to display */
    private int iCardIdx;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbDetailActivity(final CbDetailPlace pPlace,
        final CbClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);
        if (LOG.isTraceEnabled()) {
            LOG.enter(CbLogAdapter.CONSTRUCTOR,
                new String[]{"pPlace"}, new Object[]{pPlace});  //$NON-NLS-1$
        }

        if (pPlace != null) {
            CbStorage.ensureGameLoadedWithSitKey(pPlace.getSituationKey(),
                pClientFactory.getEventBus());
            iCardIdx = pPlace.getCardIdx();
        }
        if (!CbGlobal.isSituationSet()) {
            Window.alert(CbConstants.STRINGS.noGame());
        }

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$
        if (!CbGlobal.isSituationSet()) {
            // no situation loaded, so redirect to game selection
            goTo(CbConstants.DEFAULT_PLACE);
            LOG.exit("start"); //$NON-NLS-1$
            return;
        }
        final CbSituation sit = CbGlobal.getCurrentSituation();
        if (iCardIdx < 0) {
            iCardIdx = 0;
        } else if (iCardIdx >= sit.getVariant().getCards().length) {
            iCardIdx = sit.getVariant().getCards().length;
        }

        // Register this presenter (which is always new) with the (recycled) view
        CbDetailViewIF view = getClientFactory().getDetailView();
        view.setPresenter(this);
        pContainerWidget.setWidget(view.asWidget());

        // Adjust browser title
        CbUtil.setBrowserTitle(sit.getPlayer().getName() + " - " //$NON-NLS-1$
            + sit.getCardsCurrent()[iCardIdx].getConfig().getLocalizedName() + " - " //$NON-NLS-1$
            + sit.getGame().getName());

        showCard(iCardIdx);

        LOG.exit("start"); //$NON-NLS-1$
    }



    @Override
    public void showCard(final int pCardIdx)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("showCard",  //$NON-NLS-1$
                new String[]{"pCardIdx"},  //$NON-NLS-1$
                new Object[]{Integer.valueOf(pCardIdx)});
        }
        final CbCardCurrent[] cardsCurrent = CbGlobal.getCardsCurrent();
        final CbCardCurrent card = cardsCurrent[pCardIdx];
        CbDetailVO vo = new CbDetailVO(card);

        int creditReceived = 0;
        int creditReceivedPlan = 0;

        // supportING cards
        List<CbCardEntry> entries = new ArrayList<CbCardEntry>();
        for (CbCardCurrent c : cardsCurrent) {
            CbCardConfig config = c.getConfig();
            int credit = config.getCreditGiven()[pCardIdx];
            if (credit > 0) {
                CbCardEntry entry = new CbCardEntry();
                entry.setCardIdx(config.getMyIdx());
                entry.setCredit(credit);
                entry.setDisplayName(config.getLocalizedName());
                entry.setState(c.getState());
                entries.add(entry);
                if (c.getState().isAffectingCredit()) {
                    creditReceivedPlan += credit;
                    if (c.getState() == CbState.Owned) {
                        creditReceived += credit;
                    }
                }
            }
        }
        vo.setCreditFrom(entries);

        vo.setCreditPercent(ONE_HUNDRED * creditReceived
            / card.getConfig().getCreditReceivedTotal());
        vo.setCreditPercentInclPlan(ONE_HUNDRED * creditReceivedPlan
            / card.getConfig().getCreditReceivedTotal());

        // supportED cards
        entries = new ArrayList<CbCardEntry>();
        final int[] creditGiven = card.getConfig().getCreditGiven();
        for (int i = 0; i < creditGiven.length; i++) {
            if (creditGiven[i] > 0) {
                CbCardEntry entry = new CbCardEntry();
                entry.setCardIdx(i);
                entry.setCredit(creditGiven[i]);
                entry.setDisplayName(cardsCurrent[i].getConfig().getLocalizedName());
                entry.setState(cardsCurrent[i].getState());
                entries.add(entry);
            }
        }
        vo.setSupports(entries);

        // FIXME: When the game is loaded on this view, we always get 'Absent'!
        //        This is because the states are not calculated. Doing this requires
        //        a redesign of the 'Cards' activity, so that calculating the states
        //        only changes the model, not the view. The view should be changed
        //        by a separate call.
        vo.setStatusMsg(getStatusMsg(card));
        
        getClientFactory().getDetailView().showCard(vo);
        LOG.exit("showCard"); //$NON-NLS-1$
    }



    private String getStatusMsg(final CbCardCurrent pCard)
    {
        String result = null;
        CbState state = pCard.getState();
        switch (state) {
            case Owned:
                result = CbConstants.STRINGS.stateDetailOwned();
                break;
            case Planned:
                result = CbConstants.STRINGS.stateDetailPlanned();
                break;
            case Absent:
                result = CbConstants.STRINGS.stateDetailAbsent();
                break;
            case Unaffordable:
                result = CbConstants.STRINGS.stateDetailUnaffordable();
                break;
            case PrereqFailed:
                String preReqName = pCard.getConfig().getAllCardsConfig()
                    [pCard.getConfig().getPrereq()].getLocalizedName();
                result = CbConstants.MESSAGES.stateDetailPrereqFailed(preReqName);
                break;
            case DiscouragedBuy:
                result = CbConstants.MESSAGES.stateDetailDiscouragedBuy(pCard.getPointsDelta());
                break;
            default:
                result = "Unknown state: " + state.toString(); //$NON-NLS-1$
                break;
        }
        return result;
    }
}
