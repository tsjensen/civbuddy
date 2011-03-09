/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 08.03.2011
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
package com.tj.civ.client.model;


/**
 * Indicates that the implementing class is a wrapper class for a JSO.
 *
 * @author Thomas Jensen
 */
public interface CcJsoWrapperIF
{
    /**
     * Uses the value just set for the wrapped JSO to recalculate the instance
     * fields which depend on the JSO values (usually, that's <em>all</em>
     * instance fields).
     * <p>Subclasses of {@link CcIndependentlyPersistableObject} call this method
     * automatically upon construction.
     */
    void evaluateJsoState();
}
