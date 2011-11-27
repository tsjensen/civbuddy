/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 31.03.2011
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CbGameLoadedEvent;
import com.tj.civ.client.event.CbGameLoadedHandlerIF;
import com.tj.civ.client.model.CbVariantConfig;
import com.tj.civ.client.model.jso.CbCommodityConfigJSO;
import com.tj.civ.client.model.jso.CbFundsJSO;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbPlayersPlace;
import com.tj.civ.client.widgets.CbCheckBox;
import com.tj.civ.client.widgets.CbIconButton;
import com.tj.civ.client.widgets.CbLabel;
import com.tj.civ.client.widgets.CbMessageBox;
import com.tj.civ.client.widgets.CbMessageBox.CbResultCallbackIF;
import com.tj.civ.client.widgets.CbNavigationButton;
import com.tj.civ.client.widgets.CbStatsIndicator;


/**
 * Implementation of the 'Funds' view.
 *
 * @author Thomas Jensen
 */
public class CbFundsView
    extends Composite
    implements CbFundsViewIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbFundsView.class);

    /** name of the ListBox's DOM attribute giving its commodity index */
    private static final String DOMATTR_SEL_IDX = "cbSelIdx"; //$NON-NLS-1$

    /** this view's presenter */
    private CbFundsViewIF.CbPresenterIF iPresenter;

    /** the ID of the variant for which we were last initialized */
    private String iLastInitedForVariant = null;

    /** The text box containing the total funds value. Only editable when detailed
     *  tracking is turned off. */
    private IntegerBox iTotalFundsBox;

    /** text box containing the treasury value */
    private IntegerBox iTreasuryBox;

    /** text box containing the bonus value */
    private IntegerBox iBonusBox;

    /** Stats row entry about the current total funds */
    private CbStatsIndicator iTotalFundsIndicator;

    /** Stats row entry about the number of commodity cards */
    private CbStatsIndicator iNumCommIndicator;

    /** the 'clear all funds' button */
    private CbIconButton iBtnClear;

    /** the checkbox row for overall enabling/disabling of funds tracking */
    private FlowPanel iCheckBoxRowOverall;

    /** the checkbox row for enabling/disabling detail tracking */
    private FlowPanel iCheckBoxRowDetailed;

    /** Panel including all the widgets for detailed funds tracking */
    private FlowPanel iDetailPanel;

    /** Panel including all the widgets for coarse funds tracking */
    private FlowPanel iCoarsePanel;

    /** view title heading */
    private Label iViewTitle;

    /** focus handler which selects the entire input text when the box receives focus */
    private static final FocusHandler TXTFOCUSHANDLER = new FocusHandler() {
        @Override
        public void onFocus(final FocusEvent pEvent)
        {
            ValueBoxBase<?> source = (ValueBoxBase<?>) pEvent.getSource();
            if (source.getValue() != null) {
                source.setSelectionRange(0, source.getText().length());
            }
        }
    };



    /**
     * Constructor.
     * @param pEventBus the event bus, used to register the game change handler
     */
    public CbFundsView(final EventBus pEventBus)
    {
        super();
        LOG.enter(CbLogAdapter.CONSTRUCTOR);

        /*
         * Title Bar
         */
        iViewTitle = new InlineLabel("Funds"); //$NON-NLS-1$
        // heading is set to the player name when the activity starts

        final CbNavigationButton btnBack = new CbNavigationButton(
            CbNavigationButton.CbPosition.left, CbConstants.STRINGS.fundsBtnBack(),
            CbConstants.STRINGS.fundsBtnBackTitle());
        btnBack.addButton(CbConstants.IMG_BUNDLE.navIconPlayers(),
            CbConstants.STRINGS.btnTitleChangeUser());
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
        headPanel.add(iViewTitle);
        headPanel.setStyleName(CbConstants.CSS.cbTitleBar());
        headPanel.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        headPanel.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());
        FlowPanel headPanelIeWrapper = new FlowPanel();
        headPanelIeWrapper.setStyleName(CbConstants.CSS.cbTitleBarIeWrapper());
        headPanelIeWrapper.add(headPanel);

        /*
         * Extra Bar (stats)
         */
        FlowPanel extraPanelIeWrapper = buildExtraBar();

        /*
         * Bottom Bar (Action Buttons)
         */
        FlowPanel bottomBarIeWrapper = buildBottomBar();

        /*
         * the czechboxes
         */
        iCheckBoxRowOverall = buildCheckBoxRow(CbConstants.STRINGS.viewFundsCheckboxMain(),
            true, new ClickHandler() {
                @Override
                public void onClick(final ClickEvent pEvent)
                {
                    boolean oldValue = getCheckboxValue(iCheckBoxRowOverall);
                    iPresenter.onEnableToggled(!oldValue);
                }
        });
        iCheckBoxRowDetailed = buildCheckBoxRow(CbConstants.STRINGS.fundsDetailed(),
            false, new ClickHandler() {
                @Override
                public void onClick(final ClickEvent pEvent)
                {
                    if (((CbCheckBox) iCheckBoxRowDetailed.getWidget(1)).isEnabled()) {
                        boolean oldValue = getCheckboxValue(iCheckBoxRowDetailed);
                        iPresenter.onDetailToggled(!oldValue);
                    }
                }
        });
        FlowPanel cbPanel = new FlowPanel();
        cbPanel.setStyleName(CbConstants.CSS.cbPageItem());
        cbPanel.add(iCheckBoxRowOverall);
        cbPanel.add(iCheckBoxRowDetailed);

        /*
         * Page items for coarse tracking
         */
        CbLabel lblTotalFunds = new CbLabel(CbConstants.STRINGS.fundsTotalLabel(), true,
            CbConstants.CSS.cbPageItemInputLabel(), CbConstants.CSS.cbPageItemInputLabelDisabled());
        lblTotalFunds.setEnabled(false);

        iTotalFundsBox = new IntegerBox();
        // TODO extract into a widget (with bonus box) and add validation
        final int maxLen = String.valueOf(CbFundsJSO.MAX_TOTAL_FUNDS).length();
        iTotalFundsBox.setMaxLength(maxLen);
        iTotalFundsBox.setVisibleLength(maxLen);
        iTotalFundsBox.setAlignment(TextAlignment.RIGHT);
        iTotalFundsBox.setEnabled(false);
        iTotalFundsBox.addFocusHandler(TXTFOCUSHANDLER);
        // use numerical input pad on iPhone
        iTotalFundsBox.getElement().setAttribute("pattern", "[0-9]*"); //$NON-NLS-1$ //$NON-NLS-2$
        iTotalFundsBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> pEvent)
            {
                iPresenter.onTotalFundsBoxChanged(pEvent.getValue());
            }
        });
        
        FlowPanel totalFundsRow = new FlowPanel();
        totalFundsRow.setStyleName(CbConstants.CSS.cbPageItemInput());
        totalFundsRow.add(lblTotalFunds);
        totalFundsRow.add(iTotalFundsBox);
        
        iCoarsePanel = new FlowPanel();
        iCoarsePanel.setStyleName(CbConstants.CSS.cbPageItem());
        iCoarsePanel.add(totalFundsRow);
        
        /*
         * Page items for DETAILED tracking
         * (empty until initialized for a variant)
         */

        iDetailPanel = new FlowPanel();
        iDetailPanel.setStyleName(CbConstants.CSS.cbPageItem());
        iDetailPanel.setVisible(false);

        /*
         * View widget
         */
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.setStyleName(CbConstants.CSS.cbFundsViewMargin());
        viewPanel.add(headPanelIeWrapper);
        viewPanel.add(extraPanelIeWrapper);
        viewPanel.add(cbPanel);
        viewPanel.add(iCoarsePanel);
        viewPanel.add(iDetailPanel);
        viewPanel.add(bottomBarIeWrapper);
        initWidget(viewPanel);
        
        // register for game change events
        pEventBus.addHandler(CbGameLoadedEvent.TYPE, new CbGameLoadedHandlerIF() {
            @Override
            public void onGameLoaded(final CbGameLoadedEvent pEvent)
            {
                CbVariantConfig variant = CbGlobal.getGame().getVariant();
                initializeVariant(variant.getPersistenceKey(), variant.getCommodities());
            }
        });
        
        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    private FlowPanel buildExtraBar()
    {
        iTotalFundsIndicator = new CbStatsIndicator(CbConstants.STRINGS.statsFunds(), null, true);
        iTotalFundsIndicator.addStyleName(CbConstants.CSS.cbExtraBarNorthWest());

        iNumCommIndicator = new CbStatsIndicator(
            CbConstants.STRINGS.fundsCommodities(), null, false);
        iNumCommIndicator.addStyleName(CbConstants.CSS.cbExtraBarNorthEast());

        FlowPanel extraBar = new FlowPanel();
        extraBar.add(iTotalFundsIndicator);
        extraBar.add(iNumCommIndicator);
        extraBar.setStyleName(CbConstants.CSS.cbExtraBar());
        extraBar.addStyleName(CbConstants.CSS_EXTRABAR_GRADIENT);
        extraBar.addStyleName(CbConstants.CSS.cbExtraBarFunds());

        FlowPanel extraPanelIeWrapper = new FlowPanel();
        extraPanelIeWrapper.setStyleName(CbConstants.CSS.cbExtraBarIeWrapper());
        extraPanelIeWrapper.addStyleName(CbConstants.CSS.cbExtraBarFunds());
        extraPanelIeWrapper.add(extraBar);

        return extraPanelIeWrapper;
    }



    private FlowPanel buildBottomBar()
    {
        iBtnClear = new CbIconButton(CbIconButton.CbPosition.right,
            CbConstants.IMG_BUNDLE.iconClear());
        iBtnClear.setTitle(CbConstants.STRINGS.clearFundsDesc());
        iBtnClear.setEnabled(false);
        iBtnClear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                CbMessageBox.showOkCancel(CbConstants.STRINGS.askAreYouSure(),
                    SafeHtmlUtils.fromString(CbConstants.STRINGS.askClearFunds()),
                    CbFundsView.this, new CbResultCallbackIF() {
                        @Override
                        public void onResultAvailable(final boolean pOkPressed)
                        {
                            if (pOkPressed) {
                                iPresenter.reset();
                                reset();
                    }   }   }
                );
            }
        });

        FlowPanel bottomBar = new FlowPanel();
        bottomBar.add(iBtnClear);
        bottomBar.setStyleName(CbConstants.CSS.cbBottomBar());
        bottomBar.addStyleName(CbConstants.CSS_TITLEBAR_GRADIENT);
        bottomBar.addStyleName(CbConstants.CSS.cbTitleBarTextShadow());

        FlowPanel bottomBarIeWrapper = new FlowPanel();
        bottomBarIeWrapper.setStyleName(CbConstants.CSS.cbBottomBarIeWrapper());
        bottomBarIeWrapper.add(bottomBar);
        
        return bottomBarIeWrapper;
    }



    private FlowPanel buildCheckBoxRow(final String pLabelCaption,
        final boolean pEnabled, final ClickHandler pClickHandler)
    {
        CbLabel lblRow = new CbLabel(pLabelCaption, true,
            CbConstants.CSS.cbPageItemCheckBoxLabel(),
            CbConstants.CSS.cbPageItemCheckBoxLabelDisabled());
        lblRow.setEnabled(pEnabled);
        final CbCheckBox checkbox = new CbCheckBox();
        checkbox.setValue(Boolean.FALSE, false, false);
        checkbox.setEnabled(pEnabled);

        FlowPanel rowPanel = new FlowPanel();
        rowPanel.setStyleName(CbConstants.CSS.cbPageItemCheckBox());
        rowPanel.add(lblRow);
        rowPanel.add(checkbox);
        rowPanel.sinkEvents(Event.ONCLICK);
        rowPanel.addHandler(pClickHandler, ClickEvent.getType());

        return rowPanel;
    }



    /**
     * (Re-)builds the Detail Panel according to the given configuration data from
     * the current variant. By means of an event handler, this should automatically
     * be executed as soon as a new game is loaded.
     * @param pVariantId the variant ID, so we don't do it too often
     * @param pCommodities the commodity definition of the game variant
     */
    private void initializeVariant(final String pVariantId,
        final CbCommodityConfigJSO[] pCommodities)
    {
        LOG.enter("initializeVariant"); //$NON-NLS-1$
        if (pVariantId.equals(iLastInitedForVariant)) {
            // do nothing, because the variant is already implemented in this view
            LOG.exit("initializeVariant"); //$NON-NLS-1$
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("initializeVariant", //$NON-NLS-1$
                "Initializing 'Funds' view with newly loaded game variant " //$NON-NLS-1$
                + pVariantId + " (" + (pCommodities != null ? pCommodities.length : 0) //$NON-NLS-1$
                + " commodities)"); //$NON-NLS-1$
        }
        iDetailPanel.clear();

        /*
         * Treasury
         */
        CbLabel treasuryLabel = new CbLabel(CbConstants.STRINGS.treasury(), true,
            CbConstants.CSS.cbPageItemInputLabel(), CbConstants.CSS.cbPageItemInputLabelDisabled());

        // TODO additional slider bar with touch* event handling (with bonus box)
        iTreasuryBox = new IntegerBox();
        iTreasuryBox.setMaxLength(2);
        iTreasuryBox.setVisibleLength(2);
        iTreasuryBox.setAlignment(TextAlignment.RIGHT);
        iTreasuryBox.addFocusHandler(TXTFOCUSHANDLER);
        // use numerical input pad on iPhone
        iTreasuryBox.getElement().setAttribute("pattern", "[0-9]*"); //$NON-NLS-1$ //$NON-NLS-2$
        iTreasuryBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> pEvent)
            {
                iPresenter.onTreasuryBoxChanged(pEvent.getValue());
            }});
        
        FlowPanel treasuryRow = new FlowPanel();
        treasuryRow.setStyleName(CbConstants.CSS.cbPageItemInput());
        treasuryRow.add(treasuryLabel);
        treasuryRow.add(iTreasuryBox);

        /*
         * Commodities
         */
        List<FlowPanel> commRows = new ArrayList<FlowPanel>();
        final ChangeHandler sbch = new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent pEvent)
            {
                ListBox src = (ListBox) pEvent.getSource();
                int idx = src.getElement().getPropertyInt(DOMATTR_SEL_IDX);
                int newNumber = src.getSelectedIndex();
                iPresenter.onCommodityChange(idx, newNumber);
            }
        };
        for (int c = 0; c < pCommodities.length; c++)
        {
            CbCommodityConfigJSO commJSO = pCommodities[c];
            final int base = commJSO.getBase();

            String name = base + " - " + commJSO.getLocalizedName(); //$NON-NLS-1$
            CbLabel lblComm = new CbLabel(name, true, CbConstants.CSS.cbPageItemInputLabel(),
                CbConstants.CSS.cbPageItemInputLabelDisabled());

            ListBox selector = new ListBox();
            for (int i = 0; i <= commJSO.getMaxCount(); i++)
            {
                int points = i * base;
                if (!commJSO.isWineSpecial()) {
                    points *= i;
                }
                String text = "--"; //$NON-NLS-1$
                if (i > 0) {
                    text = CbConstants.MESSAGES.fundsCommodityOption(i, points);
                }
                selector.addItem(text);
            }
            selector.setSelectedIndex(0);
            selector.addChangeHandler(sbch);
            // TODO focus/blur handler to highlight row and connect to label
            selector.getElement().setPropertyInt(DOMATTR_SEL_IDX, c);

            FlowPanel commRow = new FlowPanel();
            commRow.setStyleName(CbConstants.CSS.cbPageItemInput());
            commRow.add(lblComm);
            commRow.add(selector);
            
            commRows.add(commRow);
        }

        /*
         * Bonus Box
         */
        CbLabel lblBonus = new CbLabel(CbConstants.STRINGS.fundsBonus(), true,
            CbConstants.CSS.cbPageItemInputLabel(), CbConstants.CSS.cbPageItemInputLabelDisabled());

        iBonusBox = new IntegerBox();
        iBonusBox.setValue(Integer.valueOf(0));
        final int maxLen = String.valueOf(CbFundsJSO.MAX_BONUS).length();
        iBonusBox.setMaxLength(maxLen);
        iBonusBox.setAlignment(TextAlignment.RIGHT);
        iBonusBox.setTitle(CbConstants.STRINGS.fundsBonusTitle());
        iBonusBox.addFocusHandler(TXTFOCUSHANDLER);
        // use numerical input pad on iPhone
        iBonusBox.getElement().setAttribute("pattern", "[0-9]*"); //$NON-NLS-1$ //$NON-NLS-2$
        iBonusBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> pEvent)
            {
                iPresenter.onBonusChanged(pEvent.getValue());
            }
        });

        FlowPanel bonusRow = new FlowPanel();
        bonusRow.setStyleName(CbConstants.CSS.cbPageItemInput());
        bonusRow.add(lblBonus);
        bonusRow.add(iBonusBox);

        /*
         * Rebuild Detail Panel
         */
        iDetailPanel.add(treasuryRow);
        for (FlowPanel commRow : commRows) {
            iDetailPanel.add(commRow);
        }
        iDetailPanel.add(bonusRow);

        iLastInitedForVariant = pVariantId;

        LOG.exit("initializeVariant"); //$NON-NLS-1$
    }



    @Override
    public void setDetailTracking(final boolean pDetailed, final boolean pAnimate)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setDetailTracking",  //$NON-NLS-1$
                new String[]{"pDetailed", "pAnimate"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Boolean.valueOf(pDetailed), Boolean.valueOf(pAnimate)});
        }

        // TODO fade animation?
        iCoarsePanel.setVisible(!pDetailed);
        iDetailPanel.setVisible(pDetailed);
        iNumCommIndicator.setEnabled(pDetailed);
        setCheckboxValue(iCheckBoxRowDetailed, pDetailed, pAnimate);

        LOG.exit("setDetailTracking"); //$NON-NLS-1$
    }



    @Override
    public boolean isEnabled()
    {
        return getCheckboxValue(iCheckBoxRowOverall);
    }

    @Override
    public void setEnabled(final boolean pEnabled, final boolean pAnimate)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setEnabled",  //$NON-NLS-1$
                new String[]{"pEnabled", "pAnimate"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Boolean.valueOf(pEnabled), Boolean.valueOf(pAnimate)});
        }
        if (pEnabled != isEnabled())
        {
            if (LOG.isDebugEnabled()) {
                LOG.debug("setEnabled", //$NON-NLS-1$
                    "checkbox was flipped"); //$NON-NLS-1$
            }

            // Coarse widgets
            setRowEnabled((FlowPanel) iCoarsePanel.getWidget(0), pEnabled);

            // Checkbox
            setCheckboxValue(iCheckBoxRowOverall, pEnabled, pAnimate);
            setRowEnabled(iCheckBoxRowDetailed, pEnabled);
            
            // Icon buttons
            iBtnClear.setEnabled(pEnabled);
        }
        LOG.exit("setEnabled"); //$NON-NLS-1$
    }



    private boolean getCheckboxValue(final FlowPanel pCheckBoxRow)
    {
        CbCheckBox cb = (CbCheckBox) pCheckBoxRow.getWidget(1);
        return cb.getValue().booleanValue();
    }

    private void setCheckboxValue(final FlowPanel pCheckBoxRow,
        final boolean pNewValue, final boolean pAnimate)
    {
        CbCheckBox cb = (CbCheckBox) pCheckBoxRow.getWidget(1);
        cb.setValue(Boolean.valueOf(pNewValue), false, pAnimate);
    }

    private void setRowEnabled(final FlowPanel pCheckBoxRow, final boolean pEnabled)
    {
        for (Iterator<Widget> iter = pCheckBoxRow.iterator(); iter.hasNext();)
        {
            HasEnabled w = (HasEnabled) iter.next();
            w.setEnabled(pEnabled);
        }
    }



    @Override
    public void setPresenter(final CbPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public void initializeSituation(final CbFundsJSO pFundsJso, final int pNumCommodities)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("initializeSituation",  //$NON-NLS-1$
                new String[]{"pNumCommodities", "pFundsJso"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{Integer.valueOf(pNumCommodities), pFundsJso});
        }

        // checkboxes without animation
        setEnabled(pFundsJso.isEnabled(), false);
        if (pFundsJso.isEnabled()) {
            // if funds not enabled, detail is always off
            setDetailTracking(pFundsJso.isDetailed(), false);
        }

        // coarse panel and extrabar
        setTotalFunds(pFundsJso.getTotalFunds());
        setNumCommodities(pNumCommodities);

        // detail panel
        final int numRows = iDetailPanel.getWidgetCount();
        for (int i = 0; i < numRows; i++)
        {
            if (i == 0) {
                iTreasuryBox.setValue(Integer.valueOf(pFundsJso.getTreasury()), false);
            } else if (i == numRows - 1) {
                iBonusBox.setValue(Integer.valueOf(pFundsJso.getBonus()), false);
            } else {
                ListBox selector = (ListBox) ((FlowPanel) iDetailPanel.getWidget(i)).getWidget(1);
                selector.setSelectedIndex(pFundsJso.getCommodityCount(i - 1));
            }
        }

        LOG.exit("initializeSituation"); //$NON-NLS-1$
    }



    @Override
    public void setTotalFundsBoxOnly(final int pNewValue)
    {
        iTotalFundsBox.setValue(Integer.valueOf(pNewValue));
        iTotalFundsBox.setSelectionRange(0, iTotalFundsBox.getText().length());
    }



    @Override
    public void setTotalFunds(final int pNewValue)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("setTotalFunds",  //$NON-NLS-1$
                new String[]{"pNewValue"},  //$NON-NLS-1$
                new Object[]{Integer.valueOf(pNewValue)});
        }

        iTotalFundsBox.setValue(Integer.valueOf(pNewValue), false);
        iTotalFundsIndicator.setValue(pNewValue);

        LOG.exit("setTotalFunds"); //$NON-NLS-1$
    }



    @Override
    public void setTreasury(final int pNewValue)
    {
        iTreasuryBox.setValue(Integer.valueOf(pNewValue), false);  // no events
        iTreasuryBox.setSelectionRange(0, iTreasuryBox.getText().length());
    }



    @Override
    public void setNumCommodities(final int pNewValue)
    {
        iNumCommIndicator.setValue(pNewValue);
    }



    @Override
    public void setBonusBoxOnly(final int pBonus)
    {
        iBonusBox.setValue(Integer.valueOf(pBonus), false);  // no events
        iBonusBox.setSelectionRange(0, iBonusBox.getText().length());
    }



    /**
     * Reset all widget values in the funds view to zero.
     */
    private void reset()
    {
        for (Iterator<Widget> iter = iDetailPanel.iterator(); iter.hasNext();)
        {
            FlowPanel rowPanel = (FlowPanel) iter.next();
            Widget w = rowPanel.getWidget(1);
            if (w instanceof IntegerBox) {
                ((IntegerBox) w).setValue(Integer.valueOf(0), false);
            } else {
                ((ListBox) w).setSelectedIndex(0);
            }
        }
        setNumCommodities(0);
        setTotalFunds(0);
    }



    @Override
    public void setTitleHeading(final String pTitle)
    {
        iViewTitle.setText(pTitle);
    }
}
