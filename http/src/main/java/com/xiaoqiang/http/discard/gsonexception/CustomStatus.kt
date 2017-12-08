package com.xiaoqiang.http.discard.gsonexception

import java.io.Serializable

/**
 * Created by admin3 on 2017/12/4.
 */
class CustomStatus : Serializable{

    val status: Int = 0
    val msg: String? = null
    //不参与序列化和反序列化
    @Transient private val result: String? = null

    val isRequestSuccess: Boolean
        get() = status != 1
}