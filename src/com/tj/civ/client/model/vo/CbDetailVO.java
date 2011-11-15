/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 20.07.2011
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
package com.tj.civ.client.model.vo;

import java.util.List;

import com.tj.civ.client.model.CbCardCurrent;
import com.tj.civ.client.model.CbGroup;
import com.tj.civ.client.model.CbState;


/**
 * View object representing those attributes of a
 * {@link com.tj.civ.client.model.CbCardCurrent} (direct and calculated) that are
 * shown in the contents of the 'Detail' view.
 *
 * @author Thomas Jensen
 */
public class CbDetailVO
    extends CbAbstractViewObject
{
    /** the card to display */
    private CbCardCurrent iCard;

    /** the status message */
    private String iStatusMsg = null;

    /** credit received */
    private List<CbCardEntry> iCreditFrom = null;

    /** cards supported by this card */
    private List<CbCardEntry> iSupports = null;

    /** percentage of potential credit maximum presently received by Owned cards */
    private int iCreditPercent;

    /** percentage of potential credit maximum presently received by Owned and
     *  Planned cards */
    private int iCreditPercentInclPlan;



    /**
     * An entry in the list of cards supported or the list of cards which this card
     * received credit from.
     *
     * @author Thomas Jensen
     */
    public static class CbCardEntry
    {
        /** display name */
        private String iDisplayName;

        /** credit given / received */
        private int iCredit;

        /** current card state */
        private CbState iState;

        /** card index, used in links to this card */
        private int iCardIdx;



        public String getDisplayName()
        {
            return iDisplayName;
        }

        public void setDisplayName(final String pDisplayName)
        {
            iDisplayName = pDisplayName;
        }



        public int getCredit()
        {
            return iCredit;
        }

        public void setCredit(final int pCredit)
        {
            iCredit = pCredit;
        }



        public CbState getState()
        {
            return iState;
        }

        public void setState(final CbState pState)
        {
            iState = pState;
        }



        public int getCardIdx()
        {
            return iCardIdx;
        }

        public void setCardIdx(final int pCardIdx)
        {
            iCardIdx = pCardIdx;
        }
    }



    /**
     * Constructor.
     * @param pCard the card shown
     */
    public CbDetailVO(final CbCardCurrent pCard)
    {
        iCard = pCard;
    }



    public String getDisplayName()
    {
        return iCard.getConfig().getLocalizedName();
    }



    public int getIndex()
    {
        return iCard.getConfig().getMyIdx();
    }



    public int getCostCurrent()
    {
        return iCard.getCostCurrent();
    }



    public int getCostNominal()
    {
        return iCard.getConfig().getCostNominal();
    }
    


    @Override
    public String getPrimaryText()
    {
        return getDisplayName();
    }

    @Override
    public void setPrimaryText(final String pPrimaryText)
    {
        throw new RuntimeException("setPrimaryText(): Operation not supported"); //$NON-NLS-1$
    }



    @Override
    public String getSecondaryText()
    {
        return null;
    }



    public String getAttributes()
    {
        return iCard.getConfig().getLocalizedAttributes();
    }



    public String getCalamityEffects()
    {
        return iCard.getConfig().getLocalizedCalamityEffects();
    }



    public CbState getState()
    {
        return iCard.getState();
    }



    public String getStatusMsg()
    {
        return iStatusMsg;
    }

    public void setStatusMsg(final String pStatusMsg)
    {
        iStatusMsg = pStatusMsg;
    }



    public List<CbCardEntry> getCreditFrom()
    {
        return iCreditFrom;
    }

    public void setCreditFrom(final List<CbCardEntry> pCreditFrom)
    {
        iCreditFrom = pCreditFrom;
    }



    public List<CbCardEntry> getSupports()
    {
        return iSupports;
    }

    public void setSupports(final List<CbCardEntry> pSupports)
    {
        iSupports = pSupports;
    }



    public int getCreditPercent()
    {
        return iCreditPercent;
    }

    public void setCreditPercent(final int pCreditPercent)
    {
        iCreditPercent = pCreditPercent;
    }



    public int getCreditPercentInclPlan()
    {
        return iCreditPercentInclPlan;
    }

    public void setCreditPercentInclPlan(final int pCreditPercentInclPlan)
    {
        iCreditPercentInclPlan = pCreditPercentInclPlan;
    }



    public int getSupportsTotal()
    {
        return iCard.getConfig().getCreditGivenTotal();
    }



    public CbGroup[] getGroups()
    {
        return iCard.getConfig().getGroups();
    }
}
