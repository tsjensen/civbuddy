
/**
 * Programmatic access to localized strings.
 */
export class L10nUtil
{
    public static getLocalizedString(pKey: string, pCallback: (v: string[]) => void): void {
        L10nUtil.getLocalizedStringInternal(pKey, pCallback);
    }

    public static getLocalizedStringWithArgs(pKey: string, pArgs: object, pCallback: (v: string[]) => void): void {
        L10nUtil.getLocalizedStringInternal([pKey, pArgs], pCallback);
    }

    private static getLocalizedStringInternal(pKey: any, pCallback: (v: string[]) => void): void {
        if (document.hasOwnProperty('l10n')) {
            const localization = (<any> document)['l10n'];  // tslint:disable-line:no-string-literal
            localization.formatValues(pKey).then(pCallback, pCallback);
        }
    }
}
