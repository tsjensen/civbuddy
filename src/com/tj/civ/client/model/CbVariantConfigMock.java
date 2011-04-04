/*
 * CivCounsel - A Civilization Tactics Guide
 * Copyright (c) 2010 Thomas Jensen
 * $Id$
 * Date created: 26.12.2010
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

import java.util.SortedSet;
import java.util.TreeSet;

import com.tj.civ.client.model.jso.CcCardConfigJSO;
import com.tj.civ.client.model.jso.CcCommodityConfigJSO;
import com.tj.civ.client.model.jso.CcVariantConfigJSO;
import com.tj.civ.client.resources.CcConstants;


/**
 * Mock for the 'Original Civilization' game variant.
 *
 * @author Thomas Jensen
 */
public class CcVariantConfigMock
    extends CcVariantConfig
{
    /** name of the game variant this mock describes */
    public static final String VARIANT_ID = "Original"; //$NON-NLS-1$

    /** number of civilization cards */
    public static final int NUM_CARDS = 16;

    /** number of commodities */
    public static final int NUM_COMMODITIES = 11;



    private static CcVariantConfigJSO buildVariantJso()
    {
        CcVariantConfigJSO result = CcVariantConfigJSO.create();

        result.setVariantId(VARIANT_ID);
        result.getDisplayNames().setDefaultEn("Original Game"); //$NON-NLS-1$
        result.getDisplayNames().setStringI18n(CcConstants.LOCALE_DE,
            "Originalspiel"); //$NON-NLS-1$
        result.setVersion(1);
        result.setUrl(null);
        result.setNumCardsLimit(11);
        
        SortedSet<Integer> targetOpts = new TreeSet<Integer>();
        targetOpts.add(Integer.valueOf(1200));
        targetOpts.add(Integer.valueOf(1300));
        targetOpts.add(Integer.valueOf(1400));
        result.setTargetOptions(targetOpts);
        
        CcCardConfigJSO[] cardsJso = new CcCardConfigJSO[NUM_CARDS];

        CcCardConfigJSO card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Pottery"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Töpfern"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn(
            "Reduces the effects of Famine when combined with Grain"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE,
            "Reduziert den Schaden bei Hungersnot durch Einsatz von Korn"); //$NON-NLS-1$
        card.setCostNominal(45);
        card.setGroups(new CcGroup[]{CcGroup.Crafts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 10, 10, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0});
        cardsJso[0] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Clothmaking"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Weben"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("Increases ship movement by one"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE,
            "Schiffe können 1 Feld weiter ziehen"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(45);
        card.setGroups(new CcGroup[]{CcGroup.Crafts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{10, 0, 10, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0});
        cardsJso[1] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Metalworking"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Metallverarbeitung"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("Increased combat effectiveness"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE,
            "Verbesserte Kampfkraft"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(80);
        card.setGroups(new CcGroup[]{CcGroup.Crafts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{10, 10, 0, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0});
        cardsJso[2] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Agriculture"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Landwirtschaft"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn(
            "Increases population limits of areas by one"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE,
            "Bevölkerungslimit pro Region wird um 1 erhöht"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn(
            "No direct effects. City reduction is mitigated by one token"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE,
            "Keine direkte Auswirkung, bei Reduktion von " //$NON-NLS-1$
            + "Städten bleibt 1 Einheit mehr übrig"); //$NON-NLS-1$
        card.setCostNominal(110);
        card.setGroups(new CcGroup[]{CcGroup.Crafts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{10, 10, 10, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0});
        cardsJso[3] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Engineering"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Ingenieurwesen"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn(
            "Increases the effectiveness of attack against and defense of cities"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE,
            "Verbessert Angriff gegen und Verteidigung von Städten"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("Reduces the effects of Flood"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE,
            "Vermindert den Schaden durch Überschwemmung"); //$NON-NLS-1$
        card.setCostNominal(140);
        card.setGroups(new CcGroup[]{CcGroup.Sciences, CcGroup.Crafts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{10, 10, 10, 10, 0, 20, 20, 20, 20, 0, 0, 0, 0, 0, 10, 20});
        cardsJso[4] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Astronomy"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Astronomie"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("Allows movement across open sea areas"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE,
            "Erlaubt Schiffsbewegung über die offene See"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(80);
        card.setGroups(new CcGroup[]{CcGroup.Sciences});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 20, 0, 20, 20, 20, 0, 0, 0, 0, 0, 0, 20});
        cardsJso[5] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Coinage"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Münzwesen"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn(
            "Allows taxation rates to vary from one to three tokens per city"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE,
            "Steuern dürfen von 1 bis 3 Punkte pro Stadt betragen"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(110);
        card.setGroups(new CcGroup[]{CcGroup.Sciences});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 20, 20, 0, 20, 20, 0, 0, 0, 0, 0, 0, 20});
        cardsJso[6] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Medicine"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Medizin"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("Reduces the effects of Epidemic"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE,
            "Vermindert den Schaden durch eine Epidemie"); //$NON-NLS-1$
        card.setCostNominal(140);
        card.setGroups(new CcGroup[]{CcGroup.Sciences});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 20, 20, 20, 0, 20, 0, 0, 0, 0, 0, 0, 20});
        cardsJso[7] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Mysticism"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Mystizismus"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(30);
        card.setGroups(new CcGroup[]{CcGroup.Arts, CcGroup.Sciences});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 20, 20, 20, 20, 0, 5, 5, 5, 5, 5, 0, 20});
        cardsJso[8] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Drama & Poetry"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Schauspiel & Poesie"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(60);
        card.setGroups(new CcGroup[]{CcGroup.Arts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 5, 5, 25, 5, 0, 0});
        cardsJso[9] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Music"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Musik"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(60);
        card.setGroups(new CcGroup[]{CcGroup.Arts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 0, 5, 5, 5, 0, 30});
        cardsJso[10] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Architecture"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Architektur"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(80);
        card.setGroups(new CcGroup[]{CcGroup.Civics, CcGroup.Arts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 20, 20, 15, 15});
        cardsJso[11] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Literacy"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Literatur"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn("None"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.setCostNominal(110);
        card.setGroups(new CcGroup[]{CcGroup.Civics, CcGroup.Arts});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 0, 30, 25, 25});
        cardsJso[12] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Law"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Gesetz"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn(
            "Reduces the effects of Civil Disorder and Iconoclasm and Heresy"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE,
            "Vermindert den Schaden durch Aufruhr und durch " //$NON-NLS-1$
            + "Bildersturm und Ketzerei"); //$NON-NLS-1$
        card.setCostNominal(170);
        card.setGroups(new CcGroup[]{CcGroup.Civics});
        card.setPrereq(-1);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        cardsJso[13] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Democracy"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Demokratie"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn(
            "Reduces the effects of Civil War and Civil Disorder"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE,
            "Vermindert den Schaden durch Bürgerkrieg und durch Aufruhr"); //$NON-NLS-1$
        card.setCostNominal(200);
        card.setGroups(new CcGroup[]{CcGroup.Civics});
        card.setPrereq(13);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        cardsJso[14] = card;

        card = CcCardConfigJSO.create();
        card.getNames().setDefaultEn("Philosophy"); //$NON-NLS-1$
        card.getNames().setStringI18n(CcConstants.LOCALE_DE, "Philosophie"); //$NON-NLS-1$
        card.getAttributes().setDefaultEn("None"); //$NON-NLS-1$
        card.getAttributes().setStringI18n(CcConstants.LOCALE_DE, "Keine"); //$NON-NLS-1$
        card.getClamityEffects().setDefaultEn(
            "Alters the effects of Civil War and reduces the effects " //$NON-NLS-1$
            + "of Iconoclasm and Heresy"); //$NON-NLS-1$
        card.getClamityEffects().setStringI18n(CcConstants.LOCALE_DE,
            "Verändert die Auswirkungen des Bürgerkriegs und vermindert den " //$NON-NLS-1$
            + "Schaden durch Bildersturm und Ketzerei"); //$NON-NLS-1$
        card.setCostNominal(240);
        card.setGroups(new CcGroup[]{CcGroup.Civics});
        card.setPrereq(13);
        card.setCreditGiven(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        cardsJso[15] = card;

        result.setCards(cardsJso);  // AFTER init, since a copy is made

        result.setCommodities(buildCommodityArray());

        return result;
    }



    private static CcCommodityConfigJSO[] buildCommodityArray()
    {
        CcCommodityConfigJSO[] result = new CcCommodityConfigJSO[NUM_COMMODITIES];
        
        result[0] = CcCommodityConfigJSO.create();
        result[0].getNames().setDefaultEn("Hides"); //$NON-NLS-1$
        result[0].getNames().setStringI18n(CcConstants.LOCALE_DE, "Felle"); //$NON-NLS-1$
        result[0].setBase(1);
        result[0].setMaxCount(7);
        
        result[1] = CcCommodityConfigJSO.create();
        result[1].getNames().setDefaultEn("Ochre"); //$NON-NLS-1$
        result[1].getNames().setStringI18n(CcConstants.LOCALE_DE, "Ocker"); //$NON-NLS-1$
        result[1].setBase(1);
        result[1].setMaxCount(7);
        
        result[2] = CcCommodityConfigJSO.create();
        result[2].getNames().setDefaultEn("Iron"); //$NON-NLS-1$
        result[2].getNames().setStringI18n(CcConstants.LOCALE_DE, "Eisen"); //$NON-NLS-1$
        result[2].setBase(2);
        result[2].setMaxCount(5);
        
        result[3] = CcCommodityConfigJSO.create();
        result[3].getNames().setDefaultEn("Papyrus"); //$NON-NLS-1$
        result[3].getNames().setStringI18n(CcConstants.LOCALE_DE, "Papyrus"); //$NON-NLS-1$
        result[3].setBase(2);
        result[3].setMaxCount(5);
        
        result[4] = CcCommodityConfigJSO.create();
        result[4].getNames().setDefaultEn("Salt"); //$NON-NLS-1$
        result[4].getNames().setStringI18n(CcConstants.LOCALE_DE, "Salz"); //$NON-NLS-1$
        result[4].setBase(3);
        result[4].setMaxCount(9);
        
        result[5] = CcCommodityConfigJSO.create();
        result[5].getNames().setDefaultEn("Grain"); //$NON-NLS-1$
        result[5].getNames().setStringI18n(CcConstants.LOCALE_DE, "Korn"); //$NON-NLS-1$
        result[5].setBase(4);
        result[5].setMaxCount(8);
        
        result[6] = CcCommodityConfigJSO.create();
        result[6].getNames().setDefaultEn("Cloth"); //$NON-NLS-1$
        result[6].getNames().setStringI18n(CcConstants.LOCALE_DE, "Stoffe"); //$NON-NLS-1$
        result[6].setBase(5);
        result[6].setMaxCount(7);
        
        result[7] = CcCommodityConfigJSO.create();
        result[7].getNames().setDefaultEn("Bronze"); //$NON-NLS-1$
        result[7].getNames().setStringI18n(CcConstants.LOCALE_DE, "Bronze"); //$NON-NLS-1$
        result[7].setBase(6);
        result[7].setMaxCount(6);
        
        result[8] = CcCommodityConfigJSO.create();
        result[8].getNames().setDefaultEn("Spices"); //$NON-NLS-1$
        result[8].getNames().setStringI18n(CcConstants.LOCALE_DE, "Gewürze"); //$NON-NLS-1$
        result[8].setBase(7);
        result[8].setMaxCount(5);
        
        result[9] = CcCommodityConfigJSO.create();
        result[9].getNames().setDefaultEn("Gems"); //$NON-NLS-1$
        result[9].getNames().setStringI18n(CcConstants.LOCALE_DE, "Juwelen"); //$NON-NLS-1$
        result[9].setBase(8);
        result[9].setMaxCount(4);
        
        result[10] = CcCommodityConfigJSO.create();
        result[10].getNames().setDefaultEn("Gold"); //$NON-NLS-1$
        result[10].getNames().setStringI18n(CcConstants.LOCALE_DE, "Gold"); //$NON-NLS-1$
        result[10].setBase(9);
        result[10].setMaxCount(3);

        return result;
    }



    /**
     * Constructor.
     */
    public CcVariantConfigMock()
    {
        super(buildVariantJso());
    }
}
