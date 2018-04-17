
export class Util
{
    private constructor() { }


    public static getUrlParameter(pParamName: string): string | null {
        let result: string | null = null;
        const pageUrl: string = decodeURIComponent(window.location.search.substring(1));
        const params: string[] = pageUrl.split('&');
        for (const param of params) {
            const paramKeyValue: string[] = param.split('=');
            if (paramKeyValue[0] === pParamName && typeof (paramKeyValue[1]) !== undefined) {
                result = paramKeyValue[1];
                break;
            }
        }
        return result;
    }


    public static buildMap<V>(pObj: object): Map<string, V> {
        return Object.keys(pObj).reduce((map, key: string) => map.set(key, (<any> pObj)[key]), new Map<string, V>());
    }
}
