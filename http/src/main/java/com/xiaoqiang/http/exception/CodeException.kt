package com.xiaoqiang.http.exception

//import android.support.annotation.IntDef
//import java.lang.annotation.RetentionPolicy

/**
 * Created by admin3 on 2017/12/4.
 */
class CodeException {

    companion object{
        /*网络错误*/
        const val NETWORD_ERROR : Long= 1001L
        /*http连接_错误*/
        const val HTTP_ERROR = 1002L
        /*连接超时*/
        const val TIMEOUT_ERROR = 1003L
        /*fastjson错误*/
        const val JSON_ERROR = 1004L
        /*未知错误*/
        const val UNKNOWN_ERROR = 1005L
        /*运行时异常-包含自定义异常*/
        const val RUNTIME_ERROR = 1006L
        /*无法解析该域名*/
        const val UNKOWNHOST_ERROR = 1007L
    }
//    @IntDef(NETWORD_ERROR, HTTP_ERROR, RUNTIME_ERROR, UNKNOWN_ERROR, JSON_ERROR, UNKOWNHOST_ERROR)
    annotation class CodeEp
}