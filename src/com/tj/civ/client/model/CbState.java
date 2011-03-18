/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-25
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
 * States that a card can be in.
 * 
 * <p>The following diagram illustrates the relationship between states, provided
 * an unchanged resource situation (treasury and commodities) in planning mode:
 *
 * <p><img src="doc-files/CardStates.png" alt="Card State Diagram"
 *    style="margin-left:2em;" />
 *
 * <p>Note that all states within the highlighted region on the left side count
 * as 'Absent' when it comes to credit bar calculation.
 * Therefore, 'Absent', 'Planned', and 'Owned' are special states.<br>
 * The transition between 'PrereqFailed' and 'DiscouragedBuy' is dashed, because
 * the game variants that the author knows do not include it. To make this
 * transition relevant, one of the cheap cards would have to have a prerequisite
 * card. However, this might happen in theory, so this app covers the possibility.
 *
 * <p>'Owned' is the final state that cards cannot leave. They reach this state only
 * by committing the cards flagged as planned.
 *
 * @author Thomas Jensen
 */
public enum CcState
{
    /** The player currently owns this card */
    Owned('X', true),

    /** The player plans to buy this card */
    Planned('p', true),

    /** The player does not own or plan to buy this card.
     *  <p>CAUTION: The states 'Owned' and 'Planned' must be declared before this
     *  one, all others afterwards! */
    Absent('_', false),

    /** Buying this card is impossible due to insufficient resources */ 
    Unaffordable('_', false),
    
    /** Buying this card is impossible because its prerequisites aren't met */
    PrereqFailed('_', false),

    /** Buying this card is discouraged, because it would make it impossible to
     *  reach the total winning points by civilization cards. The original game
     *  variant includes a limit on the number of civilization cards a player can
     *  buy. */
    DiscouragedBuy('_', false);



    /** If this flag is cleared, the credit bar will show 'absent' */
    private boolean iAffectsCredit;

    /** key used to persist the state. All states below 'absent' will be set to
     *  'absent' after unmarshaling */
    private char iKey;



    private CcState(final char pKey, final boolean pAffectsCredit)
    {
        iKey = pKey;
        iAffectsCredit = pAffectsCredit;
    }



    /**
     * Getter.
     * @return {@link #iAffectsCredit}
     */
    public boolean isAffectingCredit()
    {
        return iAffectsCredit;
    }



    public char getKey()
    {
        return iKey;
    }



    /**
     * Convert a primitive key into an instance of this enum.
     * @param pKey the key char
     * @return an enum instance, or <code>null</code> of the key is invalid
     */
    public static CcState fromKey(final char pKey)
    {
        CcState result = null;
        for (CcState grp : CcState.values()) {
            if (grp.getKey() == pKey) {
                result = grp;
                break;
            }
        }
        return result;
    }



    /**
     * Create a state array of the given size, where each state is initialized
     * to {@link #Absent}.
     * @param pNumCards number of elements in the array
     * @return a new array of 'Absent' states
     */
    public static CcState[] createInitialStateArray(final int pNumCards)
    {
        CcState[] result = new CcState[pNumCards];
        for (int i = 0; i < result.length; i++) {
            result[i] = CcState.Absent;
        }
        return result;
    }
}
