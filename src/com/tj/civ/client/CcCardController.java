/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-03
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
package com.tj.civ.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tj.civ.client.event.CcAllStatesEvent;
import com.tj.civ.client.event.CcAllStatesHandler;
import com.tj.civ.client.event.CcEventBus;
import com.tj.civ.client.event.CcFundsEvent;
import com.tj.civ.client.event.CcFundsHandler;
import com.tj.civ.client.event.CcStateEvent;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcGroup;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.widgets.CcCardCostIndicator;
import com.tj.civ.client.widgets.CcCreditBar;
import com.tj.civ.client.widgets.CcMessageBox;
import com.tj.civ.client.widgets.CcMessageBox.CcResultCallback;


/**
 * Manages the civilization card list grid.
 *
 * @author Thomas Jensen
 */
public final class CcCardController
{
    /** logger for this class */
    //private static final Logger LOG = Logger.getLogger(CcCardController.class.getName());

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

    /** reference to the array of current card states */
    private CcCardCurrent[] iCardsCurrent = null;

    /** the Grid managed by this class */
    private Grid iGrid = null;

    /** the credit bar widgets, ordered by row */
    private CcCreditBar[] iCreditBars = null;

    /** the card cost indicator widgets, ordered by row */
    private CcCardCostIndicator[] iCostIndicators = null;

    /** the card name widgets, ordered by row */
    private Label[] iCardNames = null;

    /** sum of current costs of the currently planned cards */
    private int iPlannedInvestment = 0;

    /** the sum of the nominal costs of all planned cards, plus the sum of the
     *  nominal costs of all cards already owned */
    private int iNominalSumInclPlan = 0;

    /** the state manager passed upon construction */
    private CcCardStateManager iStateCtrl;

    /** indicates if we are in revision mode (<code>true</code>) or in planning
     *  mode (<code>false</code>) */
    private boolean iIsRevising = false;



    private ImageResource getGroupIcon(final CcGroup pGroup)
    {
        ImageResource result = null;
        switch (pGroup) {
            case Arts:
                result = CcConstants.IMG_BUNDLE.groupArts();
                break;
            case Crafts:
                result = CcConstants.IMG_BUNDLE.groupCrafts();
                break;
            case Civics:
                result = CcConstants.IMG_BUNDLE.groupCivics();
                break;
            case Religion:
                result = CcConstants.IMG_BUNDLE.groupReligion();
                break;
            case Sciences:
                result = CcConstants.IMG_BUNDLE.groupSciences();
                break;
            default:
                throw new IllegalArgumentException("unknown group"); //$NON-NLS-1$
        }
        return result;
    }



    private void updateStateWidget(final Grid pGrid, final int pRow,
        final CcState pNewState)
    {
        Widget w = pGrid.getWidget(pRow, COL_STATE);
        if (pNewState == CcState.Owned) {
            if (!(w instanceof Image)) {
                w = new Image(CcConstants.IMG_BUNDLE.stateOwned());
                w.setStyleName(CcConstants.CSS.ccColState());
                pGrid.setWidget(pRow, COL_STATE, w);
            }
        }
        else if (pNewState == CcState.Planned) {
            if (w instanceof Label) {
                ((Label) w).setText("?");  //$NON-NLS-1$
            } else {
                w = new Label("?");  //$NON-NLS-1$
                w.setStyleName(CcConstants.CSS.ccColState());
                pGrid.setWidget(pRow, COL_STATE, w);
            }
        }
        else {
            if (w instanceof Label) {
                ((Label) w).setText(" ");  //$NON-NLS-1$
            } else {
                w = new Label(" ");  //$NON-NLS-1$
                w.setStyleName(CcConstants.CSS.ccColState());
                pGrid.setWidget(pRow, COL_STATE, w);
            }
        }
    }



    private void updateCreditBars(final CcCardConfig pCard)
    {
        int[] creditGiven = pCard.getCreditGiven();
        for (int row = 0; row < creditGiven.length; row++)
        {
            if (creditGiven[row] > 0) {
                iCreditBars[row].update(pCard.getMyIdx());
            }
        }
    }



