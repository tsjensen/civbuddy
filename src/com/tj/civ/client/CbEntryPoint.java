/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-11
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

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbLogAdapter;
import com.tj.civ.client.common.CbStorage;
import com.tj.civ.client.model.CbVariantsBuiltIn;
import com.tj.civ.client.resources.CbClientBundleIF;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CbEntryPoint
    implements EntryPoint
{
    /** logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbEntryPoint.class);

    /** the topmost widget */
    private SimplePanel iAppWidget = new SimplePanel();



    private static void setUncaughtExceptionHandler()
    {
        final UncaughtExceptionHandler gwtHandler = GWT.getUncaughtExceptionHandler();
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(final Throwable pEx)
            {
                if (LOG.isErrorEnabled() && pEx != null) {
                    LOG.error("Unexpected error: " //$NON-NLS-1$
                        + pEx.getMessage(), pEx);
                }
                gwtHandler.onUncaughtException(pEx);
            }
        });
    }



    /**
     * Make sure that the built-in variants are present in HTML5 storage. Old
     * versions of these variants have their own persistence keys and might still
     * be in use by old games, so we don't delete them.
     */
    private void assertBuiltInVariants()
    {
        for (CbVariantsBuiltIn biv : CbVariantsBuiltIn.values()) {
            CbStorage.saveVariant(biv.getVariantConfig());
        }
    }



    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad()
    {
        LOG.enter("onModuleLoad");  //$NON-NLS-1$

        // Inject CSS
        CbClientBundleIF.INSTANCE.css().ensureInjected();

        CbClientFactoryIF clientFactory = GWT.create(CbClientFactoryIF.class);
        PlaceController placeController = clientFactory.getPlaceController();
        final EventBus eventBus = clientFactory.getEventBus();
        iAppWidget.setStyleName(CbConstants.CSS.cbAppWidget());
        
        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityMapper activityMapper = new CbActivityMapper(clientFactory);
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(iAppWidget);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        CbPlaceHistoryMapperIF historyMapper = GWT.create(CbPlaceHistoryMapperIF.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, CbConstants.DEFAULT_PLACE);

        // Add a handler for logging uncaught errors
        setUncaughtExceptionHandler();

        // built-in variants should be present in HTML5 storage
        assertBuiltInVariants();

        // Add it to the root panel.
        RootPanel.get(CbConstants.INJECTION_POINT).add(iAppWidget);

        // Goes to the place represented on URL else default place
        historyHandler.handleCurrentHistory();

        LOG.exit("onModuleLoad");  //$NON-NLS-1$
    }
}
