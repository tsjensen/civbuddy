/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-11-27
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import com.tj.civ.client.common.CbLogAdapter;


/**
 * A simple helper class for managing an element's title.
 *
 * @author Thomas Jensen
 */
public class CbTitleBackup
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbTitleBackup.class);

    /** where we store the title text of disabled buttons for later reactivation */
    private static final String DOMATTR_TITLE_BACKUP = "cbTitleBkp";  //$NON-NLS-1$

    /** the element attribute '<tt>title</tt>' */
    private static final String DOMATTR_TITLE = "title";  //$NON-NLS-1$

    /** the DOM element we're attached to */
    private Element iElement;

    /** flag indicating if the title is currently shown */
    private boolean iTitleShown;



    /**
     * Constructor.
     * @param pElement a DOM element
     */
    public CbTitleBackup(final Element pElement)
    {
        iElement = pElement;
        iTitleShown = true;
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
            DOM.setElementProperty(iElement, DOMATTR_TITLE_BACKUP, null);
        }
        DOM.removeElementAttribute(iElement, DOMATTR_TITLE);
        iTitleShown = false;
    }



    /**
     * Set title to value of backup property and clear backup.
     */
    public void show()
    {
        String title = DOM.getElementProperty(iElement, DOMATTR_TITLE_BACKUP);
        if (title != null && title.length() > 0) {
            DOM.setElementProperty(iElement, DOMATTR_TITLE, title);
        } else {
            LOG.warn("show", //$NON-NLS-1$
                "Should show title but no title backup found. " //$NON-NLS-1$
                + "Leaving current title (whatever it is)."); //$NON-NLS-1$
        }
        DOM.setElementProperty(iElement, DOMATTR_TITLE_BACKUP, null);
        iTitleShown = true;
    }



    /**
     * Set the title text without changing the display state.
     * @param pTitle the new title text
     */
    public void setTitle(final String pTitle)
    {
        if (pTitle != null && pTitle.length() > 0) {
            if (iTitleShown) {
                DOM.setElementProperty(iElement, DOMATTR_TITLE, pTitle);
            } else {
                DOM.setElementProperty(iElement, DOMATTR_TITLE_BACKUP, pTitle);
            }
        } else {
            DOM.removeElementAttribute(iElement, DOMATTR_TITLE);
            DOM.setElementProperty(iElement, DOMATTR_TITLE_BACKUP, null);
        }
    }
}
