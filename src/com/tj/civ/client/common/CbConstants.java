/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 07.01.2011
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
package com.tj.civ.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import com.tj.civ.client.places.CbAbstractPlace;
import com.tj.civ.client.places.CcGamesPlace;
import com.tj.civ.client.resources.CcClientBundleIF;
import com.tj.civ.client.resources.CcCssResourceIF;
import com.tj.civ.client.resources.CcImagesIF;
import com.tj.civ.client.resources.CcLocalizedConstantsIF;
import com.tj.civ.client.resources.CcLocalizedMessagesIF;


/**
 * Global application constants.
 *
 * @author Thomas Jensen
 */
public final class CbConstants
{
    /** the name of this application */
    public static final String APPNAME = "CivBuddy"; //$NON-NLS-1$

    /** Name of English locale */
    public static final String LOCALE_EN = "en"; //$NON-NLS-1$

    /** Name of German locale */
    public static final String LOCALE_DE = "de"; //$NON-NLS-1$

    /** how many pixels per point of credit. This is used to calculate credit bar
     *  widths */
    public static final float BAR_PIXEL_POINT_RATIO = 1.2f;

    /** Unit 'pixels' in CSS dimension specifications */
    public static final String UNIT_PIXEL = "px"; //$NON-NLS-1$

    /** our image bundle */
    public static final CcImagesIF IMG_BUNDLE = GWT.create(CcImagesIF.class);

    /** localized constants used by the application */
    public static final CcLocalizedConstantsIF STRINGS = GWT.create(CcLocalizedConstantsIF.class);

    /** localized parameterized messages used by the application */
    public static final CcLocalizedMessagesIF MESSAGES = GWT.create(CcLocalizedMessagesIF.class);

    /** our CSS resource */
    public static final CcCssResourceIF CSS = CcClientBundleIF.INSTANCE.css();

    /** Element where this application is injected into the HTML host page */
    public static final String INJECTION_POINT = "injectionPoint"; //$NON-NLS-1$

    /** index of the cards tab in the tab bar */
    public static final int TABNUM_CARDS = 0;

    /** for technical reasons, the blue gradient which is used by both the button
     *  panel and the inner stats panel is defined on the HTML host page, not the
     *  CSS resource */
    public static final String CSS_BLUEGRADIENT = "cc-blueGradient"; //$NON-NLS-1$

    /** simple date format <tt>yyyy-MM-dd</tt> */
    public static final DateTimeFormat DATE_FORMAT =
        DateTimeFormat.getFormat("yyyy-MM-dd"); //$NON-NLS-1$

    /** simple date format <tt>yyyy-MM-dd</tt> */
    public static final DateTimeFormat TIMESTAMP_FORMAT =
        DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.SSS"); //$NON-NLS-1$

    /** the default place, which is the 'Games' place */
    public static final CbAbstractPlace DEFAULT_PLACE = new CcGamesPlace();



    /**
     * Constructor.
     */
    private CbConstants()
    {
        super();
    }
}
