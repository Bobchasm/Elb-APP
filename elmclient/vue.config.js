module.exports = {
  devServer: {
    // 你可以保持前端在8081，或者让它不指定
    // port: 8081, 
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 你的后端地址
        changeOrigin: true,
        pathRewrite: {
          '^/api': '' // 重写路径，将请求中的 '/api' 移除
        }
      }
    }
  }
}