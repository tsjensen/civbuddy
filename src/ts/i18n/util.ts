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


/**
 * Programmatic access to localized strings.
 */
export class L10nUtil {

    public static getLocalizedString(pKey: string, pCallback: (v: string[]) => void): void {
        L10nUtil.getLocalizedStringInternal(pKey, pCallback);
    }

    public static getLocalizedStringWithArgs(pKey: string, pArgs: object, pCallback: (v: string[]) => void): void {
        L10nUtil.getLocalizedStringInternal([pKey, pArgs], pCallback);
    }

    private static getLocalizedStringInternal(pKey: any, pCallback: (v: string[]) => void): void {
        if (document.hasOwnProperty('l10n')) {
            const localization = (document as any)['l10n'];  // tslint:disable-line:no-string-literal
            localization.formatValues(pKey).then(pCallback, pCallback);
        }
    }
}
