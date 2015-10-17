/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-02-13
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import org.civbuddy.client.common.CbConstants;
import org.civbuddy.client.model.vo.CbAbstractViewObject;


/**
 * Displays one entry with primary and secondary text in the list of view objects
 * on the abstact list view.
 * The primary text serves as ID, and is used for equals(), hashcode() etc.
 *
 * @author Thomas Jensen
 * @param <V> type of view object we are displaying
 */
public class CbVoListEntry<V extends CbAbstractViewObject>
    extends Composite
    implements Comparable<CbVoListEntry<V>>
{
    /** the information on the view object to display */
    private V iViewObject;

    /** name label */
    private Label iLblName;

    /** current row index in the list that's showing us */
    private int iRowIdx;



    /**
     * Constructor.
     * @param pViewObject the view object to display
     */
    public CbVoListEntry(final V pViewObject)
    {
        super();
        iViewObject = pViewObject;

        iLblName = new Label(iViewObject.getPrimaryText());
        iLblName.setStyleName(CbConstants.CSS.ccGameName());
        Label lblVariant = new Label(iViewObject.getSecondaryText());
        lblVariant.setStyleName(CbConstants.CSS.ccGameVariant());

        FlowPanel fp = new FlowPanel();
        fp.add(iLblName);
        fp.add(lblVariant);
        fp.setStyleName(CbConstants.CSS.cbDisplayWidget2line());
        initWidget(fp);
    }



    @Override
    public int compareTo(final CbVoListEntry<V> pOther)
    {
        return iViewObject.getPrimaryText().compareToIgnoreCase(
            pOther.iViewObject.getPrimaryText());
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        String text1 = iViewObject != null ? iViewObject.getPrimaryText() : null;
        result = prime * result + ((text1 == null) ? 0 : text1.hashCode());
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

        @SuppressWarnings("unchecked")
        CbVoListEntry<V> other = (CbVoListEntry<V>) pOther;
        return compareTo(other) == 0;
    }



    /**
     * Setter.
     * @param pPrimaryText the new primary text
     */
    public void setPrimaryText(final String pPrimaryText)
    {
        iViewObject.setPrimaryText(pPrimaryText);
        iLblName.setText(pPrimaryText);
    }



    public int getRowIdx()
    {
        return iRowIdx;
    }

    public void setRowIdx(final int pRowIdx)
    {
        iRowIdx = pRowIdx;
    }



    public V getViewObject()
    {
        return iViewObject;
    }
}
