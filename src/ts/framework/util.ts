
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
