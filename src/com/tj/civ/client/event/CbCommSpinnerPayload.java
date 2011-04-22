/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 23.01.2011
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
package com.tj.civ.client.event;


/**
 * Payload of the {@link com.google.gwt.event.logical.shared.ValueChangeEvent}s
 * fired by {@link com.tj.civ.client.widgets.CbCommoditySpinner}s.
 *
 * @author Thomas Jensen
 */
public class CbCommSpinnerPayload
{
    /** the index of the changed commodity */
    private int iCommIdx;

    /** change in the number of commodity cards of the spinner's type */
    private int iDeltaNumber;

    /** change in the card points of this commodity */
    private int iDeltaPoints;



    /**
     * Constructor.
     * @param pCommIdx the index of the changed commodity
     * @param pDeltaNumber change in the number of commodity cards
     * @param pDeltaPoints change in the card points of this commodity
     */
    public CbCommSpinnerPayload(final int pCommIdx, final int pDeltaNumber, final int pDeltaPoints)
    {
        iCommIdx = pCommIdx;
        iDeltaNumber = pDeltaNumber;
        iDeltaPoints = pDeltaPoints;
    }



    public int getCommIdx()
    {
        return iCommIdx;
    }



    public int getDeltaNumber()
    {
        return iDeltaNumber;
    }



    public int getDeltaPoints()
    {
        return iDeltaPoints;
    }



    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CbCommSpinnerPayload{iCommIdx="); //$NON-NLS-1$
        sb.append(iCommIdx);
        sb.append(", iDeltaNumber="); //$NON-NLS-1$
        sb.append(iDeltaNumber);
        sb.append(", iDeltaPoints="); //$NON-NLS-1$
        sb.append(iDeltaPoints);
        sb.append("}"); //$NON-NLS-1$
        return sb.toString();
    }
}