    private void updateCostIndicators(final CcCardCurrent pCard)
    {
        final CcState state = pCard.getState();
        int[] creditGiven = pCard.getConfig().getCreditGiven();
        for (int rowIdx = 0; rowIdx < creditGiven.length; rowIdx++)
        {
            if (creditGiven[rowIdx] > 0) {
                CcCardCurrent card = pCard.getAllCardsCurrent()[rowIdx];
                if (state == CcState.Owned) {
                    card.setCostCurrent(Math.max(0, card.getCostCurrent() - creditGiven[rowIdx]));
                } else {
                    card.setCostCurrent(Math.max(0, card.getCostCurrent() + creditGiven[rowIdx]));
                }
                iCostIndicators[rowIdx].setCurrentCost(card.getCostCurrent());
            }
        }
    }



    private Panel createGroupIconPanel(final CcCardConfig pCard)
    {
        VerticalPanel result = new VerticalPanel();
        result.setStyleName(CcConstants.CSS.ccPanelGrpIcons());
        for (CcGroup group : pCard.getGroups()) {
            Image grpImg = new Image(getGroupIcon(group));
            if (CcConstants.LOCALE_EN.equalsIgnoreCase(
                LocaleInfo.getCurrentLocale().getLocaleName()))
            {
                grpImg.setAltText(group.getNameEN());
                grpImg.setTitle(group.getNameEN());
            }
            else {
                grpImg.setAltText(group.getNameDE());
                grpImg.setTitle(group.getNameDE());
            }
            result.add(grpImg);
        }
        return result;
    }



    private Label createCardName(final CcCardConfig pCard)
    {
        Label name = new Label(pCard.getLocalizedName());
        name.setStyleName(CcConstants.CSS.ccCardName());
        return name;
    }



