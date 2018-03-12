import * as Mustache from 'mustache';

import { BaseController } from '../framework/framework';
import { appOptions } from '../main';
import { builtInVariants, RuleOptionJson, RulesJson } from '../rules/rules';
import { GameDao, GameDaoImpl } from '../storage/dao';



export class GamesController
    extends BaseController
{
    public constructor() {
        super();
    }


    public populateGameList(pGames: GameDao[]): void {
        $('#gameList > div').remove();
        for (let game of pGames) {
            const variant: RulesJson = builtInVariants[game.variantKey];
            const rulesName: string = variant.displayNames[appOptions.language];
            const optionDesc: string = GameDaoImpl.buildOptionDescriptor(variant, game.options, appOptions.language);
            this.addGame(game.key, game.name, rulesName, optionDesc);
        }
    }

    public addGame(pGameKey: string, pGameName: string, pRulesName: string, pOptionDesc: string): void {
        let htmlTemplate: string = $('#gameTemplate').html();
        let rendered: string = Mustache.render(htmlTemplate, {
            'ruleDisplayName': pRulesName,
            'gameKey': pGameKey,
            'gameName': pGameName,
            'options': pOptionDesc
        });
        $('#gameList').append(rendered);
    }

    public removeGame(pGameKey: string): void {
        $('#' + pGameKey).remove();
    }

    public setAppVersion(pAppVersion: string, pDirty: boolean): void {
        const elem: JQuery<HTMLElement> = $('#appVersion');
        elem.html(pAppVersion);
        if (pDirty) {
            elem.removeClass('text-muted');
            elem.addClass('text-warning');
        } else {
            elem.addClass('text-muted');
            elem.removeClass('text-warning');
        }
    }
}


export class NewGameModalController
    extends BaseController
{
    public constructor() {
        super();
    }


    public setupGameNameValidation(pHandler: JQuery.EventHandler<HTMLElement>): void {
        $('#inputGameName').blur(pHandler);
        $('#inputGameName').keyup(pHandler);
    }

    public getGameDtoFromDialog(pNewGameKey: string): GameDao {
        const gameName: string = this.getValueFromInput('inputGameName', 'ERROR - remove me');
        const ruleKey: string = this.getValueFromRadioButtons('rulesRadios', builtInVariants.keys[0]);
        const variant: RulesJson = builtInVariants[ruleKey];
        const optionValues: Object = this.buildOptionValueMap(variant);
        const dto: GameDao = new GameDaoImpl(pNewGameKey, gameName, ruleKey, optionValues, {});
        return dto;
    }

    private buildOptionValueMap(pVariant: RulesJson): Object {
        let result: Object = {};
        if (pVariant.options !== null && pVariant.options.length > 0) {
            for (let option of pVariant.options) {
                let v: string = option.defaultValue;
                if (option.type === 'checkbox') {
                    v = $('#option-' + option.id).is(':checked').toString();
                } else {
                    console.log('ERROR: Unknown option type - ' + option.type);
                }
                result[option.id] = v;
            }
        }
        return result;
    }


    public setGameName(pGameName: string): void {
        const inputField: HTMLElement = document.getElementById('inputGameName') as HTMLElement;
        inputField.setAttribute('value', pGameName);
    }


    public hideModal(): void {
        $('#newGameModal').modal('hide');
    }


    public addVariantsToModal(): void {
        $('#rulesRadios > div').remove();
        let htmlTemplate: string = $('#rulesRadioTemplate').html();
        let first: boolean = true;
        for (let variantId in builtInVariants) {
            let variant: RulesJson = builtInVariants[variantId];
            let rendered: string = Mustache.render(htmlTemplate, {
                'variantId': variantId,
                'checked': first,
                'displayName': variant.displayNames[appOptions.language]
            });
            first = false;
            $('#rulesRadios').append(rendered);
        }
    }


    public chooseVariant(pVariantId: string): void {
        const variant: RulesJson = builtInVariants[pVariantId];
        const options: RuleOptionJson[] = variant.options;
        $('#rulesOptions > div').remove();

        if (options.length === 0) {
            this.showElement($('#rulesOptions > p'));
            return;
        }
        this.hideElement($('#rulesOptions > p'));

        for (let option of options) {
            if (option.type === 'checkbox') {
                const defaultValue: boolean = option.defaultValue === 'true';
                let htmlTemplate: string = $('#optionCheckBoxTemplate').html();
                let rendered: string = Mustache.render(htmlTemplate, {
                    'optionId': option.id,
                    'optionDisplayName': option.displayNames[appOptions.language],
                    'checked': defaultValue,
                    'explanation': option.explanation[appOptions.language]
                });
                $('#rulesOptions').append(rendered);
            } else {
                console.log('ERROR: Unknown option type - ' + option.type);
            }
        }
    }


    public getGameNameFromInput(): string {
        return this.getValueFromInput('inputGameName', '');
    }

    public focusCursorInModal(): void {
        this.focusAndPositionCursor('inputGameName');
    }

    public displayNamingError(pIsInvalid: boolean, pNoNameGiven: boolean): void {
        this.setNameIsInvalid('newGameModal', 'inputGameName', 'games-newModal-label-', pIsInvalid, pNoNameGiven);
    }
}
