const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require("path");

module.exports = {
    entry: {
        civbuddy: ["babel-polyfill", "./build/ts/app.js", "./build/ts/lifecycle.js"],
    },
    output: {
        filename: "build/dist/js/[name].js",
        libraryTarget: 'var',
        library: 'CivBuddy'
    },
    plugins: [
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