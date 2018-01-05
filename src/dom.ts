
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
