// tslint:disable:no-implicit-dependencies
import 'mocha';

import { expect } from 'chai';

import { Calculator } from '../../../src/ts/cards/calc';
import { Language } from '../../../src/ts/rules/rules';




describe('Powerset function', () => {
    const underTest: Calculator = new Calculator(undefined, Language.EN);

    it('should compute the powerset', () => {
        const result: string[][] = underTest['powerSet'](['foo', 'bar']);  // tslint:disable-line:no-string-literal
        expect(result).to.deep.equal([[], ['foo'], ['bar'], ['foo', 'bar']]);
    });

});
