package com.newbie.uier

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newbie.uier.databinding.ActivityMainBinding
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()
    lateinit var courseAdapter: CourseAdapter
    lateinit var viewbinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initData()
    }

    fun initData() {
        var bvid = "BV1YW411e7n5"
        viewModel.bvidData.observe(this, {
            val dataBean: BvidBean = it.data
            for(data in dataBean.pages){
                data.aid = dataBean.aid
            }
            courseAdapter = CourseAdapter(this, dataBean.pages)
            courseAdapter.setOnItemClickListener(object : CourseAdapter.OnItemClickListener{
                override fun onItemClick(cid : String, aid : String) {
                    viewModel.videoData.observe(this@MainActivity,{
                        val dataBean: VideoBean = it.data
                        viewbinding.AsVideoPlayer.setVideoPath(dataBean.durl[0].url, aid)
                        viewbinding.AsVideoPlayer.setListener(object : VideoPlayerListener() {
                            override fun onBufferingUpdate(p0: IMediaPlayer?, p1: Int) {
                                TODO("Not yet implemented")
                            }

                            override fun onCompletion(p0: IMediaPlayer?) {
                                TODO("Not yet implemented")
                            }

                            override fun onPrepared(p0: IMediaPlayer?) {
                                TODO("Not yet implemented")
                            }

                            override fun onInfo(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
                                TODO("Not yet implemented")
                            }

                            override fun onVideoSizeChanged(
                                p0: IMediaPlayer?,
                                p1: Int,
                                p2: Int,
                                p3: Int,
                                p4: Int
                            ) {
                                TODO("Not yet implemented")
                            }

                            override fun onError(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
                                TODO("Not yet implemented")
                            }

                            override fun onSeekComplete(p0: IMediaPlayer?) {
                                TODO("Not yet implemented")
                            }

                        })
                        viewbinding.AsVideoPlayer.start()
                    })
                    viewModel.requestVideo(cid, bvid)
                }
            })
            viewbinding.rvCourse.layoutManager = LinearLayoutManager(
                this,
                RecyclerView.VERTICAL, false
            )
            viewbinding.rvCourse.adapter = courseAdapter
        })


        viewModel.requestBvid(bvid)
    }


    //图片加载方法
    fun returnBitMap(url: String?): Bitmap? {
        var myFileUrl: URL? = null
        var bitmap: Bitmap? = null
        try {
            myFileUrl = URL(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        try {
            val conn = myFileUrl!!.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            val `is` = conn.inputStream
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    //unicode解析 网上抄的
    fun unicodeDecode(theString: String): String? {
        var aChar: Char
        val len = theString.length
        val outBuffer = StringBuffer(len)
        var x = 0
        while (x < len) {
            aChar = theString[x++]
            if (aChar == '\\') {
                aChar = theString[x++]
                if (aChar == 'u') {
                    // Read the xxxx
                    var value = 0
                    for (i in 0..3) {
                        aChar = theString[x++]
                        value = when (aChar) {
                            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> (value shl 4) + aChar.toInt() - '0'.toInt()
                            'a', 'b', 'c', 'd', 'e', 'f' -> (value shl 4) + 10 + aChar.toInt() - 'a'.toInt()
                            'A', 'B', 'C', 'D', 'E', 'F' -> (value shl 4) + 10 + aChar.toInt() - 'A'.toInt()
                            else -> throw IllegalArgumentException(
                                "Malformed   \\uxxxx   encoding."
                            )
                        }
                    }
                    outBuffer.append(value.toChar())
                } else {
                    if (aChar == 't') aChar = '\t' else if (aChar == 'r') aChar =
                        '\r' else if (aChar == 'n') aChar = '\n'
//                        else if (aChar == 'f') aChar = '\f'
                    outBuffer.append(aChar)
                }
            } else outBuffer.append(aChar)
        }
        return outBuffer.toString()
    }
}