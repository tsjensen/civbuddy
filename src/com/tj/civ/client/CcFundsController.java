/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 09.01.2011
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.gen2.picker.client.SliderBar;
import com.google.gwt.gen2.picker.client.SliderBar.LabelFormatter;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tj.civ.client.event.CcCommSpinnerPayload;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.widgets.CcCommoditySpinner;
import com.tj.civ.client.widgets.CcLabel;
import com.tj.civ.client.widgets.CcMessageBox;
import com.tj.civ.client.widgets.CcMessageBox.CcResultCallback;
import com.tj.civ.client.widgets.CcStatsIndicator;


/**
 * Manages the funds tab panel.
 *
 * @author Thomas Jensen
 */
public class CcFundsController
    implements HasEnabled
{
    /** logger for this class */
    private static final Logger LOG = Logger.getLogger(CcFundsController.class.getName());

    /** minimum treasury */
    private static final int TREASURY_MIN = 0;
    
    /** maximum possible treasury */
    private static final int TREASURY_MAX = 56;

    /** number of tick marks on the treasury slider. Should be an integer divisor
     *  of {@link #TREASURY_MAX} */
    private static final int TREASURY_NUM_TICKS = 8;

    /** the current situation */
    private CcSituation iSituation;

    /** Do we track funds at all? If not, the state {@link CcState#Unaffordable}
     *  will never be set */ 
    private boolean iEnabled = false;

    /** the top level panel on the 'funds' tab. This is what this class manages */
    private Panel iPanel;

    /** total funds available */
    private int iTotalFunds = 0;

    /** The text box containing the total funds value. Only editable when detailed
     *  tracking is turned off. */
    private TextBox iTotalFundsBox;

    /** Stats row entry about the current total funds */
    private CcStatsIndicator iTotalFundsIndicator;

    /** Stats row entry about the number of commodity cards */
    private CcStatsIndicator iNumCommIndicator;

    /** the 'clear all funds' button */
    private Button iBtnClear;

    /** the button for overall enabling/disabling of funds tracking */
    private ToggleButton iBtnToggleFunds;

    /** the button for enabling/disabling detail tracking */
    private ToggleButton iBtnToggleDetail;

    /** Panel including all the widgets for detailed funds tracking */
    private LazyPanel iDetailPanel;

    /** Panel including all the widgets for coarse funds tracking */
    private Panel iCoarsePanel;

    /** List of all widgets containing information on the funds details, used for
     *  recalculating the total funds */
    private List<HasValue<?>> iDetailWidgets = new ArrayList<HasValue<?>>();

    /** List of all widgets which can be enabled/disabled. Used in connection with
     *  {@link #iBtnToggleFunds} */
    private List<HasEnabled> iActivatableWidgets = new ArrayList<HasEnabled>();

    /** the value currently added to the added values of all commodities */
    private int iCurrentBonus = 0;

    /** focus handler for the TextBoxes on this panel. Select the entire input text
     *  when the TextBox receives focus */
    private static final FocusHandler TXTFOCUSHANDLER = new FocusHandler() {
        @Override
        public void onFocus(final FocusEvent pEvent)
        {
            TextBox source = (TextBox) pEvent.getSource();
            source.setSelectionRange(0, source.getValue().length());
        }
    };



    /**
     * Wrapper that adds the {@link HasEnabled} interface to the unspeakable
     * {@link SliderBar} which already implements it without declaring it.
     * @author Thomas Jensen
     */
    private class CcSliderBarEnabler extends SliderBar implements HasEnabled
    {
        CcSliderBarEnabler(final double pMinValue, final double pMaxValue,
            final LabelFormatter pLabelFormatter, final SliderBarImages pImages)
        {
            super(pMinValue, pMaxValue, pLabelFormatter, pImages);
        }

        CcSliderBarEnabler(final double pMinValue, final double pMaxValue,
            final LabelFormatter pLabelFormatter)
        {
            super(pMinValue, pMaxValue, pLabelFormatter);
        }

        CcSliderBarEnabler(final double pMinValue, final double pMaxValue)
        {
            super(pMinValue, pMaxValue);
        }
    }



    private Panel createFundsButtonPanel()
    {
        iBtnClear = new Button("Clear");
        iBtnClear.setStyleName(CcConstants.CSS.ccButton());
        iBtnClear.setTitle("Set all funds to zero");
        iBtnClear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                CcMessageBox.showOkCancel(CcConstants.STRINGS.askAreYouSure(),
                    SafeHtmlUtils.fromString("This will reset all funds data."),
                    iPanel, new CcResultCallback() {
                        @Override
                        public void onResultAvailable(final boolean pResult)
                        {
                            if (pResult) {
                                reset();
                    }   }   }
                );
            }
        });
        iActivatableWidgets.add(iBtnClear);

        iBtnToggleFunds = new ToggleButton("Off", "On");
        //iBtnToggleFunds.setStyleName(CcConstants.CSS.ccButton());
        iBtnToggleFunds.setTitle("Enable funds tracking");
        iBtnToggleFunds.setEnabled(true);
        iBtnToggleFunds.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                ToggleButton button = (ToggleButton) pEvent.getSource();
                setEnabled(button.getValue().booleanValue());
            }
        });

        HorizontalPanel result = new HorizontalPanel();
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        result.add(new HTML("&nbsp;"));
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        result.add(iBtnClear);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        result.add(iBtnToggleFunds);
        result.setStyleName(CcConstants.CSS.ccButtonPanel() + " " //$NON-NLS-1$
            + CcConstants.CSS_BLUEGRADIENT);
        return result;
    }



    /**
     * Constructor.
     * @param pSituation the current situation
     */
    public CcFundsController(final CcSituation pSituation)
    {
        iSituation = pSituation;

        VerticalPanel workaround = new VerticalPanel();
        workaround.setStyleName(CcConstants.CSS.ccStats());
        HorizontalPanel statsHp = new HorizontalPanel();
        statsHp.setStyleName(CcConstants.CSS.ccStatsInner() + " " //$NON-NLS-1$
            + CcConstants.CSS_BLUEGRADIENT);
        iTotalFundsIndicator = new CcStatsIndicator(CcConstants.STRINGS.statsFunds(), null, true);
        iActivatableWidgets.add(iTotalFundsIndicator);
        statsHp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        statsHp.add(iTotalFundsIndicator);
        iNumCommIndicator = new CcStatsIndicator("Commodities", null, false);
        iActivatableWidgets.add(iNumCommIndicator);
        statsHp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        statsHp.add(iNumCommIndicator);
        workaround.add(statsHp);

        CcLabel label = new CcLabel("Total_Funds:");
        iActivatableWidgets.add(label);
        iTotalFundsBox = new TextBox();
        iTotalFundsBox.setMaxLength(4);
        iTotalFundsBox.setVisibleLength(4);
        iTotalFundsBox.addFocusHandler(TXTFOCUSHANDLER);
        iTotalFundsBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> pEvent)
            {
                if (isIntBetween(pEvent.getValue(), 0, 1509)) {
                    int newValue = Integer.parseInt(pEvent.getValue());
                    updateTotalFunds(newValue);
                }
                else {
                    TextBox src = (TextBox) pEvent.getSource();
                    src.setValue(String.valueOf(iTotalFunds));
                    src.setSelectionRange(0, src.getValue().length());
                }
            }});
        iActivatableWidgets.add(iTotalFundsBox);
        iCoarsePanel = new HorizontalPanel();
        iCoarsePanel.add(label);
        iCoarsePanel.add(iTotalFundsBox);
        
        CcLabel detLabel = new CcLabel("Detailed Tracking:");
        iActivatableWidgets.add(detLabel);
        iBtnToggleDetail = new ToggleButton("Off", "On");
        iBtnToggleDetail.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> pEvent)
            {
                setDetailTracking(pEvent.getValue().booleanValue());
            }});
        iBtnToggleDetail.setValue(Boolean.TRUE, false);  // no event yet
        iActivatableWidgets.add(iBtnToggleDetail);
        HorizontalPanel detHp = new HorizontalPanel();
        detHp.add(detLabel);
        detHp.add(iBtnToggleDetail);
        
        final CcLabel treasuryLabel = new CcLabel(CcConstants.STRINGS.treasury());
        iActivatableWidgets.add(treasuryLabel);
        final TextBox txtTreasury = new TextBox();
        final CcSliderBarEnabler sb = new CcSliderBarEnabler(TREASURY_MIN, TREASURY_MAX);
        txtTreasury.setMaxLength(2);
        txtTreasury.setVisibleLength(2);
        txtTreasury.setValue("0");
        txtTreasury.addFocusHandler(TXTFOCUSHANDLER);
        txtTreasury.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> pEvent)
            {
                TextBox source = (TextBox) pEvent.getSource();
                onTreasuryBoxChange(source, sb, pEvent.getValue());
            }});
        iActivatableWidgets.add(txtTreasury);
        iDetailWidgets.add(txtTreasury);
        sb.setWidth("250px");
        sb.setNumLabels(TREASURY_NUM_TICKS);
        sb.setNumTicks(TREASURY_NUM_TICKS);
        sb.setStepSize(1);
        sb.setCurrentValue(0.0d, false);
        sb.setLabelFormatter(new LabelFormatter()
        {
            @Override
            public String formatLabel(final SliderBar pSlider, final double pValue)
            {
                // show labels as ints
                return String.valueOf((int) pValue);
            }
        });
        sb.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Double> pEvent)
            {
                String v = txtTreasury.getValue();
                int oldValue = 0;
                if (v != null && v.length() > 0) {
                    oldValue = Integer.parseInt(v);
                }
                int newValue = (int) pEvent.getValue().doubleValue();
                txtTreasury.setValue(String.valueOf(newValue));
                updateTotalFunds(iTotalFunds + newValue - oldValue);
            }
        });
        iActivatableWidgets.add(sb);
        final HorizontalPanel treasuryHp = new HorizontalPanel();
        treasuryHp.add(txtTreasury);
        treasuryHp.add(sb);

        final ValueChangeHandler<CcCommSpinnerPayload> vch =
            new ValueChangeHandler<CcCommSpinnerPayload>()
        {
            @Override
            public void onValueChange(final ValueChangeEvent<CcCommSpinnerPayload> pEvent)
            {
                CcCommSpinnerPayload payload = pEvent.getValue();
                CcFundsController.this.updateTotalFunds(
                    CcFundsController.this.iTotalFunds + payload.getDeltaPoints());
                CcFundsController.this.iNumCommIndicator.setValue(
                    CcFundsController.this.iNumCommIndicator.getValue() + payload.getDeltaNumber());
            }
        };
        final int numCells = pSituation.getCommoditiesCurrent().length + 1;
        final Grid grid = new Grid(Math.round(numCells / 2), 2);
        int c = 0;
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColumnCount(); col++) {
                if (c < numCells - 1) {
                    CcCommoditySpinner cs = new CcCommoditySpinner(
                        pSituation.getGame().getVariant().getCommodities()[c++]);
                    cs.addValueChangeHandler(vch);
                    grid.setWidget(row, col, cs);
                    iDetailWidgets.add(cs);
                    iActivatableWidgets.add(cs);
                } else if (c < numCells) {
                    VerticalPanel vp = new VerticalPanel();
                    CcLabel lblBonus = new CcLabel("Bonus");
                    lblBonus.setTitle("Simply adds points to your funds");
                    iActivatableWidgets.add(lblBonus);
                    vp.add(lblBonus);
                    TextBox txtBonus = new TextBox();
                    vp.add(txtBonus);
                    txtBonus.setValue(String.valueOf(iCurrentBonus));
                    txtBonus.setMaxLength(4);
                    txtBonus.setVisibleLength(4);
                    txtBonus.setTitle("Arbitrary points you want added to your funds");
                    txtBonus.addValueChangeHandler(new ValueChangeHandler<String>() {
                        @Override
                        public void onValueChange(final ValueChangeEvent<String> pEvent)
                        {
                            TextBox source = (TextBox) pEvent.getSource();
                            onBonusChange(source, pEvent.getValue());
                        }});
                    txtBonus.addFocusHandler(TXTFOCUSHANDLER);
                    grid.setWidget(row, col, vp);
                    iActivatableWidgets.add(txtBonus);
                    iDetailWidgets.add(txtBonus);
                }
            }
        }

        iDetailPanel = new LazyPanel() {
            @Override
            protected Widget createWidget()
            {
                VerticalPanel lvp = new VerticalPanel();
                lvp.add(treasuryLabel);
                lvp.add(treasuryHp);
                lvp.add(grid);
                return lvp;
            }
        };
        VerticalPanel fp2 = new VerticalPanel();
        fp2.add(createFundsButtonPanel());
        fp2.add(workaround);
        fp2.add(detHp);
        fp2.add(iCoarsePanel);
        fp2.add(iDetailPanel);
