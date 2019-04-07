import 'mocha';

import { expect } from 'chai';

import { Calculator } from '../../../src/ts/cards/calc';
import { Language } from '../../../src/ts/rules/rules';



describe('Powerset function', () => {
    const underTest: Calculator = new Calculator(undefined as any, Language.EN);

    it('should handle empty input arrays', () => {
        const result: string[][] = underTest['powerSet']([]);
        expect(result).to.deep.equal([[]]);
    });

    it('should handle singleton arrays', () => {
        const result: string[][] = underTest['powerSet'](['foo']);
        expect(result).to.deep.equal([[], ['foo']]);
    });

    it('should compute the powerset', () => {
        const result: string[][] = underTest['powerSet'](['foo', 'bar']);
        expect(result).to.deep.equal([[], ['foo'], ['bar'], ['foo', 'bar']]);
    });

    it('should place the longest item last, and the shortest first', () => {
        // tslint:disable-next-line:no-string-literal
        const result: string[][] = underTest['powerSet'](['foo', 'bar', 'baz']);
        expect(result[0]).to.have.lengthOf(0);
        expect(result[result.length - 1]).to.have.lengthOf(3);
    });
});