    /**
     * Constructor.
     * Set up a new grid to manage.
     * @param pCardsCurrent the current card states
     * @param pStateCtrl the state manager
     */
    public CcCardController(final CcCardCurrent[] pCardsCurrent,
        final CcCardStateManager pStateCtrl)
    {
        super();
        iStateCtrl = pStateCtrl;

        Grid g = new Grid(pCardsCurrent.length, NUM_COLS);
        iCardsCurrent = pCardsCurrent;
        iCreditBars = new CcCreditBar[pCardsCurrent.length];
        iCostIndicators = new CcCardCostIndicator[pCardsCurrent.length];
        iCardNames = new Label[pCardsCurrent.length];
        iNominalSumInclPlan = 0;
        iPlannedInvestment = 0;

        for (int row = 0; row < pCardsCurrent.length; row++)
        {
            HorizontalPanel hp = new HorizontalPanel();
            hp.setWidth("230px");
            iCardNames[row] = createCardName(pCardsCurrent[row].getConfig());
            hp.add(iCardNames[row]);
            CcCardCostIndicator cost = new CcCardCostIndicator(
                pCardsCurrent[row].getConfig().getCostNominal());
            iCostIndicators[row] = cost;
            hp.add(cost);

            VerticalPanel vp = new VerticalPanel();
            vp.add(hp);
            CcCreditBar creditBar = CcCreditBar.create(pCardsCurrent[row]);
            iCreditBars[row] = creditBar;
            vp.add(creditBar);
            //vp.setBorderWidth(1);
            
            updateStateWidget(g, row, pCardsCurrent[row].getState());
            g.setWidget(row, COL_GROUPS, createGroupIconPanel(pCardsCurrent[row].getConfig()));
            g.setWidget(row, COL_CARD, vp);
            Label more = new Label(">>");  //$NON-NLS-1$
            more.setTitle(CcConstants.STRINGS.cardDetails());
            more.setStyleName(CcConstants.CSS.ccColMore());
            g.setWidget(row, COL_MORE, more);

            g.getRowFormatter().setStyleName(row, CcConstants.CSS.ccRow());

            g.getCellFormatter().setStyleName(row, COL_STATE, CcConstants.CSS.ccColState());
            g.getCellFormatter().setStyleName(row, COL_GROUPS, CcConstants.CSS.ccColGrpIcons());
            g.getCellFormatter().setStyleName(row, COL_MORE, CcConstants.CSS.ccColMore());
        }
        g.setCellPadding(0);
        g.setCellSpacing(0);
        //g.setBorderWidth(0);
        g.setStyleName(CcConstants.CSS.ccGrid());
        ColumnFormatter cf = g.getColumnFormatter();
        cf.setWidth(COL_STATE, "30px");
        cf.setWidth(COL_GROUPS, "24px");
        cf.setWidth(COL_CARD, "236px");
        cf.setWidth(COL_MORE, "28px");
        
        g.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                handleGridClick1(pEvent);
            }
        });

        iGrid = g;
        
        CcEventBus.INSTANCE.addHandler(CcFundsEvent.TYPE, new CcFundsHandler() {
            @Override
            public void onFundsChanged(final CcFundsEvent pEvent)
            {
                iStateCtrl.recalcAll(CcCardController.this);
                if (pEvent.isFundsEnabled() && pEvent.getFunds() < iPlannedInvestment) {
                    CcMessageBox.showAsyncMessage("Notice",
                        SafeHtmlUtils.fromString(CcConstants.STRINGS.noFunds()), iGrid);
                }
            }
        });
    }



    /**
     * Initializations of a new instance which cannot be performed in the constructor.
     * Call this only once.
     */
    public void init()
    {
        CcEventBus.INSTANCE.addHandler(CcAllStatesEvent.TYPE, new CcAllStatesHandler() {
            @Override
            public void onAllStatesChanged(final CcAllStatesEvent pEvent)
            {
                if (pEvent.getSource() instanceof CcEntryPoint) {
                    // we are at the end of onModuleLoad()
                    iStateCtrl.recalcAll(CcCardController.this);
                }
            }
        });
    }



    /**
     * Reset the state of all cards in state 'Planned' (if any) to an 'Absent' state.
     */
    public void resetPlans()
    {
        for (CcCardCurrent card : iCardsCurrent)
        {
            if (card.getState() == CcState.Planned) {
                card.setState(CcState.Absent);
                updateStateWidget(iGrid, card.getMyIdx(), CcState.Absent);
                updateCreditBars(card.getConfig());
                updateStyle(card.getMyIdx());
            }
        }
        iPlannedInvestment = 0;
        recalcNominalSumInclPlan();
        iStateCtrl.recalcAll(this);
        CcEventBus.INSTANCE.fireEventFromSource(
            new CcAllStatesEvent(), CcCardController.this);
    }



    /**
     * Determine if any cards are in the state 'Planned'. 
     * @return <code>true</code> if so
     */
    public boolean hasAnyPlans()
    {
        boolean result = false;
        for (CcCardCurrent card : iCardsCurrent)
        {
            if (card.getState() == CcState.Planned) {
                result = true;
                break;
            }
        }
        return result;
    }



    /**
     * Set cards flagged as 'Planned' to 'Owned'.
     */
    public void commit()
    {
        for (CcCardCurrent card : iCardsCurrent)
        {
            if (card.getState() == CcState.Planned) {
                card.setState(CcState.Owned);
                updateStateWidget(iGrid, card.getMyIdx(), CcState.Owned);
                updateCostIndicators(card);
                updateCreditBars(card.getConfig());
                updateStyle(card.getMyIdx());
            }
        }
        iPlannedInvestment = 0;
        recalcNominalSumInclPlan();

        iStateCtrl.recalcAll(this);
        CcEventBus.INSTANCE.fireEventFromSource(
            new CcAllStatesEvent(), CcCardController.this);
    }



    /**
     * Handle a mouse click detected in the card grid.
     * @param pEvent click event that triggered this call
     */
    private void handleGridClick1(final ClickEvent pEvent)
    {
        Cell cell = ((Grid) pEvent.getSource()).getCellForEvent(pEvent);
        int rowIdx = cell.getRowIndex();
        if (cell.getCellIndex() == COL_MORE) {
            // trigger display of card details
            // TODO trigger display of card details
            return;
        }
        final CcCardCurrent card = iCardsCurrent[rowIdx];

        final CcState state = card.getState();
        if (iIsRevising) {
            if (state != CcState.Owned) {
                card.setState(CcState.Owned);
                iNominalSumInclPlan += card.getConfig().getCostNominal();
            } else {
                card.setState(CcState.Absent);
                iNominalSumInclPlan -= card.getConfig().getCostNominal();
            }
            handleGridClick2(card);
        }
        else {
            if (state != CcState.Owned && state != CcState.PrereqFailed) {
                if (state == CcState.Unaffordable || state == CcState.DiscouragedBuy) {
                    CcMessageBox.showOkCancel(CcConstants.STRINGS.askAreYouSure(),
                        getPlanMsg(rowIdx, state), iGrid,
                        new CcResultCallback() {
                            @Override
                            public void onResultAvailable(final boolean pResult)
                            {
                                if (pResult) {
                                    // TODO: Wenn DiscouragedBuy einmal trotzdem durchgeführt
                                    //       wird, die Funktion deaktivieren, da sonst
                                    //       immer alles rot wäre
                                    handleGridClick1a(card, state);
                                }
                            }
                        }
                    );
                }
                else {
                    handleGridClick1a(card, state);
                }
            }
            else {
                // TODO show "forbidden" sign as long as mouse key remains pressed
                //      SliderBar can already do this and has the cursor
            }
        }
    }



    /**
     * This part of the handleGridClick() flow is reached only when a card is
     * clicked in planning mode and the user has confirmed the decision (if asked).
     * @param pCard the card that was clicked
     * @param pState the new state that the card is in
     */
    private void handleGridClick1a(final CcCardCurrent pCard, final CcState pState)
    {
        if (pState != CcState.Planned) {
            pCard.setState(CcState.Planned);
            iPlannedInvestment += pCard.getCostCurrent();
            iNominalSumInclPlan += pCard.getCostCurrent();
        } else {
            pCard.setState(CcState.Absent);
            iPlannedInvestment -= pCard.getCostCurrent();
            iNominalSumInclPlan -= pCard.getCostCurrent();
        }
        handleGridClick2(pCard);
    }



    private void handleGridClick2(final CcCardCurrent pCard)
    {
        // Update card state indicator
        updateStateWidget(iGrid, pCard.getMyIdx(), pCard.getState());

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                // TODO: diese Combo aus Schritten in einer Funktion bündeln (achtung state widget)
                updateCreditBars(pCard.getConfig());
                updateStyle(pCard.getMyIdx());
                if (iIsRevising) {
                    updateCostIndicators(pCard);
                } else {
                    iStateCtrl.recalcAll(CcCardController.this);
                }
                CcEventBus.INSTANCE.fireEventFromSource(
                    new CcStateEvent(pCard.getMyIdx(), pCard.getState()), CcCardController.this);
            }
        });
    }



    private SafeHtml getPlanMsg(final int pRowIdx, final CcState pState)
    {
        SafeHtml result = null;
        if (pState == CcState.DiscouragedBuy) {
            result = SafeHtmlUtils.fromSafeConstant(CcConstants.STRINGS.askDiscouraged()
                + "<br/>" + iCardNames[pRowIdx].getTitle()); //$NON-NLS-1$
        } else if (pState == CcState.Unaffordable) {
            result = SafeHtmlUtils.fromSafeConstant(CcConstants.STRINGS.askUnaffordable());
        } else {
            result = SafeHtmlUtils.fromString("(programming error)"); //$NON-NLS-1$
        }
        return result;
    }



    /**
     * Switches the card controller from planning mode to revise mode.
     */
    public void enterReviseMode()
    {
        for (CcCardCurrent card : iCardsCurrent)
        {
            final CcState previous = card.getState();
            if (previous != CcState.Owned) {
                card.setState(CcState.Absent);
                updateStateWidget(iGrid, card.getMyIdx(), CcState.Absent);
                updateCreditBars(card.getConfig());
                updateStyle(card.getMyIdx());
            }
        }
        iPlannedInvestment = 0;
        iIsRevising = true;
        CcEventBus.INSTANCE.fireEventFromSource(
            new CcAllStatesEvent(), CcCardController.this);
    }

    /**
     * Switches the card controller from revise mode to planning mode.
     */
    public void leaveReviseMode()
    {
        iIsRevising = false;
        iStateCtrl.recalcAll(this);
        CcEventBus.INSTANCE.fireEventFromSource(
            new CcAllStatesEvent(), CcCardController.this);
    }


    private String state2style(final CcState pState)
    {
        String result = null;
        switch (pState) {
            case Owned: result = CcConstants.CSS.ccRowOwned(); break;
            case Planned: result = CcConstants.CSS.ccRowPlanned(); break;
            case Absent: result = CcConstants.CSS.ccRowAbsent(); break;
            case Unaffordable: result = CcConstants.CSS.ccRowUnaffordable(); break;
            case DiscouragedBuy: result = CcConstants.CSS.ccRowDiscouragedBuy(); break;
            case PrereqFailed: result = CcConstants.CSS.ccRowPrereqFailed(); break;
            default: result = ""; break; //$NON-NLS-1$
        }
        return result;
    }

    /**
     * Set the CSS style of the given card to the value required by its state.
     * @param pRowIdx index of the card
     */
    public void updateStyle(final int pRowIdx)
    {
        final CcState state = iCardsCurrent[pRowIdx].getState();
        final String newStyleName = CcConstants.CSS.ccRow()
            + " " + state2style(state);  //$NON-NLS-1$
        final RowFormatter rf = iGrid.getRowFormatter();
        if (!newStyleName.equals(rf.getStyleName(pRowIdx))) {
            rf.setStyleName(pRowIdx, newStyleName);
        }
        
        // States 'Owned', 'Planned', and 'Absent' have no state reasons set.
        if (state.isAffectingCredit() || state == CcState.Absent) {
            setStateReason(pRowIdx, "");  //$NON-NLS-1$
        }
    }



    /**
     * Getter.
     * @return {@link #iGrid}
     */
    public Grid getGrid()
    {
        return iGrid;
    }



    /**
     * Getter.
     * @return {@link #iIsRevising}
     */
    public boolean isRevising()
    {
        // TODO Das sollte static sein, und kann dann per ClientBundle/CssResource
        //      im CSS verwendet werden, um abhängige Style zu haben
        // TODO Ebenso können die Dimensionen im CSS, die von den Icons abhängen,
        //      dynamisch zur Compilezeit an die tatsächlichen Icons angepasst werden
        return iIsRevising;
    }



    /**
     * Getter.
     * @return {@link #iCardsCurrent}
     */
    public CcCardCurrent[] getCardsCurrent()
    {
        return iCardsCurrent;
    }



    /**
     * Determine the number of cards in states 'Owned' or 'Planned'.
     * @return just that
     */
    public int getNumCardsAffectingCredit()
    {
        int result = 0;
        for (CcCardCurrent card : iCardsCurrent) {
            if (card.getState().isAffectingCredit()) {
                result++;
            }
        }
        return result;
    }



    /**
     * Getter.
     * @return {@link #iPlannedInvestment}
     */
    public int getPlannedInvestment()
    {
        return iPlannedInvestment;
    }



    /**
     * Getter.
     * @return {@link #iNominalSumInclPlan}
     */
    public int getNominalSumInclPlan()
    {
        return iNominalSumInclPlan;
    }



    /**
     * Recalculates {@link #iNominalSumInclPlan}, which is required after major
     * updates of the card states.
     */
    private void recalcNominalSumInclPlan()
    {
        int result = 0;
        for (CcCardCurrent card : iCardsCurrent) {
            if (card.getState().isAffectingCredit()) {
                result += card.getConfig().getCostNominal();
            }
        }
        iNominalSumInclPlan = result;
    }



    /**
     * Set the HTML title of a card name to the given <tt>pReason</tt>. This is
     * used to display the reasons for some card states. 
     * @param pRowIdx index of the card
     * @param pReason new title text
     */
    public void setStateReason(final int pRowIdx, final String pReason)
    {
        if (pReason != null) {
            final CcState currentState = iCardsCurrent[pRowIdx].getState();
            if (!currentState.isAffectingCredit() && currentState != CcState.Absent)
            {
                iCardNames[pRowIdx].setTitle(pReason);
            }
        }
    }
}
