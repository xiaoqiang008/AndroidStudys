package com.xiaoqiang.http.api

import android.util.Log
import com.google.gson.Gson
import com.xiaoqiang.http.exception.RetryWhenNetworkException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import rx.functions.Func1
import rx.functions.Func2
import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.sql.Time
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.net.ssl.*

/**
 * Created by admin3 on 2017/11/29.
 */
object HttpManager{

    private var client : Retrofit? = null
    private var logBack: LogBack? = null

    init {
        Log.i("HttpManager","HttpManager init")
    }

    fun <T> setBaseApi(observable: Observable<T>, httpSubscriber: Observer<T>, int: Int = 3, delayTime: Long = 10000){
        Log.i("HttpManager","setBaseApi")
        var cout = 0
        observable
                /*http请求线程*/
                .retryWhen{ throwableObservable ->
                    throwableObservable
                            .subscribeOn(Schedulers.io())
                            .flatMap(Function<Throwable, ObservableSource<*>> {
                                //如果发射的onError就终止
                                Log.i("HttpManager", "throwableObservable:$cout")
                                if(int == -1){
                                    Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                                }else if(cout < int) {
                                    cout ++
                                    Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                                }else{
                                    Observable.error<Function<Throwable, ObservableSource<*>>>(Throwable("retryWhen终止啦"))
                                }
                }) }
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpSubscriber)

    }

    fun <T> setBaseApis(observable: Observable<T>, httpSubscriber: Observer<T>, int: Int = 3, delayTime: Long = 10000,errorBack:(Int) -> Unit){
        Log.i("HttpManager","setBaseApi")
        var cout = 0
        observable
                /*http请求线程*/
                .retryWhen{ throwableObservable ->
                    throwableObservable
                            .subscribeOn(Schedulers.io())
                            .flatMap(Function<Throwable, ObservableSource<*>> {
                                //如果发射的onError就终止
                                Log.i("HttpManager", "throwableObservable:$cout")
                                if(int == -1){
                                    errorBack(-1)
                                    Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                                }else if(cout < int) {
                                    errorBack(cout)
                                    cout ++
                                    Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                                }else{
                                    Observable.error<Function<Throwable, ObservableSource<*>>>(Throwable("retryWhen终止啦"))
                                }
                            }) }
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpSubscriber)

    }

    class Wrapper(private val throwable: Throwable, private val index: Int)

    fun <T> getApiService(service : Class<T> ) : T?{
        return client?.create(service)
    }

    fun httpInit(baseUrl : String, connectTimeout : Long, logBack: LogBack? = null, vararg certificates : InputStream) : Retrofit{
        Log.i("HttpManager","HttpManager init")
        this.logBack = logBack
        var httpLoggingInterceptor: HttpLoggingInterceptor? = null
        if(logBack != null) {
            httpLoggingInterceptor = HttpLoggingInterceptor(HttpLogger())
        }else{
            httpLoggingInterceptor = HttpLoggingInterceptor()
        }
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClients = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                if(certificates.size > 0) {
                    okHttpClients.sslSocketFactory(createSSLSocketFactory(*certificates), TrustAllManager())
                            .hostnameVerifier(TrustAllHostnameVerifier())
                }

         client = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClients.build())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return client!!
    }




    fun createSSLSocketFactory() : SSLSocketFactory?{
        var sSLSocketFactory: SSLSocketFactory? = null

        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<TrustManager>(TrustAllManager()),
                    SecureRandom())
            sSLSocketFactory = sc.getSocketFactory()
        } catch (e: Exception) {
        }


        return sSLSocketFactory
    }

    fun createSSLSocketFactory(vararg certificates : InputStream) : SSLSocketFactory?{
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(
                    null)
            var index = 0
            for (certificate in certificates) {
                val certificateAlias = Integer.toString(index++)
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate))

                try {
                    if (certificate != null)
                        certificate.close()
                } catch (e: IOException) {
                }

            }

            val sslContext = SSLContext.getInstance("TLS")

            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

            trustManagerFactory.init(keyStore)
            sslContext.init(null,
                    trustManagerFactory.getTrustManagers(),
                    SecureRandom()
            )

            return  sslContext.getSocketFactory()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private class TrustAllManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    private class TrustAllHostnameVerifier : HostnameVerifier {

         override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
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

    interface LogBack{
        fun log(message: String)
    }

    class HttpLogger : HttpLoggingInterceptor.Logger {

        override fun log(message: String) {
            if(logBack != null)
                logBack?.log(message)
        }
    }
}