//        Label delme = new Label("ON");
//        delme.getElement().setAttribute("style", "font-size: x-small; color:#FF0000;"
//            + " font-weight: normal; text-shadow: 0 0 0.2em #F87, 0 0 0.2em #F87");
//        fp2.add(delme);
        iPanel = fp2;

        setDetailTracking(iBtnToggleDetail.getValue().booleanValue());
        iBtnToggleFunds.setValue(Boolean.valueOf(iEnabled), false);
        setEnabled(iEnabled);
    }



    private void setDetailTracking(final boolean pToEnable)
    {
        iTotalFundsBox.setEnabled(!pToEnable);
        iCoarsePanel.setVisible(!pToEnable);
        iDetailPanel.setVisible(pToEnable);
        iNumCommIndicator.setEnabled(pToEnable);
        if (pToEnable) {
            recalcTotalFunds();
        }
    }



    private void recalcTotalFunds()
    {
        int sum = 0;
        for (HasValue<?> w : iDetailWidgets) {
            Object v = w.getValue();
            if (v instanceof CcCommSpinnerPayload) {
                sum += ((CcCommSpinnerPayload) v).getDeltaPoints();  // absolute
            } else if (v instanceof String) {
                sum += Integer.parseInt((String) v);
            } else if (LOG.isLoggable(Level.WARNING)) {
                LOG.warning("Unknown detail widget value type " //$NON-NLS-1$
                    + (v != null ? v.getClass().getName() : "null") //$NON-NLS-1$
                    + " - BUG!"); //$NON-NLS-1$
            }
        }
        updateTotalFunds(sum);
    }



    /**
     * Reset all values in the funds panel to zero.
     */
    private void reset()
    {
        final CcCommSpinnerPayload csZero = new CcCommSpinnerPayload(0, 0);
        for (HasValue<?> w : iDetailWidgets) {
            if (w instanceof CcCommoditySpinner) {
                ((CcCommoditySpinner) w).setValue(csZero, false);
            } else if (w instanceof TextBox) {
                ((TextBox) w).setValue("0", true); //$NON-NLS-1$  // with events!
            } else if (LOG.isLoggable(Level.WARNING)) {
                LOG.warning("Unknown detail widget type " //$NON-NLS-1$
                    + (w != null ? w.getClass().getName() : "null") //$NON-NLS-1$
                    + " - BUG!"); //$NON-NLS-1$
            }
        }
        iNumCommIndicator.setValue(0);
        updateTotalFunds(0);
    }



    private void updateTotalFunds(final int pNewValue)
    {
        iTotalFunds = pNewValue;
        iTotalFundsBox.setValue(String.valueOf(pNewValue));
        iTotalFundsIndicator.setValue(pNewValue);
    }



    private boolean isIntBetween(final String pNewValue, final int pMin, final int pMax)
    {
        int newValue = 0;
        if (pNewValue != null && pNewValue.length() > 0) {
            try {
                newValue = Integer.parseInt(pNewValue);
            }
            catch (NumberFormatException e) {
                return false;   // was no int
            }
        }
        if (newValue < pMin || newValue > pMax) {
            return false;
        }
        return true;
    }



    private void onTreasuryBoxChange(final TextBox pSource, final SliderBar pSb,
        final String pNewValue)
    {
        if (isIntBetween(pNewValue, TREASURY_MIN, TREASURY_MAX)) {
            int newValue = Integer.parseInt(pNewValue);
            updateTotalFunds(iTotalFunds + newValue - ((int) pSb.getCurrentValue()));
            pSb.setCurrentValue(newValue);
        }
        else {
            String oldValue = String.valueOf((int) pSb.getCurrentValue());
            pSource.setValue(oldValue);
            pSource.setSelectionRange(0, oldValue.length());
        }
    }



    private void onBonusChange(final TextBox pSource, final String pNewValue)
    {
        if (isIntBetween(pNewValue, 0, 500)) {
            int newValue = Integer.parseInt(pNewValue);
            updateTotalFunds(iTotalFunds + newValue - iCurrentBonus);
            iCurrentBonus = newValue;
        }
        else {
            String oldStr = String.valueOf(iCurrentBonus);
            pSource.setValue(oldStr);
            pSource.setSelectionRange(0, oldStr.length());
        }
    }



    public int getFunds()
    {
        return iTotalFunds;
    }



    /**
     * Is funds tracking enabled?
     * @return <code>true</code> if yes 
     */
    @Override
    public boolean isEnabled()
    {
        return iEnabled;
    }

    /**
     * Enable or disable funds tracking altogether. If funds tracking is disabled,
     * civilization cards will never enter the 'Unaffordable' state.
     * @param pEnabled <code>true</code> to enable
     * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
     */
    @Override
    public void setEnabled(final boolean pEnabled)
    {
        updateTotalFunds(0);
        for (HasEnabled w : iActivatableWidgets) {
            w.setEnabled(pEnabled);
        }
        iEnabled = pEnabled;
    }



    /**
     * Getter.
     * @return {@link #iPanel}
     */
    public Panel getPanel()
    {
        return iPanel;
    }
}
