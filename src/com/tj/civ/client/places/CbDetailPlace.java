/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-03-20
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.tj.civ.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

import com.tj.civ.client.common.CbLogAdapter;


/**
 * The 'Detail' place.
 *
 * @author Thomas Jensen
 */
public class CbDetailPlace
    extends CbAbstractPlace
{
    /** Logger for this class */
    private static final CbLogAdapter LOG = CbLogAdapter.getLogger(CbDetailPlace.class);

    /** seperator char between data elements */
    private static final char SEP = '.';

    /** the persistence key of the current situation */
    private String iSituationKey;

    /** index number of the card to display */
    private int iCardIdx;



    /**
     * Performs text serialization and deserialization of {@link CbDetailPlace}s.
     * @author Thomas Jensen
     */
    public static class CbTokenizer implements PlaceTokenizer<CbDetailPlace>
    {
        @Override
        public String getToken(final CbDetailPlace pPlace)
        {
            // GWT urlencodes the token so it will be valid within one browser.
            // However, links containing a token cannot necessarily be shared among
            // users of different browsers. We don't need that, so we're ok.
            return pPlace.getToken();
        }

        @Override
        public CbDetailPlace getPlace(final String pToken)
        {
            return new CbDetailPlace(pToken);
        }
    }



    /**
     * Constructor.
     * @param pSituationKey the persistence key of the current situation
     * @param pCardIdx index number of the card to display
     */
    public CbDetailPlace(final String pSituationKey, final int pCardIdx)
    {
        super();
        if (LOG.isTraceEnabled()) {
            LOG.enter(CbLogAdapter.CONSTRUCTOR,
                new String[]{"pSituationKey", "pCardIdx"},  //$NON-NLS-1$ //$NON-NLS-2$
                new Object[]{pSituationKey, Integer.valueOf(pCardIdx)});
        }
        iSituationKey = pSituationKey;
        iCardIdx = pCardIdx;
        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    /**
     * Constructor.
     * @param pToken the data token
     */
    public CbDetailPlace(final String pToken)
    {
        super();
        if (LOG.isTraceEnabled()) {
            LOG.enter(CbLogAdapter.CONSTRUCTOR,
                new String[]{"pToken"}, new Object[]{pToken});  //$NON-NLS-1$
        }

        iCardIdx = 0;
        iSituationKey = null;
        if (pToken != null && pToken.length() > 0) {
            int dotPos = pToken.indexOf(SEP);
            if (dotPos > 0) {
                iSituationKey = pToken.substring(0, dotPos);
                try {
                    iCardIdx = Integer.parseInt(pToken.substring(dotPos + 1));
                }
                catch (NumberFormatException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(CbLogAdapter.CONSTRUCTOR,
                            "Error parsing card index: " + e.getMessage()); //$NON-NLS-1$
                    }
                }
            }
        }

        LOG.exit(CbLogAdapter.CONSTRUCTOR);
    }



    public String getSituationKey()
    {
        return iSituationKey;
    }



    public int getCardIdx()
    {
        return iCardIdx;
    }



    @Override
    public String getToken()
    {
        return iSituationKey + SEP + iCardIdx;
    }
}
