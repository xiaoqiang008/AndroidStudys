package com.xiaoqiang.http.progress

import android.util.Log
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException
import okio.BufferedSource
import okio.ForwardingSource

/**
 * 自定义进度的body
 * Created by admin3 on 2017/11/29.
 */
class DownloadResponseBody(var responseBody : ResponseBody?,
                           var progressListener : DownloadProgressListener?) : ResponseBody(){
//    var bufferedSource: BufferedSource?
//    init {
//        bufferedSource = Okio.buffer(source(responseBody!!.source()))
//    }
    override fun source(): BufferedSource {
        return Okio.buffer(source(responseBody!!.source()))
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            internal var totalBytesRead = 0L
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                Log.i("DownloadResponseBody","read：$totalBytesRead/$byteCount")
                progressListener!!.update(totalBytesRead, responseBody!!.contentLength(), bytesRead == -1L)
                return bytesRead
            }
        }
    }

    override fun contentLength(): Long {
        return responseBody!!.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody!!.contentType()
    }
}