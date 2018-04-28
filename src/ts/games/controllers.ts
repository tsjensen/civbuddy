import * as Mustache from 'mustache';

import { BaseController } from '../framework/framework';
import { builtInVariants, RuleOptionJson, RulesJson } from '../rules/rules';
import { AppOptions, GameDao, GameDaoImpl } from '../storage/dao';
import { GlobalOptions } from '../storage/storage';



export class GamesController
    extends BaseController {

    public constructor() {
        super();
    }


    public populateGameList(pGames: GameDao[]): void {
        const appOptions: AppOptions = new GlobalOptions().get();
        const games: GameDao[] = pGames.slice();
        games.sort((a: GameDao, b: GameDao) => a.name.localeCompare(b.name));
        $('#gameList > div').remove();
        for (const game of games) {
            const variant: RulesJson = builtInVariants.get(game.variantKey) as RulesJson;
            const rulesName: string = (variant.displayNames as any)[appOptions.language];
            const optionDesc: string = GameDaoImpl.buildOptionDescriptor(variant, game.options, appOptions.language);
            this.addGame(game.key, game.name, rulesName, optionDesc);
        }
    }

    public addGame(pGameKey: string, pGameName: string, pRulesName: string, pOptionDesc: string): void {
        const htmlTemplate: string = $('#gameTemplate').html();
        const rendered: string = Mustache.render(htmlTemplate, {
            'gameKey': pGameKey,
            'gameName': pGameName,
            'options': pOptionDesc,
            'ruleDisplayName': pRulesName
        });
        $('#gameList').prepend(rendered);
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
    extends BaseController {

    public constructor() {
        super();
    }


    public setupGameNameValidation(pHandler: JQuery.EventHandler<HTMLElement>): void {
        $('#inputGameName').blur(pHandler);
        $('#inputGameName').keyup(pHandler);
    }

    public getGameDtoFromDialog(pNewGameKey: string): GameDao {
        const gameName: string = this.getValueFromInput('inputGameName', 'ERROR - remove me');
        const ruleKey: string = this.getValueFromRadioButtons('rulesRadios', builtInVariants.keys().next().value);
        const variant: RulesJson = builtInVariants.get(ruleKey) as RulesJson;
        const optionValues: object = this.buildOptionValueMap(variant);
        const dto: GameDao = new GameDaoImpl(pNewGameKey, gameName, ruleKey, optionValues, {});
        return dto;
    }

    private buildOptionValueMap(pVariant: RulesJson): object {
        const result: object = {};
        if (pVariant.options.length > 0) {
            for (const option of pVariant.options) {
                let v: string = option.defaultValue;
                if (option.type === 'checkbox') {
                    v = $('#option-' + option.id).is(':checked').toString();
                } else {
                    console.log('ERROR: Unknown option type - ' + option.type);
                }
                (result as any)[option.id] = v;
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
        const appOptions: AppOptions = new GlobalOptions().get();
        $('#rulesRadios > div').remove();
        const htmlTemplate: string = $('#rulesRadioTemplate').html();
        let first: boolean = true;
        for (const [variantId, variant] of builtInVariants.entries()) {
            const rendered: string = Mustache.render(htmlTemplate, {
                'checked': first,
                'displayName': (variant.displayNames as any)[appOptions.language],
                'variantId': variantId
            });
            first = false;
            $('#rulesRadios').append(rendered);
        }
    }


    public chooseVariant(pVariantId: string): void {
        const appOptions: AppOptions = new GlobalOptions().get();
        const variant: RulesJson = builtInVariants.get(pVariantId) as RulesJson;
        const options: RuleOptionJson[] = variant.options;
        $('#rulesOptions > div').remove();

        if (options.length === 0) {
            this.showElement($('#rulesOptions > p'));
            return;
        }
        this.hideElement($('#rulesOptions > p'));

        for (const option of options) {
            if (option.type === 'checkbox') {
                const defaultValue: boolean = option.defaultValue === 'true';
                const htmlTemplate: string = $('#optionCheckBoxTemplate').html();
                const rendered: string = Mustache.render(htmlTemplate, {
                    'checked': defaultValue,
                    'explanation': (option.explanation as any)[appOptions.language],
                    'optionDisplayName': (option.displayNames as any)[appOptions.language],
                    'optionId': option.id
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
