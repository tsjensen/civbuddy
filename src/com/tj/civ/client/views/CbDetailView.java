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
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbGroup;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.model.vo.CbDetailVO;
import com.tj.civ.client.model.vo.CbDetailVO.CbCardEntry;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbDetailPlace;
import com.tj.civ.client.widgets.CbInlineFlowPanel;


/**
 * Implementation of the 'Detail' view.
 *
 * @author Thomas Jensen
 */
public class CbDetailView
    extends Composite
    implements CbDetailViewIF
{
    /** this view's presenter */
    private CbDetailViewIF.CbPresenterIF iPresenter;

    /** the data we're currently displaying (<code>null</code> at view init) */
    private CbDetailVO iCurrentViewObject;

    /** 'Up' button at top of view */
    private Button iBtnUp;
    
    /** 'Down' button at top of view */
    private Button iBtnDown;

    /** the card display name and cost */
    private Label iLblTitle;

    /** the card's groups, including icons */
    private Panel iGroupsPanel;

    /** the text describing the card state */
    private Label iLblStateDesc;

    /** the cards giving credit to this one */
    private Panel iCreditPanel;

    /** the card's attributes description */
    private Label iLblAttributes;

    /** the card's calamity effects description */
    private Label iLblCalamityEffects;

    /** the cards that this one gives credit to */
    private Panel iSupportsPanel;

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
            InlineLabel name = null;
            if (CbConstants.LOCALE_DE.equalsIgnoreCase(
                    LocaleInfo.getCurrentLocale().getLocaleName()))
            {
                name = new InlineLabel(pGroup.getNameDE());
            }
            else {
                name = new InlineLabel(pGroup.getNameEN());
            }
            
            Image grpImg = new Image(pGroup.getIcon());
            grpImg.addStyleName(CbConstants.CSS.ccDetailGroupWidgetImage());
            
            Panel fp = new CbInlineFlowPanel();
            fp.setStyleName(CbConstants.CSS.ccDetailGroupWidget());
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
         */
        public CbCardDisplay(final CbGroup[] pGroups, final CbCardEntry pCardEntry,
            final CbDetailView pView)
        {
            iView = pView;
            iIndex = pCardEntry.getCardIdx();

            String text = pCardEntry.getDisplayName() + " ("  //$NON-NLS-1$
                + (pCardEntry.getCredit() < 0 ? '-' : '+')
                + pCardEntry.getCredit() + ')';
            int p = text.indexOf(' ');
            String text1 = text.substring(0, p);
            String text2 = text.substring(p);

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

            if (pCardEntry.getState() == CbState.Owned) {
                outer.addStyleName(CbConstants.CSS.ccDetailCardWidgetStateOwned());
            } else if (pCardEntry.getState() == CbState.Planned) {
                outer.addStyleName(CbConstants.CSS.ccDetailCardWidgetStatePlanned());
            } else {
                outer.addStyleName(CbConstants.CSS.ccDetailCardWidgetStateOther());
            }

            initWidget(outer);
            addClickHandler(CARD_CLICK_HANDLER);
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
        HorizontalPanel headPanel = new HorizontalPanel();
        Label heading = new Label(CbConstants.STRINGS.viewDetailTitle());
        heading.setStyleName(CbConstants.CSS.ccHeading());

        Button btnBack = new Button(SafeHtmlUtils.fromSafeConstant(
            CbConstants.STRINGS.viewDetailButtonBack()));
        btnBack.setStyleName(CbConstants.CSS.ccButton());
        btnBack.setTitle(CbConstants.STRINGS.viewDetailButtonBackTitle());
        btnBack.setEnabled(true);
        btnBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(new CbCardsPlace(
                    CbGlobal.getCurrentSituation().getPersistenceKey()));
            }
        });

        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        headPanel.add(btnBack);
        headPanel.setCellWidth(btnBack, "12%"); //$NON-NLS-1$
        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        headPanel.add(heading);
        headPanel.setStyleName(CbConstants.CSS.ccButtonPanel());
        headPanel.addStyleName(CbConstants.CSS_BLUEGRADIENT);

        iBtnUp = new Button("Up");
        iBtnUp.setStyleName(CbConstants.CSS.ccButton());
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

        iBtnDown = new Button("Down");
        iBtnDown.setStyleName(CbConstants.CSS.ccButton());
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

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttonPanel.add(iBtnUp);
        buttonPanel.add(iBtnDown);
        buttonPanel.setStyleName(CbConstants.CSS.ccButtonPanel());
        buttonPanel.addStyleName(CbConstants.CSS_BLUEGRADIENT);

        iLblTitle = new Label(buildTitleMsg("NotSet", 0, 1));  //$NON-NLS-1$
        iLblTitle.setStyleName(CbConstants.CSS.ccDetailCardTitle());

        iGroupsPanel = new FlowPanel();

        iLblStateDesc = new Label("No card selected.");   //$NON-NLS-1$
        iLblStateDesc.setStyleName(CbConstants.CSS.ccDetailStateDescription());

        HTML lblCredit = buildCreditMsg(0, 0);
        lblCredit.setStyleName(CbConstants.CSS.ccDetailSectionTitle());
        iCreditPanel = new FlowPanel();
        iCreditPanel.setStyleName(CbConstants.CSS.ccDetailSection());
        iCreditPanel.add(lblCredit);

        Label lblAttrCapt = new InlineLabel(
            CbConstants.STRINGS.viewDetailSectionHeadingAttributes() + ": "); //$NON-NLS-1$
        lblAttrCapt.setStyleName(CbConstants.CSS.ccDetailSectionTitle());
        iLblAttributes = new InlineLabel("None"); //$NON-NLS-1$
        Panel attrPanel = new FlowPanel();
        attrPanel.setStyleName(CbConstants.CSS.ccDetailSection());
        attrPanel.add(lblAttrCapt);
        attrPanel.add(iLblAttributes);

        Label lblCalaCapt = new InlineLabel(
            CbConstants.STRINGS.viewDetailSectionHeadingCalamityEffects() + ": "); //$NON-NLS-1$
        lblCalaCapt.setStyleName(CbConstants.CSS.ccDetailSectionTitle());
        iLblCalamityEffects = new InlineLabel("None"); //$NON-NLS-1$
        Panel calaPanel = new FlowPanel();
        calaPanel.setStyleName(CbConstants.CSS.ccDetailSection());
        calaPanel.add(lblCalaCapt);
        calaPanel.add(iLblCalamityEffects);

        Label lblSupports = new InlineLabel(buildSupportsMsg(0));
        lblSupports.setStyleName(CbConstants.CSS.ccDetailSectionTitle());
        iSupportsPanel = new FlowPanel();
        iSupportsPanel.setStyleName(CbConstants.CSS.ccDetailSection());
        iSupportsPanel.add(lblSupports);

        Panel vp = new VerticalPanel();
        vp.add(headPanel);
        vp.add(buttonPanel);
        vp.add(iLblTitle);
        vp.add(iGroupsPanel);
        vp.add(iLblStateDesc);
        vp.add(iCreditPanel);
        vp.add(iCreditPanel);
        vp.add(attrPanel);
        vp.add(calaPanel);
        vp.add(iSupportsPanel);

        initWidget(vp);
    }



    private HTML buildCreditMsg(final int pCreditPrecentage,
        final int pCreditPrecentageInclPlan)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(CbConstants.STRINGS.viewDetailSectionHeadingCredit());
        sb.append(" ("); //$NON-NLS-1$
        sb.append(pCreditPrecentage);
        sb.append('%');
        if (pCreditPrecentageInclPlan > 0) {
            sb.append(", <span class=\""); //$NON-NLS-1$
            sb.append(CbConstants.CSS.ccDetailSectionTitlePlanned());
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
        return CbConstants.STRINGS.viewDetailSectionHeadingSupports()
            + " (" + pTotalSupport + "): "; //$NON-NLS-1$ //$NON-NLS-2$
    }



    private String buildTitleMsg(final String pCardName, final int pCurrentCost,
        final int pNominalCost)
    {
        return pCardName + " (" + pCurrentCost + " / " //$NON-NLS-1$ //$NON-NLS-2$
            + pNominalCost + ')';
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
            pDetails.getCostCurrent(), pDetails.getCostNominal()));

        iGroupsPanel.clear();
        for (CbGroup group : pDetails.getGroups()) {
            iGroupsPanel.add(new CbGroupDisplay(group));
        }

        iLblStateDesc.setText(pDetails.getStatusMsg());

        iCreditPanel.clear();
        HTML lblCredit = buildCreditMsg(pDetails.getCreditPercent(),
            pDetails.getCreditPercentInclPlan());
        lblCredit.setStyleName(CbConstants.CSS.ccDetailSectionTitle());
        iCreditPanel.add(lblCredit);
        final CbCardConfig[] cardsConfig = CbGlobal.getGame().getVariant().getCards();
        for (Iterator<CbCardEntry> iter = pDetails.getCreditFrom().iterator(); iter.hasNext();)
        {
            CbCardEntry from = iter.next();
            iCreditPanel.add(new CbCardDisplay(
                cardsConfig[from.getCardIdx()].getGroups(), from, this));
            if (iter.hasNext()) {
                iCreditPanel.add(new InlineLabel(", ")); //$NON-NLS-1$
            }
        }

        iLblAttributes.setText(pDetails.getAttributes());
        iLblCalamityEffects.setText(pDetails.getCalamityEffects());

        // TODO implement showCard()

        iBtnUp.setEnabled(pDetails.getIndex() > 0);
        iBtnUp.setTitle(iBtnUp.isEnabled()
            ? cardsConfig[pDetails.getIndex() - 1].getLocalizedName() : null);
        iBtnDown.setEnabled(pDetails.getIndex() < cardsConfig.length - 1);
        iBtnDown.setTitle(iBtnDown.isEnabled()
            ? cardsConfig[pDetails.getIndex() + 1].getLocalizedName() : null);
    }
}
