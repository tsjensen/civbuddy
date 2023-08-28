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

import * as Mustache from 'mustache';

import { BaseController } from '../framework/framework';
import { Language } from '../rules/rules';
import { StorageSupport } from '../storage/storage';


/**
 * Manages display of the active language and its alternatives.
 */
export class LanguageController
    extends BaseController {

    public constructor() {
        super();
    }


    public showLanguage(pSelectedLanguage: Language): void {
        const otherLanguage = pSelectedLanguage === Language.EN ? Language.DE : Language.EN;
        const otherLabel: string = pSelectedLanguage === Language.EN ? 'Deutsch' : 'English';

        const htmlTemplate: string = $('#flagTemplate').html();
        const otherFlagHtml: string = Mustache.render(htmlTemplate, {
            'alt': otherLanguage.toUpperCase(),
            'fileName': otherLanguage.toString(),
            'langParam': this.pageWantsLangParam() ? otherLanguage.toString() : undefined,
            'menuText': otherLabel
        });

        const elem: JQuery<HTMLElement> = $('#otherLanguageFlags');
        if (elem.length > 0) {
            elem.empty();
            elem.append(otherFlagHtml);
        }
    }


    private pageWantsLangParam(): boolean {
        return !new StorageSupport().isLocalStorageUsed();
    }


    public requestL10nLanguage(pRequestedLanguage: Language): void {
        if (document.hasOwnProperty('l10n')) {
            // It's ok to list only the requested language.
            (document as any)['l10n'].requestLanguages([pRequestedLanguage]);  // tslint:disable-line:no-string-literal
        }
    }
}
