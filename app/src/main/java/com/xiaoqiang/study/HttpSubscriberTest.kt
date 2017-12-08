package com.xiaoqiang.study

import android.util.Log
import com.xiaoqiang.http.exception.ApiException
import com.xiaoqiang.http.subscribers.HttpSubscribers
import io.reactivex.disposables.Disposable

/**
 * Created by admin3 on 2017/12/1.
 */
class HttpSubscriberTest : HttpSubscribers<Int>(){

    val TAG = "HttpSubscriberTest"

    override fun requestSubscribe(d: Disposable) {
        Log.i(TAG,"requestSubscribe")
    }

    override fun requestSuccee(t: Int) {
        Log.i(TAG,"requestSuccee:$t")
    }

    override fun requestFail(e: ApiException) {
        Log.i(TAG,"requestFail:"+e.codes+"/"+e.displayMessage)
    }

    override fun requestComplete() {
        Log.i(TAG,"requestComplete")
    }

    override fun requestProgress(read: Long, count: Long, done: Boolean) {
        Log.i(TAG,"update:$read/$count/$done")
    }
}