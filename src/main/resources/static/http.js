/**
 * 创建http对象，并创建get/post/delete方法
 * @type {{get: http.get, post: http.post, delete: http.delete}}
 */
var http = {
    get: function (url, params, customHandlerException) {
        //get方法封装时间戳，防止ie浏览器走缓存获取数据
        var times = Date.parse(new Date()) / 1000
        var timesPass = "?_t=" + times
        if (!url.startsWith('http')) {
            url = baseUrl + url + timesPass
        }

        if (customHandlerException) {
            return new Promise(function (resolve, reject) {
                https.get(url, {
                    params: params,
                }).then(function (response) {
                    resolve(response.data
                    )
                }).catch(function (err) {
                    reject(err)
                })
            })
        }
        return new Promise(function (resolve, reject) {
            httpCustom.get(url, {
                params: params,
            }).then(function (response) {
                resolve(response.data
                )
            }).catch(function (err) {
                    reject(err)
                }
            )
        })
    },
    post: function (url, data, customHandlerException) {
        if (!url.startsWith('http')) {
            url = baseUrl + url
        }
        data = JSON.stringify(data)
        if (customHandlerException) {
            return new Promise(function (resolve, reject) {
                https.post(url, data).then(function (response) {
                    resolve(response.data)
                }).catch(function (err) {
                    reject(err)
                })
            })
        }
        return new Promise(function (resolve, reject) {
            httpCustom.post(url, data).then(function (response) {
                resolve(response.data)
            }).catch(function (err) {
                reject(err)
            })
        })
    },
    delete: function (url, data, customHandlerException) {
        if (!url.startsWith('http')) {
            url = baseUrl + url
        }
        data = JSON.stringify(data)
        if (customHandlerException) {
            return new Promise(function (resolve, reject) {
                https.delete(url, data).then(function (response) {
                    resolve(response.data)
                }).catch(function (err) {
                    reject(err)
                })
            })
        }
        return new Promise(function (resolve, reject) {
            httpCustom.delete(url, data).then(function (response) {
                resolve(response.data)
            }).catch(function (err) {
                reject(err)
            })
        })
    }
}

/**
 * 创建https对象和httpCustom对象
 */
var httpCustom = axios.create({
    timeout: 1000 * 30,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }
})
/**
 *对于出现异常时还需要做其他操作，可使用该实例
 */
var https = axios.create({
    timeout: 1000 * 30,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }

})

/**
 * 请求拦截
 */
axios.interceptors.request.use(function (config) {
    //herders配置
    config.headers = {"token": getToken(), "Content-Type": "application/json;charset=UTF-8"}
    //参数配置
    return config
}, function (error) {
    return Promise.reject(error)
})

https.interceptors.request.use(function (config) {
    //herders配置
    config.headers = {"token": getToken(), "Content-Type": "application/json;charset=UTF-8"}
    //参数配置
    return config
}, function (error) {
    return Promise.reject(error)
})


httpCustom.interceptors.request.use(function (config) {
    //herders配置
    config.headers = {"token": getToken(), "Content-Type": "application/json;charset=UTF-8"}
    //参数配置
    return config
}, function (error) {
    return Promise.reject(error)
})


/**
 * 响应拦截
 */
axios.interceptors.response.use(function (response) {
    if (response.data && response.data.code == '401') { // 401, token失效
        console.log('未登录')
        parentJumpTo("/views/login")
    }
    else if (response.data && response.data.code !== 0) {
        vm.$message.error(response.data.msg)
        throw  new EipException(response.data.msg, response.data.code)
    }
    else {
        response.data = response.data.data
    }
    return response
}, function (error) {
    if (error.message && error.message === 'Network Error') {
        vm.$message.error('无法访问')
        return Promise.reject(error)
    }
    return Promise.reject(error)
})

https.interceptors.response.use(function (response) {
    if (response.data && response.data.code == '401') { // 401, token失效
        parentJumpTo("/views/login")
    }
    else if (response.data && response.data.code !== 0) {
        vm.$message.error(response.data.msg)
        throw  new EipException(response.data.msg, response.data.code)
    }
    else {
        response.data = response.data.data
    }
    return response
}, function (error) {
    if (error.message && error.message === 'Network Error') {
        vm.$message.error('无法访问')
        return Promise.reject(error)
    }
    return Promise.reject(error)
})

httpCustom.interceptors.response.use(function (response) {
    if (response.data && response.data.code == '401') { // 401, token失效
        parentJumpTo("/views/login")
    }
    else if (response.data && response.data.code !== 0) {
        vm.$message.error(response.data.msg)
        throw  new EipException(response.data.msg, response.data.code)
    }
    else {
        response.data = response.data.data
    }
    return response
}, function (error) {
    if (error.message && error.message === 'Network Error') {
        vm.$message.error('无法访问')
        return Promise.reject(error)
    }
    return Promise.reject(error)
})

/**
 *接口方法封装
 */
var params = {}
var customHandlerException = false

/**
 * get请求
 */
function httpGet(url, params, customHandlerException) {
    if (!url.startsWith('http')) {
        url = baseUrl + url
    }
    if (customHandlerException) {
        return new Promise(function (resolve, reject) {
            https.get(url, {
                params: params,
            }).then(function (response) {
                resolve(response.data
                )
            }).catch(function (err) {
                reject(err)
            })
        })
    }
    return new Promise(function (resolve, reject) {
        httpCustom.get(url, {
            params: params,
        }).then(function (response) {
            resolve(response.data
            )
        }).catch(function (err) {
                reject(err)
            }
        )
    })
}


/**
 * 包装uri地址
 * @param url
 * @returns {*}
 */
function wrapUrl(url) {
    if (!url.startsWith('http')) {
        url = baseUrl + url
    }
    return url
}

/**
 * 页面跳转至前端目录内
 */
function jumpTo(url) {
    window.document.location.href = httpUrl + url
}

/**
 * 父页面跳转
 */
function parentJumpTo(url) {
    parent.window.document.location.href = httpUrl + url

}