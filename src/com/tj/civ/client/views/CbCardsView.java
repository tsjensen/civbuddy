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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.widgets.CbCardWidget;
import com.tj.civ.client.widgets.CbIconButton;
import com.tj.civ.client.widgets.CbMessageBox;
import com.tj.civ.client.widgets.CbMessageBox.CbResultCallbackIF;
import com.tj.civ.client.widgets.CbMoreArrow;
import com.tj.civ.client.widgets.CbNavigationButton;
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
    /** this view's presenter */
    private CbCardsViewIF.CbPresenterIF iPresenter;

    /** flag indicating revise mode (<code>true</code>) or planning mode
     *  (<code>false</code>) */
    private boolean iReviseMode = false;

    /** the header title of the view */
    private Label iViewTitle;

    /** the buy cards button */
    private CbIconButton iBtnCommit;
    
    /** the revise button */
    private CbIconButton iBtnRevise;
    
    /** the 'Funds >' button */
    private CbNavigationButton iBtnFunds;

    /** the list of cards in this view */
    private FlowPanel iCardsPanel;

    /** the statistics widget */
    private CbStatistics iStatsWidget;

    /** the ID of the variant for which we were last initialized */
    private String iLastInitedForVariant = null;



    private Panel createCardBottomBar()
    {
        iBtnRevise = new CbIconButton(CbIconButton.CbPosition.center,
            CbConstants.IMG_BUNDLE.iconRevise());
        iBtnRevise.setTitle(CbConstants.STRINGS.btnTitleRevise());
        iBtnRevise.setEnabled(true);
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

        iBtnCommit = new CbIconButton(CbIconButton.CbPosition.right,
            CbConstants.IMG_BUNDLE.iconBuy());
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

        FlowPanel result = new FlowPanel();
        result.add(iBtnRevise);
        result.add(iBtnCommit);
        result.setStyleName(CbConstants.CSS.cbBottomBar());
        result.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        result.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());
        return result;
    }



    /**
     * Constructor.
     */
    public CbCardsView()
    {
        iViewTitle = new InlineLabel(CbConstants.STRINGS.cardsViewTitle());

        CbNavigationButton btnBack = new CbNavigationButton(
            CbNavigationButton.CbPosition.left, CbConstants.STRINGS.changeUser(),
            CbConstants.IMG_BUNDLE.navIconPlayers(), CbConstants.STRINGS.btnTitleChangeUser());
        btnBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(iPresenter.getPlayersPlace());
            }
        });

        iBtnFunds = new CbNavigationButton(CbNavigationButton.CbPosition.right,
            CbConstants.STRINGS.funds(), CbConstants.STRINGS.cardsBtnFundsTip());
        iBtnFunds.setEnabled(true);
        iBtnFunds.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(iPresenter.getFundsPlace());
            }
        });

        FlowPanel headPanel = new FlowPanel();
        headPanel.add(btnBack);
        headPanel.add(iViewTitle);
        headPanel.add(iBtnFunds);
        headPanel.setStyleName(CbConstants.CSS.cbTitleBar());
        headPanel.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        headPanel.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());

        iStatsWidget = new CbStatistics(0, 0);

        iCardsPanel = new FlowPanel();
        iCardsPanel.setStyleName(CbConstants.CSS.cbPageItem());

        FlowPanel viewPanel = new FlowPanel();
        viewPanel.add(headPanel);
        viewPanel.add(iStatsWidget);
        viewPanel.add(iCardsPanel);
        viewPanel.add(createCardBottomBar());
        viewPanel.setStyleName(CbConstants.CSS.cbCardsViewMargin());
        initWidget(viewPanel);
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



    @Override
    public void initializeGridContents(final CbCardCurrent[] pCardsCurrent,
        final String pVariantId)
    {
        final int numRows = pCardsCurrent.length;
        iLastInitedForVariant = pVariantId;

        final ClickHandler stateClickHandler = new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                int rowIdx = ((CbCardWidget) pEvent.getSource()).getMyIdx();
                iPresenter.onStateClicked(rowIdx);
            }
        };
        final ClickHandler moreClickHandler = new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                int rowIdx = ((CbMoreArrow) pEvent.getSource()).getMyIdx();
                iPresenter.onMoreClicked(rowIdx);
            }
        };

        iCardsPanel.clear();
        for (int row = 0; row < numRows; row++)
        {
            CbCardWidget cw = new CbCardWidget(pCardsCurrent[row]);
            cw.addClickHandler(stateClickHandler);
            CbMoreArrow more = new CbMoreArrow(CbConstants.STRINGS.cardDetails(), row);
            more.addClickHandler(moreClickHandler);

            FlowPanel fp = new FlowPanel();
            fp.add(cw);
            fp.add(more);
            fp.setStyleName(CbConstants.CSS.cbGeneralListItem());
            iCardsPanel.add(fp);
        }
        
        iStatsWidget.addEventHandlers(iPresenter.getEventBus());
    }



    private void toggleReviseMode()
    {
        // TODO change CSS to indicate mode
        if (isRevising())
        {
            iReviseMode = false;
            iPresenter.leaveReviseMode();
            iBtnRevise.setTitle(CbConstants.STRINGS.revise());
            iBtnFunds.setEnabled(true);
            // leave commit button disabled
        }
        else {
            setCommitButtonEnabled(false);
            iBtnRevise.setTitle(CbConstants.STRINGS.reviseDone());
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
        CbCardWidget cw = getCardsWidget(pRowIdx);
        cw.setState(pNewState);

        // update state reason
        if (pNewState.isAffectingCredit() || pNewState == CbState.Absent) {
            // States 'Owned', 'Planned', and 'Absent' have no state reasons
            cw.setStateReason(null);
        } else {
            cw.setStateReason(pStateReason);
        }

        // update row style
        Widget row = iCardsPanel.getWidget(pRowIdx);
        row.setStyleName(CbConstants.CSS.cbGeneralListItem());
        if (pNewState == CbState.Unaffordable) {
            row.addStyleName(CbConstants.CSS.cbCardsViewStateUnaffordable());
        } else if (pNewState == CbState.PrereqFailed) {
            row.addStyleName(CbConstants.CSS.cbCardsViewStatePrereqFailed());
        } else if (pNewState == CbState.DiscouragedBuy) {
            row.addStyleName(CbConstants.CSS.cbCardsViewStateDiscouragedBuy());
        }
    }



    private CbCardWidget getCardsWidget(final int pRowIdx)
    {
        return (CbCardWidget) ((FlowPanel) iCardsPanel.getWidget(pRowIdx)).getWidget(0);
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
        getCardsWidget(pRowIdx).setCostDisplay(pCurrentCost);
    }



    @Override
    public void updateCreditBar(final int pRowIdx, final int pGivingCardIdx)
    {
        getCardsWidget(pRowIdx).updateCreditBar(pGivingCardIdx);
    }



    @Override
    public String getStateReason(final int pRowIdx)
    {
        return getCardsWidget(pRowIdx).getStateReason();
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



    @Override
    public void setTitleHeading(final String pTitle)
    {
        iViewTitle.setText(pTitle);
    }
}
