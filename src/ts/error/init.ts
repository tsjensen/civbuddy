/*
 * CivBuddy - A calculator app for players of Francis Tresham's original Civilization board game (1980)
 * Copyright (C) 2012-2023 Thomas Jensen
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

import { AbstractPageContext, AbstractPageInitializer, Page } from '../framework/framework';
import { AppVersion } from '../framework/version';
import { GamesController } from '../games/controllers';
import { Language } from '../rules/rules';


/**
 * The page context object of the 'error' page.
 */
export class ErrorPageContext extends AbstractPageContext {
}


/**
 * The page initializer of the 'error' page.
 */
export class ErrorPageInitializer
    extends AbstractPageInitializer<ErrorPageContext> {

    private readonly gamesCtrl: GamesController = new GamesController();

    constructor() {
        super(Page.ERROR, new ErrorPageContext());
    }


    protected parseTemplates(): void {
        // no templates
    }

    protected pageLoaded(): void {
        this.displayAppVersion();
        this.activateLocalizedErrorMessage(this.getAppOptions().language);
    }

    protected languageChanged(pPrevious: Language, pNew: Language): void {
        this.activateLocalizedErrorMessage(pNew);
    }

    private activateLocalizedErrorMessage(pLang: Language): void {
        this.gamesCtrl.hideElement($('*[class^="civbuddy-lang-"],*[class*=" civbuddy-lang-"]'));
        this.gamesCtrl.showElement($('.civbuddy-lang-' + pLang.toLowerCase()));
    }

    private displayAppVersion(): void {
        const v: AppVersion = this.pageContext.appVersion;
        this.gamesCtrl.setAppVersion(v.getCombinedVersion(), v.isDirty());
    }
}
