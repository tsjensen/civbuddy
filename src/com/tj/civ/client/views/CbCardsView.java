/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-22
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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.CbCardConfig;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbGroup;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.widgets.CbCardCostIndicator;
import com.tj.civ.client.widgets.CbCreditBar;
import com.tj.civ.client.widgets.CbMessageBox;
import com.tj.civ.client.widgets.CbMessageBox.CbResultCallbackIF;
import com.tj.civ.client.widgets.CbMoreArrow;
import com.tj.civ.client.widgets.CbStatistics;


/**
 * Implementation of the 'Cards' view.
 *
 * @author Thomas Jensen
 */
public class CbCardsView
    extends Composite
    implements CbCardsViewIF
{
    /** grid column index, state indicator */
    private static final int COL_STATE = 0;

    /** grid column index, group icons */
    private static final int COL_GROUPS = 1;

    /** grid column index, card info */
    private static final int COL_CARD = 2;

    /** grid column index, more button */
    private static final int COL_MORE = 3;

    /** number of columns in the grid */
    private static final int NUM_COLS = 4;

    /** this view's presenter */
    private CbCardsViewIF.CbPresenterIF iPresenter;

    /** flag indicating revise mode (<code>true</code>) or planning mode
     *  (<code>false</code>) */
    private boolean iReviseMode = false;
    
    /** the buy cards button */
    private Button iBtnCommit = null;
    
    /** the revise button */
    private Button iBtnRevise = null;
    
    /** the 'Funds >' button */
    private Button iBtnFunds = null;

    /** the Grid managed by this class */
    private Grid iGrid = null;

    /** the credit bar widgets, ordered by row */
    private CbCreditBar[] iCreditBars = null;

    /** the card cost indicator widgets, ordered by row */
    private CbCardCostIndicator[] iCostIndicators = null;

    /** the card name widgets, ordered by row */
    private Label[] iCardNames = null;

    /** the statistics widget */
    private CbStatistics iStatsWidget;

    /** the ID of the variant for which we were last initialized */
    private String iLastInitedForVariant = null;



    private Panel createCardButtonPanel()
    {
        iBtnFunds = new Button(CbConstants.STRINGS.funds());
        iBtnFunds.setStyleName(CbConstants.CSS.ccButton());
        iBtnFunds.setTitle(CbConstants.STRINGS.cardsBtnFundsTip());
        iBtnFunds.setEnabled(true);
        iBtnFunds.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(iPresenter.getFundsPlace());
            }
        });

        iBtnCommit = new Button(CbConstants.STRINGS.commit());
        iBtnCommit.setStyleName(CbConstants.CSS.ccButton());
        iBtnCommit.setTitle(CbConstants.STRINGS.btnTitleBuyCards());
        iBtnCommit.setEnabled(false);
        iBtnCommit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (iPresenter.hasAnyPlans()) {
                    iPresenter.commit();
                }
            }
        });

        iBtnRevise = new Button(CbConstants.STRINGS.revise());
        iBtnRevise.setStyleName(CbConstants.CSS.ccButton());
        iBtnRevise.setTitle(CbConstants.STRINGS.btnTitleRevise());
        iBtnRevise.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (isRevising() || !iPresenter.hasAnyPlans()) {
                    toggleReviseMode();
                } else {
                    CbMessageBox.showOkCancel(CbConstants.STRINGS.askAreYouSure(),
                        SafeHtmlUtils.fromString(CbConstants.STRINGS.askClearPlans()),
                        null, new CbResultCallbackIF() {
                            @Override
                            public void onResultAvailable(final boolean pResult)
                            {
                                if (pResult) {
                                    toggleReviseMode();
                                }
                            }
                        }
                    );
                }
            }
        });

        HorizontalPanel result = new HorizontalPanel();
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        result.add(iBtnFunds);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        result.add(iBtnRevise);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        result.add(iBtnCommit);
        result.setStyleName(CbConstants.CSS.ccButtonPanel() + " " //$NON-NLS-1$
            + CbConstants.CSS_BLUEGRADIENT);
        return result;
    }



    /**
     * Constructor.
     */
    public CbCardsView()
    {
        Label heading = new Label(CbConstants.STRINGS.cardsViewTitle());
        heading.setStyleName(CbConstants.CSS.ccHeading());

        Button btnBack = new Button(
            SafeHtmlUtils.fromSafeConstant(CbConstants.STRINGS.changeUser()));
        btnBack.setStyleName(CbConstants.CSS.ccButton());
        btnBack.setTitle(CbConstants.STRINGS.btnTitleChangeUser());
        btnBack.setEnabled(true);
        btnBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(iPresenter.getPlayersPlace());
            }
        });

        HorizontalPanel headPanel = new HorizontalPanel();
        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        headPanel.add(btnBack);
        headPanel.setCellWidth(btnBack, "12%"); //$NON-NLS-1$
        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        headPanel.add(heading);
        headPanel.setStyleName(CbConstants.CSS.ccButtonPanel());
        headPanel.addStyleName(CbConstants.CSS_BLUEGRADIENT);

        iStatsWidget = new CbStatistics(0, 0);

        VerticalPanel outerVP = new VerticalPanel();
        outerVP.add(headPanel);
        outerVP.add(createCardButtonPanel());
        outerVP.add(iStatsWidget);
        outerVP.add(createCardsGrid());

        initWidget(outerVP);
    }



    @Override
    public void setPresenter(final CbCardsViewIF.CbPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public boolean isCommitButtonEnabled()
    {
        return iBtnCommit.isEnabled();
    }

    @Override
    public void setCommitButtonEnabled(final boolean pEnabled)
    {
        iBtnCommit.setEnabled(pEnabled);
    }



    private Label createCardName(final String pLocalizedName)
    {
        Label name = new Label(pLocalizedName);
        name.setStyleName(CbConstants.CSS.ccCardName());
        return name;
    }



    private Grid createCardsGrid()
    {
        final int numRowsInit = 0;
        Grid g = new Grid(numRowsInit, NUM_COLS);
        iCreditBars = new CbCreditBar[numRowsInit];
        iCostIndicators = new CbCardCostIndicator[numRowsInit];
        iCardNames = new Label[numRowsInit];

        g.setCellPadding(0);
        g.setCellSpacing(0);
        g.setBorderWidth(0);
        g.setStyleName(CbConstants.CSS.ccGrid());
        ColumnFormatter cf = g.getColumnFormatter();
        cf.setWidth(COL_STATE, "30px");
        cf.setWidth(COL_GROUPS, "24px");
        cf.setWidth(COL_CARD, "236px");
        cf.setWidth(COL_MORE, "28px");
        
        g.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                Cell cell = ((Grid) pEvent.getSource()).getCellForEvent(pEvent);
                int rowIdx = cell.getRowIndex();
                if (cell.getCellIndex() == COL_MORE) {
                    iPresenter.onMoreClicked(rowIdx);
                } else {
                    iPresenter.onStateClicked(rowIdx);
                }
            }
        });

        iGrid = g;
        return g;
    }



    @Override
    public void initializeGridContents(final CbCardCurrent[] pCardsCurrent,
        final String pVariantId)
    {
        final int numRows = pCardsCurrent.length;
        iGrid.resize(numRows, NUM_COLS);
        iCreditBars = new CbCreditBar[numRows];
        iCostIndicators = new CbCardCostIndicator[numRows];
        iCardNames = new Label[numRows];
        iLastInitedForVariant = pVariantId;

        for (int row = 0; row < numRows; row++)
        {
            iCardNames[row] = createCardName(
                pCardsCurrent[row].getConfig().getLocalizedName());

            CbCardCostIndicator cost = new CbCardCostIndicator(
                pCardsCurrent[row].getConfig().getCostNominal());
            iCostIndicators[row] = cost;

            CbCreditBar creditBar = CbCreditBar.create(pCardsCurrent[row]);
            iCreditBars[row] = creditBar;

            HorizontalPanel hp = new HorizontalPanel();
            hp.setWidth("236px");
            hp.add(iCardNames[row]);
            hp.add(cost);
            VerticalPanel vp = new VerticalPanel();
            vp.add(hp);
            vp.add(creditBar);
            
            updateStateWidget(iGrid, row, pCardsCurrent[row].getState());
            iGrid.setWidget(row, COL_GROUPS, createGroupIconPanel(pCardsCurrent[row].getConfig()));
            iGrid.setWidget(row, COL_CARD, vp);
            iGrid.setWidget(row, COL_MORE, new CbMoreArrow(CbConstants.STRINGS.cardDetails()));

            iGrid.getRowFormatter().setStyleName(row, CbConstants.CSS.ccRow());

            iGrid.getCellFormatter().setStyleName(row, COL_STATE, CbConstants.CSS.ccColState());
            iGrid.getCellFormatter().setStyleName(row, COL_GROUPS, CbConstants.CSS.ccColGrpIcons());
            iGrid.getCellFormatter().setStyleName(row, COL_MORE, CbConstants.CSS.ccColMore());
        }
        
        iStatsWidget.addEventHandlers(iPresenter.getEventBus());
    }



    private Panel createGroupIconPanel(final CbCardConfig pCard)
    {
        VerticalPanel result = new VerticalPanel();
        result.setStyleName(CbConstants.CSS.ccPanelGrpIcons());
        for (CbGroup group : pCard.getGroups()) {
            Image grpImg = new Image(group.getIcon());
            if (CbConstants.LOCALE_DE.equalsIgnoreCase(
                LocaleInfo.getCurrentLocale().getLocaleName()))
            {
                grpImg.setAltText(group.getNameDE());
                grpImg.setTitle(group.getNameDE());
            }
            else {
                grpImg.setAltText(group.getNameEN());
                grpImg.setTitle(group.getNameEN());
            }
            result.add(grpImg);
        }
        return result;
    }



    private void updateStateWidget(final Grid pGrid, final int pRow,
        final CbState pNewState)
    {
        Widget w = pGrid.getWidget(pRow, COL_STATE);
        if (pNewState == CbState.Owned) {
            if (!(w instanceof Image)) {
                w = new Image(CbConstants.IMG_BUNDLE.stateOwned());
                w.setStyleName(CbConstants.CSS.ccColState());
                pGrid.setWidget(pRow, COL_STATE, w);
            }
        }
        else if (pNewState == CbState.Planned) {
            if (w instanceof Label) {
                ((Label) w).setText("?");  //$NON-NLS-1$
            } else {
                w = new Label("?");  //$NON-NLS-1$
                w.setStyleName(CbConstants.CSS.ccColState());
                pGrid.setWidget(pRow, COL_STATE, w);
            }
        }
        else {
            if (w instanceof Label) {
                ((Label) w).setText(" ");  //$NON-NLS-1$
            } else {
                w = new Label(" ");  //$NON-NLS-1$
                w.setStyleName(CbConstants.CSS.ccColState());
                pGrid.setWidget(pRow, COL_STATE, w);
            }
        }
    }



    private String state2style(final CbState pState)
    {
        String result = null;
        switch (pState) {
            case Owned: result = CbConstants.CSS.ccRowOwned(); break;
            case Planned: result = CbConstants.CSS.ccRowPlanned(); break;
            case Absent: result = CbConstants.CSS.ccRowAbsent(); break;
            case Unaffordable: result = CbConstants.CSS.ccRowUnaffordable(); break;
            case DiscouragedBuy: result = CbConstants.CSS.ccRowDiscouragedBuy(); break;
            case PrereqFailed: result = CbConstants.CSS.ccRowPrereqFailed(); break;
            default: result = ""; break; //$NON-NLS-1$
        }
        return result;
    }



    private void toggleReviseMode()
    {
        if (isRevising())
        {
            iReviseMode = false;
            iPresenter.leaveReviseMode();
            iBtnRevise.setText(CbConstants.STRINGS.revise());
            iBtnFunds.setEnabled(true);
            // leave commit button disabled
        }
        else {
            setCommitButtonEnabled(false);
            iBtnRevise.setText(CbConstants.STRINGS.reviseDone());
            iBtnFunds.setEnabled(false);
            iReviseMode = true;
            iPresenter.enterReviseMode();
        }
    }



    @Override
    public boolean isRevising()
    {
        // TODO Das sollte static sein, und kann dann per ClientBundle/CssResource
        //      im CSS verwendet werden, um abhängige Style zu haben
        // TODO Ebenso können die Dimensionen im CSS, die von den Icons abhängen,
        //      dynamisch zur Compilezeit an die tatsächlichen Icons angepasst werden
        return iReviseMode;
    }



    @Override
    public void setState(final int pRowIdx, final CbState pNewState,
        final String pStateReason)
    {
        updateStateWidget(iGrid, pRowIdx, pNewState);

        // update row style
        final RowFormatter rf = iGrid.getRowFormatter();
        rf.setStyleName(pRowIdx, CbConstants.CSS.ccRow());
        rf.addStyleName(pRowIdx, state2style(pNewState));

        // update state reason
        if (pNewState.isAffectingCredit() || pNewState == CbState.Absent) {
            // States 'Owned', 'Planned', and 'Absent' have no state reasons
            iCardNames[pRowIdx].setTitle(null);
        } else {
            iCardNames[pRowIdx].setTitle(pStateReason);
        }
        
        // TODO once the state is set to 'Owned', display only the nominal cost
    }



    @Override
    public void updateStats(final int pPointsTarget, final Integer pNumCardsLimit)
    {
        iStatsWidget.setLimits(pPointsTarget, pNumCardsLimit);
        iStatsWidget.handleAllStatesChanged();
    }



    @Override
    public void setCostDisplay(final int pRowIdx, final int pCurrentCost)
    {
        iCostIndicators[pRowIdx].setCurrentCost(pCurrentCost);
    }



    @Override
    public void updateCreditBar(final int pRowIdx, final int pGivingCardIdx)
    {
        iCreditBars[pRowIdx].update(pGivingCardIdx);
    }



    @Override
    public String getStateReason(final int pRowIdx)
    {
        return iCardNames[pRowIdx].getTitle();
    }



    @Override
    public Widget getWidget()
    {
        return super.getWidget();
    }



    @Override
    public void updateFunds(final int pTotalFunds, final boolean pEnabled)
    {
        iStatsWidget.updateFunds(pTotalFunds, pEnabled);
    }



    @Override
    public void setDesperate(final boolean pIsDesperate)
    {
        iStatsWidget.setDesperate(pIsDesperate);
    }



    @Override
    public String getLastVariantId()
    {
        return iLastInitedForVariant;
    }
}
