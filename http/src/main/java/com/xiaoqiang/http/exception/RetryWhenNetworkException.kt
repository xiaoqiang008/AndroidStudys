package com.xiaoqiang.http.exception

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer


/**
 * retry条件
 * Created by WZG on 2016/10/17.
 */
class RetryWhenNetworkException : Observable<Throwable>(){
    val TAG = "RetryWhen"
    override fun subscribeActual(observer: Observer<in Throwable>?) {
        Log.i(TAG,"subscribeActual")
        Observable.range(1,3)
    }
}