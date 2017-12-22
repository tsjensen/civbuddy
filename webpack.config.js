const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    entry: "./build/ts/app.js",
    output: {
        filename: "build/dist/js/civbuddy.js"
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