package com.xiaoqiang.http.api

import android.util.Log
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

    init {
        Log.i("HttpManager","HttpManager init")
    }

    fun <T> setBaseApi(observable: Observable<T>, httpSubscriber: Observer<T>, int: Int = 3){
        Log.i("HttpManager","setBaseApi")
        var cout = 0
        observable
                /*http请求线程*/
//                .retryWhen {
//                    throwableObservable ->
//
//                    throwableObservable.zipWith(Observable.range(1, count + 1))
//                    Log.i("HttpManager","throwableObservable")
//                    Observable.interval(3000, TimeUnit.SECONDS).take(3)
//                }
                .retryWhen{ throwableObservable ->
                    throwableObservable
                            .subscribeOn(Schedulers.io())
                            .flatMap(Function<Throwable, ObservableSource<*>> {
                                //                                observable.repeat()
                                //如果发射的onError就终止
                                Log.i("HttpManager", "throwableObservable:$cout")
//                                observable.repeat(30000)
                                if(int == -1){
                                    Observable.timer(1, TimeUnit.SECONDS)
                                }else if(cout < int) {
                                    cout ++
                                    Observable.timer(1, TimeUnit.SECONDS)
                                }else{
                                    Observable.error<Function<Throwable, ObservableSource<*>>>(Throwable("retryWhen终止啦"))
                                }

//                                observable.zipWith(Observable.range(1,2),{t,t2->})
//                                Observable.zip(firstRequest, secondRequest, new BiFunction<FirstBean, SecondBean, WholeBean>() {
//                                    @Override
//                                    public WholeBean apply(@NonNull FirstBean firstBean, @NonNull SecondBean secondBean) throws Exception {
//                                        //结合数据为一体
//                                    }
//                                });
//                    Observable.error<Function<Throwable, ObservableSource<*>>>(Throwable("retryWhen终止啦"))
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

    fun httpInit(baseUrl : String, connectTimeout : Long, vararg certificates : InputStream) : Retrofit{
        Log.i("HttpManager","HttpManager init")
        val httpLoggingInterceptor = HttpLoggingInterceptor()
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
//        ,object : X509TrustManager{
//            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun getAcceptedIssuers(): Array<X509Certificate> {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//        }

         client = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClients.build())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ResponseConverterFactory<T>(Gson()))
//                .addConverterFactory(CustomGsonConverterFactory.Companion.create())
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
}