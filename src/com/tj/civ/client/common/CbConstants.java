/*
 * CivBuddy - A Civilization Tactics Guide
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
import com.tj.civ.client.places.CbGamesPlace;
import com.tj.civ.client.resources.CbBuildNumberIF;
import com.tj.civ.client.resources.CbClientBundleIF;
import com.tj.civ.client.resources.CbCssResourceIF;
import com.tj.civ.client.resources.CbImagesIF;
import com.tj.civ.client.resources.CbLocalizedConstantsIF;
import com.tj.civ.client.resources.CbLocalizedMessagesIF;
import com.tj.civ.client.resources.CbVersionIF;


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
    public static final CbImagesIF IMG_BUNDLE = GWT.create(CbImagesIF.class);

    /** localized constants used by the application */
    public static final CbLocalizedConstantsIF STRINGS = GWT.create(CbLocalizedConstantsIF.class);

    /** localized parameterized messages used by the application */
    public static final CbLocalizedMessagesIF MESSAGES = GWT.create(CbLocalizedMessagesIF.class);

    /** accessor for the current build number */
    public static final CbBuildNumberIF BUILD_NUM = GWT.create(CbBuildNumberIF.class);

    /** accessor for the current version number and state */
    public static final CbVersionIF VERSION = GWT.create(CbVersionIF.class);

    /** our CSS resource */
    public static final CbCssResourceIF CSS = CbClientBundleIF.INSTANCE.css();

    /** Element where this application is injected into the HTML host page */
    public static final String INJECTION_POINT = "injectionPoint"; //$NON-NLS-1$

    /** for technical reasons, the blue gradient which is used by both the button
     *  panel and the inner stats panel is defined on the HTML host page, not the
     *  CSS resource */
    @Deprecated
    public static final String CSS_BLUEGRADIENT = "cc-blueGradient"; //$NON-NLS-1$

    /** for technical reasons, the gradient which is used by both the title bar and
     *  the bottom bar is defined on the HTML host page, not the CSS resource */
    public static final String CSS_TITLEBAR_GRADIENT = "cb-titlebar-gradient"; //$NON-NLS-1$

    /** for technical reasons, the gradient which is used by the extra bar is defined
     *  defined on the HTML host page, not the CSS resource */
    public static final String CSS_EXTRABAR_GRADIENT = "cb-extrabar-gradient"; //$NON-NLS-1$

    /** CSS selector prefix for {@link com.tj.civ.client.widgets.CbIconButton} styles */
    public static final String CSS_ICONBUTTON = "cb-iconbutton-"; //$NON-NLS-1$

    /** CSS selector prefix for {@link com.tj.civ.client.widgets.CbNavigationButton} styles */
    public static final String CSS_NAVBUTTON = "cb-nav"; //$NON-NLS-1$

    /** simple date format <tt>yyyy-MM-dd</tt> */
    public static final DateTimeFormat DATE_FORMAT =
        DateTimeFormat.getFormat("yyyy-MM-dd"); //$NON-NLS-1$

    /** timestamp format <tt>yyyy-MM-dd HH:mm:ss.SSS</tt> */
    public static final DateTimeFormat TIMESTAMP_FORMAT =
        DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.SSS"); //$NON-NLS-1$

    /** the default place, which is the 'Games' place */
    public static final CbAbstractPlace DEFAULT_PLACE = new CbGamesPlace();

    /** substring of a persistence key indicating a default variant */
    public static final String DEFAULT_VARIANT_MARK = "-CBDEFAULT_"; //$NON-NLS-1$

    /** DOM standard attribute name 'id' */
    public static final String DOMATTR_ID = "id"; //$NON-NLS-1$

    /** DOM standard attribute name 'style' */
    public static final String DOMATTR_STYLE = "style"; //$NON-NLS-1$

    /** DOM standard attribute name 'disabled' */
    public static final String DOMATTR_DISABLED = "disabled"; //$NON-NLS-1$

    /** flag indicating whether the current browser runs on a touch screen device */
    public static final boolean IS_TOUCH_DEVICE = CbUtil.isTouchDevice();

    /** the CSS style class on the body element which enables CSS hovers */
    public static final String CLASS_HOVERS_ENABLED = "enableCssHover"; //$NON-NLS-1$



    /**
     * Constructor.
     */
    private CbConstants()
    {
        super();
    }
}
