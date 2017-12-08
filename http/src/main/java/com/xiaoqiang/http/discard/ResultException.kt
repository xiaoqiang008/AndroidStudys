package com.xiaoqiang.http.discard

/**
 * Created by admin3 on 2017/12/4.
 */
class ResultException(var errCode: Int, var msg: String) : RuntimeException(msg) {

    val errCodes = 0

}