/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-11-04
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbGroup;
import com.tj.civ.client.model.CbState;


/**
 * Represents a civilization card in the 'Cards' view, including credit bar,
 * costs, groups and state. Note that the 'More' arrow is <em>not</em> part of
 * this widget.
 *
 * @author Thomas Jensen
 */
public class CbCardWidget
    extends Composite
    implements HasClickHandlers
{
    /** the localized card name */
    private Label iCardName;

    /** the cost indicator showing current and nominal cost */
    private CbCardCostIndicator iCostIndicator;

    /** the credit bar showing credit given by other card to this card */
    private CbCreditBar iCreditBar;

    /** the icon indicating state 'Owned' */
    private Image iStateIconOwned;

    /** the icon indicating state 'Planned' */
    private Image iStateIconPlanned;

    /** the index value of the card represented by this widget */
    private int iMyIdx;



    /**
     * Constructor.
     * @param pCard the card to show
     */
    public CbCardWidget(final CbCardCurrent pCard)
    {
        CbCardConfig cardConfig = pCard.getConfig();
        iMyIdx = pCard.getMyIdx();

        // State Icon
        iStateIconOwned = new Image(CbConstants.IMG_BUNDLE.stateOwned());
        iStateIconPlanned = new Image(CbConstants.IMG_BUNDLE.statePlanned());
        FlowPanel p1 = new FlowPanel();
        p1.add(iStateIconOwned);
        p1.add(iStateIconPlanned);
        p1.setStyleName(CbConstants.CSS.cbCwStateIconDiv());

        // Group Indicators
        FlowPanel p2 = new FlowPanel();
        boolean first = true;
        for (CbGroup group : cardConfig.getGroups()) {
            Image grpImg = new Image(group.getIcon());
            String grpName = group.getLocalizedName();
            grpImg.setAltText(grpName);
            grpImg.setTitle(grpName);

            FlowPanel imgFp = new FlowPanel();
            imgFp.add(grpImg);
            if (first) {
                imgFp.setStyleName(CbConstants.CSS.cbCwGroupIcon1());
                first = false;
            } else {
                imgFp.setStyleName(CbConstants.CSS.cbCwGroupIcon2());
            }
            p2.add(imgFp);
        }
        p2.setStyleName(CbConstants.CSS.cbCwGroupIcons());

        // Card Name
        iCardName = new Label(cardConfig.getLocalizedName());
        iCardName.setStyleName(CbConstants.CSS.cbCwCardName());

        // Card Cost
        iCostIndicator = new CbCardCostIndicator(cardConfig.getCostNominal());
        setState(pCard.getState());   // AFTER state icon and cost indicator

        // Credit Bar
        iCreditBar = new CbCreditBar(pCard);

        FlowPanel p3 = new FlowPanel();
        p3.add(iCardName);
        p3.add(iCostIndicator);
        p3.add(iCreditBar);
        p3.setStyleName(CbConstants.CSS.cbCwP3());

        FlowPanel fp = new FlowPanel();
        fp.add(p1);
        fp.add(p2);
        fp.add(p3);
        fp.setStyleName(CbConstants.CSS.cbCwOuterDiv());
        initWidget(fp);
    }



    /**
     * Updates the state indicator icon to the given state.
     * @param pState the new state to show
     */
    public void setState(final CbState pState)
    {
        if (pState == CbState.Owned) {
            iStateIconPlanned.setVisible(false);
            iStateIconOwned.setVisible(true);
            iCostIndicator.setOwned(true);
        }
        else if (pState == CbState.Planned) {
            iStateIconOwned.setVisible(false);
            iStateIconPlanned.setVisible(true);
            iCostIndicator.setOwned(false);
        }
        else {
            iStateIconOwned.setVisible(false);
            iStateIconPlanned.setVisible(false);
            iCostIndicator.setOwned(false);
        }
    }



    /**
     * Updates the current cost value shown.
     * @param pCurrentCost the new value
     */
    public void setCostDisplay(final int pCurrentCost)
    {
        iCostIndicator.setCurrentCost(pCurrentCost);
    }



    public String getStateReason()
    {
        return iCardName.getTitle();
    }

    /**
     * Set the state reason, which is displayed as title attribute of the card name.
     * @param pStateReason the new state reason, or <code>null</code> for none
     */
    public void setStateReason(final String pStateReason)
    {
        iCardName.setTitle(pStateReason);
    }



    /**
     * Delegate to {@link CbCreditBar#update}.
     * @param pGivingCardIdx index of the giving card which has changed state
     */
    public void updateCreditBar(final int pGivingCardIdx)
    {
        iCreditBar.update(pGivingCardIdx);
    }



    @Override
    public HandlerRegistration addClickHandler(final ClickHandler pHandler)
    {
        return addDomHandler(pHandler, ClickEvent.getType());
    }



    public int getMyIdx()
    {
        return iMyIdx;
    }
}
