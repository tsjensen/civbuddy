const CopyWebpackPlugin = require('copy-webpack-plugin');
const WebpackVersionFilePlugin = require('webpack-version-file-plugin');
const execa = require('execa');
const path = require('path');

const gitHash = execa.sync('git', ['rev-parse', '--short', 'HEAD']).stdout;
const gitNumCommits = Number(execa.sync('git', ['rev-list', 'HEAD', '--count']).stdout);
const gitDirty = execa.sync('git', ['status', '-s', '-uall']).stdout.length > 0;

module.exports = {
    entry: {
        civbuddy: ['babel-polyfill', './build/ts/main.js'],
    },
    output: {
        filename: 'build/dist/js/[name].js',
        libraryTarget: 'var',
        library: 'CivBuddy'
    },
    plugins: [
        new WebpackVersionFilePlugin({
            packageFile: path.join(__dirname, 'package.json'),
            template: path.join(__dirname, 'version.ejs'),
            outputFile: path.join('build/ts/', 'version.json'),
            extras: {
                'githash': gitHash,
                'gitNumCommits': gitNumCommits,
                'timestamp': Date.now(),
                'dirty': gitDirty
            }
        }),
        new CopyWebpackPlugin([
            {from: 'resources', to: 'build/dist', ignore: ['rules/*.json', '**/*.html']},
            {from: 'resources/rules', to: 'build/ts/rules'},
            {from: 'node_modules/bootswatch/dist/darkly', to: 'build/dist/css'},
            {from: 'node_modules/bootstrap/dist', to: 'build/dist', ignore: ['.DS_Store']},
            {from: 'node_modules/open-iconic/font/css/open-iconic-bootstrap.min.css', to: 'build/dist/css'},
            {from: 'node_modules/open-iconic/font/fonts', to: 'build/dist/fonts'},
            {from: 'node_modules/l20n/dist/web/l20n.min.js', to: 'build/dist/js'}
        ], {
            copyUnmodified: false
        })
    ]
}
