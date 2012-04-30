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
package com.tj.civ.client.views;

import java.util.ArrayList;
import java.util.List;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.vo.CbVariantVO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbPlayersPlace;
import com.tj.civ.client.views.CbVariantsViewIF.CbPresenterIF;
import com.tj.civ.client.widgets.CbVoListEntry;


/**
 * Implementation of the 'Variants' view.
 *
 * @author Thomas Jensen
 */
public class CbVariantsView
    extends CbAbstractListView<CbVoListEntry<CbVariantVO>, CbPresenterIF>
    implements CbVariantsViewIF
{
    /** message texts used in this view */
    private static final CbMessages MSGS = new CbMessages();

    static {
        MSGS.setViewTitle(CbConstants.STRINGS.viewVariantsHeading());
        MSGS.setHeaderHint(CbConstants.STRINGS.viewVariantsHeaderHint());
        MSGS.setBtnBackCaption(CbConstants.STRINGS.viewVariantsNavbuttonBack());
        MSGS.setBtnBackTooltip(CbConstants.STRINGS.viewVariantsNavbuttonBackTitle());
        MSGS.setBtnNewTooltip(CbConstants.STRINGS.viewVariantsButtonNewTitle());
        MSGS.setBtnEditTooltip(null);
        MSGS.setBtnRemoveTooltip(CbConstants.STRINGS.viewVariantsButtonRemoveTitle());
        MSGS.setEmptyListMessage(CbConstants.STRINGS.viewVariantsMessageEmptyList());
        MSGS.setSelectTooltip(CbConstants.STRINGS.viewVariantsChooseTitle());
    }



    /**
     * Constructor.
     */
    public CbVariantsView()
    {
        super(MSGS, false);
    }



    @Override
    public void setVariants(final List<CbVariantVO> pVariantList)
    {
        List<CbVoListEntry<CbVariantVO>> widgets = new ArrayList<CbVoListEntry<CbVariantVO>>();
        if (pVariantList != null) {
            for (CbVariantVO vo : pVariantList)
            {
                widgets.add(new CbVoListEntry<CbVariantVO>(vo));
            }
        }
        setDisplayWidgets(widgets);
    }



    @Override
    public void addVariant(final CbVariantVO pVariantVo)
    {
        addDisplayWidget(new CbVoListEntry<CbVariantVO>(pVariantVo));
    }



    @Override
    public void deleteVariant(final String pVariantKey)
    {
        removeDisplayWidget(pVariantKey);
    }



    @Override
    protected String getIdFromWidget(final CbVoListEntry<CbVariantVO> pWidget)
    {
        return pWidget.getViewObject().getPersistenceKey();
    }



    @Override
    protected CbAbstractPlace getPreviousPlace()
    {
        return CbConstants.DEFAULT_PLACE;
    }



    @Override
    protected CbAbstractPlace getNextPlace(final String pVariantKey)
    {
        String gameKey = getPresenter().createNewGame(getPresenter().getGameName(), pVariantKey);
        if (gameKey != null) {
            return new CbPlayersPlace(gameKey);
        } else {
            return CbConstants.DEFAULT_PLACE;
        }
    }



    @Override
    public void setHeaderHint(final String pHeaderHint)
    {
        MSGS.setHeaderHint(pHeaderHint);
    }
}
