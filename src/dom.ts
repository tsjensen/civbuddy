
export function getValueFromInput(pInputFieldName: string, pDefault: string): string {
    let result: string = pDefault;
    const v: string | number | string[] | undefined = $('#' + pInputFieldName).val();
    if (typeof(v) === 'string' && v.trim().length > 0) {
        result = v.trim();
    }
    return result;
}

export function getValueFromRadioButtons(pRadioGroupName: string, pDefault: string): string {
    let result: string = pDefault;
    const checkedRadioField: JQuery<HTMLElement> = $('#' + pRadioGroupName + ' input:radio:checked');
    const v: string | number | string[] | undefined = checkedRadioField.val();
    if (typeof(v) === 'string' && v.length > 0) {
        result = v;
    }
    return result;
}

export function focusAndPositionCursor(pInputFieldName: string): void {
    const inputField: HTMLInputElement | null = <HTMLInputElement>document.getElementById(pInputFieldName);
    if (inputField !== null) {
        inputField.focus();
        inputField.selectionStart = inputField.selectionEnd = inputField.value.length;
    }
}


export function setNameIsInvalid(pModalId: string, pInputId: string, pI10nIdPart: string, pIsInvalid: boolean,
     pNoNameGiven: boolean): void
{
    if (pIsInvalid) {
        $('#' + pInputId).addClass('is-invalid');
        $('#' + pModalId + ' div.modal-footer > button.btn-success').addClass('disabled');
        let errorMsg: JQuery<HTMLElement> = $('#' + pInputId + ' ~ div.invalid-feedback');
        errorMsg.attr('data-l10n-id', pI10nIdPart + (pNoNameGiven ? 'empty' : 'invalidName'));
        errorMsg.removeClass('d-none');
        errorMsg.parent().addClass('has-danger');
    }
    else {
        $('#' + pInputId).removeClass('is-invalid');
        $('#' + pModalId + ' div.modal-footer > button.btn-success').removeClass('disabled');
        let errorMsg: JQuery<HTMLElement> = $('#' + pInputId + ' ~ div.invalid-feedback');
        errorMsg.addClass('d-none');
        errorMsg.parent().removeClass('has-danger');
    }
}

export function showElement(pElement: JQuery<HTMLElement>): void {
    pElement.removeClass('d-none');
}

export function hideElement(pElement: JQuery<HTMLElement>): void {
    pElement.addClass('d-none');
}


// TODO move this somewhere else, as it has nothing to do with DOM handling
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
