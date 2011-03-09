/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 08.03.2011
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

import com.tj.civ.client.common.CcUtil;


/**
 * Mock for a typical player situation.
 * 
 * @author Thomas Jensen
 */
public class CcSituationMock
    extends CcSituation
{

    private static CcSituationJSO buildSituationJso()
    {
        CcPlayerJSO player = CcPlayerJSO.create();
        player.setName("Thomas"); //$NON-NLS-1$
        final int winningTotalCrete = 1200;
        player.setWinningTotal(winningTotalCrete);

        CcSituationJSO result = CcSituationJSO.create(player);
        final int numCardsInVariantMock = 16;    // should match CcVariantConfigMock
        CcState[] states = new CcState[numCardsInVariantMock];
        for (int i = 0; i < states.length; i++) {
            states[i] = CcState.Absent;
        }
        result.setStates(states);
        return result;
    }



    /**
     * Constructor.
     * @param pVariant the game variant
     */
    public CcSituationMock(final CcVariantConfig pVariant)
    {
        super(CcUtil.getUuid(), buildSituationJso(), pVariant);
    }
}
