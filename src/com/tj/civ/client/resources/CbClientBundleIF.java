/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 20.01.2011
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
package com.tj.civ.client.resources;

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
}
