import * as storage from './storage/storage';
import { Language } from './rules/rules';
import { appOptions, activateLanguage } from './main';


export enum Page {
    GAMES = 'games',
    PLAYERS = 'players',
    CARDS = 'cards',
    FUNDS = 'funds'
}

export interface PageContext {
    // tagging interface
}


/**
 * Common superclass of the page initializer classes.
 */
export abstract class AbstractPageInitializer<C extends PageContext>
{
    /** the CSS selector of the page's modal, including the starting hash (#) */
    protected readonly modalId?: string = undefined;

    constructor(protected readonly page: Page, protected readonly pageContext: C, pModalId?: string) {
        if (typeof(pModalId) === 'string') {
            this.modalId = pModalId.startsWith('#') ? pModalId : '#' +  pModalId;
        }
    }

    /** Perform page initialization. **DO NOT OVERRIDE THIS METHOD** */
    public /*final*/ init(): void {
        if (typeof(this.modalId) === 'string') {
            $(document).on('shown.bs.modal', this.modalId, () => {
                this.modalDisplayed();
            });
        }

        window.addEventListener('applanguagechanged', () => {
            this.languageChanged();
        });

        storage.ensureBuiltInVariants();
        $(() => { // execute after DOM has loaded
            this.parseTemplates();
            activateLanguage(appOptions.language);
            this.pageLoaded();
        });
    }

    public getInitialPageContext(): C {
        return this.pageContext;
    }

    /** The page's modal was displayed, fade-in animation completed. */
    protected modalDisplayed(): void {
        // do nothing - implementation in subclass
    }

    /** Parse all the Mustache templates required by the page. */
    protected abstract parseTemplates(): void;

    /** The page was loaded and the DOM is ready. */
    protected abstract pageLoaded(): void;

    /** The user has changed the display language. */
    protected abstract languageChanged(): void;
}


/**
 * A CivBuddy functional command / activity.
 * @template R return type of the command execution
 */
export interface Activity<R> {
    execute(pLanguage: Language): R;
}


export abstract class AbstractController
{
    protected getValueFromInput(pInputFieldName: string, pDefault: string): string {
        let result: string = pDefault;
        const v: string | number | string[] | undefined = $('#' + pInputFieldName).val();
        if (typeof(v) === 'string' && v.trim().length > 0) {
            result = v.trim();
        }
        return result;
    }
    
    protected getValueFromRadioButtons(pRadioGroupName: string, pDefault: string): string {
        let result: string = pDefault;
        const checkedRadioField: JQuery<HTMLElement> = $('#' + pRadioGroupName + ' input:radio:checked');
        const v: string | number | string[] | undefined = checkedRadioField.val();
        if (typeof(v) === 'string' && v.length > 0) {
            result = v;
        }
        return result;
    }
    
    protected focusAndPositionCursor(pInputFieldName: string): void {
        const inputField: HTMLInputElement | null = <HTMLInputElement>document.getElementById(pInputFieldName);
        if (inputField !== null) {
            inputField.focus();
            inputField.selectionStart = inputField.selectionEnd = inputField.value.length;
        }
    }


    protected setNameIsInvalid(pModalId: string, pInputId: string, pI10nIdPart: string, pIsInvalid: boolean,
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


    public showElement(pElement: JQuery<HTMLElement>): void {
        pElement.removeClass('d-none');
    }

    public hideElement(pElement: JQuery<HTMLElement>): void {
        pElement.addClass('d-none');
    }
}
