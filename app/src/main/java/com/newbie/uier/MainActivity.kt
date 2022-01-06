package com.newbie.uier

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.JZDataSource
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.newbie.uier.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()
    lateinit var courseAdapter: CourseAdapter
    lateinit var viewbinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        viewbinding.startPlay.setOnClickListener {

//        }
        viewModel.videoName.observe(this@MainActivity, Observer {
            val dataBean: BvidBean = it.data
            courseAdapter = CourseAdapter(this@MainActivity, dataBean.pages)
            viewbinding.rvCourse.layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.VERTICAL, false
            )
            viewbinding.rvCourse.adapter = courseAdapter

//                abvGo(dataBean.toString())
        })
        initData()
    }

    fun initData() {
        var bvid = "BV1YW411e7n5"
        var cookie = " "
        try {
            var name: String? = null
            //定位数据
            //判断下这是不是个纯bv号
            if (isENChar(bvid)) {
                if (bvid.substring(0, 5).contains("https") || bvid.substring(0, 5).contains("http")
                    || bvid.contains("bilibili.com") or bvid.contains("https://b23.tv/")
                ) {
                    //输入一个视频链接，直接给截取公共解析
                    //这个方案存在问题，当输入错误链接异常时可能无法及时停止解析
                    if (bvid.contains("ep") || bvid.contains("EP")) {
                        epGo(bvid)
                    } else {
                        public_jx(bvid)
                    }
                } else if (bvid.substring(0, 2).contains("av") || bvid.substring(0, 2)
                        .contains("AV")
                ) {
                    //过滤掉多余的东西
                    if (bvid.contains("AV")) {
                        bvid = bvid.replace("AV".toRegex(), "")
                    } else {
                        //如果不是，就直接让接口解析
                        bvid = bvid.replace("av".toRegex(), "")
                    }
                    var dataBean: BvidBean
                    viewModel.requestBvid(bvid)
//                        name = dataBean.pages
//                        name = HttpUtils.doGet(
//                            "https://api.bilibili.com/x/web-interface/view?aid=" + bvid,
//                            cookie
//                        )
                    if (name != null) {
                        abvGo(name)
                    }
                } else if (bvid.substring(0, 2).contains("bv") || bvid.substring(0, 2)
                        .contains("BV")
                ) {
//                        name = HttpUtils.doGet(
//                            "https://api.bilibili.com/x/web-interface/view?bvid=" + bvid,
//                            cookie
//                        )

                    viewModel.requestBvid(bvid)

//                        viewModel.requestBvid(bvid)
//                        name = viewModel.videoName.value
                    if (name != null) {
                        abvGo(name)
                    }
                } else if (bvid.substring(0, 2).contains("ss") || bvid.substring(0, 2)
                        .contains("SS")
                ) {
                    //输入一个ss的番剧链接，为了准确
                    if (!bvid.substring(0, 5).contains("https") && !bvid.contains("http")) {
                        bvid = "https://www.bilibili.com/bangumi/play/" + bvid
                    }
                    public_jx(bvid)
                } else if (bvid.substring(0, 2).contains("ep") || bvid.substring(0, 2)
                        .contains("EP")
                ) {
                    bvid = "https://www.bilibili.com/bangumi/play/" + bvid
                    epGo(bvid)
                } else {

                }
            } else {
                //全数字一定是av
                name = HttpUtils.doGet(
                    "https://api.bilibili.com/x/web-interface/view?aid=" + bvid,
                    cookie
                )
                if (name != null) {
                    abvGo(name)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isENChar(string: String?): Boolean {
        var flag = false
        val p = Pattern.compile("[a-zA-z]")
        if (p.matcher(string).find()) {
            flag = true
        }
        return flag
    }

    //取中间方法
    fun sj(str: String, start: String, end: String?): String {
        var str = str
        if (str.contains(start) && str.contains(end!!)) {
            str = str.substring(str.indexOf(start) + start.length)
            return str.substring(0, str.indexOf(end))
        }
        return ""
    }

    //av和bv解析方法
    private fun abvGo(name: String) {
        var name = name
//            VideoJson = name
        var cookie = ""
        var ImageUrl = sj(name, "pic\":\"", "\",")
        var Title = sj(name, "title\":\"", "\",")
        var aid = sj(name, "\"aid\":", ",\"")
        var bvid = sj(name, "\"bvid\":\"", "\",")
        var Mid = sj(name, "mid\":", ",\"")
        var up = sj(name, "\"name\":\"", "\",")
        var Copyright = sj(name, "copyright\":", ",\"")
        val json = JSONObject(name)
        val data = json.getJSONObject("data")
        var tName = data.getString("tname")
        val pages = data.getJSONArray("pages")
        val UPNameJson = data.getJSONObject("owner")
        val UPUser = UPNameJson.getString("name")
        val videoStat = data.getJSONObject("stat")
        var upMid = UPNameJson.getInt("mid")
        var upFace = UPNameJson.getString("face")
        var playVolume = videoStat.getInt("view")
        var barrageVolume = videoStat.getInt("danmaku")
        var upName = UPUser
        val VideoDesc = data.getString("desc")
        name = sj(name, "cid\":", ",\"")
        var VideoTitle: String? = null
        //初步获取视频分辨率
        var jxUrl =
            "https://api.bilibili.com/x/player/playurl?cid=" + name + "&bvid=" + bvid + "&type=json&fourk=1"
        val jsonStr = HttpUtils.doGet(jxUrl, cookie)
        //分辨率解析列表展示
        val jsonVideo = JSONObject(jsonStr)
        val dataStr = jsonVideo.getJSONObject("data")
        val pagesVideo = dataStr.getJSONArray("accept_description")
        val quality = dataStr.getJSONArray("accept_quality")
        for (i in 0 until pagesVideo.length()) {
            val hz = quality.getString(i)
//                listVideo.add(pagesVideo.getString(i) + "[" + hz + "]")
        }
        //关闭弹窗，提示失败
        if (pages.length() == 1) {
            VideoTitle = pages.getString(0)
            val TitleJson = JSONObject(VideoTitle)
            val TitleStr = TitleJson.getString("part")
//                list.add("[$name]$TitleStr")
//                listVideoName.add(TitleStr)
        } else {
            for (i in 0 until pages.length()) {
                val honor = pages.getJSONObject(i)
                val TitleStr = honor.getString("part")
                val cid1 = honor.getString("cid")
//                    list.add("[$cid1]$TitleStr")
//                    listVideoName.add(TitleStr)
            }
        }
        if (name == "" || bvid == "") {

        } else {
            videoView(jxUrl, ImageUrl, Title, aid)
        }
    }

    //番剧单独解析方法
    fun epGo(name: String) {
        //这里是判断一下番剧的数据
        //获取番剧页面源码
        var cookie = ""
        var name = name
        val ep: String = HttpUtils.doGet(name, cookie) ?: return
        //截取需要的部分
        var epStr: String = sj(ep, "<script>window.__INITIAL_STATE__=", "</script>")
        var Title = sj(epStr, "\"h1Title\":\"", "\",")
        //再单独切出aid，bvid，图片链接这些东西
        epStr = sj(epStr, "epInfo", "parentNode.removeChild")
        var aid = sj(epStr, "aid\":", ",\"")
        var bvid = sj(epStr, "bvid\":\"BV", "\",")
        var Mid = sj(name, "mid\":", ",\"")
        var Copyright = sj(name, "copyright\":", ",\"")
        var ImageUrl = sj(epStr, "bangumi\",\"cover\":\"", "\",")
        //图片需要转码
        ImageUrl = "http:" + unicodeDecode(ImageUrl)
        //番剧列表化
        val epJson: String? = HttpUtils.doGet(
            "https://api.bilibili.com/x/web-interface/view?aid=" + aid,
            cookie
        )
//        VideoJson = epJson
        val json = JSONObject(epJson)
        val data = json.getJSONObject("data")
        var tName = data.getString("tname")
        val UPNameJson = data.getJSONObject("owner")
        val videoStat = data.getJSONObject("stat")
        var upMid = UPNameJson.getInt("mid")
        var upFace = UPNameJson.getString("face")
        var playVolume = videoStat.getInt("view")
        var barrageVolume = videoStat.getInt("danmaku")
        val UPUser = UPNameJson.getString("name")
        var upName = UPUser
        val VideoDesc = data.getString("desc")
        val pages = data.getJSONArray("pages")
        var VideoTitle: String? = null
        //截取标题 截取cid
        name = sj(epStr, "cid\":", ",\"")
        var cid = name
        //初步获取视频分辨率
        var jxUrl =
            "https://api.bilibili.com/x/player/playurl?cid=" + name + "&bvid=" + bvid + "&type=json&fourk=1"
        val jsonStr: String? = HttpUtils.doGet(jxUrl, cookie)
        //分辨率解析列表展示
        val jsonVideo = JSONObject(jsonStr)
        val dataStr = jsonVideo.getJSONObject("data")
        val pagesVideo = dataStr.getJSONArray("accept_description")
        val quality = dataStr.getJSONArray("accept_quality")
        for (i in 0 until pagesVideo.length()) {
            val hz = quality.getString(i)
//            listVideo.add(pagesVideo.getString(i) + "[" + hz + "]")
        }
        //判断下这个截取的数据是不是空的
        //关闭弹窗，提示失败
        if (pages.length() == 1) {
            VideoTitle = pages.getString(0)
            val TitleJson = JSONObject(VideoTitle)
            val TitleStr = TitleJson.getString("part")
//            list.add("[$name]$TitleStr")
//            listVideoName.add(TitleStr)
        } else {
            for (i in 0 until pages.length()) {
                val honor = pages.getJSONObject(i)
                val TitleStr = honor.getString("part")
                val cid1 = honor.getString("cid")
//                list.add("[$cid1]$TitleStr")
//                listVideoName.add(TitleStr)
            }
        }
        if (bvid == "" || aid == "") {
//                runOnUiThread { //关闭弹窗，提示失败
//                    Toast.makeText(applicationContext, "看起来没有解析到", Toast.LENGTH_SHORT).show()
//                }
        } else {
            //加载显示数据
//                epView()
        }
    }

    fun public_jx(name: String) {
        var cookie = ""
        var name: String? = name
        name = HttpUtils.doGet(name, cookie)
        if (name == null) return
        //String newEpUrl = sj(name, "<script type=\"application/ld+json\">", "</script>");
        name = sj(name, "<script>window.__INITIAL_STATE__=", ";(function")
        if (name.contains("couponSelected")) {
            val finalName = name
//                runOnUiThread {
//                    var epJson: JSONObject? = null
//                    try {
//                        epJson = JSONObject(finalName)
//                        val epArray = epJson.getJSONArray("epList")
//                        val epAsAidArray: MutableList<String> =
//                            ArrayList()
//                        val epAsSetArray: MutableList<String> =
//                            ArrayList()
//                        epAsSetArray.clear()
//                        epAsAidArray.clear()
//                        val names = arrayOfNulls<String>(epArray.length())
//                        for (i in 0 until epArray.length()) {
//                            val VideoData = epArray.getJSONObject(i)
//                            val avid = VideoData.getInt("aid")
//                            val titleFormat = VideoData.getString("titleFormat")
//                            val longTitle = VideoData.getString("longTitle")
//                            val badge = VideoData.getString("badge")
//                            names[i] = "$titleFormat $longTitle $badge"
//                            epAsAidArray.add(avid.toString() + "")
//                        }
//                    val builder = AlertDialog.Builder(this@VideoAsActivity)
//                    builder.setTitle("请选择解析的子集")
//                    //设置Dialog为多选框，且无默认选项（null）
//                    builder.setMultiChoiceItems(
//                        names, null
//                    ) { dialog, which, isChecked ->
//
//                        //设置点击事件：如果选中则添加进choose，如果取消或者未选择则移出choose
//                        if (isChecked) {
//                            epAsSetArray.add(epAsAidArray[which])
//                        } else {
//                            epAsSetArray.remove(epAsAidArray[which])
//                        }
//                    }
//                    //设置正面按钮以及点击事件（土司显示choose内容）
//                    builder.setNegativeButton("取消", null)
//                    builder.setPositiveButton(
//                        "确定"
//                    ) { dialog, which ->
//                        Toast.makeText(this@VideoAsActivity, "选取完成", Toast.LENGTH_SHORT).show()
//                        Thread {
//                            val name: String = HttpUtils.doGet(
//                                "https://api.bilibili.com/x/web-interface/view?aid=" + epAsSetArray[0],
//                                com.imcys.bilibilias.`as`.VideoAsActivity.cookie
//                            )
//                            try {
//                                abvGo(name)
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                            }
//                        }.start()
//                    }
//                    builder.show() //显示Dialog对话框
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
        } else {
            var bvid = sj(name, "aid\":", ",")
            name = HttpUtils.doGet(
                "https://api.bilibili.com/x/web-interface/view?aid=" + bvid,
                cookie
            )
            if (name != null) {
                abvGo(name)
            }
        }
    }

    private fun videoView(jxUrl: String, ImageUrl: String, Title: String, aid: String) {
        var cookie = " "
        //控件定位
//            val batcLinearLayout =
//                findViewById<View>(R.id.As_batchDownload_LinearLayout) as LinearLayout
        var pre = this@MainActivity.viewbinding.AsImageView
//            TextView1 = findViewById<View>(R.id.As_Title) as TextView
//            TextView2 = findViewById<View>(R.id.As_UP) as TextView
//            val PlayText = findViewById<View>(R.id.As_Play) as TextView
//            val DanMuText = findViewById<View>(R.id.As_DanMu) as TextView
//            val FaceImage = findViewById<View>(R.id.As_Up_Face) as ImageView
        val bitmap: Bitmap? = returnBitMap(ImageUrl)
        //显示番剧图片
        var StrData = HttpUtils.doGet(
            jxUrl + "&fnval=1",
            cookie
        )
        StrData = StrData?.let { sj(it, "url\":\"", "\",") }
        StrData = StrData?.let { unicodeDecode(it) }
        //显示番剧图片
        pre.post(Runnable { //加载推荐
//                TabLoad()
            // TODO Auto-generated method stub
//                TextView1.setText(Title)
//                TextView2.setText(upName)
            //设置UP主信息
//                Glide.with(this@VideoAsActivity).load(upFace)
//                    .apply(RequestOptions.bitmapTransform(CircleCrop())).into(FaceImage)
//                PlayText.text =
//                    " 播放:" + VerificationUtils.DigitalConversion(playVolume).toString() + " "
//                DanMuText.text =
//                    " 弹幕:" + VerificationUtils.DigitalConversion(barrageVolume).toString() + " "
            pre.setImageBitmap(bitmap)
//                ListArray(list)
//                ListArrayVideo(listVideo)
//                listCode.add("mp4")
//                listCode.add("mp4【音频视频分离下载最快】")
//                listCode.add("flv")
//                ListArrayCode(listCode)
            val jzDataSource = JZDataSource(StrData, Title)
            jzDataSource.headerMap.put(
                "Cookie",
                cookie
            )
            jzDataSource.headerMap.put(
                "Referer",
                "https://www.bilibili.com/video/av" + aid + "/"
            )
            jzDataSource.headerMap.put(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0"
            )
            var jzVideoPlayerStandard = this@MainActivity.viewbinding.AsVideoPlayer
            jzVideoPlayerStandard.setUp(jzDataSource, JzvdStd.SCREEN_NORMAL)
            Glide.with(this@MainActivity).load(ImageUrl)
                .into(jzVideoPlayerStandard.posterImageView)
            //判断是否展示批量下载按钮
//                if (list.size > 1) {
//                    batcLinearLayout.visibility = View.VISIBLE
//                } else {
//                    batchIF = false
//                    batcLinearLayout.visibility = View.GONE
//                }
        })
//            pd2.cancel()
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