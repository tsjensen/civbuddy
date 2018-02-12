
// TODO general utilities
export function getUrlParameter(pParamName: string): string | null {
    let result: string | null = null;
    let pageUrl: string = decodeURIComponent(window.location.search.substring(1));
    let params: string[] = pageUrl.split('&');
    for (let i: number = 0; i < params.length; i++) {
        let paramKeyValue: string[] = params[i].split('=');
        if (paramKeyValue[0] === pParamName && typeof(paramKeyValue[1]) !== undefined) {
            result = paramKeyValue[1];
            break;
        }
    }
    return result;
}

export function buildMap<V>(pObj: Object): Map<string, V> {
    return Object.keys(pObj).reduce((map, key: string) => map.set(key, pObj[key]), new Map<string, V>());
}

export function htmlEncode(pValue: string): string {
    // create a in-memory div, set its inner text (which jQuery automatically encodes)
    // then grab the encoded contents back out.  The div never exists on the page.
    return $('<div/>').text(pValue).html();
}
