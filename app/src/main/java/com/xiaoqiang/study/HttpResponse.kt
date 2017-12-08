package com.xiaoqiang.study


class HttpResponse<T> {
    var code: Int = 0//正确错误码
    var describe = ""//错误描述
    var result: T? = null//返回json类

    val isSuccess: Boolean
        get() = if (code == 1000) {
            true
        } else {
            false
        }

    override fun toString(): String {
        return if (result != null) {
            "HttpResponse{" +
                    "code=" + code +
                    ", describe='" + describe + '\'' +
                    "result=" + result!!.toString() +
                    '}'
        } else {
            "HttpResponse{" +
                    "code=" + code +
                    ", describe='" + describe + '\'' +
                    '}'
        }
    }
}
