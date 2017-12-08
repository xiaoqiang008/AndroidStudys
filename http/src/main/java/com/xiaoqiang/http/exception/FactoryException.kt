package com.xiaoqiang.http.exception

import android.net.ParseException
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import okio.Timeout
import org.json.JSONException
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * Created by admin3 on 2017/12/4.
 */
object FactoryException {
    private val HttpException_MSG = "网络错误"
    private val ConnectException_MSG = "连接失败"
    private val TimeoutException_MSG = "连接超时"
    private val JSONException_MSG = "fastjeson解析失败"
    private val UnknownHostException_MSG = "无法解析该域名"

    /**
     * 解析异常
     *
     * @param e
     * @return
     */
    fun analysisExcetpion(e: Throwable): ApiException {
        val apiException = ApiException(e)
        if (e is HttpException || e is SocketException) {
            /*网络错误*/
            apiException.codes = CodeException.HTTP_ERROR
            apiException.displayMessage = HttpException_MSG
        } else if (e is HttpTimeException) {
            /*自定义运行时异常*/
            apiException.codes = CodeException.RUNTIME_ERROR
            apiException.displayMessage = e.message
        } else if (e is ConnectException ) {
            /*链接异常*/
            apiException.codes = CodeException.HTTP_ERROR
            apiException.displayMessage = ConnectException_MSG
        } else if(e is SocketTimeoutException) {
            /*连接超时*/
            apiException.codes = CodeException.TIMEOUT_ERROR
            apiException.displayMessage = TimeoutException_MSG
        } else if (e is JsonIOException || e is JSONException || e is ParseException || e is IllegalStateException) {
            /*fastjson解析异常*/
            apiException.codes = CodeException.JSON_ERROR
            apiException.displayMessage = JSONException_MSG
        } else if (e is UnknownHostException) {
            /*无法解析该域名*/
            apiException.codes = CodeException.UNKOWNHOST_ERROR
            apiException.displayMessage = UnknownHostException_MSG
        } else if(e.cause is IllegalStateException || e.fillInStackTrace() is MalformedJsonException
                || e.cause is JsonSyntaxException || e.fillInStackTrace() is EOFException){
            /*fastjson解析异常*/
            apiException.codes = CodeException.JSON_ERROR
            apiException.displayMessage = JSONException_MSG
        }
        else {
            /*未知异常*/
            apiException.codes = CodeException.UNKNOWN_ERROR
            apiException.displayMessage = e.message
        }
        return apiException
    }
}