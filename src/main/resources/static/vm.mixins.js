/**
 * vue变量名称
 * @type {{}}
 */
var vm = {}
/**
 * 以mixins混入方式引入vm变量，采用钩子函数方式定义
 */
var vmMixins = {
    data: function () {
        return {}
    },
    beforeCreate: function () {
        vm = this
    }
}