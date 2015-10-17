/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-14
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.views;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import org.civbuddy.client.activities.CbListPresenterIF;
import org.civbuddy.client.model.vo.CbVariantVO;


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
     * Override the preset header hint on special occasions.
     * @param pHeaderHint the new header hint
     */
    void setHeaderHint(final String pHeaderHint);



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



        /**
         * Create a new game and persist it.
         * @param pGameName the new game's name
         * @param pVariantKey the variant's persistence key
         * @return the new game's persistence key on success, or <code>null</code>
         *          on error
         */
        String createNewGame(final String pGameName, final String pVariantKey);
    }
}
