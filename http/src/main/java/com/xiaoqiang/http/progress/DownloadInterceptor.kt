package com.xiaoqiang.http.progress

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

/**
 * 成功回调处理
 * Created by admin3 on 2017/11/29.
 */
class DownloadInterceptor(var progressListener : DownloadProgressListener) : Interceptor{
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        return originalResponse.newBuilder()
                .body(DownloadResponseBody(originalResponse.body(), progressListener))
                .build()
    }
}