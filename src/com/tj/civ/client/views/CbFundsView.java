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
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.event.CbCommSpinnerPayload;
import com.tj.civ.client.model.jso.CbCommodityConfigJSO;
import com.tj.civ.client.model.jso.CbFundsJSO;
import com.tj.civ.client.widgets.CbCommoditySpinner;
import com.tj.civ.client.widgets.CbLabel;
import com.tj.civ.client.widgets.CbMessageBox;
import com.tj.civ.client.widgets.CbMessageBox.CbResultCallbackIF;
import com.tj.civ.client.widgets.CbStatsIndicator;
import com.tj.civ.client.widgets.CbWineSpecial;


/**
 * Implementation of the 'Funds' view.
 *
 * @author Thomas Jensen
 */
public class CbFundsView
    extends Composite
    implements CbFundsViewIF, HasEnabled
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbFundsView.class);

    /** by default, funds tracking is generally disabled */
    private static final boolean DEFAULT_STATE = false;

    /** by default, detailed funds tracking is turned off */
    private static final boolean DEFAULT_STATE_DETAIL = false;

    /** this view's presenter */
    private CbFundsViewIF.CbPresenterIF iPresenter;

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
    private Button iBtnClear;

    /** the button for overall enabling/disabling of funds tracking */
    private ToggleButton iBtnToggleFunds;

    /** the button for enabling/disabling detail tracking */
    private ToggleButton iBtnToggleDetail;

    /** Panel including all the widgets for detailed funds tracking */
    private VerticalPanel iDetailPanel;

    /** Grid including the commodity spinners */
    private Grid iSpinnersGrid;

    /** Panel including all the widgets for coarse funds tracking */
    private Panel iCoarsePanel;

    /** List of all widgets containing information on the funds details, used for
     *  recalculating the total funds */
    private List<IsWidget> iDetailWidgets = new ArrayList<IsWidget>();

    /** the {@link #iDetailWidgets} created in the constructor.
     *  {@link #iDetailWidgets} is reset to this when {@link #initialize} is called */
    private List<IsWidget> iDetailWidgetsBase = new ArrayList<IsWidget>();

    /** List of all widgets which can be enabled/disabled. Used in connection with
     *  {@link #iBtnToggleFunds} */
    private List<HasEnabled> iActivatableWidgets = new ArrayList<HasEnabled>();

    /** the {@link #iActivatableWidgets} created in the constructor.
     *  {@link #iActivatableWidgets} is reset to this when {@link #initialize}
     *  is called */
    private List<HasEnabled> iActivatableWidgetsBase = new ArrayList<HasEnabled>();

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



