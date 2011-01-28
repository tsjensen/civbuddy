/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-05
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
 * Describes the current situation of a single player.
 *
 * @author Thomas Jensen
 */
public class CcSituation
{
    /** the player whose situation this is */
    private CcPlayer iPlayer;

    /** reference to the game that this situation is part of */
    private CcGame iGame;

    /** The current civilization card situation. The order of cards must be the same
     *  as in the variant config */
    private CcCardCurrent[] iCardsCurrent = null;

    /** Total funds available to the player at the moment, according to the
     *  'Commodities' panel */
    private int iFunds = 0;

    /** Funds remaining (equals total funds minus current costs of planned cards) */ 
    private int iFundsPlanned = 0;

    /** current commodity counts. The order of cards must be the same
     *  as in the variant config */
    private int[] iCommoditiesCurrent = null;

    /** number of groups of which the player owns at least one card */
    private int iNumGroups = 0;

    /** {@link #iNumGroups}, but including planned cards */
    private int iNumGroupsPlanned = 0;

    /** current number of winning points from civilization cards */ 
    private int iWinningPoints = 0;

    /** {@link #iWinningPoints}, but including planned cards */
    private int iWinningPointsPlanned = 0;

    /** number of owned cards */
    private int iNumCards = 0;

    /** {@link #iNumCards}, but including planned cards */
    private int iNumCardsPlanned = 0;



    /**
     * Constructor.
     * @param pPlayer the player whose situation this is
     */
    public CcSituation(final CcPlayer pPlayer)
    {
        iPlayer = pPlayer;
    }



    /**
     * Getter.
     * @return {@link #iCardsCurrent}
     */
    public CcCardCurrent[] getCardsCurrent()
    {
        return iCardsCurrent;
    }

    /**
     * Setter.
     * @param pCardsCurrent the new value of {@link #iCardsCurrent}
     */
    public void setCardsCurrent(final CcCardCurrent[] pCardsCurrent)
    {
        iCardsCurrent = pCardsCurrent;
    }



    /**
     * Getter.
     * @return {@link #iFunds}
     */
    public int getFunds()
    {
        return iFunds;
    }



    /**
     * Setter.
     * @param pFunds the new value of {@link #iFunds}
     */
    public void setFunds(final int pFunds)
    {
        iFunds = pFunds;
    }



    /**
     * Getter.
     * @return {@link #iFundsPlanned}
     */
    public int getFundsPlanned()
    {
        return iFundsPlanned;
    }



    /**
     * Setter.
     * @param pFundsPlanned the new value of {@link #iFundsPlanned}
     */
    public void setFundsPlanned(final int pFundsPlanned)
    {
        iFundsPlanned = pFundsPlanned;
    }



    /**
     * Getter.
     * @return {@link #iNumGroups}
     */
    public int getNumGroups()
    {
        return iNumGroups;
    }



    /**
     * Setter.
     * @param pNumGroups the new value of {@link #iNumGroups}
     */
    public void setNumGroups(final int pNumGroups)
    {
        iNumGroups = pNumGroups;
    }



    /**
     * Getter.
     * @return {@link #iNumGroupsPlanned}
     */
    public int getNumGroupsPlanned()
    {
        return iNumGroupsPlanned;
    }



    /**
     * Setter.
     * @param pNumGroupsPlanned the new value of {@link #iNumGroupsPlanned}
     */
    public void setNumGroupsPlanned(final int pNumGroupsPlanned)
    {
        iNumGroupsPlanned = pNumGroupsPlanned;
    }



    /**
     * Getter.
     * @return {@link #iWinningPoints}
     */
    public int getWinningPoints()
    {
        return iWinningPoints;
    }



    /**
     * Setter.
     * @param pWinningPoints the new value of {@link #iWinningPoints}
     */
    public void setWinningPoints(final int pWinningPoints)
    {
        iWinningPoints = pWinningPoints;
    }



    /**
     * Getter.
     * @return {@link #iWinningPointsPlanned}
     */
    public int getWinningPointsPlanned()
    {
        return iWinningPointsPlanned;
    }



    /**
     * Setter.
     * @param pWinningPointsPlanned the new value of {@link #iWinningPointsPlanned}
     */
    public void setWinningPointsPlanned(final int pWinningPointsPlanned)
    {
        iWinningPointsPlanned = pWinningPointsPlanned;
    }



    /**
     * Getter.
     * @return {@link #iNumCards}
     */
    public int getNumCards()
    {
        return iNumCards;
    }



    /**
     * Setter.
     * @param pNumCards the new value of {@link #iNumCards}
     */
    public void setNumCards(final int pNumCards)
    {
        iNumCards = pNumCards;
    }



    /**
     * Getter.
     * @return {@link #iNumCardsPlanned}
     */
    public int getNumCardsPlanned()
    {
        return iNumCardsPlanned;
    }



    /**
     * Setter.
     * @param pNumCardsPlanned the new value of {@link #iNumCardsPlanned}
     */
    public void setNumCardsPlanned(final int pNumCardsPlanned)
    {
        iNumCardsPlanned = pNumCardsPlanned;
    }



    /**
     * Getter.
     * @return {@link #iGame}
     */
    public CcGame getGame()
    {
        return iGame;
    }

    /**
     * Setter.
     * @param pGame the new value of {@link #iGame}
     */
    public void setGame(final CcGame pGame)
    {
        iGame = pGame;
    }



    /**
     * Getter.
     * @return {@link #iCommoditiesCurrent}
     */
    public int[] getCommoditiesCurrent()
    {
        return iCommoditiesCurrent;
    }

    /**
     * Setter.
     * @param pCommoditiesCurrent the new value of {@link #iCommoditiesCurrent}
     */
    public void setCommoditiesCurrent(final int[] pCommoditiesCurrent)
    {
        iCommoditiesCurrent = pCommoditiesCurrent;
    }



    /**
     * Getter.
     * @return {@link #iPlayer}
     */
    public CcPlayer getPlayer()
    {
        return iPlayer;
    }
}
