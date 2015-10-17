/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-29
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.civbuddy.client.model;


/**
 * Describes a civilization card in an active game. This includes dynamic information
 * based on haves and plans.
 *
 * @author tsjensen
 */
public class CbCardCurrent
{
    /** If the card is already present, planned, or just absent */
    private CbState iState = CbState.Absent;

    /** If and only if the card state is {@link CbState#DiscouragedBuy}, this value
     *  indicates by how many points the target would be missed. If the card state is
     *  something other than 'DiscouragedBuy', this value is undefined. */
    private int iPointsDelta = 0;

    /** the current cost of the card (nominal - current credit) */
    private int iCostCurrent;

    /** the card as defined in the config file */
    private CbCardConfig iCardConfig;

    /* ---------- technical values ------------ */

    /** The array that this object is part of */
    private CbCardCurrent[] iAllCardsCurrent = null;

    /** The index that this object has in {@link #iAllCardsCurrent} */
    private int iMyIdx = -1;



    /**
     * Constructor.
     * @param pAllCardsCurrent the array that this object is part of
     * @param pCardConfig the configured information on this card
     */
    public CbCardCurrent(final CbCardCurrent[] pAllCardsCurrent, final CbCardConfig pCardConfig)
    {
        iCardConfig = pCardConfig;
        iCostCurrent = pCardConfig.getCostNominal();
        iAllCardsCurrent = pAllCardsCurrent;
        iMyIdx = pCardConfig.getMyIdx();
    }



    /**
     * Getter.
     * @return {@link #iState}
     */
    public CbState getState()
    {
        return iState;
    }

    /**
     * Setter.
     * @param pState the new value of {@link #iState}
     */
    void setState(final CbState pState)
    {
        iState = pState;
    }



    /**
     * Getter.
     * @return {@link #iCostCurrent}
     */
    public int getCostCurrent()
    {
        return iCostCurrent;
    }

    /**
     * Setter.
     * @param pCostCurrent the new value of {@link #iCostCurrent}
     */
    public void setCostCurrent(final int pCostCurrent)
    {
        iCostCurrent = Math.max(0, pCostCurrent);
    }



    /**
     * Getter.
     * @return {@link #iCardConfig}
     */
    public CbCardConfig getConfig()
    {
        return iCardConfig;
    }



    /**
     * Getter.
     * @return {@link #iAllCardsCurrent}
     */
    public CbCardCurrent[] getAllCardsCurrent()
    {
        return iAllCardsCurrent;
    }



    /**
     * Getter.
     * @return {@link #iMyIdx}
     */
    public int getMyIdx()
    {
        return iMyIdx;
    }



    public int getPointsDelta()
    {
        return iPointsDelta;
    }

    public void setPointsDelta(final int pPointsDelta)
    {
        iPointsDelta = pPointsDelta;
    }
}
