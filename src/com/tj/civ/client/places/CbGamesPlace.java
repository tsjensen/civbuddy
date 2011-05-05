/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 15.02.2011
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
package com.tj.civ.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;


/**
 * The 'Games' place.
 *
 * @author Thomas Jensen
 */
public class CbGamesPlace
    extends CbAbstractPlace
{
    /** dummy token */
    private static final String TOKEN = "ok"; //$NON-NLS-1$

    /** the name of a new game to be created */
    private String iGameName;

    /** the variant key of a new game to be created */
    private String iVariantKey;



    /**
     * Constructor.
     */
    public CbGamesPlace()
    {
        super();
        iVariantKey = null;
        iGameName = null;
    }



    /**
     * Constructor.
     * @param pVariantKey the variant key of a new game to be created
     * @param pGameName the name of a new game to be created
     */
    public CbGamesPlace(final String pVariantKey, final String pGameName)
    {
        super();
        String vKey = pVariantKey != null ? pVariantKey.trim() : null;
        String gName = pGameName != null ? pGameName.trim() : null;
        if (vKey != null && vKey.length() > 0 && gName != null && gName.length() > 0) {
            iVariantKey = vKey;
            iGameName = gName;
        }
    }



    /**
     * Performs text serialization and deserialization of {@link CbGamesPlace}s.
     * @author Thomas Jensen
     */
    public static class CbTokenizer implements PlaceTokenizer<CbGamesPlace>
    {
        @Override
        public String getToken(final CbGamesPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.getToken();
        }

        @Override
        public CbGamesPlace getPlace(final String pToken)
        {
            String token = pToken != null ? pToken.trim() : null;
            CbGamesPlace result = new CbGamesPlace();
            if (token != null && token.length() > 0 && !TOKEN.equals(token)) {
                int pc = token.indexOf(',');
                if (pc > 0 && pc < token.length() - 1) {
                    result = new CbGamesPlace(token.substring(0, pc),
                        token.substring(pc + 1));
                }
            }
            return result;
        }
    }



    @Override
    public String getToken()
    {
        if (iGameName != null && iVariantKey != null) {
            return iVariantKey + ',' + iGameName;
        } else {
            return TOKEN;
        }
    }



    public String getGameName()
    {
        return iGameName;
    }



    public String getVariantKey()
    {
        return iVariantKey;
    }
}
