/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-04
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.model.jso;

import com.google.gwt.core.client.JavaScriptObject;

import com.tj.civ.client.common.CbUtil;
import com.tj.civ.client.model.CbState;


/**
 * Holds the persisted part of a player's situation.
 *
 * @author Thomas Jensen
 */
public final class CbSituationJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CbSituationJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @param pPlayer the player whose situation th#is is
     * @param pNumCards number of cards in this situation (depends on game variant)
     * @param pNumCommodities number of commodities in the game (depends on variant)
     * @return the new instance
     */
    public static CbSituationJSO create(final CbPlayerJSO pPlayer,
        final int pNumCards, final int pNumCommodities)
    {
        CbSituationJSO result = createObject().cast();
        result.setPlayer(pPlayer);
        result.setFunds(CbFundsJSO.create(pNumCommodities));
        result.setStates(CbState.createInitialStateArray(pNumCards));
        return result;
    }



    /**
     * Factory method.
     * @param pJson the JSON representation of a {@link CbSituationJSO}
     * @return a new instance
     */
    public static CbSituationJSO create(final String pJson)
    {
        return CbUtil.createFromJson(pJson);
    }



    /**
     * Getter.
     * @return the persistable funds data
     */
    public native CbFundsJSO getFunds()
    /*-{
        return this.funds;
    }-*/;

    /**
     * Sets the persistable funds data.
     * @param pFunds the new values
     */
    public native void setFunds(final CbFundsJSO pFunds)
    /*-{
        this.funds = pFunds;
    }-*/;



    /**
     * Getter.
     * @return the player whose situation this is
     */
    public native CbPlayerJSO getPlayer()
    /*-{
        return this.player;
    }-*/;

    /**
     * Sets the player whose situation this is.
     * @param pPlayer the new values
     */
    private native void setPlayer(final CbPlayerJSO pPlayer)
    /*-{
        this.player = pPlayer;
    }-*/;



    /**
     * Read a card state.
     * @param pIdx index into the card state array
     * @return state
     */
    public CbState getState(final int pIdx)
    {
        return CbState.fromKey(getStatesJs().charAt(pIdx));
    }

    /**
     * Read the persisted card states.
     * @return array of states
     */
    public CbState[] getStates()
    {
        String arr = getStatesJs();
        CbState[] result = null;
        if (arr != null && arr.length() > 0) {
            result = new CbState[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                result[i] = CbState.fromKey(arr.charAt(i));
            }
        }
        return result;
    }

    /**
     * Set the persisted card states.
     * @param pStates the new values
     */
    public void setStates(final CbState[] pStates)
    {
        if (pStates != null && pStates.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (CbState state : pStates) {
                sb.append(state.getKey());
            }
            setStatesJs(sb.toString());
        }
    }

    /**
     * Set the persisted card state.
     * @param pIdx index into the card state array
     * @param pState the new value
     */
    public void setState(final int pIdx, final CbState pState)
    {
        StringBuilder sb = new StringBuilder(getStatesJs());
        sb.setCharAt(pIdx, pState.getKey());
        setStatesJs(sb.toString());
    }

    private native String getStatesJs()
    /*-{
        return this.cardStates;
    }-*/;

    private native void setStatesJs(final String pStates)
    /*-{
        this.cardStates = pStates;
    }-*/;
}
