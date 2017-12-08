package com.xiaoqiang.study

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.xiaoqiang.http.progress.DownFileInfo
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    var downFileInfo = DownFileInfo()
    var downFileInfo1 = DownFileInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        sample_text.text = stringFromJNI()
        val test = Test(this)
        start.setOnClickListener({
//            var filename = getFilesDir().getAbsolutePath() + "/test1.gz"
            var filename = "/data/user/0/com.xiaoqiang.study/files/aa/test.rar"
            var ds : Disposable? = null
            downFileInfo = DownFileInfo(filename,0,0,"http://192.168.0.155:8080/test1/DownloadFile?fileName=test.rar")

            if(test.start(this, sample_text,downFileInfo) == 0){
                toast("正在下载")
            }
        })
        pause.setOnClickListener({

            test.pause(downFileInfo)
        })

        starts.setOnClickListener({
            //            var filename = getFilesDir().getAbsolutePath() + "/test1.gz"
            var filename = "/data/user/0/com.xiaoqiang.study/files/aa/test1.rar"
            var ds : Disposable? = null
            downFileInfo1 = DownFileInfo(filename,0,0,"http://192.168.0.155:8080/test1/DownloadFile?fileName=test1.rar")
            test.start(this, sample_texts,downFileInfo1)
        })
        pauses.setOnClickListener({
            test.pause(downFileInfo1)
        })



        //        String filename = getFilesDir().getAbsolutePath() + "/test1.gz";
        //        String url = "http://192.168.0.155:8080/test1/DownloadFile?fileName=test.gz";

    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }

        private fun getRootView(context: Activity): View {
            return (context.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        }
    }

}
