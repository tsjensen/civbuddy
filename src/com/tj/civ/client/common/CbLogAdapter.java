/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-04-12
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

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;


/**
 * java.util.logging based GWT-compatible log adapter for this application.
 *
 * @author Thomas Jensen
 */
public final class CbLogAdapter
{
    /** log message formatter */
    private static final Formatter FORMATTER = new CbLogFormatter();

    /** number of class name fragments to preserve in log messages */
    private static final int CLIP = 3;

    /** a pair of parentheses (<tt>"()"</tt>) */
    private static final String BRACES = "()"; //$NON-NLS-1$

    /** last three fragments of the class name */
    private String iClippedName;

    /** the logger we are wrapping */
    private Logger iLogger;



    private CbLogAdapter(final Logger pLogger)
    {
        super();
        iLogger = pLogger;
        iClippedName = clipClassName(pLogger.getName());
    }



    /**
     * Factory method.
     * <p>All handlers of the wrapped {@link Logger}, including the parent handlers
     * and the handlers of the root logger, are assigned a {@link CbLogFormatter}
     * to ensure a consistent message format.
     * @param pClazz class which the logger is associated to
     * @return a log adapter instance for the class
     */
    public static CbLogAdapter getLogger(final Class<?> pClazz)
    {
        CbLogAdapter result = null;
        if (pClazz != null) {
            result = new CbLogAdapter(Logger.getLogger(pClazz.getName()));
        }
        result.iLogger.setUseParentHandlers(true);
        for (Logger logger = result.iLogger; logger != null; logger = logger.getParent())
        {
            for (Handler handler : logger.getHandlers()) {
                handler.setFormatter(FORMATTER);
            }
        }
        return result;
    }



    private boolean isLogging(final Level pLevel)
    {
        return LogConfiguration.loggingIsEnabled() && iLogger.isLoggable(pLevel);
    }



    private static String clipClassName(final String pClassName)
    {
        String result = "null"; //$NON-NLS-1$
        if (pClassName != null) {
            result = pClassName;
            int p = result.lastIndexOf('.');
            for (int n = 1; p > 0 && n < CLIP; n++) {
                p = result.lastIndexOf('.', p - 1);
            }
            if (p > 0) {
                result = result.substring(p + 1);
            }
        }
        return result;
    }



    public void enter(final String pMethodName)
    {
        if (isLogging(Level.FINEST)) {
            String meth = pMethodName;
            if (meth != null && !meth.endsWith(BRACES)) {
                meth += BRACES;
            }
            iLogger.log(Level.FINEST, CbLogFormatter.PREFIX_ENTER + iClippedName
                + " - " + meth + " - enter"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
