/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 29.12.2010
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
 * Describes a civilization card in an active game. This includes dynamic information
 * based on haves and plans.
 *
 * @author tsjensen
 */
public class CcCardCurrent
{
    /** If the card is already present, planned, or just absent */
    private CcState iState = CcState.Absent;

    /** If the card state is 'planned', this flag may be set to indicate that the
     *  card was previously a discouraged buy. This is not needed for times when
     *  the card is removed from the plan, but used as an indication to the player. */
    private boolean iDiscouraged = false;

    /** the current cost of the card (nominal - current credit) */
    private int iCostCurrent;

    /** the card as defined in the config file */
    private CcCardConfig iCardConfig;

    /* ---------- technical values ------------ */

    /** The array that this object is part of */
    private CcCardCurrent[] iAllCardsCurrent = null;

    /** The index that this object has in {@link #iAllCardsCurrent} */
    private int iMyIdx = -1;



    /**
     * Constructor.
     * @param pAllCardsCurrent the array that this object is part of
     * @param pCardConfig the configured information on this card
     */
    public CcCardCurrent(final CcCardCurrent[] pAllCardsCurrent,
        final CcCardConfig pCardConfig)
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
    public CcState getState()
    {
        return iState;
    }

    /**
     * Setter.
     * @param pState the new value of {@link #iState}
     */
    public void setState(final CcState pState)
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
        iCostCurrent = pCostCurrent;
    }



    /**
     * Getter.
     * @return {@link #iCardConfig}
     */
    public CcCardConfig getConfig()
    {
        return iCardConfig;
    }



    /**
     * Getter.
     * @return {@link #iAllCardsCurrent}
     */
    public CcCardCurrent[] getAllCardsCurrent()
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



    /**
     * Getter.
     * @return {@link #iDiscouraged}
     */
    public boolean isDiscouraged()
    {
        return iDiscouraged;
    }

    /**
     * Setter.
     * @param pDiscouraged the new value of {@link #iDiscouraged}
     */
    public void setDiscouraged(final boolean pDiscouraged)
    {
        iDiscouraged = pDiscouraged;
    }
}