//  /**
//  * Wrapper that adds the {@link HasEnabled} interface to the unspeakable
//  * {@link SliderBar} which already implements it without declaring it.
//  * @author Thomas Jensen
//  */
// private class CcSliderBarEnabler extends SliderBar implements HasEnabled
// {
//     CcSliderBarEnabler(final double pMinValue, final double pMaxValue,
//         final LabelFormatter pLabelFormatter, final SliderBarImages pImages)
//     {
//         super(pMinValue, pMaxValue, pLabelFormatter, pImages);
//     }
//
//     CcSliderBarEnabler(final double pMinValue, final double pMaxValue,
//         final LabelFormatter pLabelFormatter)
//     {
//         super(pMinValue, pMaxValue, pLabelFormatter);
//     }
//
//     CcSliderBarEnabler(final double pMinValue, final double pMaxValue)
//     {
//         super(pMinValue, pMaxValue);
//     }
// }



    private Panel createFundsButtonPanel()
    {
        Button btnBack = new Button(SafeHtmlUtils.fromSafeConstant(
            CbConstants.STRINGS.fundsBtnBackHtml()));
        btnBack.setStyleName(CbConstants.CSS.ccButton());
        btnBack.setTitle(CbConstants.STRINGS.fundsBtnBackTitle());
        btnBack.setEnabled(true);
        btnBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goBack();
            }
        });

        iBtnClear = new Button(CbConstants.STRINGS.clearFunds());
        iBtnClear.setStyleName(CbConstants.CSS.ccButton());
        iBtnClear.setTitle(CbConstants.STRINGS.clearFundsDesc());
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
        iActivatableWidgetsBase.add(iBtnClear);

        iBtnToggleFunds = new ToggleButton(CbConstants.STRINGS.off(), CbConstants.STRINGS.on());
        //iBtnToggleFunds.setStyleName(CbConstants.CSS.ccButton());
        iBtnToggleFunds.setTitle(CbConstants.STRINGS.enableFunds());
        iBtnToggleFunds.setEnabled(true);
        iBtnToggleFunds.setValue(Boolean.valueOf(DEFAULT_STATE), false);
        iBtnToggleFunds.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                ToggleButton source = (ToggleButton) pEvent.getSource();
                boolean enabled = source.getValue().booleanValue();
                iPresenter.onEnableToggled(enabled);
            }
        });

        HorizontalPanel result = new HorizontalPanel();
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        result.add(btnBack);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        result.add(iBtnClear);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        result.add(iBtnToggleFunds);
        result.setStyleName(CbConstants.CSS.ccButtonPanel());
        result.addStyleName(CbConstants.CSS.ccButtonThirds());
        result.addStyleName(CbConstants.CSS_BLUEGRADIENT);
        return result;
    }



    /**
     * Constructor.
     */
    public CbFundsView()
    {
        super();

        VerticalPanel workaround = new VerticalPanel();
        workaround.setStyleName(CbConstants.CSS.ccStats());
        HorizontalPanel statsHp = new HorizontalPanel();
        statsHp.setStyleName(CbConstants.CSS.ccStatsInner() + " " //$NON-NLS-1$
            + CbConstants.CSS_BLUEGRADIENT);
        iTotalFundsIndicator = new CbStatsIndicator(CbConstants.STRINGS.statsFunds(), null, true);
        iActivatableWidgetsBase.add(iTotalFundsIndicator);
        statsHp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        statsHp.add(iTotalFundsIndicator);
        iNumCommIndicator = new CbStatsIndicator(
            CbConstants.STRINGS.fundsCommodities(), null, false);
        iActivatableWidgetsBase.add(iNumCommIndicator);
        statsHp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        statsHp.add(iNumCommIndicator);
        workaround.add(statsHp);

        CbLabel label = new CbLabel(CbConstants.STRINGS.fundsTotalLabel());
        iActivatableWidgetsBase.add(label);
        // TODO extract into a widget (with bonus box) and add validation
        iTotalFundsBox = new IntegerBox();
        final int maxLen = String.valueOf(CbFundsJSO.MAX_TOTAL_FUNDS).length();
        iTotalFundsBox.setMaxLength(maxLen);
        iTotalFundsBox.setVisibleLength(maxLen);
        iTotalFundsBox.setAlignment(TextAlignment.RIGHT);
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
        iActivatableWidgetsBase.add(iTotalFundsBox);
        iCoarsePanel = new HorizontalPanel();
        iCoarsePanel.add(label);
        iCoarsePanel.add(iTotalFundsBox);
        
        CbLabel detLabel = new CbLabel(CbConstants.STRINGS.fundsDetailed());
        iActivatableWidgetsBase.add(detLabel);
        iBtnToggleDetail = new ToggleButton(CbConstants.STRINGS.off(), CbConstants.STRINGS.on());
        iBtnToggleDetail.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> pEvent)
            {
                iPresenter.onDetailToggled(pEvent.getValue().booleanValue());
            }
        });
        iBtnToggleDetail.setValue(Boolean.valueOf(DEFAULT_STATE_DETAIL), false);  // no event yet
        iActivatableWidgetsBase.add(iBtnToggleDetail);
        HorizontalPanel detHp = new HorizontalPanel();
        detHp.add(detLabel);
        detHp.add(iBtnToggleDetail);
        
        final CbLabel treasuryLabel = new CbLabel(CbConstants.STRINGS.treasury());
        iActivatableWidgetsBase.add(treasuryLabel);
        iTreasuryBox = new IntegerBox();
//        final CcSliderBarEnabler sb = new CcSliderBarEnabler(TREASURY_MIN, TREASURY_MAX);
        iTreasuryBox.setMaxLength(2);
        iTreasuryBox.setVisibleLength(2);
        iTreasuryBox.setValue(Integer.valueOf(0));
        iTreasuryBox.setAlignment(TextAlignment.RIGHT);
        iTreasuryBox.addFocusHandler(TXTFOCUSHANDLER);
        iTreasuryBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> pEvent)
            {
                iPresenter.onTreasuryBoxChanged(pEvent.getValue());
            }});
        iActivatableWidgetsBase.add(iTreasuryBox);
        iDetailWidgetsBase.add(iTreasuryBox);
//        sb.setWidth("250px");
//        sb.setNumLabels(CcFundsJso.TREASURY_NUM_TICKS);
//        sb.setNumTicks(CcFundsJso.TREASURY_NUM_TICKS);
//        sb.setStepSize(1);
//        sb.setCurrentValue(0.0d, false);
//        sb.setLabelFormatter(new LabelFormatter()
//        {
//            @Override
//            public String formatLabel(final SliderBar pSlider, final double pValue)
//            {
//                // show labels as ints
//                return String.valueOf((int) pValue);
//            }
//        });
//        sb.addValueChangeHandler(new ValueChangeHandler<Double>() {
//            @Override
//            public void onValueChange(final ValueChangeEvent<Double> pEvent)
//            {
//                Integer v = txtTreasury.getValue();
//                int oldValue = 0;
//                if (v != null) {
//                    oldValue = v.intValue();
//                }
//                int newValue = (int) pEvent.getValue().doubleValue();
//                txtTreasury.setValue(Integer.valueOf(newValue));
//                updateTotalFunds(iTotalFunds + newValue - oldValue);
//            }
//        });
//        // TODO touch* events to enable dragging on iPhone --> it's own widget
//        iActivatableWidgetsBase.add(sb);

        final HorizontalPanel treasuryHp = new HorizontalPanel();
        treasuryHp.add(iTreasuryBox);
