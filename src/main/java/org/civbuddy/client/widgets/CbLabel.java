/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-24
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
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

import org.civbuddy.client.common.CbConstants;


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
    extends Composite
    implements HasEnabled
{
    /** <code>true</code> if the label is enabled */
    private boolean iIsEnabled = true;

    /** CSS style to use when widget is enabled */
    private String iStyleEnabled;

    /** CSS style to use when widget is disabled */
    private String iStyleDisabled;



    /**
     * Constructor.
     */
    public CbLabel()
    {
        this(CbConstants.CSS.ccLabel(), CbConstants.CSS.ccLabelDisabled());
    }



    /**
     * Constructor.
     * @param pStyleEnabled CSS style to use when widget is enabled
     * @param pStyleDisabled CSS style to use when widget is disabled
     */
    public CbLabel(final String pStyleEnabled, final String pStyleDisabled)
    {
        iStyleEnabled = pStyleEnabled;
        iStyleDisabled = pStyleDisabled;
        Label w = new Label();
        w.setStyleName(pStyleEnabled);
        initWidget(w);
    }



    /**
     * Constructor.
     * @param pText the new label's text
     * @param pInline <code>true</code> to render in a <tt>&lt;span&gt;</tt>,
     *          <code>false</code> to render in a <tt>&lt;div&gt;</tt>
     * @param pStyleEnabled CSS style to use when widget is enabled
     * @param pStyleDisabled CSS style to use when widget is disabled
     */
    public CbLabel(final String pText, final boolean pInline,
        final String pStyleEnabled, final String pStyleDisabled)
    {
        Label w = null;
        if (pInline) {
            w = new InlineLabel(pText);
        } else {
            w = new Label(pText);
        }
        iStyleEnabled = pStyleEnabled;
        iStyleDisabled = pStyleDisabled;
        w.setStyleName(pStyleEnabled);
        initWidget(w);
    }



    /**
     * Constructor.
     * @param pText the new label's text
     */
    public CbLabel(final String pText)
    {
        this(pText, false, CbConstants.CSS.ccLabel(), CbConstants.CSS.ccLabelDisabled());
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
            setStyleName(iStyleEnabled);
        } else {
            setStyleName(iStyleDisabled);
        }
        iIsEnabled = pEnabled;
    }



    /**
     * Sets the label's content to the given text.
     * @param pText the widget's new text
     * @see Label#setText(String)
     */
    public void setText(final String pText)
    {
        ((Label) getWidget()).setText(pText);
    }
}
