package com.xiaoqiang.http.progress

/**
 * Created by admin3 on 2017/11/29.
 */
interface DownloadProgressListener {
    /**
     * 下载进度
     * @param read
     * @param count
     * @param done
     */
    fun update(read: Long, count: Long, done: Boolean)

}