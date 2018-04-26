import * as Mustache from 'mustache';

import { appOptions, buttonClick, runActivityInternal } from '../main';
import { Language } from '../rules/rules';
import * as storage from '../storage/storage';



export enum Page {
    CROSS = 'cross', // virtual page for grouping cross-cutting activities
    GAMES = 'games',
    PLAYERS = 'players',
    CARDS = 'cards',
    FUNDS = 'funds',
    ERROR = 'error'
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
        if (typeof (pModalId) === 'string') {
            this.modalId = pModalId.startsWith('#') ? pModalId : '#' + pModalId;
        }
    }

    /** Perform page initialization. **DO NOT OVERRIDE THIS METHOD** */
    public /*final*/ init(): void {
        if (typeof (this.modalId) === 'string') {
            $(document).on('shown.bs.modal', this.modalId, () => {
                this.modalDisplayed();
            });
        }

        window.addEventListener('applanguagechanged', (event) => {
            // tslint:disable-next-line:no-string-literal
            this.languageChanged((event as any)['detail'].oldLang, (event as any)['detail'].newLang);
            BaseController.addButtonClickHandlers('#otherLanguageFlags');
            $('#right-dropdown > a.dropdown-toggle').dropdown('toggle');   // close dropdown
            window.setTimeout(BaseController.adjustLion, 100);
        });

        storage.ensureBuiltInVariants();
        $(() => { // execute after DOM has loaded
            Mustache.parse($('#flagTemplate').html());  // from head.html, present on all pages
            Mustache.parse($('#switchPlayerLinkTemplate').html());
            this.parseTemplates();
            runActivityInternal(Page.CROSS, 'activateLanguage', appOptions.language.toString());
            this.pageLoaded();
            window.setTimeout(() => {
                BaseController.addButtonClickHandlers();
                BaseController.inlineSvgs();
            }, 100);
        });

        $(window).resize(BaseController.adjustLion);
        window.addEventListener('cardListChanged', BaseController.adjustLion);
        $(window).on('load', () => window.setTimeout(BaseController.adjustLion, 500));
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
export class BaseController {
    protected constructor() { }


    public static inlineSvgs(): void {
        const svgs: JQuery<HTMLElement> = $('img.inline-svg[src$=".svg"]');
        svgs.each(function () {
            const $img = jQuery(this);
            const imgURL: string = $img.attr('src') as string;
            const attributes = $img.prop('attributes');

            $.get(imgURL, function (data) {  // tslint:disable-line:only-arrow-functions
                let $svg = jQuery(data).find('svg');
                $svg = $svg.removeAttr('xmlns:a');

                // Loop through IMG attributes and apply on SVG
                $.each(attributes, function () {
                    $svg.attr(this.name, this.value);
                });
                $img.replaceWith($svg);
            }, 'xml');
        });
    }


    /**
     * For all HTML elements that have a 'civbuddy-button' attribute, parse its value and add a click handler to the
     * element which invokes the CivBuddy activity specified by the 'civbuddy-button' attribute. Value syntax is
     * 'pageName, commandName, ...args'.
     * @param pSelector prepended to `[civbuddy-button]` as parent if present, to constrain the search scope
     */
    public static addButtonClickHandlers(pSelector?: string): void {
        const buttons: JQuery<HTMLElement> = typeof (pSelector) === 'undefined' ?
            $('[civbuddy-button]') : $(pSelector + ' [civbuddy-button]');
        buttons.each(function () {
            const button = jQuery(this);
            const argsStr: string = button.attr('civbuddy-button') as string;
            const args: string[] = argsStr.split(/\s*,\s*/);
            const page: Page = Page[args[0].toUpperCase() as keyof typeof Page];
            const command: string = args[1];
            const params: string[] = args.slice(2);
            button.click(() => {
                buttonClick(button[0], page, command, ...params);
                return false;
            });
        });
    }



    /**
     * Resize the background lion when its available space changes. Hide it when there is not enough space.
     */
    public static adjustLion(): void {
        const rowElem: JQuery<HTMLElement> = $('div.lion-row.lion-row-allowed');
        if (rowElem.length > 0) {
            const vSpacePx: number = BaseController.getLionSpace();
            if (vSpacePx > 0) {
                const viewportWidth: number = $(window).width() as number;
                const lionMaxHeight: number = Math.trunc(viewportWidth / 2);
                const lionHeight: number = vSpacePx > lionMaxHeight ? lionMaxHeight : vSpacePx;
                const margin: number = Math.trunc((vSpacePx - lionHeight) / 2);
                const lionImg: JQuery<HTMLElement> = $('div.lion-row img');
                lionImg.height(lionHeight);
                lionImg.css('margin-top', margin + 'px');
                rowElem.removeClass('d-none');
            } else {
                rowElem.addClass('d-none');
            }
        }
    }

    /**
     * The number of vertical pixels available to the background lion
     */
    private static getLionSpace(): number {
        const viewportHeight: number = $(window).height() as number;
        const viewportWidth: number = $(window).width() as number;
        const upperBound: number = BaseController.getUpperBound();
        const footer: JQuery<HTMLElement> = $('footer');
        const lowerBound: number = footer.length ? footer.get(0).getBoundingClientRect().top : viewportHeight;
        let result: number = Math.max(lowerBound - upperBound - 30, 0);
        if (result < (viewportWidth < 700 ? 1 : 2) * 100) {
            // we have less than 100px of space (200px if the viewport is wider than 700px), which is too little
            result = 0;
        }
        return result;
    }

    private static getUpperBound(): number {
        let row: JQuery<HTMLElement> = $('div.lion-row').prev(':not(.d-none)');
        if (row.length === 0) {
            row = $('div.lion-row').prev().prev();
        }
        const fenceBounds = row.get(0).getBoundingClientRect();
        return fenceBounds.bottom;
    }

    public setLionAllowed(pAllowed: boolean): void {
        const rowElem: JQuery<HTMLElement> = $('div.lion-row');
        if (pAllowed) {
            rowElem.addClass('lion-row-allowed');
        } else {
            rowElem.removeClass('lion-row-allowed');
            this.hideElement(rowElem);
        }
    }


    protected getValueFromInput(pInputFieldName: string, pDefault: string): string {
        let result: string = pDefault;
        const v: string | number | string[] | undefined = $('#' + pInputFieldName).val();
        if (typeof (v) === 'string' && v.trim().length > 0) {
            result = v.trim();
        }
        return result;
    }

    protected getValueFromRadioButtons(pRadioGroupName: string, pDefault: string): string {
        let result: string = pDefault;
        const checkedRadioField: JQuery<HTMLElement> = $('#' + pRadioGroupName + ' input:radio:checked');
        const v: string | number | string[] | undefined = checkedRadioField.val();
        if (typeof (v) === 'string' && v.length > 0) {
            result = v;
        }
        return result;
    }

    protected focusAndPositionCursor(pInputFieldName: string): void {
        const inputField: HTMLInputElement | null = document.getElementById(pInputFieldName) as HTMLInputElement | null;
        if (inputField !== null) {
            inputField.focus();
            inputField.selectionStart = inputField.selectionEnd = inputField.value.length;
        }
    }


    protected setNameIsInvalid(pModalId: string, pInputId: string, pI10nIdPart: string, pIsInvalid: boolean,
        pNoNameGiven: boolean): void {
        if (pIsInvalid) {
            $('#' + pInputId).addClass('is-invalid');
            $('#' + pModalId + ' div.modal-footer > button.btn-success').addClass('disabled');
            const errorMsg: JQuery<HTMLElement> = $('#' + pInputId + ' ~ div.invalid-feedback');
            errorMsg.attr('data-l10n-id', pI10nIdPart + (pNoNameGiven ? 'empty' : 'invalidName'));
            errorMsg.removeClass('d-none');
            errorMsg.parent().addClass('has-danger');
        }
        else {
            $('#' + pInputId).removeClass('is-invalid');
            $('#' + pModalId + ' div.modal-footer > button.btn-success').removeClass('disabled');
            const errorMsg: JQuery<HTMLElement> = $('#' + pInputId + ' ~ div.invalid-feedback');
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


    /**
     * Finds those anchors which link to something with query parameters, and adds a click handler to them, so that
     * the page transition is performed in JavaScript. This prevents home screen apps on iOS to switch to Safari.
     * From https://stackoverflow.com/a/10813468/1005481
     */
    public addJsHandlerToAnchors(): void {
        $('a.add-situation-id,a.add-game-id').click(function () {
            window.location.href = String($(this).attr('href'));
            return false;
        });
    }
}


/**
 * Common superclass of all Navbar controllers, providing some common functionality for navbars.
 * CHECK This would be better suited to a helper class than a super class.
 */
export class BaseNavbarController
    extends BaseController {
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
        const elem: JQuery<HTMLElement> = $('#variantOptions');
        elem.html(pOptionDesc);
        if (pOptionDesc === '--') {
            this.hideElement(elem);
        } else {
            this.showElement(elem);
        }
    }


    /**
     * Update the navbar dropdown which shows the players of this game.
     * @param pCurrentPlayerName name of the current player
     * @param pSituations map from player name to situationKey, including the current player
     */
    public updatePlayersDropdown(pPage: Page, pCurrentPlayerName: string, pSituations: Map<string, string>): void {
        $('#currentPlayerName').html(pCurrentPlayerName);

        $('#playerDropdown > a.switch-player-link').remove();

        const parent: JQuery<HTMLElement> = $('#playerDropdown');
        const switchPlayerLinkTemplate: string = $('#switchPlayerLinkTemplate').html();
        const dropdownDivider: JQuery<HTMLElement> = $('#playerDropdown > div.dropdown-divider');
        const playerNames: string[] = Array.from(pSituations.keys());
        if (playerNames.length > 1) {
            this.showElement(dropdownDivider);
            for (const playerName of playerNames.sort().reverse()) {
                const situationId: string = pSituations.get(playerName) as string;
                if (playerName !== pCurrentPlayerName) {
                    const renderedLink: string = Mustache.render(switchPlayerLinkTemplate, {
                        'pageName': pPage.toString().toLowerCase(),
                        'playerName': playerName,
                        'situationId': situationId
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
