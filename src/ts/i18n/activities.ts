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

import { Activity, Page } from '../framework/framework';
import { runActivityInternal } from '../main';
import { Language } from '../rules/rules';
import { GlobalOptions } from '../storage/storage';
import { LanguageController } from './controllers';



/**
 * Change the active display language.
 */
export class ChangeLanguageActivity
    implements Activity {

    public constructor(private readonly newLanguage: string) { }

    public execute(pPreviousLanguage: Language): void {
        const globalOptions: GlobalOptions = new GlobalOptions();
        const newLanguage: Language = Language[this.newLanguage.toUpperCase() as keyof typeof Language];
        globalOptions.get().language = newLanguage;
        globalOptions.writeOptions();
        runActivityInternal(Page.CROSS, 'activateLanguage', newLanguage.toString());
        window.dispatchEvent(new CustomEvent('applanguagechanged', {
            'detail': { 'oldLang': pPreviousLanguage, 'newLang': newLanguage }
        }));
    }
}



/**
 * Activate the language that is already set on the current page.
 */
export class ActivateLanguageActivity
    implements Activity {

    private readonly langCtrl: LanguageController = new LanguageController();

    public execute(pLanguage: Language): void {
        this.langCtrl.showLanguage(pLanguage);
        this.langCtrl.requestL10nLanguage(pLanguage);
    }
}
