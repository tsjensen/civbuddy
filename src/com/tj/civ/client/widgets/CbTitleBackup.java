/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-11-27
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
import com.google.gwt.user.client.Element;


/**
 * A simple helper class for managing an element's title.
 *
 * @author Thomas Jensen
 */
public class CbTitleBackup
{
    /** where we store the title text of disabled buttons for later reactivation */
    private static final String DOMATTR_TITLE_BACKUP = "cbTitleBkp";  //$NON-NLS-1$

    /** the element attribute '<tt>title</tt>' */
    private static final String DOMATTR_TITLE = "title";  //$NON-NLS-1$

    /** the DOM element we're attached to */
    private Element iElement;



    /**
     * Constructor.
     * @param pElement a DOM element
     */
    public CbTitleBackup(final Element pElement)
    {
        iElement = pElement;
    }



    /**
     * Store title value in backup property and clear title.
     */
    public void hide()
    {
        String title = DOM.getElementAttribute(iElement, DOMATTR_TITLE);
        if (title != null && title.length() > 0) {
            DOM.setElementProperty(iElement, DOMATTR_TITLE_BACKUP, title);
        } else {
            DOM.removeElementAttribute(iElement, DOMATTR_TITLE_BACKUP);
        }
        DOM.removeElementAttribute(iElement, DOMATTR_TITLE);
    }



    /**
     * Set title to value of backup property and clear backup.
     */
    public void show()
    {
        String title = DOM.getElementAttribute(iElement, DOMATTR_TITLE_BACKUP);
        if (title != null && title.length() > 0) {
            DOM.setElementProperty(iElement, DOMATTR_TITLE, title);
        } else {
            DOM.removeElementAttribute(iElement, DOMATTR_TITLE);
        }
        DOM.removeElementAttribute(iElement, DOMATTR_TITLE_BACKUP);
    }
}
