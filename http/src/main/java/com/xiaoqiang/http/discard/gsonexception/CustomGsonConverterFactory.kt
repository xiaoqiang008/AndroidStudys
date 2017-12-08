package com.xiaoqiang.http.discard.gsonexception

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by Panda on 2017/12/4.
 * 自定义工厂类 内容和原始一样 只修改CustomGsonResponseBodyConverter类即可
 */
class CustomGsonConverterFactory constructor(val gson: Gson?) : Converter.Factory() {

    init {
        if (gson == null) throw NullPointerException("gson == null")
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): CustomGsonResponseBodyConverter<out Any> {
        val adapter = gson!!.getAdapter(TypeToken.get(type!!))
        return CustomGsonResponseBodyConverter(gson, adapter as TypeAdapter<*>)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        val adapter = gson!!.getAdapter(TypeToken.get(type!!))
        return CustomGsonRequestBodyConverter(gson, adapter as TypeAdapter<*>)
    }

    companion object {

        @JvmOverloads
        fun create(gson: Gson = Gson()): CustomGsonConverterFactory {
            return CustomGsonConverterFactory(gson)
        }
    }
}