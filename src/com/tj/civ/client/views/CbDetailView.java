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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.model.CbGroup;
import com.tj.civ.client.model.vo.CbDetailVO;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbDetailPlace;


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
            
            FlowPanel fp = new FlowPanel();
            fp.add(grpImg);
            fp.add(name);

            initWidget(fp);
        }
    }



    /**
     * Constructor.
     */
    public CbDetailView()
    {
        HorizontalPanel headPanel = new HorizontalPanel();
        Label heading = new Label("Details");
        heading.setStyleName(CbConstants.CSS.ccHeading());

        Button btnBack = new Button(SafeHtmlUtils.fromSafeConstant("&lt;&nbsp;Cards"));
        btnBack.setStyleName(CbConstants.CSS.ccButton());
        btnBack.setTitle("Back to the cards");
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
                    Math.max(iCurrentViewObject.getIndex() + 1,
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
        iLblTitle.setStyleName("TODO");   // TODO style

        iGroupsPanel = new FlowPanel();

        iLblStateDesc = new Label("No card selected.");   //$NON-NLS-1$
        iLblStateDesc.setStyleName("TODO");    // TODO style

        Label lblCredit = new InlineLabel(buildCreditMsg(0));
        lblCredit.setStyleName(CbConstants.CSS.ccDetailSectionTitle());
        iCreditPanel = new FlowPanel();
        iCreditPanel.setStyleName(CbConstants.CSS.ccDetailSection());
        iCreditPanel.add(lblCredit);

        Label lblAttrCapt = new InlineLabel("Attributes" + ": "); //$NON-NLS-2$
        lblAttrCapt.setStyleName(CbConstants.CSS.ccDetailSectionTitle());
        iLblAttributes = new InlineLabel("None"); //$NON-NLS-1$
        Panel attrPanel = new FlowPanel();
        attrPanel.setStyleName(CbConstants.CSS.ccDetailSection());
        attrPanel.add(lblAttrCapt);
        attrPanel.add(iLblAttributes);

        Label lblCalaCapt = new InlineLabel("Calamity Effects" + ": "); //$NON-NLS-2$
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



    private String buildCreditMsg(final int pCreditPrecentage)
    {
        return "Credit"
            + " (" + pCreditPrecentage + "%): "; //$NON-NLS-1$ //$NON-NLS-2$
    }



    private String buildSupportsMsg(final int pTotalSupport)
    {
        return "Supports"
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
        iLblTitle.setText(buildTitleMsg(pDetails.getDisplayName(),
            pDetails.getCostCurrent(), pDetails.getCostNominal()));

        // TODO implement showCard()

        iLblAttributes.setText(pDetails.getAttributes());
        iLblCalamityEffects.setText(pDetails.getCalamityEffects());

        // TODO implement showCard()
    }
}