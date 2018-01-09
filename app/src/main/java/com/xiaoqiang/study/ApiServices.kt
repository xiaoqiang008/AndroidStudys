package com.xiaoqiang.study


import io.reactivex.Observable
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by admin3 on 2017/11/29.
 */

interface ApiServices{
    //用户登录
    @POST("AndStudysServer/user/loginUser")
    fun  goLoginUser(@Query("userId") userId : String, @Query("userPass") userPass:String) : Observable<HttpResponse<Int>>

    @GET("test1/login")
    fun loginUser() : Observable<HttpResponse<Int>>

    @GET("test1/logins")
    fun loginUsers() : Observable<HttpResponse<Int>>

    /*大文件需要加入这个判断，防止下载过程中写入到内存中*/
    @GET("test1/DownloadFile?fileName=test.rar")
    @Streaming
    fun downloadFile() : Observable<ResponseBody>

    /*大文件需要加入这个判断，防止下载过程中写入到内存中*/
    @GET()
    @Streaming
    fun downloadFiles(@Url url : String) : Observable<ResponseBody>
}