/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 24.01.2011
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
package com.tj.civ.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.common.CbConstants;


/**
 * A {@link Label} that can be enabled and disabled.
 * 
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>cc-Label
 *     <dd>style in enabled state
 * <dt>cc-LabelDisabled
 *     <dd>style in disabled state
 * </dl>
 *
 * @author Thomas Jensen
 */
public class CbLabel
    extends Label
    implements HasEnabled
{
    /** <code>true</code> if the label is enabled */
    private boolean iIsEnabled = true;



    /**
     * Constructor.
     */
    public CbLabel()
    {
        super();
    }



    /**
     * Constructor.
     * @param pText the new label's text
     */
    public CbLabel(final String pText)
    {
        super(pText);
    }



    /**
     * Constructor.
     * @param pElement the element to be used
     */
    public CbLabel(final Element pElement)
    {
        super(pElement);
    }



    /**
     * Constructor.
     * @param pText the new label's text
     * @param pDir the text's direction. Note that {@code DEFAULT} means direction
     *          should be inherited from the widget's parent element.
     */
    public CbLabel(final String pText, final Direction pDir)
    {
        super(pText, pDir);
    }



    /**
     * Constructor.
     * @param pText the new label's text
     * @param pDirectionEstimator A DirectionEstimator object used for automatic
     *          direction adjustment. For convenience,
     *          {@link #DEFAULT_DIRECTION_ESTIMATOR} can be used.
     */
    public CbLabel(final String pText, final DirectionEstimator pDirectionEstimator)
    {
        super(pText, pDirectionEstimator);
    }



    /**
     * Constructor.
     *
     * @param pText the new label's text
     * @param pWordWrap <code>false</code> to disable word wrapping
     */
    public CbLabel(final String pText, final boolean pWordWrap)
    {
        super(pText, pWordWrap);
    }



    @Override
    public boolean isEnabled()
    {
        return iIsEnabled;
    }



    @Override
    public void setEnabled(final boolean pEnabled)
    {
        if (pEnabled) {
            setStyleName(CbConstants.CSS.ccLabel());
        } else {
            setStyleName(CbConstants.CSS.ccLabelDisabled());
        }
        iIsEnabled = pEnabled;
    }
}
