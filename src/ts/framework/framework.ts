import * as Mustache from 'mustache';

import { appOptions, runActivityInternal } from '../main';
import { Language } from '../rules/rules';
import * as storage from '../storage/storage';



export enum Page {
    CROSS = 'cross', // virtual page for grouping cross-cutting activities
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

        window.addEventListener('applanguagechanged', (event) => {
            this.languageChanged(event['detail'].oldLang, event['detail'].newLang);
        });

        storage.ensureBuiltInVariants();
        $(() => { // execute after DOM has loaded
            Mustache.parse($('#flagTemplate').html());  // from head.html, present on all pages
            Mustache.parse($('#switchPlayerLinkTemplate').html());
            this.parseTemplates();
            runActivityInternal(Page.CROSS, 'activateLanguage', appOptions.language.toString());
            this.pageLoaded();
            window.setTimeout(function() {
                BaseController.inlineSvgs();
            }, 100);
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

    /**
     * The user has changed the display language.
     * @param pPrevious the previously active language
     * @param pNew the newly selected language
     */
    protected abstract languageChanged(pPrevious: Language, pNew: Language): void;
}



/**
 * A CivBuddy functional command / activity.
 */
export interface Activity {
    execute(pLanguage: Language): void;
}



/**
 * Common superclass of all controllers, providing some common functionality.
 * CHECK This would be better suited to a helper class than a super class.
 */
export class BaseController
{
    protected constructor() { }


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
        const inputField: HTMLInputElement | null = <HTMLInputElement | null>document.getElementById(pInputFieldName);
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


    public static inlineSvgs(): void {
        const svgs: JQuery<HTMLElement> = $('img.inline-svg[src$=".svg"]');
        svgs.each(function() {
            let $img = jQuery(this);
            const imgURL: string = $img.attr('src') as string;
            let attributes = $img.prop('attributes');

            $.get(imgURL, function(data) {
                // Get the SVG tag, ignore the rest
                let $svg = jQuery(data).find('svg');

                // Remove any invalid XML tags
                $svg = $svg.removeAttr('xmlns:a');

                // Loop through IMG attributes and apply on SVG
                $.each(attributes, function() {
                    $svg.attr(this.name, this.value);
                });

                // Replace IMG with SVG
                $img.replaceWith($svg);
            }, 'xml');
        });
    }
}


/**
 * Common superclass of all Navbar controllers, providing some common functionality for navbars.
 * CHECK This would be better suited to a helper class than a super class.
 */
export class BaseNavbarController
    extends BaseController
{
    protected constructor() {
        super();
    }


    public setGameName(pGameName: string): void {
        const elem: JQuery<HTMLElement> = $('#gameName1');
        elem.html(pGameName);
        if (pGameName.length > 17) {
            elem.attr('title', pGameName);
        } else {
            elem.removeAttr('title');
        }
        $('#gameName2').html(pGameName);
    }


    public setVariantName(pVariantName: string): void {
        $('#variantName').html(pVariantName);
    }


    public setOptionDesc(pOptionDesc: string): void {
        $('#variantOptions').html(pOptionDesc);
    }


    /**
     * Update the navbar dropdown which shows the players of this game.
     * @param pCurrentPlayerName name of the current player
     * @param pSituations map from player name to situationKey, including the current player
     */
    public updatePlayersDropdown(pPage: Page, pCurrentPlayerName: string, pSituations: Map<string, string>): void
    {
        $('#currentPlayerName').html(pCurrentPlayerName);

        $('#playerDropdown > a.switch-player-link').remove();

        const parent: JQuery<HTMLElement> = $('#playerDropdown');
        const switchPlayerLinkTemplate: string = $('#switchPlayerLinkTemplate').html();
        const dropdownDivider: JQuery<HTMLElement> = $('#playerDropdown > div.dropdown-divider');
        const playerNames: string[] = Array.from(pSituations.keys());
        if (playerNames.length > 1) {
            this.showElement(dropdownDivider);
            for (let playerName of playerNames.sort().reverse()) {
                const situationId: string = pSituations.get(playerName) as string;
                if (playerName !== pCurrentPlayerName) {
                    const renderedLink: string = Mustache.render(switchPlayerLinkTemplate, {
                        'situationId': situationId,
                        'playerName': playerName,
                        'pageName': pPage.toString().toLowerCase()
                    });
                    parent.prepend(renderedLink);
                }
            }
        }
        else {
            this.hideElement(dropdownDivider);
        }
    }

    public disableLink(pElement: JQuery<HTMLElement>): void {
        pElement.addClass('disabled');
    }

    public enableLink(pElement: JQuery<HTMLElement>): void {
        pElement.removeClass('disabled');
    }
}
