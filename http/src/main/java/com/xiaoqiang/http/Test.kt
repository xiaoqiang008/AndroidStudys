package com.xiaoqiang.http

/**
 * Created by admin3 on 2017/11/29.
 */
class Test {

    fun test1(vararg int: Int){

    }

    fun test2(vararg int: Int){
        test1(*int)
    }
}