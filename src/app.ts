import * as jsonOriginal from './rules/original.json';
import * as jsonOriginalWe from './rules/original_we.json';
import * as jsonAdvanced from './rules/advanced.json';
import { RulesJson, CardJson, Language } from './model/rules';

export function hello() {
    let obj2: RulesJson = <any>jsonOriginal;
    let law: CardJson | undefined = obj2.cards['Law'];
    if (law !== undefined) {
        console.log("We have cards: " + law.attributes[Language.EN]);
    } else {
        console.log("barf");
    }
    //const word = Object.keys((<any>jsonOriginal).cards).length;
    console.log("world");
}

/*
// activate first input field after showing modal
$('#newGameModal').on('shown.bs.modal', function () {
    setTimeout(function (){
        $('#inputGameName').focus();
    }, 300);  // was: 1000 TODO
})
*/

hello();
