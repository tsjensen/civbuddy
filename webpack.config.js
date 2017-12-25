const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require("path");

module.exports = {
    entry: {
        civbuddy: ["./build/ts/app.js", "./build/ts/lifecycle.js"],
    },
    output: {
        filename: "build/dist/js/[name].js",
        libraryTarget: 'var',
        library: 'CivBuddy'
    },
    plugins: [
        new CopyWebpackPlugin([
            {from: 'resources', to: 'build/dist', ignore: ['rules/*.json']},
            {from: 'resources/rules', to: 'build/ts/rules'},
            {from: 'vendor/bootswatch-darkly-v4.0.0-beta.2', to: 'build/dist'},
            {from: 'vendor/bootstrap-4.0.0-beta.2', to: 'build/dist'},
            {from: 'vendor/openiconic-1.1.0', to: 'build/dist'}
        ], {
            copyUnmodified: false
        })
    ]
}