const WebpackObfuscator = require('webpack-obfuscator');

module.exports = {
  devServer: {
    port: 80
  },
  configureWebpack: config => {
    if (process.env.NODE_ENV === 'production') {
      config.devtool = false;
      config.plugins = config.plugins || [];
      config.plugins.push(
        new WebpackObfuscator(
          {
            rotateStringArray: true,
            stringArray: true,
            stringArrayEncoding: ['base64'],
            stringArrayThreshold: 0.75,
            splitStrings: true,
            splitStringsChunkLength: 8,
            selfDefending: true,
            simplify: true,
            compact: true,
            controlFlowFlattening: false,
            deadCodeInjection: false,
            disableConsoleOutput: false
          },
          ['chunk-vendors*.js']
        )
      );
    }
  }
};