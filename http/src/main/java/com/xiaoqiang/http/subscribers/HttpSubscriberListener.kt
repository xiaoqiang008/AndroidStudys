package com.xiaoqiang.http.subscribers

import android.util.Log
import com.xiaoqiang.http.exception.ApiException
import com.xiaoqiang.http.exception.FactoryException
import com.xiaoqiang.http.progress.DownloadProgressListener
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 * Created by admin3 on 2017/11/30.
 */
interface HttpSubscriberListener<T> : Observer<T> {
//    private val TAG = "HttpSubscriberListener"
    override fun onSubscribe(d: Disposable) {
//        Log.i(TAG,"onSubscribe")
        requestSubscribe(d)
    }
    override fun onNext(t: T) {
//        Log.i(TAG,"onNext:"+t)
        requestSuccee(t)
    }
    override fun onError(e: Throwable) {
//        Log.i(TAG,"onError:"+e.message+"/"+e.localizedMessage)
        requestFail(FactoryException.analysisExcetpion(e))
    }

    override fun onComplete() {
//        Log.i(TAG,"onComplete")
        requestComplete()
    }


    fun requestSubscribe(d: Disposable)
    fun requestSuccee(t : T)
    fun requestFail(e : ApiException)
    fun requestComplete()
}