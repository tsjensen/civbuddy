
export class Util
{
    private constructor() { }


    public static getUrlParameter(pParamName: string): string | null {
        let result: string | null = null;
        let pageUrl: string = decodeURIComponent(window.location.search.substring(1));
        let params: string[] = pageUrl.split('&');
        for (let i: number = 0; i < params.length; i++) {
            let paramKeyValue: string[] = params[i].split('=');
            if (paramKeyValue[0] === pParamName && typeof (paramKeyValue[1]) !== undefined) {
                result = paramKeyValue[1];
                break;
            }
        }
        return result;
    }


    public static buildMap<V>(pObj: Object): Map<string, V> {
        return Object.keys(pObj).reduce((map, key: string) => map.set(key, pObj[key]), new Map<string, V>());
    }
}
