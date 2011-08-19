/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-08-19
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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/**
 * A {@link com.google.gwt.user.client.ui.FlowPanel} wrapped by a SPAN instead of a
 * DIV.
 *
 * @author Thomas Jensen
 */
public class CbInlineFlowPanel
    extends ComplexPanel
    implements InsertPanel.ForIsWidget
{
    /**
     * Constructor.
     */
    public CbInlineFlowPanel()
    {
        setElement(DOM.createSpan());
    }



    @Override
    public void add(final Widget pWidget)
    {
        add(pWidget, getElement());
    }



    @Override
    public void insert(final Widget pWidget, final int pBeforeIndex)
    {
        insert(pWidget, getElement(), pBeforeIndex, true);
    }



    @Override
    public void insert(final IsWidget pWidget, final int pBeforeIndex)
    {
        insert(asWidgetOrNull(pWidget), pBeforeIndex);
    }
}
