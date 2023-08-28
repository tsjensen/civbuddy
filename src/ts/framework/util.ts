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

export class Util {

    private constructor() { }


    public static getUrlParameter(pParamName: string): string | null {
        let result: string | null = null;
        const pageUrl: string = decodeURIComponent(window.location.search.substring(1));
        const params: string[] = pageUrl.split('&');
        for (const param of params) {
            const paramKeyValue: string[] = param.split('=');
            if (paramKeyValue.length === 2 && paramKeyValue[0] === pParamName) {
                result = paramKeyValue[1];
                break;
            }
        }
        return result;
    }


    public static buildMap<V>(pObj: object): Map<string, V> {
        return Object.keys(pObj).reduce((map, key: string) => map.set(key, (pObj as any)[key]), new Map<string, V>());
    }



    public static hideFields(...pFieldsToHide: string[]): (pKey: string, pValue: any) => any {
        return (pKey: string, pValue: any) => {
            if (pFieldsToHide.indexOf(pKey) >= 0) {
                return undefined;
            }
            return pValue;
        };
    }

    public static getJsonElement(pElementName: string, pJson: object): string {
        let result: string = '';
        if (pJson.hasOwnProperty(pElementName)) {
            result = (pJson as any)[pElementName];
        }
        return result;
    }

    public static parseQuietly(pContent: string): object {
        let json: object = {};
        try {
            json = JSON.parse(pContent);
        } catch (e) {
            // ignore
        }
        return json;
    }
}
