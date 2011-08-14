/*
 * CivBuddy - A Civilization Tactics Guide
 * Copyright (c) 2011 Thomas Jensen
 * $Id$
 * Date created: 2011-07-20
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
package com.tj.civ.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.tj.civ.client.common.CbConstants;
import com.tj.civ.client.common.CbGlobal;
import com.tj.civ.client.model.vo.CbDetailVO;
import com.tj.civ.client.places.CbCardsPlace;
import com.tj.civ.client.places.CbDetailPlace;


/**
 * Implementation of the 'Detail' view.
 *
 * @author Thomas Jensen
 */
public class CbDetailView
    extends Composite
    implements CbDetailViewIF
{
    /** this view's presenter */
    private CbDetailViewIF.CbPresenterIF iPresenter;

    /** the data we're currently displaying (<code>null</code> at view init) */
    private CbDetailVO iCurrentViewObject;

    /** 'Up' button at top of view */
    private Button iBtnUp;
    
    /** 'Down' button at top of view */
    private Button iBtnDown;



    /**
     * Constructor.
     */
    public CbDetailView()
    {
        HorizontalPanel headPanel = new HorizontalPanel();
        Label heading = new Label("Details");
        heading.setStyleName(CbConstants.CSS.ccHeading());

        Button btnBack = new Button(SafeHtmlUtils.fromSafeConstant("&lt;&nbsp;Cards"));
        btnBack.setStyleName(CbConstants.CSS.ccButton());
        btnBack.setTitle("Back to the cards");
        btnBack.setEnabled(true);
        btnBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(new CbCardsPlace(
                    CbGlobal.getCurrentSituation().getPersistenceKey()));
            }
        });

        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        headPanel.add(btnBack);
        headPanel.setCellWidth(btnBack, "12%"); //$NON-NLS-1$
        headPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        headPanel.add(heading);
        headPanel.setStyleName(CbConstants.CSS.ccButtonPanel());
        headPanel.addStyleName(CbConstants.CSS_BLUEGRADIENT);

        iBtnUp = new Button("Up");
        iBtnUp.setStyleName(CbConstants.CSS.ccButton());
        iBtnUp.setTitle(null);
        iBtnUp.setEnabled(false);
        iBtnUp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(new CbDetailPlace(
                    CbGlobal.getCurrentSituation().getPersistenceKey(),
                    Math.max(iCurrentViewObject.getIndex() - 1, 0)));
            }
        });

        iBtnDown = new Button("Down");
        iBtnDown.setStyleName(CbConstants.CSS.ccButton());
        iBtnDown.setTitle(null);
        iBtnDown.setEnabled(false);
        iBtnDown.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent pEvent)
            {
                iPresenter.goTo(new CbDetailPlace(
                    CbGlobal.getCurrentSituation().getPersistenceKey(),
                    Math.max(iCurrentViewObject.getIndex() + 1,
                        CbGlobal.getCardsCurrent().length - 1)));
            }
        });

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttonPanel.add(iBtnUp);
        buttonPanel.add(iBtnDown);
        buttonPanel.setStyleName(CbConstants.CSS.ccButtonPanel());
        buttonPanel.addStyleName(CbConstants.CSS_BLUEGRADIENT);

        // TODO
    }



    @Override
    public void setPresenter(final CbDetailViewIF.CbPresenterIF pPresenter)
    {
        iPresenter = pPresenter;
    }



    @Override
    public void showCard(final CbDetailVO pDetails)
    {
        // TODO implement showCard()
    }
}
