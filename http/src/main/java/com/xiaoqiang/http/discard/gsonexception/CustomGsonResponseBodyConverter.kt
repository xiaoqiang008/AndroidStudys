package com.xiaoqiang.http.discard.gsonexception

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import kotlin.text.Charsets.UTF_8
import com.google.gson.JsonParseException
import android.util.Log


/**
 * Created by Panda on 2017/11/18.
 * 自定义响应类  修改做拦截抛出操作 应用到Gson反序列和序列化部分的知识
 */

class CustomGsonResponseBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
//        val httpStatus = gson.fromJson(response, T::class.java)
        //验证status返回是否为1
        try {
            JsonParser().parse(response)
        } catch (e: JsonParseException) {
//            throw CustomException(httpStatus.status, httpStatus.msg.toString())
            Log.i("convert","JsonParseException")
        }

//        if (httpStatus.isRequestSuccess) {
//            value.close()
//            //不为-1，表示响应数据不正确，抛出异常
//            throw CustomException(httpStatus.status, httpStatus.msg.toString())
//        }

        //继续处理body数据反序列化，注意value.string() 不可重复使用
        val contentType = value.contentType()
        val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
        val inputStream = ByteArrayInputStream(response.toByteArray())
        val reader = InputStreamReader(inputStream, charset!!)
        val jsonReader = gson.newJsonReader(reader)

        try {
            return adapter.read(jsonReader)
        } finally {
            value.close()
        }
    }
}