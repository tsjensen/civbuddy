import * as CopyWebpackPlugin from 'copy-webpack-plugin';
import * as execa from 'execa';
import * as path from 'path';
import * as webpack from 'webpack';
import * as WebpackVersionFilePlugin from 'webpack-version-file-plugin';

const gitHash: string = execa.sync('git', ['rev-parse', '--short', 'HEAD']).stdout;
const gitNumCommits: number = Number(execa.sync('git', ['rev-list', 'HEAD', '--count']).stdout);
const gitDirty: boolean = execa.sync('git', ['status', '-s', '-uall']).stdout.length > 0;

const config: webpack.Configuration = {
    entry: './src/ts/main.ts',
    output: {
        filename: 'build/dist/js/civbuddy.js',
        libraryTarget: 'var',
        library: 'CivBuddy'
    },
    devtool: 'source-map',
    module: {
        loaders: [
            { test: /\.ts$/, use: 'ts-loader' }
        ]
    },
    plugins: [
        new WebpackVersionFilePlugin({
            packageFile: path.resolve(__dirname, 'package.json'),
            template: path.resolve(__dirname, 'version.ejs'),
            outputFile: path.join('build/', 'version.json'),
            extras: {
                'githash': gitHash,
                'gitNumCommits': gitNumCommits,
                'timestamp': Date.now(),
                'dirty': gitDirty
            }
        }),
        new CopyWebpackPlugin([
            {from: 'resources', to: 'build/dist', ignore: ['rules/*.json', '**/*.html']},
            {from: 'node_modules/bootstrap/dist/js', to: 'build/dist/js', ignore: ['.DS_Store']},
            {from: 'node_modules/open-iconic/font/css/open-iconic-bootstrap.min.css', to: 'build/dist/css'},
            {from: 'node_modules/open-iconic/font/fonts', to: 'build/dist/fonts'},
            {from: 'node_modules/l20n/dist/web/l20n.min.js', to: 'build/dist/js'}
        ], {
            copyUnmodified: false
        })
    ],
    resolve: {
        extensions: ['.ts', '.js']
    }
};

export default config;
