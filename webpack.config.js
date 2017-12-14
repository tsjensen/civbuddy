const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    entry: "./build/ts/app.js",
    output: {
        filename: "build/dist/js/civbuddy.js"
    },
    plugins: [
        new CopyWebpackPlugin([
            {from: 'resources', to: 'build/dist'},
            {from: 'vendor/*', to: 'build/dist'}
        ], {
            copyUnmodified: false
        })
    ]
}