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
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbState;
import com.tj.civ.client.widgets.CbBarButton;
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
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbCardsView.class);

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

    /** the 'Done' button to exit revise mode */
    private CbBarButton iBtnExitRevise;

    /** a label saying 'Revising ...' shown on bottom bar during revise mode */
    private Label iRevisingMsg;

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
        iRevisingMsg = new InlineLabel(CbConstants.STRINGS.viewCardsMsgRevising());
        iRevisingMsg.setVisible(false);

        iBtnExitRevise = new CbBarButton(CbBarButton.CbPosition.right,
            CbConstants.STRINGS.viewCardsButtonReviseDone(),
            CbConstants.STRINGS.viewCardsButtonReviseDoneTitle());
        iBtnExitRevise.setVisible(false);
        iBtnExitRevise.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                // leave revise mode
                toggleReviseMode();
            }
        });

        iBtnRevise = new CbIconButton(CbIconButton.CbPosition.center,
            CbConstants.IMG_BUNDLE.iconRevise());
        iBtnRevise.setTitle(CbConstants.STRINGS.viewCardsButtonReviseTitle());
        iBtnRevise.setEnabled(true);
        iBtnRevise.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                // enter revise mode
                if (!iPresenter.hasAnyPlans()) {
                    toggleReviseMode();
                } else {
                    CbMessageBox.showOkCancel(CbConstants.STRINGS.askAreYouSure(),
                        SafeHtmlUtils.fromString(CbConstants.STRINGS.viewCardsAskClearPlans()),
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
        iBtnCommit.setTitle(CbConstants.STRINGS.viewCardsButtonBuyTitle());
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

        FlowPanel bottomBar = new FlowPanel();
        bottomBar.add(iRevisingMsg);
        bottomBar.add(iBtnExitRevise);
        bottomBar.add(iBtnRevise);
        bottomBar.add(iBtnCommit);
        bottomBar.setStyleName(CbConstants.CSS.cbBottomBar());
        bottomBar.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        bottomBar.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());

        FlowPanel bottomBarIeWrapper = new FlowPanel();
        bottomBarIeWrapper.setStyleName(CbConstants.CSS.cbBottomBarIeWrapper());
        bottomBarIeWrapper.add(bottomBar);
        return bottomBarIeWrapper;
    }



    /**
     * Constructor.
     */
    public CbCardsView()
    {
        iViewTitle = new InlineLabel(CbConstants.STRINGS.viewCardsHeading());

        CbNavigationButton btnBack = new CbNavigationButton(
            CbNavigationButton.CbPosition.left, CbConstants.STRINGS.viewCardsNavbuttonBack(),
            CbConstants.IMG_BUNDLE.navIconPlayers(),
            CbConstants.STRINGS.viewCardsNavbuttonBackTitle());
        btnBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (isRevising()) {
                    toggleReviseMode();
                }
                iPresenter.goTo(iPresenter.getPlayersPlace());
            }
        });

        iBtnFunds = new CbNavigationButton(CbNavigationButton.CbPosition.right,
            CbConstants.STRINGS.viewCardsNavbuttonForward(),
            CbConstants.STRINGS.viewCardsNavbuttonForwardTitle());
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
        FlowPanel headPanelIeWrapper = new FlowPanel();
        headPanelIeWrapper.setStyleName(CbConstants.CSS.cbTitleBarIeWrapper());
        headPanelIeWrapper.add(headPanel);

        iStatsWidget = new CbStatistics(0, 0);

        iCardsPanel = new FlowPanel();
        iCardsPanel.setStyleName(CbConstants.CSS.cbPageItem());

        FlowPanel viewPanel = new FlowPanel();
        viewPanel.add(headPanelIeWrapper);
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

        // TODO listen for CbGameLoadedEvent, like funds view (-> performance)
        iCardsPanel.clear();
        for (int row = 0; row < numRows; row++)
        {
            CbCardWidget cw = new CbCardWidget(pCardsCurrent[row]);
            cw.addClickHandler(stateClickHandler);
            CbMoreArrow more = new CbMoreArrow(CbConstants.STRINGS.viewCardsDetailsTitle(), row);
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
        if (isRevising())
        {
            iReviseMode = false;
            iPresenter.leaveReviseMode();
            iBtnExitRevise.setVisible(false);
            iBtnRevise.setVisible(true);
            iRevisingMsg.setVisible(false);
            iBtnFunds.setEnabled(true);
            iBtnCommit.setVisible(true);
            // leave commit button disabled
        }
        else {
            setCommitButtonEnabled(false);
            iBtnCommit.setVisible(false);
            iBtnRevise.setVisible(false);
            iBtnExitRevise.setVisible(true);
            iRevisingMsg.setVisible(true);
            iBtnFunds.setEnabled(false);
            iReviseMode = true;
            iPresenter.enterReviseMode();
        }
    }



    @Override
    public boolean isRevising()
    {
        return iReviseMode;
    }



    @Override
    public void setState(final int pRowIdx, final CbState pNewState,
        final String pStateReason)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setState",  //$NON-NLS-1$
                new String[]{"pRowIdx", "pNewState",  //$NON-NLS-1$//$NON-NLS-2$
                    "pStateReason"},  //$NON-NLS-1$
                new Object[]{Integer.valueOf(pRowIdx), pNewState, pStateReason});
        }
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

        LOG.exit("setState"); //$NON-NLS-1$
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
