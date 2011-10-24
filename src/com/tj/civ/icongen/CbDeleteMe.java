/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: Oct 21, 2011
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
package com.tj.civ.icongen;


/**
 * Temporary test class.
 *
 * @author Thomas Jensen
 */
public final class CbDeleteMe
{

    private static void sineTest()
    {
        final int steps = 11;
        final int degs = 180;

        for (int i = 0; i < steps; i++) {
            System.out.print(i + ", "); //$NON-NLS-1$
        }
        System.out.println();
        for (int i = 0; i < steps; i++) {
            double s = Math.sin(Math.toRadians(degs * i / (steps - 1)));
            System.out.print(String.format("%5.3f", Double.valueOf(s)) //$NON-NLS-1$
                + ", "); //$NON-NLS-1$
        }
        System.out.println();
        for (int i = 0; i < steps; i++) {
            double s = Math.sin(Math.toRadians(degs * i / (steps - 1)));
            System.out.print(String.format("%5.3f", Double.valueOf(s * steps)) //$NON-NLS-1$
                + ", "); //$NON-NLS-1$
        }
        System.out.println();
        for (int i = 0; i < steps; i++) {
            double s = Math.sin(Math.toRadians(degs * i / (steps - 1)));
            System.out.print(((int) Math.round(steps * s)) + ", "); //$NON-NLS-1$
        }
    }



    /**
     * Main.
     * @param pArgs args
     */
    public static void main(final String[] pArgs)
    {
        sineTest();
    }



    private CbDeleteMe()
    {
        super();
    }
}
