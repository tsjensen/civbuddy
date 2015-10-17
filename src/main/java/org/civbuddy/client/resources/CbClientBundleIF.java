/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-20
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;


/**
 * This application's ClientBundle.
 *
 * @author Thomas Jensen
 */
public interface CbClientBundleIF
    extends ClientBundle
{
    /** the single instance of this ClientBundle */
    CbClientBundleIF INSTANCE = GWT.create(CbClientBundleIF.class);

    /** our main CSS.
     *  @return CSSResource */
    @Source("CivBuddy.css")
    CbCssResourceIF css();

    /** the definition file of the 'Original Civilization' game variant.
     *  @return TextResource */
    @Source("variant_org.json")
    TextResource variantOrg();

    /** the definition file of the 'Original Civilization with Western Expansion'
     *  game variant.
     *  @return TextResource */
    @Source("variant_org_we.json")
    TextResource variantOrgWE();

    /** the definition file of the 'Advanced Civilization' game variant.
     *  @return TextResource */
    @Source("variant_adv.json")
    TextResource variantAdvanced();

    /** logger configuration in our own format, because java.util.logging filters are
     *  not supported by GWT yet.
     *  @return TextResource */
    @Source("logging_props.json")
    TextResource loggingProps();
}
