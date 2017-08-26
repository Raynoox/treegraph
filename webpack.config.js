var path = require("path");

module.exports = {
    entry: "./src/main/js/app.jsx",
    output: {
        path: __dirname+"/src/main/resources/static",
        filename: "bundle.js"
    },
    module: {
        loaders: [
            { test: /\.css$/, loader: "style!css" },
            {
                test: /\.jsx$/,
                loader: 'babel-loader',
                exclude: /node_modules/,
                query:
                    {
                        presets:['es2015','react']
                    }
            }
        ]
    }
};
