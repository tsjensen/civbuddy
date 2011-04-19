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
 * {@link java.util.logging} based GWT-compatible log adapter for this application.
 *
 * @author Thomas Jensen
 */
public final class CbLogAdapter
{
    /** log message formatter */
    private static final Formatter FORMATTER = new CbLogFormatter();

    /** method name signifying a constructor */
    public static final String CONSTRUCTOR = "<init>"; //$NON-NLS-1$

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



    private boolean isLoggable(final Level pLevel)
    {
        return LogConfiguration.loggingIsEnabled() && iLogger.isLoggable(pLevel);
    }



    public boolean isErrorEnabled()
    {
        return isLoggable(Level.SEVERE);
    }

    public boolean isWarnEnabled()
    {
        return isLoggable(Level.WARNING);
    }

    public boolean isInfoEnabled()
    {
        return isLoggable(Level.INFO);
    }

    public boolean isDebugEnabled()
    {
        return isLoggable(Level.FINE);
    }

    public boolean isDetailEnabled()
    {
        return isLoggable(Level.FINER);
    }

    public boolean isTraceEnabled()
    {
        return isLoggable(Level.FINEST);
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



    /**
     * Central log method which performs the logging.
     * @param pLevel log level of the message
     * @param pMethodName the method name (optional)
     * @param pTechPrefix either {@link CbLogFormatter#PREFIX_ENTER} or
     *          {@link CbLogFormatter#PREFIX_EXIT} or <code>null</code>. This helps
     *          {@link CbLogFormatter} detect enter and exit messages, for which
     *          there is no {@link Level}. (optional)
     * @param pMessage the message to log (optional)
     * @param pException an exception to log (optional)
     */
    private void log(final Level pLevel, final String pMethodName,
        final String pTechPrefix, final String pMessage, final Throwable pException)
    {
        String meth = pMethodName;
        if (meth != null && !meth.endsWith(BRACES) && !CONSTRUCTOR.equals(meth)) {
            meth += BRACES;
        }
        if (meth != null) {
            meth = " - " + meth; //$NON-NLS-1$
        } else {
            meth = ""; //$NON-NLS-1$
        }
        
        String prefix = pTechPrefix;
        if (pTechPrefix == null) {
            prefix = ""; //$NON-NLS-1$
        }
        
        CbLogRecord record = new CbLogRecord(pLevel,
            prefix + iClippedName + meth + ": " + pMessage); //$NON-NLS-1$
        record.setThrown(pException);
        iLogger.log(record);
    }



    /**
     * Logs a TRACE level message upon entering a method.
     *
     * @param pMethodName method name
     */
    public void enter(final String pMethodName)
    {
        if (isTraceEnabled()) {
            log(Level.FINEST, pMethodName, CbLogFormatter.PREFIX_ENTER,
                "enter", null); //$NON-NLS-1$
        }
    }



    /**
     * Logs a TRACE level message upon entering a method.
     *
     * @param pMethodName method name
     * @param pArgNames array of method argument names, of length equal to
     *          <tt>pArgValues</tt> (may be <code>null</code> )
     * @param pArgValues array of method argument values, of length equal to
     *          <tt>pArgNames</tt> (may be <code>null</code>)
     */
    public void enter(final String pMethodName, final String[] pArgNames,
        final Object[] pArgValues)
    {
        if (!isTraceEnabled()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(pMethodName);
        if (pMethodName != null && pMethodName.endsWith(BRACES)) {
            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append('(');
        if (pArgValues != null && pArgValues.length > 0)
        {
            for (int i = 0; i < pArgValues.length; i++)
            {
                if (pArgNames != null && pArgNames.length == pArgValues.length)
                {
                    sb.append(pArgNames[i]);
                    sb.append('=');
                }
                CbToString.obj2str(sb, pArgValues[i]);
                if (i < (pArgValues.length - 1))
                {
                    sb.append(", "); //$NON-NLS-1$
                }
            }
        }
        sb.append(')');
        sb.append(": enter"); //$NON-NLS-1$

        CbLogRecord record = new CbLogRecord(Level.FINEST, CbLogFormatter.PREFIX_ENTER
            + iClippedName + " - " + sb.toString()); //$NON-NLS-1$
        iLogger.log(record);
    }



    /**
     * Logs a TRACE level message upon exiting a method.
     * 
     * @param pMethodName the method name
     */
    public void exit(final String pMethodName)
    {
        if (isTraceEnabled()) {
            log(Level.FINEST, pMethodName, CbLogFormatter.PREFIX_EXIT,
                "exit", null); //$NON-NLS-1$
        }
    }



    /**
     * Logs a TRACE level message upon exiting a method.
     * 
     * @param pMethodName the method name
     * @param pResult the method's result value
     */
    public void exit(final String pMethodName, final Object pResult)
    {
        if (isTraceEnabled()) {
            log(Level.FINEST, pMethodName, CbLogFormatter.PREFIX_EXIT,
                "exit - result=" + CbToString.obj2str(pResult), null); //$NON-NLS-1$
        }
    }



    /**
     * Logs a TRACE level message indicating that the method was processed (both
     * enter and exit at the same time).
     *
     * @param pMethodName method name
     */
    public void touch(final String pMethodName)
    {
        if (isTraceEnabled()) {
            log(Level.FINEST, pMethodName, CbLogFormatter.PREFIX_TOUCH,
                "touch", null); //$NON-NLS-1$
        }
    }



    /**
     * Logs a DEBUG level message.
     * @param pMethodName the method name
     * @param pMessage the message
     */
    public void debug(final String pMethodName, final String pMessage)
    {
        if (isDebugEnabled()) {
            log(Level.FINE, pMethodName, null, pMessage, null);
        }
    }



    /**
     * Logs a DETAIL level message.
     * @param pMethodName the method name
     * @param pMessage the message
     */
    public void detail(final String pMethodName, final String pMessage)
    {
        if (isDetailEnabled()) {
            log(Level.FINER, pMethodName, null, pMessage, null);
        }
    }



    /**
     * Logs a INFO level message.
     * @param pMessage the message
     */
    public void info(final String pMessage)
    {
        if (isInfoEnabled()) {
            log(Level.INFO, null, null, pMessage, null);
        }
    }



    /**
     * Logs a WARNING level message.
     * @param pMethodName the method name
     * @param pMessage the message
     */
    public void warn(final String pMethodName, final String pMessage)
    {
        if (isWarnEnabled()) {
            log(Level.WARNING, pMethodName, null, pMessage, null);
        }
    }



    /**
     * Logs a WARNING level message.
     * @param pMethodName the method name
     * @param pMessage the message
     * @param pException an exception to be included in the message
     */
    public void warn(final String pMethodName, final String pMessage, final Throwable pException)
    {
        if (isWarnEnabled()) {
            log(Level.WARNING, pMethodName, null, pMessage, pException);
        }
    }



    /**
     * Logs a WARNING level message.
     * @param pMessage the message
     */
    public void error(final String pMessage)
    {
        if (isErrorEnabled()) {
            log(Level.SEVERE, null, null, pMessage, null);
        }
    }



    /**
     * Logs a ERROR level message.
     * @param pMessage the message
     * @param pException an exception to be included in the message
     */
    public void error(final String pMessage, final Throwable pException)
    {
        if (isErrorEnabled()) {
            log(Level.SEVERE, null, null, pMessage, pException);
        }
    }
}
