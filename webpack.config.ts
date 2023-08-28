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

import CopyWebpackPlugin from 'copy-webpack-plugin';
import * as execa from 'execa';
import * as path from 'path';
import * as webpack from 'webpack';

const VersionFile = require('webpack-version-file-plugin');

const gitHash: string = execa.sync('git', ['rev-parse', '--short', 'HEAD']).stdout;
const gitNumCommits: number = Number(execa.sync('git', ['rev-list', 'HEAD', '--count']).stdout);
const gitDirty: boolean = execa.sync('git', ['status', '-s', '-uall']).stdout.length > 0;

const config: webpack.Configuration = {
    entry: './src/ts/main.ts',
    output: {
        path: path.resolve(__dirname, 'build/dist'),
        filename: 'js/civbuddy.js',
        libraryTarget: 'var',
        library: 'CivBuddy'
    },
    devtool: 'source-map',
    mode: 'development',
    module: {
        rules: [
            { test: /\.ts$/, use: 'ts-loader' }
        ]
    },
    plugins: [
        new VersionFile({
            packageFile: path.resolve(__dirname, 'package.json'),
            template: path.resolve(__dirname, 'version.ejs'),
            outputFile: path.resolve(__dirname, 'src/ts/framework/version.json'),
            extras: {
                'githash': gitHash,
                'gitNumCommits': gitNumCommits,
                'timestamp': Date.now(),
                'dirty': gitDirty
            }
        }),
        new CopyWebpackPlugin([
            { from: 'resources', ignore: ['**/*.html'] },
            { from: 'node_modules/bootstrap/dist/js/bootstrap.min.js', to: 'js' },
            { from: 'node_modules/open-iconic/font/css/open-iconic-bootstrap.min.css', to: 'css' },
            { from: 'node_modules/open-iconic/font/fonts', to: 'fonts' },
            { from: 'build/l20n/l20n.min.js', to: 'js' }
        ], {
                copyUnmodified: false
            })
    ],
    resolve: {
        extensions: ['.ts', '.js']
    }
};

export default config;
