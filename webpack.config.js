var path = require("path");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
module.exports = {
    entry: "./src/main/js/app.jsx",
    output: {
        path: __dirname+"/src/main/resources/static",
        filename: "bundle.js"
    },
    module: {
        loaders: [
            {
                test: [/\.scss$/,/\.css$/],
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader', 'sass-loader']
                })
            },
            {
                test: [/\.jsx$/,/\.js$/],
                loader: 'babel-loader',
                exclude: /node_modules/,
                query:
                    {
                        presets:['es2015','react']
                    }
            }
        ]
    },
    plugins: [
        new ExtractTextPlugin('main.css')
    ]
};
