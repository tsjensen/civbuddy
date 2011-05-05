/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-14
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

import com.google.gwt.user.client.ui.IsWidget;

import com.tj.civ.client.activities.CbListPresenterIF;
import com.tj.civ.client.model.vo.CbVariantVO;


/**
 * Decribes the 'Variants' view.
 * 
 * @author Thomas Jensen
 */
public interface CbVariantsViewIF
    extends IsWidget
{

    /**
     * Setter. We need a setter because views are recycled, presenters are not.
     * @param pPresenter the new presenter
     */
    void setPresenter(final CbPresenterIF pPresenter);



    /**
     * Set the entire list of variants, potentially replacing a present list.
     * @param pVariantList variants
     */
    void setVariants(final List<CbVariantVO> pVariantList);



    /**
     * Add a row to the list of variants.
     * @param pVariantVo variant VO
     */
    void addVariant(final CbVariantVO pVariantVo);



    /**
     * Delete a row from the list of variants.
     * @param pVariantKey variant persistence key
     */
    void deleteVariant(final String pVariantKey);



    /**
     * Mark the variant with the given persistence key.
     * @param pVariantKey variant ID
     */
    void setMarked(final String pVariantKey);



    /**
     * Describes the presenter of the 'Variants' view.
     * 
     * @author Thomas Jensen
     */
    public interface CbPresenterIF
        extends CbListPresenterIF
    {
        /**
         * Getter.
         * @return the name of the new game to be created, or <code>null</code> if
         *              we started on the 'Variants' view
         */
        String getGameName();
    }
}
