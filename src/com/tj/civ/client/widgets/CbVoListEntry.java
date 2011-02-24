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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tj.civ.client.resources.CcConstants;


/**
 * Displays one entry in the list of games on the first view.
 * The game name serves as the game ID, and is used for equals(), hashcode() etc.
 *
 * @author Thomas Jensen
 */
public class CcGameListEntry
    extends VerticalPanel
    implements Comparable<CcGameListEntry>
{
    /** game name (and ID) */
    private String iName;

    /** name label */
    private Label iLblName;

    /** current row index in the list that's showing us */
    private int iRowIdx;



    /**
     * Constructor.
     * @param pGameName the game name to display
     * @param pVariantName the variant name of that game
     */
    public CcGameListEntry(final String pGameName, final String pVariantName)
    {
        super();
        iName = pGameName;
        iLblName = new Label(pGameName);
        iLblName.setStyleName(CcConstants.CSS.ccGameName());
        Label lblVariant = new Label(CcConstants.STRINGS.rules()
            + ": " + pVariantName); //$NON-NLS-1$
        lblVariant.setStyleName(CcConstants.CSS.ccGameVariant());
        add(iLblName);
        add(lblVariant);
        setStyleName(CcConstants.CSS.ccGameListEntry());
    }



    @Override
    public int compareTo(final CcGameListEntry pOther)
    {
        return iName.compareToIgnoreCase(pOther.iName);
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((iName == null) ? 0 : iName.hashCode());
        return result;
    }



    @Override
    public boolean equals(final Object pOther)
    {
        if (this == pOther) {
            return true;
        }
        if (pOther == null) {
            return false;
        }
        if (getClass() != pOther.getClass()) {
            return false;
        }

        CcGameListEntry other = (CcGameListEntry) pOther;
        return compareTo(other) == 0;
    }



    public String getName()
    {
        return iName;
    }



    /**
     * Setter.
     * @param pName the new value of {@link #iName}
     */
    public void setName(final String pName)
    {
        iName = pName;
        iLblName.setText(pName);
    }



    public int getRowIdx()
    {
        return iRowIdx;
    }

    public void setRowIdx(final int pRowIdx)
    {
        iRowIdx = pRowIdx;
    }
}
