/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 05.05.2011
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

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.model.vo.CbVariantVO;
import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CbGamesPlace;
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
        MSGS.setViewTitle("Variants");
        MSGS.setBtnBackCaption("Cancel");
        MSGS.setBtnBackTooltip("Return to the list of games");
        MSGS.setBtnNewTooltip("Define a new variant");
        MSGS.setBtnEditTooltip(null);
        MSGS.setBtnRemoveTooltip("Delete the selected variant");
        MSGS.setEmptyListMessage("Define a new game variant by pressing 'New'.");
        MSGS.setSelectTooltip("Select this variant");
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
        // TODO go directly to the players view
        return new CbGamesPlace(pVariantKey, getPresenter().getGameName());
    }
}
