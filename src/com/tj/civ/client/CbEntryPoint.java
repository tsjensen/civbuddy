package com.tj.civ.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tj.civ.client.event.CcAllStatesEvent;
import com.tj.civ.client.event.CcAllStatesHandler;
import com.tj.civ.client.event.CcEventBus;
import com.tj.civ.client.event.CcFundsEvent;
import com.tj.civ.client.event.CcStateEvent;
import com.tj.civ.client.event.CcStateHandler;
import com.tj.civ.client.model.CcCardConfig;
import com.tj.civ.client.model.CcCardCurrent;
import com.tj.civ.client.model.CcGame;
import com.tj.civ.client.model.CcPlayer;
import com.tj.civ.client.model.CcSituation;
import com.tj.civ.client.model.CcState;
import com.tj.civ.client.model.CcVariantConfig;
import com.tj.civ.client.model.CcVariantConfigMock;
import com.tj.civ.client.resources.CcClientBundle;
import com.tj.civ.client.resources.CcConstants;
import com.tj.civ.client.widgets.CcMessageBox;
import com.tj.civ.client.widgets.CcMessageBox.CcResultCallback;
import com.tj.civ.client.widgets.CcStatistics;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CcEntryPoint
    implements EntryPoint, CcStateHandler, CcAllStatesHandler
{
    /** logger for this class */
    //private static final Logger LOG = Logger.getLogger(CcEntryPoint.class.getName());

    /** Tab index of the 'funds' panel */
    private static final int TABPANEL_FUNDS_IDX = 1;

    /** the main tab panel allowing change between the funds and cards tabs */
    private TabPanel iTabPanel = null;

    /** the buy cards button */
    private Button iBtnCommit = null;
    
    /** the revise button */
    private Button iBtnRevise = null;
    
    /** the card controller */
    private CcCardController iCardCtrl = null;



    private CcGame mockGame(final CcVariantConfig pVariant)
    {
        CcGame result = new CcGame();
        result.setName("2011-01-05 Mock Game"); //$NON-NLS-1$
        result.setVariant(pVariant);
        CcPlayer player = new CcPlayer();
        player.setName("Thomas"); //$NON-NLS-1$
        final int winningTotalCrete = 1200;
        player.setWinningTotal(winningTotalCrete);
        CcSituation sit = new CcSituation(player);
        result.addPlayer(sit);

        CcCardCurrent[] cardsCurrent = new CcCardCurrent[pVariant.getCards().length];
        int i = 0;
        for (CcCardConfig card : pVariant.getCards()) {
            cardsCurrent[i++] = new CcCardCurrent(cardsCurrent, card);
        }
        sit.setCardsCurrent(cardsCurrent);
        sit.setCommoditiesCurrent(new int[pVariant.getCommodities().length]);

        return result;
    }



    private void toggleReviseMode()
    {
        if (iCardCtrl.isRevising())
        {
            iCardCtrl.leaveReviseMode();
            iBtnRevise.setText(CcConstants.STRINGS.revise());
            iTabPanel.getTabBar().setTabEnabled(TABPANEL_FUNDS_IDX, true);
            // leave commit button disabled
        }
        else {
            iBtnCommit.setEnabled(false);
            iBtnRevise.setText(CcConstants.STRINGS.reviseDone());
            iTabPanel.getTabBar().setTabEnabled(TABPANEL_FUNDS_IDX, false);
            iCardCtrl.enterReviseMode();
        }
    }



    private Panel createCardButtonPanel()
    {
        iBtnCommit = new Button(CcConstants.STRINGS.commit());
        iBtnCommit.setStyleName(CcConstants.CSS.ccButton());
        iBtnCommit.setTitle(CcConstants.STRINGS.btnTitleBuyCards());
        iBtnCommit.setEnabled(false);
        iBtnCommit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (iCardCtrl.hasAnyPlans()) {
                    iCardCtrl.commit();
                }
            }
        });

        final Button btnChangeUser = new Button(CcConstants.STRINGS.changeUser());
        btnChangeUser.setStyleName(CcConstants.CSS.ccButton());
        btnChangeUser.setTitle(CcConstants.STRINGS.btnTitleChangeUser());
        // TODO Change User Handler

        iBtnRevise = new Button(CcConstants.STRINGS.revise());
        iBtnRevise.setStyleName(CcConstants.CSS.ccButton());
        iBtnRevise.setTitle(CcConstants.STRINGS.btnTitleRevise());
        iBtnRevise.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                if (iCardCtrl.isRevising() || !iCardCtrl.hasAnyPlans()) {
                    toggleReviseMode();
                } else {
                    CcMessageBox.showOkCancel(CcConstants.STRINGS.askAreYouSure(),
                        SafeHtmlUtils.fromString(CcConstants.STRINGS.askClearPlans()),
                        iTabPanel, new CcResultCallback() {
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
        result.add(btnChangeUser);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        result.add(iBtnRevise);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        result.add(iBtnCommit);
        result.setStyleName(CcConstants.CSS.ccButtonPanel() + " " //$NON-NLS-1$
            + CcConstants.CSS_BLUEGRADIENT);
        return result;
    }



    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        // BEGIN MOCK
        final CcVariantConfig variant = new CcVariantConfigMock();
        final CcGame game = mockGame(variant);
        final CcSituation sit = game.getSituations().values().iterator().next();
        final CcCardCurrent[] cardsCurrent = sit.getCardsCurrent();
        // END MOCK

        // Inject CSS
        CcClientBundle.INSTANCE.css().ensureInjected();

        CcEventBus.INSTANCE.setSituation(sit);
        final CcFundsController fundsCtrl = new CcFundsController(sit);
        iCardCtrl = new CcCardController(cardsCurrent, new CcCardStateManager(variant,
            fundsCtrl, sit.getPlayer().getWinningTotal()));
        iCardCtrl.init();

        CcStatistics stats = new CcStatistics(sit);
        VerticalPanel outerVP = new VerticalPanel();
        outerVP.add(createCardButtonPanel());
        outerVP.add(stats);
        outerVP.add(iCardCtrl.getGrid());

        final TabPanel tp = new TabPanel();
        tp.add(outerVP, CcConstants.STRINGS.civCards());
        tp.add(fundsCtrl.getPanel(), CcConstants.STRINGS.funds());
        tp.selectTab(CcConstants.TABNUM_CARDS);
        tp.setStyleName(CcConstants.CSS.ccTabPanel(), true);
        tp.getDeckPanel().setStyleName(CcConstants.CSS.ccTabPanelBottom());
        //tp.getDeckPanel().setAnimationEnabled(true);
        tp.getTabBar().addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(final BeforeSelectionEvent<Integer> pEvent)
            {
                int current = ((TabBar) pEvent.getSource()).getSelectedTab();
                int sel = pEvent.getItem().intValue();
                if (sel == CcConstants.TABNUM_CARDS && current != CcConstants.TABNUM_CARDS) {
                    CcEventBus.INSTANCE.fireEventFromSource(
                        new CcFundsEvent(fundsCtrl.getFunds(), fundsCtrl.isEnabled()), tp);
                }
            }
        });
        iTabPanel = tp;

        // Add it to the root panel.
        RootPanel.get(CcConstants.INJECTION_POINT).add(tp);

        // register event handlers
        CcEventBus.INSTANCE.addHandlerToSource(CcAllStatesEvent.TYPE, iCardCtrl, this);
        CcEventBus.INSTANCE.addHandlerToSource(CcStateEvent.TYPE, iCardCtrl, this);

        // trigger initial calculation of all state information
        CcEventBus.INSTANCE.fireEventFromSource(new CcAllStatesEvent(), this);
    }



    @Override
    public void onAllStatesChanged(final CcAllStatesEvent pEvent)
    {
        updateCommitButton(null);
    }



    @Override
    public void onStateChanged(final CcStateEvent pEvent)
    {
        updateCommitButton(pEvent.getSituation().getCardsCurrent()[pEvent.getRowIdx()]);
    }



    /**
     * Enable/disable commit button.
     * @param pCard if an individual card was changed, that card; else if it's a
     *              global update, just <code>null</code>
     */
    private void updateCommitButton(final CcCardCurrent pCard)
    {
        if (iBtnCommit.isEnabled()) {
            if (!iCardCtrl.hasAnyPlans()) {
                iBtnCommit.setEnabled(false);
            }
        } else {
            if (pCard != null) {
                if (pCard.getState() == CcState.Planned) {
                    iBtnCommit.setEnabled(true);
                }
            } else if (iCardCtrl.hasAnyPlans()) {
                iBtnCommit.setEnabled(true);
            }
        }
    }
}
