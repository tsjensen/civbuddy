/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 13.02.2011
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

import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.common.CbConstants;


/**
 * Displays a label '&gt;&gt;' that serves as a 'more' icon.
 *
 * @author Thomas Jensen
 */
public class CbMoreArrow
    extends Label
{

    /**
     * Constructor.
     * @param pToolTip Tooltip appearing on the widget
     */
    public CbMoreArrow(final String pToolTip)
    {
        super(">>"); //$NON-NLS-1$
        if (pToolTip != null) {
            setTitle(pToolTip);
        }
        setStyleName(CbConstants.CSS.ccMoreLabel());
    }



    /**
     * Constructor.
     */
    public CbMoreArrow()
    {
        this(null);
    }
}
