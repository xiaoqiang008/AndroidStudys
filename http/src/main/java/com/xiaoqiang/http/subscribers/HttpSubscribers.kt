package com.xiaoqiang.http.subscribers

import android.util.Log
import com.xiaoqiang.http.exception.ApiException
import com.xiaoqiang.http.exception.FactoryException
import com.xiaoqiang.http.progress.DownloadProgressListener
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import rx.functions.Action1
import io.reactivex.android.schedulers.AndroidSchedulers
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




/**
 * Created by admin3 on 2017/11/30.
 */
abstract class HttpSubscribers<T> : Observer<T>,DownloadProgressListener{
        private val TAG = "HttpSubscriberListener"
    override fun onSubscribe(d: Disposable) {
        Log.i(TAG,"onSubscribe")
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

    override fun update(read: Long, count: Long, done: Boolean) {
        requestProgress(read,count,done)
    }

    abstract fun requestSubscribe(d: Disposable)
    abstract fun requestSuccee(t : T)
    abstract fun requestFail(e : ApiException)
    abstract fun requestComplete()
    abstract fun requestProgress(read: Long, count: Long, done: Boolean)
}