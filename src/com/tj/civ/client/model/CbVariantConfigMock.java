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

import com.tj.civ.client.resources.CcConstants;


/**
 * Mock for the 'Original Civilization' game variant.
 *
 * @author tsjensen
 */
// CHECKSTYLE:OFF
public class CcVariantConfigMock
    extends CcVariantConfig
{
    public CcVariantConfigMock()
    {
        setName("Original"); //$NON-NLS-1$
        setVersion(1);
        setUrl(null);
        setNumCardsLimit(11);
        
        CcCardConfig[] cards = new CcCardConfig[16];
        setCards(cards);

        cards[0] = new CcCardConfig(0, cards);
        cards[0].setNameEn("Pottery"); //$NON-NLS-1$
        cards[0].setNameDe("Töpfern"); //$NON-NLS-1$
        cards[0].setAttributes("None"); //$NON-NLS-1$
        cards[0].setClamityEffects("Reduces the effects of Famine when combined with Grain"); //$NON-NLS-1$
        cards[0].setCostNominal(45);
        cards[0].setGroups(new CcGroup[]{CcGroup.Crafts});
        cards[0].setPrereq(-1);
        cards[0].setCreditGiven(new int[]{0,10,10,10,10,0,0,0,0,0,0,0,0,0,10,0});

        cards[1] = new CcCardConfig(1, cards);
        cards[1].setNameEn("Clothmaking"); //$NON-NLS-1$
        cards[1].setNameDe("Weben"); //$NON-NLS-1$
        cards[1].setAttributes("Increases ship movement by one"); //$NON-NLS-1$
        cards[1].setClamityEffects("None"); //$NON-NLS-1$
        cards[1].setCostNominal(45);
        cards[1].setGroups(new CcGroup[]{CcGroup.Crafts});
        cards[1].setPrereq(-1);
        cards[1].setCreditGiven(new int[]{10,0,10,10,10,0,0,0,0,0,0,0,0,0,10,0});

        cards[2] = new CcCardConfig(2, cards);
        cards[2].setNameEn("Metalworking"); //$NON-NLS-1$
        cards[2].setNameDe("Metallverarbeitung"); //$NON-NLS-1$
        cards[2].setAttributes("Increased combat effectiveness"); //$NON-NLS-1$
        cards[2].setClamityEffects("None"); //$NON-NLS-1$
        cards[2].setCostNominal(80);
        cards[2].setGroups(new CcGroup[]{CcGroup.Crafts});
        cards[2].setPrereq(-1);
        cards[2].setCreditGiven(new int[]{10,10,0,10,10,0,0,0,0,0,0,0,0,0,10,0});

        cards[3] = new CcCardConfig(3, cards);
        cards[3].setNameEn("Agriculture"); //$NON-NLS-1$
        cards[3].setNameDe("Landwirtschaft"); //$NON-NLS-1$
        cards[3].setAttributes("Increases population limits of areas by one"); //$NON-NLS-1$
        cards[3].setClamityEffects("No direct effects. City reduction is mitigated by one token"); //$NON-NLS-1$
        cards[3].setCostNominal(110);
        cards[3].setGroups(new CcGroup[]{CcGroup.Crafts});
        cards[3].setPrereq(-1);
        cards[3].setCreditGiven(new int[]{10,10,10,0,10,0,0,0,0,0,0,0,0,0,10,0});

        cards[4] = new CcCardConfig(4, cards);
        cards[4].setNameEn("Engineering"); //$NON-NLS-1$
        cards[4].setNameDe("Ingenieurwesen"); //$NON-NLS-1$
        cards[4].setAttributes("Increases the effectiveness of attack against and defense of cities"); //$NON-NLS-1$
        cards[4].setClamityEffects("Reduces the effects of Flood"); //$NON-NLS-1$
        cards[4].setCostNominal(140);
        cards[4].setGroups(new CcGroup[]{CcGroup.Sciences, CcGroup.Crafts});
        cards[4].setPrereq(-1);
        cards[4].setCreditGiven(new int[]{10,10,10,10,0,20,20,20,20,0,0,0,0,0,10,20});

        cards[5] = new CcCardConfig(5, cards);
        cards[5].setNameEn("Astronomy"); //$NON-NLS-1$
        cards[5].setNameDe("Astronomie"); //$NON-NLS-1$
        cards[5].setAttributes("Allows movement across open sea areas"); //$NON-NLS-1$
        cards[5].setClamityEffects("None"); //$NON-NLS-1$
        cards[5].setCostNominal(80);
        cards[5].setGroups(new CcGroup[]{CcGroup.Sciences});
        cards[5].setPrereq(-1);
        cards[5].setCreditGiven(new int[]{0,0,0,0,20,0,20,20,20,0,0,0,0,0,0,20});

        cards[6] = new CcCardConfig(6, cards);
        cards[6].setNameEn("Coinage"); //$NON-NLS-1$
        cards[6].setNameDe("Münzwesen"); //$NON-NLS-1$
        cards[6].setAttributes("Allows taxation rates to vary from one to three tokens per city"); //$NON-NLS-1$
        cards[6].setClamityEffects("None"); //$NON-NLS-1$
        cards[6].setCostNominal(110);
        cards[6].setGroups(new CcGroup[]{CcGroup.Sciences});
        cards[6].setPrereq(-1);
        cards[6].setCreditGiven(new int[]{0,0,0,0,20,20,0,20,20,0,0,0,0,0,0,20});

        cards[7] = new CcCardConfig(7, cards);
        cards[7].setNameEn("Medicine"); //$NON-NLS-1$
        cards[7].setNameDe("Medizin"); //$NON-NLS-1$
        cards[7].setAttributes("None"); //$NON-NLS-1$
        cards[7].setClamityEffects("Reduces the effects of Epidemic"); //$NON-NLS-1$
        cards[7].setCostNominal(140);
        cards[7].setGroups(new CcGroup[]{CcGroup.Sciences});
        cards[7].setPrereq(-1);
        cards[7].setCreditGiven(new int[]{0,0,0,0,20,20,20,0,20,0,0,0,0,0,0,20});

        cards[8] = new CcCardConfig(8, cards);
        cards[8].setNameEn("Mysticism"); //$NON-NLS-1$
        cards[8].setNameDe("Mystizismus"); //$NON-NLS-1$
        cards[8].setAttributes("None"); //$NON-NLS-1$
        cards[8].setClamityEffects("None"); //$NON-NLS-1$
        cards[8].setCostNominal(30);
        cards[8].setGroups(new CcGroup[]{CcGroup.Arts, CcGroup.Sciences});
        cards[8].setPrereq(-1);
        cards[8].setCreditGiven(new int[]{0,0,0,0,20,20,20,20,0,5,5,5,5,5,0,20});

        cards[9] = new CcCardConfig(9, cards);
        cards[9].setNameEn("Drama & Poetry"); //$NON-NLS-1$
        cards[9].setNameDe("Schauspiel & Poesie"); //$NON-NLS-1$
        cards[9].setAttributes("None"); //$NON-NLS-1$
        cards[9].setClamityEffects("None"); //$NON-NLS-1$
        cards[9].setCostNominal(60);
        cards[9].setGroups(new CcGroup[]{CcGroup.Arts});
        cards[9].setPrereq(-1);
        cards[9].setCreditGiven(new int[]{0,0,0,0,0,0,0,0,0,0,5,25,5,5,0,0});

        cards[10] = new CcCardConfig(10, cards);
        cards[10].setNameEn("Music"); //$NON-NLS-1$
        cards[10].setNameDe("Musik"); //$NON-NLS-1$
        cards[10].setAttributes("None"); //$NON-NLS-1$
        cards[10].setClamityEffects("None"); //$NON-NLS-1$
        cards[10].setCostNominal(60);
        cards[10].setGroups(new CcGroup[]{CcGroup.Arts});
        cards[10].setPrereq(-1);
        cards[10].setCreditGiven(new int[]{0,0,0,0,0,0,0,0,0,5,0,5,5,5,0,30});

        cards[11] = new CcCardConfig(11, cards);
        cards[11].setNameEn("Architecture"); //$NON-NLS-1$
        cards[11].setNameDe("Architektur"); //$NON-NLS-1$
        cards[11].setAttributes("None"); //$NON-NLS-1$
        cards[11].setClamityEffects("None"); //$NON-NLS-1$
        cards[11].setCostNominal(80);
        cards[11].setGroups(new CcGroup[]{CcGroup.Civics, CcGroup.Arts});
        cards[11].setPrereq(-1);
        cards[11].setCreditGiven(new int[]{0,0,0,0,0,0,0,0,0,5,5,20,0,20,15,15});

        cards[12] = new CcCardConfig(12, cards);
        cards[12].setNameEn("Literacy"); //$NON-NLS-1$
        cards[12].setNameDe("Literatur"); //$NON-NLS-1$
        cards[12].setAttributes("None"); //$NON-NLS-1$
        cards[12].setClamityEffects("None"); //$NON-NLS-1$
        cards[12].setCostNominal(110);
        cards[12].setGroups(new CcGroup[]{CcGroup.Civics, CcGroup.Arts});
        cards[12].setPrereq(-1);
        cards[12].setCreditGiven(new int[]{0,0,0,0,0,0,0,0,5,5,5,0,5,30,25,25});

        cards[13] = new CcCardConfig(13, cards);
        cards[13].setNameEn("Law"); //$NON-NLS-1$
        cards[13].setNameDe("Gesetz"); //$NON-NLS-1$
        cards[13].setAttributes("None"); //$NON-NLS-1$
        cards[13].setClamityEffects("Reduces the effects of Civil Disorder and Iconoclasm and Heresy"); //$NON-NLS-1$
        cards[13].setCostNominal(170);
        cards[13].setGroups(new CcGroup[]{CcGroup.Civics});
        cards[13].setPrereq(-1);
        cards[13].setCreditGiven(new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});

        cards[14] = new CcCardConfig(14, cards);
        cards[14].setNameEn("Democracy"); //$NON-NLS-1$
        cards[14].setNameDe("Demokratie"); //$NON-NLS-1$
        cards[14].setAttributes("None"); //$NON-NLS-1$
        cards[14].setClamityEffects("Reduces the effects of Civil War and Civil Disorder"); //$NON-NLS-1$
        cards[14].setCostNominal(200);
        cards[14].setGroups(new CcGroup[]{CcGroup.Civics});
        cards[14].setPrereq(13);
        cards[14].setCreditGiven(new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});

        cards[15] = new CcCardConfig(15, cards);
        cards[15].setNameEn("Philosophy"); //$NON-NLS-1$
        cards[15].setNameDe("Philosophie"); //$NON-NLS-1$
        cards[15].setAttributes("None"); //$NON-NLS-1$
        cards[15].setClamityEffects("Alters the effects of Civil War and reduces the effects of Iconoclasm and Heresy"); //$NON-NLS-1$
        cards[15].setCostNominal(240);
        cards[15].setGroups(new CcGroup[]{CcGroup.Civics});
        cards[15].setPrereq(13);
        cards[15].setCreditGiven(new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});

        calculateValues();

        CcCommodityConfig[] commos = new CcCommodityConfig[11];
        setCommodities(commos);
        
        commos[0] = new CcCommodityConfig();
        commos[0].setNameDefaultEn("Hides"); //$NON-NLS-1$
        commos[0].setNameI18n(CcConstants.LOCALE_DE, "Felle"); //$NON-NLS-1$
        commos[0].setBase(1);
        commos[0].setMaxCount(7);
        
        commos[1] = new CcCommodityConfig();
        commos[1].setNameDefaultEn("Ochre"); //$NON-NLS-1$
        commos[1].setNameI18n(CcConstants.LOCALE_DE, "Ocker"); //$NON-NLS-1$
        commos[1].setBase(1);
        commos[1].setMaxCount(7);
        
        commos[2] = new CcCommodityConfig();
        commos[2].setNameDefaultEn("Iron"); //$NON-NLS-1$
        commos[2].setNameI18n(CcConstants.LOCALE_DE, "Eisen"); //$NON-NLS-1$
        commos[2].setBase(2);
        commos[2].setMaxCount(5);
        
        commos[3] = new CcCommodityConfig();
        commos[3].setNameDefaultEn("Papyrus"); //$NON-NLS-1$
        commos[3].setNameI18n(CcConstants.LOCALE_DE, "Papyrus"); //$NON-NLS-1$
        commos[3].setBase(2);
        commos[3].setMaxCount(5);
        
        commos[4] = new CcCommodityConfig();
        commos[4].setNameDefaultEn("Salt"); //$NON-NLS-1$
        commos[4].setNameI18n(CcConstants.LOCALE_DE, "Salz"); //$NON-NLS-1$
        commos[4].setBase(3);
        commos[4].setMaxCount(9);
        
        commos[5] = new CcCommodityConfig();
        commos[5].setNameDefaultEn("Grain"); //$NON-NLS-1$
        commos[5].setNameI18n(CcConstants.LOCALE_DE, "Korn"); //$NON-NLS-1$
        commos[5].setBase(4);
        commos[5].setMaxCount(8);
        
        commos[6] = new CcCommodityConfig();
        commos[6].setNameDefaultEn("Cloth"); //$NON-NLS-1$
        commos[6].setNameI18n(CcConstants.LOCALE_DE, "Stoffe"); //$NON-NLS-1$
        commos[6].setBase(5);
        commos[6].setMaxCount(7);
        
        commos[7] = new CcCommodityConfig();
        commos[7].setNameDefaultEn("Bronze"); //$NON-NLS-1$
        commos[7].setNameI18n(CcConstants.LOCALE_DE, "Bronze"); //$NON-NLS-1$
        commos[7].setBase(6);
        commos[7].setMaxCount(6);
        
        commos[8] = new CcCommodityConfig();
        commos[8].setNameDefaultEn("Spices"); //$NON-NLS-1$
        commos[8].setNameI18n(CcConstants.LOCALE_DE, "Gewürze"); //$NON-NLS-1$
        commos[8].setBase(7);
        commos[8].setMaxCount(5);
        
        commos[9] = new CcCommodityConfig();
        commos[9].setNameDefaultEn("Gems"); //$NON-NLS-1$
        commos[9].setNameI18n(CcConstants.LOCALE_DE, "Juwelen"); //$NON-NLS-1$
        commos[9].setBase(8);
        commos[9].setMaxCount(4);
        
        commos[10] = new CcCommodityConfig();
        commos[10].setNameDefaultEn("Gold"); //$NON-NLS-1$
        commos[10].setNameI18n(CcConstants.LOCALE_DE, "Gold"); //$NON-NLS-1$
        commos[10].setBase(9);
        commos[10].setMaxCount(3);
    }
}
