package com.xiaoqiang.http.exception

/**
 * Created by admin3 on 2017/12/4.
 */
class ApiException : Exception {
    /*错误码*/
    @get:CodeException.CodeEp
    var codes: Long = 0
    /*显示的信息*/
    var displayMessage: String? = null

    constructor(e: Throwable) : super(e) {}

    constructor(cause: Throwable, @CodeException.CodeEp code: Long, showMsg: String) : super(showMsg, cause) {
        codes = code
        displayMessage = showMsg
    }
}