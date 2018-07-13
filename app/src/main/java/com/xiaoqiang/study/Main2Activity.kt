package com.xiaoqiang.study

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        var a = 100
        var b = 0
        do {
            b++
            a = a*2
        }while(a*2 <= 100000)
        Log.i("Main2Activity","a:$a,b:$b")
    }
}
