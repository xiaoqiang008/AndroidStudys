package com.xiaoqiang.http.api

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by admin3 on 2017/11/29.
 */
object HttpManager{

    private var client : Retrofit? = null

    init {
        Log.i("HttpManager","HttpManager init")
    }

    fun <T> setBaseApi(observable: Observable<T>, httpSubscriber: Observer<T>){
        Log.i("HttpManager","setBaseApi")
        observable
                /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpSubscriber)

    }

    fun <T> getApiService(service : Class<T> ) : T{
        return client!!.create(service)
    }

    fun httpInit(baseUrl : String, connectTimeout : Long) : Retrofit{
        Log.i("HttpManager","HttpManager init")
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .build()

         client = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ResponseConverterFactory<T>(Gson()))
//                .addConverterFactory(CustomGsonConverterFactory.Companion.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return client!!
    }

    /**
     * 日志输出
     * 自行判定是否添加
     * @return
     */
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        //日志显示级别
        val level = HttpLoggingInterceptor.Level.BODY
        //新建log拦截器
        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Log.d("RxRetrofit", "Retrofit====Message:" + message) })
        loggingInterceptor.level = level
        return loggingInterceptor
    }
}