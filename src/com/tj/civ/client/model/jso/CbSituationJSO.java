/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 04.03.2011
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
package com.tj.civ.client.model.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import com.tj.civ.client.model.CcState;


/**
 * Holds the persisted part of a player's situation.
 *
 * @author Thomas Jensen
 */
public final class CcSituationJSO
    extends JavaScriptObject
{
    /**
     * JSO constructor.
     */
    protected CcSituationJSO()
    {
        super();
    }



    /**
     * Factory method.
     * @param pPlayer the player whose situation th#is is
     * @param pNumCards number of cards in this situation (depends of game variant)
     * @return the new instance
     */
    public static CcSituationJSO create(final CcPlayerJSO pPlayer, final int pNumCards)
    {
        CcSituationJSO result = createObject().cast();
        result.setPlayer(pPlayer);
        result.setFunds(CcFundsJSO.create());
        result.setStates(CcState.createInitialStateArray(pNumCards));
        return result;
    }



    /**
     * Getter.
     * @return the persistable funds data
     */
    public native CcFundsJSO getFunds()
    /*-{
        return this.funds;
    }-*/;

    /**
     * Sets the persistable funds data.
     * @param pFunds the new values
     */
    public native void setFunds(final CcFundsJSO pFunds)
    /*-{
        this.funds = pFunds;
    }-*/;



    /**
     * Getter.
     * @return the player whose situation this is
     */
    public native CcPlayerJSO getPlayer()
    /*-{
        return this.player;
    }-*/;

    /**
     * Sets the player whose situation this is.
     * @param pPlayer the new values
     */
    private native void setPlayer(final CcPlayerJSO pPlayer)
    /*-{
        this.player = pPlayer;
    }-*/;



    /**
     * Read a card state.
     * @param pIdx index into the card state array
     * @return state
     */
    public CcState getState(final int pIdx)
    {
        String s = getStatesJs().get(pIdx);
        return CcState.fromKey(s.charAt(0));
    }

    /**
     * Read the persisted card states.
     * @return array of states
     */
    public CcState[] getStates()
    {
        JsArrayString arr = getStatesJs();
        CcState[] result = null;
        if (arr != null && arr.length() > 0) {
            result = new CcState[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                result[i] = CcState.fromKey(arr.get(i).charAt(0));
            }
        }
        return result;
    }

    /**
     * Set the persisted card states.
     * @param pStates the new values
     */
    public void setStates(final CcState[] pStates)
    {
        JsArrayString arr = createArray().cast();
        if (pStates != null && pStates.length > 0) {
            for (CcState state : pStates) {
                arr.push(String.valueOf(state.getKey()));
            }
        }
        setStatesJs(arr);
    }

    /**
     * Set the persisted card state.
     * @param pIdx index into the card state array
     * @param pState the new value
     */
    public void setState(final int pIdx, final CcState pState)
    {
        getStatesJs().set(pIdx, String.valueOf(pState.getKey()));
    }

    private native JsArrayString getStatesJs()
    /*-{
        return this.cardStates;
    }-*/;

    private native void setStatesJs(final JsArrayString pStates)
    /*-{
        this.cardStates = pStates;
    }-*/;
}
