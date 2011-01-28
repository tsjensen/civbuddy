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
    Owned(true),

    /** The player plans to buy this card */
    Planned(true),

    /** The player does not own or plan to buy this card.
     *  <p>CAUTION: The states 'Owned' and 'Planned' must be declared before this
     *  one, all others afterwards! */
    Absent(false),

    /** Buying this card is impossible due to insufficient resources */ 
    Unaffordable(false),
    
    /** Buying this card is impossible because its prerequisites aren't met */
    PrereqFailed(false),

    /** Buying this card is discouraged, because it would make it impossible to
     *  reach the total winning points by civilization cards. The original game
     *  variant includes a limit on the number of civilization cards a player can
     *  buy. */
    DiscouragedBuy(false);



    /** If this flag is cleared, the credit bar will show 'absent' */
    private boolean iAffectsCredit;



    private CcState(final boolean pAffectsCredit)
    {
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
}
