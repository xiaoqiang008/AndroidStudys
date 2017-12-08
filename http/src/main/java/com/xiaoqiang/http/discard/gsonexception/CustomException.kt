package com.xiaoqiang.http.discard.gsonexception

/**
 * Created by admin3 on 2017/12/4.
 */
data class CustomException(val mErrorCode: Int, val errorMessage: String) : RuntimeException(errorMessage)