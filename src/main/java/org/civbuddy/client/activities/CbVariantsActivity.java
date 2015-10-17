/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-05-05
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.civbuddy.client.CbClientFactoryIF;
import org.civbuddy.client.common.CbConstants;
import org.civbuddy.client.common.CbLogAdapter;
import org.civbuddy.client.common.CbStorage;
import org.civbuddy.client.common.CbUtil;
import org.civbuddy.client.model.CbVariantConfig;
import org.civbuddy.client.model.vo.CbGameVO;
import org.civbuddy.client.places.CbAbstractPlace;
import org.civbuddy.client.places.CbVariantsPlace;
import org.civbuddy.client.views.CbVariantsViewIF;


/**
 * Presenter of the 'Variants' view.
 *
 * @author Thomas Jensen
 */
public class CbVariantsActivity
    extends CbAbstractActivity
    implements CbVariantsViewIF.CbPresenterIF
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbVariantsActivity.class);

    /** the name of the new game to be created as passed by the place */
    private String iGameName;



    /**
     * Constructor.
     * @param pPlace the place
     * @param pClientFactory our client factory
     */
    public CbVariantsActivity(final CbVariantsPlace pPlace,
        final CbClientFactoryIF pClientFactory)
    {
        super(pPlace, pClientFactory);
        LOG.enter(CbLogAdapter.CONSTRUCTOR);

        iGameName = pPlace != null ? pPlace.getGameName() : null;
        pClientFactory.getVariantsView().setVariants(CbStorage.loadVariantList());

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    @Override
    public void start(final AcceptsOneWidget pContainerWidget, final EventBus pEventBus)
    {
        LOG.enter("start"); //$NON-NLS-1$

        CbVariantsViewIF view = getClientFactory().getVariantsView();
        view.setPresenter(this);
        view.setMarked(null);

        CbUtil.setBrowserTitle(CbConstants.STRINGS.viewVariantsHeading());
        pContainerWidget.setWidget(view.asWidget());

        LOG.exit("start"); //$NON-NLS-1$
    }



    @Override
    public String createNewGame(final String pGameName, final String pVariantKey)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("createNewGame",  //$NON-NLS-1$
                new String[]{"pGameName", "pVariantKey"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{pGameName, pVariantKey});
        }
        if (CbUtil.isEmpty(pGameName) || CbUtil.isEmpty(pVariantKey)) {
            LOG.exit("createNewGame", null); //$NON-NLS-1$
            return null;
        }

        CbVariantConfig variant = CbStorage.loadVariant(pVariantKey);
        if (variant == null) {
            Window.alert(CbConstants.STRINGS.viewGamesMessageUnknownVariant());
            LOG.exit("createNewGame", null); //$NON-NLS-1$
            return null;
        }
        if (CbStorage.gameExists(pGameName)) {
            Window.alert(CbConstants.MESSAGES.viewGamesMessageInvalidGame(pGameName));
            LOG.exit("createNewGame", null); //$NON-NLS-1$
            return null;
        }

        CbGameVO gameVO = new CbGameVO(null, pGameName, variant.getLocalizedDisplayName());
        String key = CbStorage.saveNewGame(gameVO, variant.getPersistenceKey());
        gameVO.setPersistenceKey(key);
        if (LOG.isDebugEnabled()) {
            LOG.debug("createNewGame", //$NON-NLS-1$
                "Successfully created new game '" + key + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        LOG.exit("createNewGame", key); //$NON-NLS-1$
        return key;
    }



    @Override
    public void goTo(final CbAbstractPlace pPlace)
    {
        if (LOG.isTraceEnabled()) {
            LOG.enter("goTo",  //$NON-NLS-1$
                new String[]{"pPlace"}, new Object[]{pPlace}); //$NON-NLS-1$
        }
        getClientFactory().getVariantsView().setMarked(null);
        super.goTo(pPlace);
        LOG.exit("goTo"); //$NON-NLS-1$
    }



    @Override
    public void onNewClicked()
    {
        Window.alert("This function is not implemented yet."); //$NON-NLS-1$
        // TODO allow adding new variants
    }



    @Override
    public void onChangeClicked(final String pVariantKey)
    {
        // do nothing
    }



    @Override
    public void onRemoveClicked(final String pVariantKey)
    {
        Window.alert("This function is not implemented yet."); //$NON-NLS-1$
        // TODO implement onRemoveClicked()
        getClientFactory().getVariantsView().setMarked(null);
    }



    @Override
    public String getGameName()
    {
        return iGameName;
    }
}
