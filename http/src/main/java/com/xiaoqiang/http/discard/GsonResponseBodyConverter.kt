package com.xiaoqiang.http.discard

import android.util.Log
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.lang.reflect.Type

/**
 * Created by admin3 on 2017/12/4.
 */
class GsonResponseBodyConverter<T>(val gson : Gson, val type : Type?): Converter<ResponseBody, T> {

    override fun convert(value: ResponseBody?): T {
        val response = value.toString()
        Log.i("convert","response:" + response)
        val httpResult = gson.fromJson(response, Response::class.java)
        Log.i("convert","httpResult.code():" + httpResult.code())
        if (httpResult.code()==200){
            //200的时候就直接解析，不可能出现解析异常。因为我们实体基类中传入的泛型，就是数据成功时候的格式
            return gson.fromJson(response,type)
        }else {
            val errorResponse = gson.fromJson(response,Error::class.java)
            //抛一个自定义ResultException 传入失败时候的状态码，和信息
            throw ResultException(httpResult.code(), httpResult.message())
        }
    }
}