
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
