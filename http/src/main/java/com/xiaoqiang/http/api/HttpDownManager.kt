package com.xiaoqiang.http.api

import android.content.Context
import android.util.Log
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
import java.io.*
import java.util.concurrent.TimeUnit

/**
 * Created by admin3 on 2017/12/5.
 */
object HttpDownManager {

//    private var client : Retrofit? = null
    private val TAG = "HttpDownManager"
    var map : MutableMap<String,DownFileInfo> = mutableMapOf()
    private var client : Retrofit? = null

    init {
        Log.i(TAG,"HttpManager init")
    }

    fun start(observable: Observable<ResponseBody>, httpSubscriber: HttpSubscribers<Long>,
                       less : (response : ResponseBody) -> ResponseBody?, downFileInfo: DownFileInfo) : Int{

        downFileInfo.httpSubscriber = httpSubscriber
        if(downFileInfo.url in map.keys){
            Log.i(TAG,"setBaseApi in:"+map.get(downFileInfo.url)!!.countLength)
            map.get(downFileInfo.url)!!.httpSubscriber = httpSubscriber
            if(map.get(downFileInfo.url)!!.state == 0) {
                return 0
            }
            map.get(downFileInfo.url)!!.state = 0
        }else{
            Log.i(TAG,"setBaseApi no in")
            map.put(downFileInfo.url,downFileInfo)
        }
        Log.i(TAG,"start.downFileInfo:${map.toString()}")
        observable
                /*http请求线程*/
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io())//指定线程保存文件
                .map ({response: ResponseBody ->
                    Log.i(TAG,"map")
                    writeFile(less(response)!!,downFileInfo.url,true)
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
                .addInterceptor(object : Interceptor{
                    override fun intercept(chain: Interceptor.Chain?): Response {
                        var downFileInfo = map.get(chain!!.request().url().uri().toString())
                        Log.i(TAG,"downFileInfo.intercept:${map.toString()}")
                        Log.i(TAG,"downFileInfo.readLength:${downFileInfo!!.readLength}\n${chain!!.request().url().uri().toString()}")
                        val request = chain!!.request().newBuilder().addHeader("Range", "bytes=${downFileInfo!!.readLength}-").build()//startpos 就是数据库记录的已经下载的大小
                        val originalResponse = chain.proceed(request)
                        return originalResponse.newBuilder().build()
                    }
                })
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

    /**
     * 写入文件
     */
    @Throws
    fun  writeFile(responseBody: ResponseBody, url: String,reDown: Boolean) : Long{

        //获取文件总长度
        val allLength: Long = responseBody.contentLength()
        var downFileInfo = map.get(url)!!
        Log.i(TAG,"writeFile:"+url+"/"+downFileInfo.url)
        var isReWrite = reDown
        if (downFileInfo.countLength == 0L) {
            //新下载文件
            downFileInfo.countLength = allLength
        } else {
            if(downFileInfo.countLength != allLength){
                //未下载完的文件总长度与新的文件总长度不一样
                deletDownFile(downFileInfo.url)
                return -3
            }else{
                //未下载完文件继续下载
            }
        }
        var input: InputStream? = null
        val buf = ByteArray(2048)
        var len = 0
        var fos: FileOutputStream? = null
        //储存下载文件的目录
        var file = File(downFileInfo.savePath)
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs()
        }
        if(file.length() == allLength){
            deletDownFile(downFileInfo.url)
            return -2
        }
        val lastLen = downFileInfo.readLength
        try {
            input = responseBody.byteStream()
            fos = FileOutputStream(file,isReWrite)

            var sum: Long = 0
            len = input.read(buf)
            Log.i(TAG,"len:"+len+"/"+downFileInfo.state+"/"+downFileInfo.readLength+"/"+allLength)
            adc@ while (len != -1) {
                fos.write(buf, 0, len)
                sum += len.toLong()
                //下载中
                downFileInfo.readLength = sum + lastLen
                downFileInfo.httpSubscriber!!.requestProgress(downFileInfo.readLength,allLength,false)
                if(downFileInfo.state == 1){
                    Log.i(TAG,"pause")
                    break@adc
                }
                if(downFileInfo.readLength < allLength) {
                    len = input.read(buf)
                }else{
                    break@adc
                }
            }
            downFileInfo.httpSubscriber!!.requestProgress(downFileInfo.readLength,allLength,true)
            fos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            deletDownFile(downFileInfo.url)
            return -1
        } finally {
            try {
                if (input != null)
                    input.close()
            } catch (e: IOException) {

            }
            try {
                if (fos != null) {
                    fos.close()
                }
            } catch (e: IOException) {

            }

        }
        if(downFileInfo.readLength == allLength){
            Log.i(TAG,"delet map downFileInfo.url")
            deletDownFile(downFileInfo.url)
        }
        return downFileInfo.readLength
    }

    fun deletDownFile(string: String){
        map.remove(string)
    }

    fun pause(downFileInfo: DownFileInfo){
        map.get(downFileInfo.url)!!.state = 1
    }
}