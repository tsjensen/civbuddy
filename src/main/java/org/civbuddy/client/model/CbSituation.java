/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-01-05
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.civbuddy.client.CbCardStateManager;
import org.civbuddy.client.common.CbLogAdapter;
import org.civbuddy.client.common.CbToString;
import org.civbuddy.client.model.jso.CbFundsJSO;
import org.civbuddy.client.model.jso.CbPlayerJSO;
import org.civbuddy.client.model.jso.CbSituationJSO;


/**
 * Describes the current situation of a single player.
 *
 * @author Thomas Jensen
 */
public class CbSituation
    extends CbIndependentlyPersistableObject<CbSituationJSO>
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbSituation.class);

    /** reference to the game variant that this situation is based on */
    private CbVariantConfig iVariant;

    /** The current civilization card situation. The order of cards must be the same
     *  as in the variant config */
    private CbCardCurrent[] iCardsCurrent;

    /** the game to which this situation belongs */
    private CbGame iGame;

    /** Funds remaining (equals total funds minus current costs of planned cards) */ 
    private int iFundsPlanned = 0;

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

    /** Desperation Mode: Activated once a discouraged card is planned or bought.
     *  @see CbCardStateManager */
    private boolean iIsDesperate = false;

    /** list of all possible mining yields. This list contains the bonus granted by the 'Mining' card of the
     *  <i>Advanced Civilization</i> game variant for every commodity of which at least one and at most max-1
     *  are owned. */
    private List<Integer> iMiningYields = new ArrayList<Integer>();



    /**
     * Constructor.
     * @param pSituationJso the JSO wrapped by this class
     * @param pVariant the game variant that this situation is based on
     */
    public CbSituation(final CbSituationJSO pSituationJso, final CbVariantConfig pVariant)
    {
        super(pSituationJso);
        iVariant = pVariant;
    }



    /**
     * Getter.
     * @return {@link #iCardsCurrent}
     */
    public CbCardCurrent[] getCardsCurrent()
    {
        return iCardsCurrent;
    }

    /**
     * Setter.
     * @param pCardsCurrent the new value of {@link #iCardsCurrent}
     */
    public void setCardsCurrent(final CbCardCurrent[] pCardsCurrent)
    {
        iCardsCurrent = pCardsCurrent;
    }



    /**
     * Sets the current state of a card.
     * @param pCardIdx index of the card in {@link #iCardsCurrent}
     * @param pState the new state
     * @param pPointsDelta if <tt>pState</tt> is {@link CbState#DiscouragedBuy},
     *           this value indicates by how much we'd miss the target. This is
     *           mandatory for 'DiscouragedBuy', and should be 0 (zero) for any
     *           other state
     */
    public void setCardState(final int pCardIdx, final CbState pState,
        final int pPointsDelta)
    {
        iCardsCurrent[pCardIdx].setState(pState);
        getJso().setState(pCardIdx, pState);
        if (pState == CbState.DiscouragedBuy) {
            iCardsCurrent[pCardIdx].setPointsDelta(pPointsDelta);
        }
    }



    /**
     * Getter.
     * @return total funds available (from funds JSO)
     */
    public int getFunds()
    {
        return getJso().getFunds().getTotalFunds();
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
     * @return current commodity counts
     * @see CbFundsJSO#getCommodityCounts()
     */
    public int[] getCommoditiesCurrent()
    {
        return getJso().getFunds().getCommodityCounts();
    }

    /**
     * Setter. 
     * @param pIdx commodity index
     * @param pCount the current count for the given commodity
     */
    public void setCommodityCurrent(final int pIdx, final int pCount)
    {
        getJso().getFunds().setCommodityCount(pIdx, pCount);
    }



    public CbPlayerJSO getPlayer()
    {
        return getJso().getPlayer();
    }



    @Override
    public void evaluateJsoState(final CbSituationJSO pJso)
    {
        if (iVariant != null) {
            final CbState[] states = pJso.getStates();
            CbCardConfig[] config = iVariant.getCards();
            iCardsCurrent = new CbCardCurrent[config.length];
            for (int i = 0; i < config.length; i++)
            {
                iCardsCurrent[i] = new CbCardCurrent(iCardsCurrent, config[i]);
                iCardsCurrent[i].setState(states[i]);
            }
        }
    }



    public CbVariantConfig getVariant()
    {
        return iVariant;
    }



    public CbGame getGame()
    {
        return iGame;
    }

    public void setGame(final CbGame pGame)
    {
        iGame = pGame;
    }



    public boolean isDesperate()
    {
        return iIsDesperate;
    }

    public void setDesperate(final boolean pIsDesperate)
    {
        iIsDesperate = pIsDesperate;
    }



    public List<Integer> getMiningYields()
    {
        return iMiningYields;
    }

    /**
     * Setter.
     * @param pMiningYields the new list of potential mining yields
     */
    public void setMiningYields(final List<Integer> pMiningYields)
    {
        iMiningYields.clear();
        if (pMiningYields != null) {
            iMiningYields.addAll(pMiningYields);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("setMiningYields", //$NON-NLS-1$
                "new mining yields: " + CbToString.obj2str(pMiningYields)); //$NON-NLS-1$
        }
    }

    /**
     * Get the hightest value contained in {@link #iMiningYields}, which is the most that the player can get out of
     * mining at this time.
     * @return the highest possible mining yield
     */
    public int getHighestMiningYield()
    {
        int result = 0;
        if (iMiningYields.size() > 0) {
            result = Collections.max(iMiningYields).intValue();
        }
        return result;
    }
}
