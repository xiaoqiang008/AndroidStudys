package com.xiaoqiang.http.api

import android.content.Context
import android.util.Log
import com.xiaoqiang.http.api.HttpDownManager.writeFile
import com.xiaoqiang.http.progress.DownFileInfo
import com.xiaoqiang.http.subscribers.HttpSubscribers
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Created by admin3 on 2018/1/15.
 */
object HttpUploadManager {
    //    private var client : Retrofit? = null
    private val TAG = "HttpUploadManager"
    var map : MutableMap<String, DownFileInfo> = mutableMapOf()
    private var client : Retrofit? = null

    init {
        Log.i(TAG,"HttpManager init")
    }

    fun start(observable: Observable<ResponseBody>,httpSubscriber: HttpSubscribers<Long>) : Int{
        observable
                /*http请求线程*/
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io())//指定线程保存文件
                .map ({response: ResponseBody ->
                    Log.i(TAG,"map....")
                    0L
                })
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpSubscriber)
        return 1
    }

    fun <T> getApiService(service : Class<T> ) : T{
        return client!!.create(service)
    }

    fun httpInit(baseUrl : String, connectTimeout : Long, context: Context) : Retrofit?{
        Log.i(TAG,"HttpManager init")

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        //缓存文件夹
        val cacheFile = File(context.getExternalCacheDir().toString(), "cache")
        //缓存大小为10M
        val cacheSize = 10 * 1024 * 1024L
        //创建缓存对象
        val cache = Cache(cacheFile, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
//                .addInterceptor(object : Interceptor {
//                    override fun intercept(chain: Interceptor.Chain?): Response {
//                        var downFileInfo = map.get(chain!!.request().url().uri().toString())
//                        Log.i(TAG,"downFileInfo.intercept:${map.toString()}")
//                        Log.i(TAG,"downFileInfo.readLength:${downFileInfo!!.readLength}\n${chain!!.request().url().uri().toString()}")
//                        val request = chain!!.request().newBuilder().addHeader("Range", "bytes=${downFileInfo!!.readLength}-").build()//startpos 就是数据库记录的已经下载的大小
//                        val originalResponse = chain.proceed(request)
//                        return originalResponse.newBuilder().build()
//                    }
//                })
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .cache(cache)
                .build()

        client = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return client
    }



    fun deletDownFile(string: String){
        map.remove(string)
    }

    fun pause(downFileInfo: DownFileInfo){
        map.get(downFileInfo.url)!!.state = 1
    }
}