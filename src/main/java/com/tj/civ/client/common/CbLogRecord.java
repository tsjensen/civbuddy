/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-04-19
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.common;

import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * Subclass of {@link LogRecord} indicating to the formatter that a message was
 * created in our own code instead of GWT code.
 * 
 * <p>Adds nothing else to its superclass.
 *
 * @author Thomas Jensen
 */
public class CbLogRecord
    extends LogRecord
{
    /** serialVersionUID */
    private static final long serialVersionUID = 4826019846617074270L;



    /**
     * Construct a LogRecord with the given level and message values.
     * <p>The sequence property will be initialized with a new unique value.
     * These sequence values are allocated in increasing order within a VM.
     * <p>The millis property will be initialized to the current time.
     * <p>The thread ID property will be initialized with a unique ID for
     * the current thread.
     * <p>All other properties will be initialized to "null". 
     * 
     * @param pLevel a logging level value
     * @param pMsg the raw non-localized logging message (may be null)
     */
    public CbLogRecord(final Level pLevel, final String pMsg)
    {
        super(pLevel, pMsg);
    }
}
