/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-04-13
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

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * Formatter for log messages in this application.
 *
 * @author Thomas Jensen
 */
class CbLogFormatter
    extends Formatter
{
    /** width of the level display */
    private static final int LEVEL_WIDTH = Level.WARNING.getName().length();

    /** 10 spaces (at least 1 more than {@link #LEVEL_WIDTH}) */
    private static final String SPACES = "          "; //$NON-NLS-1$

    /** internal prefix for enter messages */
    public static final String PREFIX_ENTER = "#ENTER#: "; //$NON-NLS-1$

    /** internal prefix for exit messages */
    public static final String PREFIX_EXIT = "#EXIT#: "; //$NON-NLS-1$

    /** internal prefix for touch messages */
    public static final String PREFIX_TOUCH = "#TOUCH#: "; //$NON-NLS-1$



    /**
     * Constructor.
     */
    CbLogFormatter()
    {
        super();
    }



    /**
     * Format the given log record and return the formatted string.
     * <p>The formatted message will look like this:<br>
     * <tt>2011-04-21 22:50:34.407 ENTER  |civ.client.CbCardStateManager -
     *     isDiscouraged(pRowIdx=9): enter</tt><br>
     * where the pipe symbol in front of the class name indicates that the log
     * record was created by our own code. If the pipe symbol is absent, the log
     * message was created by some GWT of third party component.
     * 
     * @param pRecord the log record to be formatted.
     * @return the formatted log record
     */
    @Override
    public String format(final LogRecord pRecord)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(CbConstants.TIMESTAMP_FORMAT.format(new Date(pRecord.getMillis())));
        sb.append(' ');
        String lvl = pRecord.getLevel().getName();
        String msg = pRecord.getMessage();
        if (msg != null) {
            if (msg.startsWith(PREFIX_ENTER)) {
                lvl = "ENTER"; //$NON-NLS-1$
                msg = msg.substring(PREFIX_ENTER.length());
            }
            else if (msg.startsWith(PREFIX_EXIT)) {
                lvl = "EXIT"; //$NON-NLS-1$
                msg = msg.substring(PREFIX_EXIT.length());
            }
            else if (msg.startsWith(PREFIX_TOUCH)) {
                lvl = "TOUCH"; //$NON-NLS-1$
                msg = msg.substring(PREFIX_TOUCH.length());
            }
        }

        // add the level
        sb.append(lvl);
        if (LEVEL_WIDTH > lvl.length()) {
            sb.append(SPACES.substring(0, Math.max(1, LEVEL_WIDTH - lvl.length())));
        }

        // add a pipe if it's one of our own loggers
        if (pRecord instanceof CbLogRecord) {
            sb.append('|');
        } else {
            sb.append(' ');
        }

        // add the message
        sb.append(msg);

        // add the stack trace of an exception, if one was provided
        if (pRecord.getThrown() != null) {
            sb.append('\n');
            StackTraceElement[] trace = pRecord.getThrown().getStackTrace();
            if (trace != null) {
                for (int i = 0; i < trace.length; i++) {
                    sb.append("    "); //$NON-NLS-1$
                    sb.append(trace[i].toString());
                    if (i < trace.length - 1) {
                        sb.append('\n');
                    }
                }
            }
        }
        
        return sb.toString();
    }
}
