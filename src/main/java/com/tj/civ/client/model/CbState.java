/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 2010-12-25
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.model;


/**
 * States that a card can be in.
 * 
 * <p>The states 'Owned' and 'Planned' are the major states, and the only ones
 * that can be explicitly set by the user and the only ones that are persisted.
 * All other states (the ones inside the shaded box in the diagrams below) are
 * detailings of the 'Absent' state, calculated at run time.
 * 
 * <h3>State Transitions</h3>
 * 
 * The state of a card can change explicitly (because the user clicked on the card),
 * or it can change implicitly (because another card's state was changed).
 * 
 * <p>The action mode determines which state transitions are possible.
 * 
 * <p>In <b>planning mode</b>, a click on any card could trigger the following
 * changes:
 *
 * <p><img src="doc-files/CardStates-Plan.png" alt="Card State Diagram - Planning Mode"
 *    style="margin-left:3em;" />
 *
 * <p>The red dashed arrows indicate transitions which would lead to a warning
 * message being displayed (if transitioning to 'Planned'). If the warning is
 * ignored, the state transition is performed.<br>
 * The transition between 'PrereqFailed' and 'DiscouragedBuy' is dashed, because
 * the game variants that the author knows do not include it. To make this
 * transition relevant, one of the cheap cards would have to have a prerequisite
 * card. However, this might happen in theory, so this app covers the possibility.<br>
 * 'Owned' is the final state that cards cannot leave. They reach this state only
 * by committing the cards flagged as planned.
 * 
 * <p>In <b>revise mode</b>, a click on any card could trigger the following changes:
 *
 * <p><img src="doc-files/CardStates-Revise.png" alt="Card State Diagram - Revise Mode"
 *    style="margin-left:3em;" />
 *
 * <p>Only the states 'Absent' and 'Owned' exist, and a user click toggles between
 * the two.
 * 
 * <h3>Mode Transitions</h3>
 * 
 * The above already made it clear that certain actions must be performed when the
 * action mode changes.
 * 
 * <p><dl>
 * <dt>Planning Mode -&gt; Revise Mode</dt>
 * <dd>Clear all plans, then set all cards not in state 'Owned' to state 'Absent'.</dd>
 * 
 * <dt>Revise Mode -&gt; Planning Mode</dt>
 * <dd>Recalculate the detail states of all cards in state 'Absent'.</dd>
 * 
 * <dt>Return from 'Funds' view -&gt 'Cards' view</dt>
 * <dd>Recalculate the detail states of all cards not in states 'Owned' or
 *     'Planned'.</dd>
 * </dl>
 *
 * @author Thomas Jensen
 */
public enum CbState
{
    /** The player currently owns this card */
    Owned('X', true),

    /** The player plans to buy this card */
    Planned('p', true),

    /** The player does not own or plan to buy this card.
     *  <p>CAUTION: The states 'Owned' and 'Planned' must be declared before this
     *  one, all others afterwards! */
    Absent('_', false),

    /** Buying this card should not be possible due to insufficient resources */ 
    Unaffordable('_', false),
    
    /** Buying this card is impossible because its prerequisites aren't met */
    PrereqFailed('_', false),

    /** Buying this card is discouraged, because it would make it impossible to
     *  reach the total winning points by civilization cards. The original game
     *  variant includes a limit on the number of civilization cards a player can
     *  buy. */
    DiscouragedBuy('_', false);



    /** If this flag is cleared, the credit bar will show 'Absent' */
    private boolean iAffectsCredit;

    /** key used to persist the state. All states below 'Absent' will be set to
     *  'Absent' after unmarshaling */
    private char iKey;



    private CbState(final char pKey, final boolean pAffectsCredit)
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
    public static CbState fromKey(final char pKey)
    {
        CbState result = null;
        for (CbState grp : CbState.values()) {
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
    public static CbState[] createInitialStateArray(final int pNumCards)
    {
        CbState[] result = new CbState[pNumCards];
        for (int i = 0; i < result.length; i++) {
            result[i] = CbState.Absent;
        }
        return result;
    }
}
