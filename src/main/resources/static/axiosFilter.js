axios.interceptors.request.use(function (config) {
    //herders配置
    var token = window.location.search.substring(7)
    config.headers = {"token": token, "Content-Type": "application/json;charset=UTF-8"}
    //参数配置
    return config
}, function (error) {
    return Promise.reject(error)
})

/**
 * 响应拦截
 */
axios.interceptors.response.use(function (response) {
    if (response.data && response.data.code == '401') {
        vm.$confirm('请您先登录千行门户，从千行门户【代码生成】中进入, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            window.location.href = response.data.data
        }).catch(() => {
        })
    }
    return response
}, function (error) {
    return Promise.reject(error)
})