//        treasuryHp.add(sb);

        iSpinnersGrid = new Grid(1, 2);  // dummy with only the bonus box
        iSpinnersGrid.setWidget(0, 1, createBonusBox());
        iDetailPanel = new VerticalPanel();
        iDetailPanel.add(treasuryLabel);
        iDetailPanel.add(treasuryHp);
        iDetailPanel.add(iSpinnersGrid);
        iDetailPanel.add(createBonusBox());
        
        VerticalPanel fp2 = new VerticalPanel();
        fp2.setStyleName(CbConstants.CSS.ccOuterPanel());
        fp2.add(createFundsButtonPanel());
        fp2.add(workaround);
        fp2.add(detHp);
        fp2.add(iCoarsePanel);
        fp2.add(iDetailPanel);
//        Label delme = new Label("ON");
//        delme.getElement().setAttribute("style", "font-size: x-small; color:#FF0000;"
//            + " font-weight: normal; text-shadow: 0 0 0.2em #F87, 0 0 0.2em #F87");
//        fp2.add(delme);

        resetWidgetList();

        setDetailTracking(DEFAULT_STATE_DETAIL);
        setEnabled(DEFAULT_STATE);
        initWidget(fp2);
    }



    private void resetWidgetList()
    {
        iActivatableWidgets.clear();
        iActivatableWidgets.addAll(iActivatableWidgetsBase);
        iDetailWidgets.clear();
        iDetailWidgets.addAll(iDetailWidgetsBase);
    }



    private Panel createBonusBox()
    {
        Panel result = null;
        boolean firstCall = iBonusBox == null;
        if (firstCall)
        {
            CbLabel lblBonus = new CbLabel(CbConstants.STRINGS.fundsBonus());
            lblBonus.setTitle(CbConstants.STRINGS.fundsBonusTitle());

            iBonusBox = new IntegerBox();
            iBonusBox.setValue(Integer.valueOf(0));
            final int maxLen = String.valueOf(CbFundsJSO.MAX_BONUS).length();
            iBonusBox.setMaxLength(maxLen);
            iBonusBox.setVisibleLength(maxLen);
            iBonusBox.setAlignment(TextAlignment.RIGHT);
            iBonusBox.setTitle(CbConstants.STRINGS.fundsBonusTitle());
            iBonusBox.addFocusHandler(TXTFOCUSHANDLER);
            iBonusBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
                @Override
                public void onValueChange(final ValueChangeEvent<Integer> pEvent)
                {
                    iPresenter.onBonusChanged(pEvent.getValue());
                }
            });
            // use numerical input pad on iPhone
            iBonusBox.getElement().setAttribute("pattern", "[0-9]*"); //$NON-NLS-1$ //$NON-NLS-2$

            iActivatableWidgetsBase.add(lblBonus);
            iActivatableWidgetsBase.add(iBonusBox);
            iDetailWidgetsBase.add(iBonusBox);

            VerticalPanel vp = new VerticalPanel();
            vp.add(lblBonus);
            vp.add(iBonusBox);
            result = vp;
        }
        else {
            result = (Panel) iBonusBox.getParent();
        }
        return result;
    }



    @Override
    public void setDetailTracking(final boolean pDetailed)
    {
        iTotalFundsBox.setEnabled(!pDetailed);
        iCoarsePanel.setVisible(!pDetailed);
        iDetailPanel.setVisible(pDetailed);
        iNumCommIndicator.setEnabled(pDetailed);
    }



    @Override
    public boolean isEnabled()
    {
        return iBtnClear.isEnabled();
    }

    /**
     * Enable or disable the entire view.
     * @param pEnabled <code>true</code> to enable
     * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
     */
    @Override
    public void setEnabled(final boolean pEnabled)
    {
        for (HasEnabled w : iActivatableWidgets) {
            w.setEnabled(pEnabled);
        }
        if (pEnabled) {
            iBtnToggleFunds.setTitle(CbConstants.STRINGS.disableFunds());
        } else {
            iBtnToggleFunds.setTitle(CbConstants.STRINGS.enableFunds());
        }
    }



    @Override
    public void setPresenter(final CbPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public void initialize(final CbCommodityConfigJSO[] pCommodities,
        final int pNumWineSpecials, final CbFundsJSO pFundsJso)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("initialize",  //$NON-NLS-1$
                new String[]{"pNumWineSpecials"},  //$NON-NLS-1$
                new Object[]{Integer.valueOf(pNumWineSpecials)});
        }

        final ValueChangeHandler<CbCommSpinnerPayload> vch =
            new ValueChangeHandler<CbCommSpinnerPayload>()
        {
            @Override
            public void onValueChange(final ValueChangeEvent<CbCommSpinnerPayload> pEvent)
            {
                iPresenter.onSpinnerChanged(pEvent.getValue());
            }
        };
        
        resetWidgetList();

        final int numCells = pCommodities.length - pNumWineSpecials;
        final int numGridCols = 2;
        iSpinnersGrid.resize(Math.round((float) numCells / numGridCols), numGridCols);

        for (int c = 0, p = 0; c < pCommodities.length; c++)
        {
            if (pCommodities[c].isWineSpecial()) {
                continue;  // skip the wine
            }
            int col = p % numGridCols;
            int row = p / numGridCols;
            
            CbCommoditySpinner cs = new CbCommoditySpinner(c, pCommodities[c]);
            cs.setNumber(pFundsJso.getCommodityCount(c));
            cs.addValueChangeHandler(vch);
            iSpinnersGrid.setWidget(row, col, cs);
            iDetailWidgets.add(cs);
            iActivatableWidgets.add(cs);

            p++;
        }

        if (pNumWineSpecials > 0) {
            int[] idx = new int[pNumWineSpecials];
            CbCommodityConfigJSO[] conf = new CbCommodityConfigJSO[pNumWineSpecials];
            int[] counts = new int[pNumWineSpecials];
            for (int c = 0, p = 0; c < pCommodities.length; c++)
            {
                if (!pCommodities[c].isWineSpecial()) {
                    continue;  // skip everything but the wine
                }
                idx[p] = c;
                conf[p] = pCommodities[c];
                counts[p] = pFundsJso.getCommodityCount(c);
                p++;
            }            
            CbWineSpecial wsp = new CbWineSpecial(idx, conf, counts);
            wsp.addValueChangeHandler(vch);
            iDetailWidgets.add(wsp);
            iActivatableWidgets.add(wsp);
            displayWineSpecialWidget(wsp, true);
        }
        else {
            displayWineSpecialWidget(null, false);  // hide if present
        }
        
        iTreasuryBox.setValue(Integer.valueOf(pFundsJso.getTreasury()), true);
        iBonusBox.setValue(Integer.valueOf(pFundsJso.getBonus()), false);
        iBtnToggleDetail.setValue(Boolean.valueOf(pFundsJso.isDetailed()), false);
        iBtnToggleFunds.setValue(Boolean.valueOf(pFundsJso.isEnabled()), false);
        setDetailTracking(pFundsJso.isDetailed());
        setEnabled(pFundsJso.isEnabled());

        LOG.exit("initialize"); //$NON-NLS-1$
    }



    private void displayWineSpecialWidget(final CbWineSpecial pWsp, final boolean pVisible)
    {
        int vpwc = iDetailPanel.getWidgetCount();
        iDetailPanel.remove(--vpwc);  // last one is the bonus box
        if (iDetailPanel.getWidget(vpwc - 1) instanceof CbWineSpecial) {
            iDetailPanel.remove(--vpwc);
        }
        if (pVisible) {
            iDetailPanel.add(pWsp);
        }
        iDetailPanel.add(createBonusBox());
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
        iTotalFundsBox.setValue(Integer.valueOf(pNewValue));
        iTotalFundsIndicator.setValue(pNewValue);
    }



    @Override
    public void setTreasury(final int pNewValue)
    {
        iTreasuryBox.setValue(Integer.valueOf(pNewValue));  // no events
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
        iBonusBox.setValue(Integer.valueOf(pBonus));  // no events
        iBonusBox.setSelectionRange(0, iBonusBox.getText().length());
    }



    /**
     * Reset all widget values in the funds view to zero.
     */
    private void reset()
    {
        for (IsWidget w : iDetailWidgets) {
            if (w instanceof CbCommoditySpinner) {
                ((CbCommoditySpinner) w).setNumber(0);
            } else if (w instanceof IntegerBox) {
                ((IntegerBox) w).setValue(Integer.valueOf(0), true);  // with events!
            } else if (w instanceof CbWineSpecial) {
                ((CbWineSpecial) w).reset();
            } else if (LOG.isWarnEnabled()) {
                LOG.warn("reset", "Unknown detail widget type " //$NON-NLS-1$ //$NON-NLS-2$
                    + (w != null ? w.getClass().getName() : "null") //$NON-NLS-1$
                    + " - BUG!"); //$NON-NLS-1$
            }
        }
        setNumCommodities(0);
        setTotalFunds(0);
    }
}
