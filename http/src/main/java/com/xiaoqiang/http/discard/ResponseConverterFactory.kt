package com.xiaoqiang.http.discard

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by admin3 on 2017/12/4.
 */
class ResponseConverterFactory<T>(val gson: Gson) : Converter.Factory() {
    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return GsonResponseBodyConverter<T>(gson, type)
    }

    fun create(): ResponseConverterFactory<T> {
        return create(Gson())
    }

    fun create(gson: Gson): ResponseConverterFactory<T> {
        return ResponseConverterFactory(gson)
    }
}