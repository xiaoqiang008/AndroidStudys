package com.xiaoqiang.http.progress

import com.xiaoqiang.http.subscribers.HttpSubscribers

/**
 * 下载文件信息
 * @param savePath 存储位置
 * @param countLength 文件总长度
 * @param readLength 下载长度
 * @param url 下载地址
 */
class DownFileInfo(){
    var savePath: String = ""
    var countLength: Long = 0L
    var readLength: Long = 0L
    var url: String = ""
    var httpSubscriber: HttpSubscribers<Long>? = null
    var state = 0

    constructor( savePath: String,  countLength: Long,  readLength: Long,  url: String) : this(){
        this.savePath = savePath
        this.countLength = countLength
        this.readLength = readLength
        this.url = url
    }
}