package com.xiaoqiang.study

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import com.xiaoqiang.http.api.HttpDownManager
import com.xiaoqiang.http.api.HttpManager
import com.xiaoqiang.http.exception.ApiException
import com.xiaoqiang.http.exception.RetryWhenNetworkException
import com.xiaoqiang.http.progress.DownFileInfo
import com.xiaoqiang.http.subscribers.HttpSubscriberListener
import com.xiaoqiang.http.subscribers.HttpSubscribers
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import okhttp3.ResponseBody
import org.jetbrains.anko.runOnUiThread
import rx.Observable
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException
import javax.security.cert.X509Certificate


/**
 * Created by admin3 on 2017/11/29.
 */
class Test(var context: Context){
//    var baseUrl = "http://10.0.2.2:8080/"
//    var baseUrl = "https://192.168.0.48:8443/"
var baseUrl = "http://192.168.0.48:8080/"
    var apiServices: ApiServices
    var http : ApiServices?

    init {
//        HttpManager.httpInit(baseUrl,10000,context.getAssets().open("tomcat.cer"))
        HttpManager.httpInit(baseUrl,10000)
        http = HttpManager.getApiService(ApiServices::class.java!!)

        var retrofit =  HttpDownManager.httpInit(baseUrl,600000,context)
        apiServices = HttpDownManager.getApiService(ApiServices::class.java!!)
    }

    fun test(){
        Log.i("HttpManager","HttpManager init   sssss")
        HttpManager.setBaseApi(http!!.loginUser(),object : HttpSubscriberListener<HttpResponse<Int>>{
            val TAG = "object"
            override fun requestSubscribe(d: Disposable) {
                Log.i(TAG,"requestSubscribe")
            }

            override fun requestSuccee(t: HttpResponse<Int>) {
                Log.i(TAG,"requestSuccee："+Gson().toJson(t))
            }

            override fun requestFail(e: ApiException) {
                Log.i(TAG,"requestFail")
            }

            override fun requestComplete() {
                Log.i(TAG,"requestComplete")
            }
        },10)
    }

    fun test1(){
        Log.i("HttpManager","HttpManager init   sssss")
        HttpManager.setBaseApi(http!!.loginUser(),object : HttpSubscriberListener<HttpResponse<Int>>{
            val TAG = "object"
            override fun requestSubscribe(d: Disposable) {
                Log.i(TAG,"requestSubscribe")
            }

            override fun requestSuccee(t: HttpResponse<Int>) {
                Log.i(TAG,"requestSuccee："+Gson().toJson(t))
            }

            override fun requestFail(e: ApiException) {
                Log.i(TAG,"requestFail")
            }

            override fun requestComplete() {
                Log.i(TAG,"requestComplete")
            }
        },-1)
    }
    val TAG = "Test"

    fun start(context : Context,textView: TextView,downFileInfo: DownFileInfo) : Int{

        Log.i(TAG,"start:"+downFileInfo.readLength.toString()+"/"+downFileInfo.countLength +"/"+downFileInfo.url)
        val testhttp = HttpDownManager.start(apiServices.downloadFiles(downFileInfo.url)
                ,object : HttpSubscribers<Long>(){
            override fun requestSubscribe(d: Disposable) {
                Log.i(TAG,"requestSubscribe")
            }

            override fun requestSuccee(t: Long) {
                downFileInfo.readLength = t
                Log.i(TAG,"requestSuccee:$t")

            }

            override fun requestFail(e: ApiException) {
                Log.i(TAG,"requestFail:"+e.codes+"/"+e.displayMessage)

            }

            override fun requestComplete() {
                Log.i(TAG,"requestComplete:"+downFileInfo.savePath)
            }

            override fun requestProgress(read: Long, count: Long, done: Boolean) {
                val progress = (read * 1.0f / count * 100).toInt()
                context.runOnUiThread { textView.setText(progress.toString() + "%") }
//                Log.i(TAG,"update:$read/$count/$done....$progress%")
            }
        }
                ,{ response : ResponseBody? -> response}
        , downFileInfo
        )
        Log.i(TAG,"start:.....")


        return testhttp
    }



    fun pause(downFileInfo: DownFileInfo){
        HttpDownManager.pause(downFileInfo)
    }


}