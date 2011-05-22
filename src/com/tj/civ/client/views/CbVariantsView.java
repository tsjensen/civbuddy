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

import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;

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
        MSGS.setBtnBackCaption(SafeHtmlUtils.fromSafeConstant("&lt;&nbsp;Cancel"));
        MSGS.setBtnBackTooltip("Return to the list of games");
        MSGS.setBtnNewCaption("New");
        MSGS.setBtnNewTooltip("Define a new variant");
        MSGS.setBtnEditCaption(null);
        MSGS.setBtnEditTooltip(null);
        MSGS.setBtnRemoveCaption("Delete");
        MSGS.setBtnRemoveTooltip("Delete the selected variant");
        MSGS.setEmptyListMessage("Define a new game variant by pressing 'New'.");
        MSGS.setSelectTooltip("Select this variant");
    }



    /**
     * Constructor.
     */
    public CbVariantsView()
    {
        super(MSGS);
    }



    @Override
    public void setVariants(final List<CbVariantVO> pVariantList)
    {
        getEntries().clear();
        if (pVariantList != null) {
            for (CbVariantVO vo : pVariantList)
            {
                CbVoListEntry<CbVariantVO> widget = new CbVoListEntry<CbVariantVO>(vo);
                getEntries().add(widget);
            }
        }
        updateGrid(getEntries().size() - getRowCount());
    }



    @Override
    public void addVariant(final CbVariantVO pVariantVo)
    {
        CbVoListEntry<CbVariantVO> widget = new CbVoListEntry<CbVariantVO>(pVariantVo);
        getEntries().add(widget);
        updateGrid(1);
    }



    @Override
    public void deleteVariant(final String pVariantKey)
    {
        removeItem(pVariantKey);
        updateGrid(-1);
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
