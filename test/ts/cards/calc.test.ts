/*
 * CivBuddy - A calculator app for players of Francis Tresham's original Civilization board game (1980)
 * Copyright (C) 2012-2023 Thomas Jensen
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License, version 3, as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

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
