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
package com.tj.civ.client.views;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbGroup;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.vo.CbDetailVO;
import com.tj.civ.client.model.vo.CbDetailVO.CbCardEntry;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbDetailPlace;
import com.tj.civ.client.places.CbPlayersPlace;
import com.tj.civ.client.widgets.CbIconButton;
import com.tj.civ.client.widgets.CbInlineFlowPanel;
import com.tj.civ.client.widgets.CbNavigationButton;


/**
 * Implementation of the 'Detail' view.
 *
 * @author Thomas Jensen
 */
public class CbDetailView
    extends Composite
    implements CbDetailViewIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbDetailView.class);

    /** this view's presenter */
    private CbDetailViewIF.CbPresenterIF iPresenter;

    /** the data we're currently displaying (<code>null</code> at view init) */
    private CbDetailVO iCurrentViewObject;

    /** 'Up' button at top of view */
    private CbIconButton iBtnUp;
    
    /** 'Down' button at top of view */
    private CbIconButton iBtnDown;

    /** the card display name and cost */
    private Label iLblTitle;

    /** the card's groups, including icons */
    private Panel iGroupsPanel;

    /** list show what you must do to get this card */
    private FlowPanel iGetItPanel;

    /** list showing what it gains you to own this card */
    private FlowPanel iHaveItPanel;

    /** click handler for all CbCardDisplay widgets */
    private static final ClickHandler CARD_CLICK_HANDLER = new ClickHandler() {
        @Override
        public void onClick(final ClickEvent pEvent)
        {
            CbCardDisplay sender = (CbCardDisplay) pEvent.getSource();
            sender.iView.iPresenter.goTo(new CbDetailPlace(
                CbGlobal.getCurrentSituation().getPersistenceKey(),
                sender.iIndex));
        }
    };



    /**
     * Widget used to group icon and name together in the page flow.
     * @author Thomas Jensen
     */
    private static class CbGroupDisplay
        extends Composite
    {
        /**
         * Constructor.
         * @param pGroup the group
         */
        public CbGroupDisplay(final CbGroup pGroup)
        {
            InlineLabel name = new InlineLabel(pGroup.getLocalizedName());
            Image grpImg = new Image(pGroup.getIcon());
            grpImg.addStyleName(CbConstants.CSS.ccDetailGroupWidgetImage());
            
            Panel fp = new CbInlineFlowPanel();
            fp.add(grpImg);
            fp.add(name);

            initWidget(fp);
        }
    }



    /**
     * Widget used to display a card reference including group icons, credit effect,
     * and hyperlink.
     * @author Thomas Jensen
     */
    private static class CbCardDisplay
        extends Composite
        implements HasClickHandlers
    {
        /** the view that this widget belongs to */
        private CbDetailView iView;

        /** this card's index */
        private int iIndex;


        /**
         * Constructor.
         * @param pGroups the groups
         * @param pCardEntry the card entry data
         * @param pView the view that this widget belongs to
         * @param pShowCardState should the card's state be shown by coloring
         */
        public CbCardDisplay(final CbGroup[] pGroups, final CbCardEntry pCardEntry,
            final CbDetailView pView, final boolean pShowCardState)
        {
            iView = pView;
            iIndex = pCardEntry.getCardIdx();

            String text1 = pCardEntry.getDisplayName();
            String text2 = " " + (pCardEntry.getCredit() < 0 ? '+' : '-') //$NON-NLS-1$
                + pCardEntry.getCredit();
            // TODO Webkit line wraps between the minus and the number -> nowrap

            Panel fp = new CbInlineFlowPanel();
            fp.setStyleName(CbConstants.CSS.ccDetailCardWidgetInnerPanel());

            for (CbGroup group : pGroups) {
                Image grpImg = new Image(group.getIcon());
                grpImg.addStyleName(CbConstants.CSS.ccDetailGroupWidgetImage());
                fp.add(grpImg);
            }
            
            InlineLabel lblText1 = new InlineLabel(text1);
            lblText1.setStyleName(CbConstants.CSS.ccDetailCardWidgetText1());
            fp.add(lblText1);
            InlineLabel lblText2 = new InlineLabel(text2);

            CbInlineFlowPanel outer = new CbInlineFlowPanel();
            outer.setStyleName(CbConstants.CSS.ccDetailCardWidgetOuterPanel());
            outer.add(fp);
            outer.add(lblText2);

            if (pShowCardState) {
                if (pCardEntry.getState() == CbState.Owned) {
                    outer.addStyleName(CbConstants.CSS.ccDetailCardWidgetStateOwned());
                } else if (pCardEntry.getState() == CbState.Planned) {
                    outer.addStyleName(CbConstants.CSS.ccDetailCardWidgetStatePlanned());
                } else {
                    outer.addStyleName(CbConstants.CSS.ccDetailCardWidgetStateOther());
                }
            }

            initWidget(outer);
            addClickHandler(CARD_CLICK_HANDLER);
            sinkEvents(Event.ONCLICK);    // must do this or no events are received
        }



        @Override
        public HandlerRegistration addClickHandler(final ClickHandler pHandler)
        {
            return addHandler(pHandler, ClickEvent.getType());
        }
    }



    /**
     * Constructor.
     */
    public CbDetailView()
    {
        LOG.enter(CbLogAdapter.CONSTRUCTOR);

        Label heading = new InlineLabel(CbConstants.STRINGS.viewDetailHeading());

        final CbNavigationButton btnBack = new CbNavigationButton(
            CbNavigationButton.CbPosition.left, CbConstants.STRINGS.viewDetailButtonBack(),
            CbConstants.STRINGS.viewDetailButtonBackTitle());
        btnBack.addButton(CbConstants.IMG_BUNDLE.navIconPlayers(),
            CbConstants.STRINGS.viewCardsButtonBackTitle());
        btnBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (btnBack.getButtonFaceLastClicked() == 0) {
                    iPresenter.goTo(new CbPlayersPlace(
                        CbGlobal.getGame().getPersistenceKey()));
                } else {
                    iPresenter.goTo(new CbCardsPlace(
                        CbGlobal.getCurrentSituation().getPersistenceKey()));
                }
            }
        });

        Panel headPanel = new FlowPanel();
        headPanel.add(btnBack);
        headPanel.add(heading);
        headPanel.setStyleName(CbConstants.CSS.cbTitleBar());
        headPanel.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        headPanel.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());
        FlowPanel headPanelIeWrapper = new FlowPanel();
        headPanelIeWrapper.setStyleName(CbConstants.CSS.cbTitleBarIeWrapper());
        headPanelIeWrapper.add(headPanel);

        iBtnDown = new CbIconButton(CbIconButton.CbPosition.right,
            CbConstants.IMG_BUNDLE.iconNext());
        iBtnDown.setTitle(null);
        iBtnDown.setEnabled(false);
        iBtnDown.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(new CbDetailPlace(
                    CbGlobal.getCurrentSituation().getPersistenceKey(),
                    Math.min(iCurrentViewObject.getIndex() + 1,
                        CbGlobal.getCardsCurrent().length - 1)));
            }
        });

        iBtnUp = new CbIconButton(CbIconButton.CbPosition.left,
            CbConstants.IMG_BUNDLE.iconPrevious());
        iBtnUp.setTitle(null);
        iBtnUp.setEnabled(false);
        iBtnUp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(new CbDetailPlace(
                    CbGlobal.getCurrentSituation().getPersistenceKey(),
                    Math.max(iCurrentViewObject.getIndex() - 1, 0)));
            }
        });

        Panel bottomBar = new FlowPanel();
        bottomBar.add(iBtnUp);
        bottomBar.add(iBtnDown);
        bottomBar.setStyleName(CbConstants.CSS.cbBottomBar());
        bottomBar.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        bottomBar.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());
        FlowPanel bottomBarIeWrapper = new FlowPanel();
        bottomBarIeWrapper.setStyleName(CbConstants.CSS.cbBottomBarIeWrapper());
        bottomBarIeWrapper.add(bottomBar);

        iLblTitle = new Label(buildTitleMsg("NotSet", 1));  //$NON-NLS-1$
        iLblTitle.setStyleName(CbConstants.CSS.cbBackgroundTitle());

        iGroupsPanel = new FlowPanel();
        iGroupsPanel.setStyleName(CbConstants.CSS.cbDetailGroups());

        iGetItPanel = new FlowPanel();
        iGetItPanel.setStyleName(CbConstants.CSS.cbPageItem());

        iHaveItPanel = new FlowPanel();
        iHaveItPanel.setStyleName(CbConstants.CSS.cbPageItem());

        FlowPanel viewPanel = new FlowPanel();
        viewPanel.add(headPanelIeWrapper);
        viewPanel.add(bottomBarIeWrapper);
        viewPanel.add(iLblTitle);
        viewPanel.add(iGroupsPanel);
        viewPanel.add(iGetItPanel);
        viewPanel.add(iHaveItPanel);
        viewPanel.setStyleName(CbConstants.CSS.cbAbstractListViewMargin());
        initWidget(viewPanel);
        
        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    private HTML buildCreditMsg(final int pCreditPrecentage,
        final int pCreditPrecentageInclPlan)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(CbConstants.STRINGS.viewDetailSectionHeadingCredit());
        sb.append(" ("); //$NON-NLS-1$
        sb.append(pCreditPrecentage);
        sb.append('%');
        if (pCreditPrecentageInclPlan > pCreditPrecentage) {
            sb.append(", <span class=\""); //$NON-NLS-1$
            sb.append(CbConstants.CSS.cbDetailSectionTitlePlanned());
            sb.append("\">"); //$NON-NLS-1$
            sb.append(pCreditPrecentageInclPlan);
            sb.append("%</span>"); //$NON-NLS-1$
        }
        sb.append("): "); //$NON-NLS-1$
        InlineHTML result = new InlineHTML(sb.toString());
        return result;
    }



    private String buildSupportsMsg(final int pTotalSupport)
    {
        String result = CbConstants.STRINGS.viewDetailSectionHeadingSupports();
        if (pTotalSupport > 0) {
            result += " (" + pTotalSupport + ')'; //$NON-NLS-1$
        }
        result += ": "; //$NON-NLS-1$
        return result;
    }



    private String buildTitleMsg(final String pCardName, final int pNominalCost)
    {
        return pCardName + " (" +  pNominalCost + ')'; //$NON-NLS-1$
    }



    @Override
    public void setPresenter(final CbDetailViewIF.CbPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public void showCard(final CbDetailVO pDetails)
    {
        iCurrentViewObject = pDetails;

        iLblTitle.setText(buildTitleMsg(pDetails.getDisplayName(),
            pDetails.getCostNominal()));

        iGroupsPanel.clear();
        for (CbGroup group : pDetails.getGroups()) {
            iGroupsPanel.add(new CbGroupDisplay(group));
        }

        Label stateDesc = new Label();
        stateDesc.setStyleName(CbConstants.CSS.cbPageItemTextBox());
        stateDesc.addStyleName(CbConstants.CSS.ccDetailStateDescription());
        stateDesc.setText(pDetails.getStatusMsg());
        // TODO prefix small state icon (tick mark, question mark, ...)
        // TODO don't show state message if state is 'Absent'

        FlowPanel currentCostPanel = null;
        FlowPanel creditPanel = null;
        final CbCardConfig[] cardsConfig = CbGlobal.getGame().getVariant().getCards();
        if (pDetails.getState() != CbState.Owned) {
            FlowPanel currentCostPanelInner = new FlowPanel();
            currentCostPanelInner.setStyleName(CbConstants.CSS.cbPageItemTextBoxInner());
            Label currentCostTitle = new InlineLabel("Current cost:");
            currentCostTitle.setStyleName(CbConstants.CSS.cbPageItemHeader());
            Label currentCost = new InlineLabel(" " + pDetails.getCostCurrent()); //$NON-NLS-1$
            // TODO show current cost incl. plan
            currentCostPanelInner.add(currentCostTitle);
            currentCostPanelInner.add(currentCost);
            currentCostPanel = new FlowPanel();
            currentCostPanel.setStyleName(CbConstants.CSS.cbPageItemTextBox());
            currentCostPanel.add(currentCostPanelInner);

            FlowPanel creditPanelInner = new FlowPanel();
            creditPanelInner.setStyleName(CbConstants.CSS.cbPageItemTextBoxInner());
            HTML lblCredit = buildCreditMsg(pDetails.getCreditPercent(),
                pDetails.getCreditPercentInclPlan());
            lblCredit.setStyleName(CbConstants.CSS.cbPageItemHeader());
            creditPanelInner.add(lblCredit);
            for (Iterator<CbCardEntry> iter = pDetails.getCreditFrom().iterator(); iter.hasNext();)
            {
                CbCardEntry from = iter.next();
                creditPanelInner.add(new CbCardDisplay(
                    cardsConfig[from.getCardIdx()].getGroups(), from, this, true));
                if (iter.hasNext()) {
                    creditPanelInner.add(new InlineLabel(", ")); //$NON-NLS-1$
                }
            }
            // TODO message if no credit received at all (see supports below)
            creditPanel = new FlowPanel();
            creditPanel.setStyleName(CbConstants.CSS.cbPageItemTextBox());
            creditPanel.add(creditPanelInner);
        }

        iGetItPanel.clear();
        iGetItPanel.add(stateDesc);
        if (pDetails.getState() != CbState.Owned) {
            iGetItPanel.add(currentCostPanel);
            iGetItPanel.add(creditPanel);
        }
 
        Label lblAttrCapt = new InlineLabel(
            CbConstants.STRINGS.viewDetailSectionHeadingAttributes() + ": "); //$NON-NLS-1$
        lblAttrCapt.setStyleName(CbConstants.CSS.cbPageItemHeader());
        Label lblAttributes = new InlineLabel(pDetails.getAttributes());
        FlowPanel attrPanelInner = new FlowPanel();
        attrPanelInner.setStyleName(CbConstants.CSS.cbPageItemTextBoxInner());
        attrPanelInner.add(lblAttrCapt);
        attrPanelInner.add(lblAttributes);
        FlowPanel attrPanel = new FlowPanel();
        attrPanel.setStyleName(CbConstants.CSS.cbPageItemTextBox());
        attrPanel.add(attrPanelInner);

        Label lblCalaCapt = new InlineLabel(
            CbConstants.STRINGS.viewDetailSectionHeadingCalamityEffects() + ": "); //$NON-NLS-1$
        lblCalaCapt.setStyleName(CbConstants.CSS.cbPageItemHeader());
        Label lblCalamityEffects = new InlineLabel(pDetails.getCalamityEffects());
        Panel calaPanelInner = new FlowPanel();
        calaPanelInner.setStyleName(CbConstants.CSS.cbPageItemTextBoxInner());
        calaPanelInner.add(lblCalaCapt);
        calaPanelInner.add(lblCalamityEffects);
        Panel calaPanel = new FlowPanel();
        calaPanel.setStyleName(CbConstants.CSS.cbPageItemTextBox());
        calaPanel.add(calaPanelInner);

        FlowPanel supportsPanelInner = new FlowPanel();
        supportsPanelInner.setStyleName(CbConstants.CSS.cbPageItemTextBoxInner());
        Label lblSupports = new InlineLabel(buildSupportsMsg(pDetails.getSupportsTotal()));
        lblSupports.setStyleName(CbConstants.CSS.cbPageItemHeader());
        supportsPanelInner.add(lblSupports);
        boolean addedSumpn = false;
        for (Iterator<CbCardEntry> iter = pDetails.getSupports().iterator(); iter.hasNext();)
        {
            CbCardEntry to = iter.next();
            supportsPanelInner.add(new CbCardDisplay(
                cardsConfig[to.getCardIdx()].getGroups(), to, this, false));
            if (iter.hasNext()) {
                supportsPanelInner.add(new InlineLabel(", ")); //$NON-NLS-1$
            }
            addedSumpn = true;
        }
        if (!addedSumpn) {
            supportsPanelInner.add(new InlineLabel(
                CbConstants.STRINGS.viewDetailMessageSupportsNone()));
        }
        FlowPanel supportsPanel = new FlowPanel();
        supportsPanel.setStyleName(CbConstants.CSS.cbPageItemTextBox());
        supportsPanel.add(supportsPanelInner);

        iHaveItPanel.clear();
        iHaveItPanel.add(attrPanel);
        iHaveItPanel.add(calaPanel);
        iHaveItPanel.add(supportsPanel);

        iBtnUp.setEnabled(pDetails.getIndex() > 0);
        iBtnUp.setTitle(iBtnUp.isEnabled()
            ? cardsConfig[pDetails.getIndex() - 1].getLocalizedName() : null);
        iBtnDown.setEnabled(pDetails.getIndex() < cardsConfig.length - 1);
        iBtnDown.setTitle(iBtnDown.isEnabled()
            ? cardsConfig[pDetails.getIndex() + 1].getLocalizedName() : null);
    }
}
