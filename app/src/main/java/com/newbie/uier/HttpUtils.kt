package com.newbie.uier

import android.text.TextUtils
import android.util.Log
import java.io.*
import java.lang.Exception
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL

object HttpUtils {
    private const val TIMEOUT_IN_MILLIONS = 5000
    private val key: String? = null
    private val cookieVal: String? = null
    private val sessionId: String? = null

    /**
     * 异步的Get请求
     *
     * @param urlStr
     * @param callBack
     */
    fun doGetAsyn(urlStr: String?, callBack: CallBack?) {
        object : Thread() {
            override fun run() {
                try {
                    val result = doGet(urlStr, "")
                    callBack?.onRequestComplete(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 异步的Post请求
     *
     * @param urlStr
     * @param params
     * @param callBack
     * @throws Exception
     */
    @Throws(Exception::class)
    fun doPostAsyn(
        urlStr: String?, params: String?,
        callBack: CallBack?
    ) {
        object : Thread() {
            override fun run() {
                try {
                    val result = doPost(urlStr, params, "")
                    callBack?.onRequestComplete(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    fun doGet(urlStr: String?, Cookie: String?): String? {
        var url: URL? = null
        var conn: HttpURLConnection? = null
        var `is`: InputStream? = null
        var baos: ByteArrayOutputStream? = null
        try {
            url = URL(urlStr)
            conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = TIMEOUT_IN_MILLIONS
            conn!!.connectTimeout = TIMEOUT_IN_MILLIONS
            conn.requestMethod = "GET"
            conn.setRequestProperty("accept", "*/*")
            conn.setRequestProperty("connection", "Keep-Alive")
            conn.setRequestProperty("Content-type", "Keep-Alive")
            conn.setRequestProperty("accept", "text/html")
            conn.setRequestProperty("Accept-Charset", "utf-8") //设置编码语言
            conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36"
            )
//            conn.setRequestProperty("cookie", Cookie)
            return if (conn.responseCode == 200) {
                `is` = conn.inputStream
                baos = ByteArrayOutputStream()
                var len = -1
                val buf = ByteArray(128)
                while (`is`.read(buf).also { len = it } != -1) {
                    baos.write(buf, 0, len)
                }
                baos.flush()
                baos.toString()
            } else {
                throw RuntimeException(conn.responseCode.toString() + " responseCode is not 200 ... ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
            }
            try {
                baos?.close()
            } catch (e: IOException) {
            }
            conn!!.disconnect()
        }
        return null
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    fun doPost(url: String?, param: String?, Cookie: String?): String {
        var out: PrintWriter? = null
        var `in`: BufferedReader? = null
        var result = ""
        try {
            val realUrl = URL(url)
            // 打开和URL之间的连接
            val conn = realUrl
                .openConnection() as HttpURLConnection
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*")
            conn.setRequestProperty("connection", "Keep-Alive")
            conn.requestMethod = "POST"
            conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0"
            )
            conn.setRequestProperty("cookie", Cookie)
            conn.setRequestProperty("charset", "utf-8")
            conn.setRequestProperty("referer", "https://www.bilibili.com/bangumi/play/ep93415")
            conn.useCaches = false
            // 发送POST请求必须设置如下两行
            conn.doOutput = true
            conn.doInput = true
            conn.readTimeout = TIMEOUT_IN_MILLIONS
            conn.connectTimeout = TIMEOUT_IN_MILLIONS
            val sessionId = ""
            val cookieVal = ""
            val key: String? = null
            if (param != null && param.trim { it <= ' ' } != "") {
                // 获取URLConnection对象对应的输出流
                out = PrintWriter(conn.outputStream)
                // 发送请求参数
                out.print(param)
                // flush输出流的缓冲
                out.flush()
            }

            // 定义BufferedReader输入流来读取URL的响应
            `in` = BufferedReader(
                InputStreamReader(conn.inputStream)
            )
            var line: String
            while (`in`.readLine().also { line = it } != null) {
                result += line
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } // 使用finally块来关闭输出流、输入流
        finally {
            try {
                out?.close()
                `in`?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return result
    }

    /**
     * post方式请求 获取ck
     * @param loginAction
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getCookie(param: String, loginAction: String?): String {
        //登录
        val url = URL(loginAction)
        val conn = url.openConnection() as HttpURLConnection
        conn.doInput = true
        conn.doOutput = true
        conn.requestMethod = "POST"
        val out = conn.outputStream
        out.write(param.toByteArray())
        out.flush()
        out.close()
        var sessionId = ""
        var cookieVal = ""
        var key: String? = null
        //取cookie
        var i = 1
        while (conn.getHeaderFieldKey(i).also { key = it } != null) {
            if (key.equals("set-cookie", ignoreCase = true)) {
                cookieVal = conn.getHeaderField(i)
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"))
                sessionId = "$sessionId$cookieVal;"
            }
            i++
        }
        return sessionId
    }

    /**
     * 异步Post请求，提交json参数
     * @param urlPath
     * @param json
     * @param callBack
     */
    fun doJsonPostAnsy(urlPath: String?, json: String?, callBack: CallBack?) {
        object : Thread() {
            override fun run() {
                try {
                    val result = doJsonPost(urlPath, json)
                    callBack?.onRequestComplete(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
    //发送JSON字符串 如果成功则返回成功标识。
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param urlPath   发送请求的 URL
     * @param json 请求参数，请求参数应该是 "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\":[\"0x504b96ac2c9f3ffe39545e342446548059d501b1\",\"latest\"],\"id\":1}" 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    fun doJsonPost(urlPath: String?, json: String?): String {
        // HttpClient 6.0被抛弃了
        var result = ""
        var reader: BufferedReader? = null
        try {
            val url = URL(urlPath)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            // 发送POST请求必须设置如下两行
            conn.doOutput = true
            conn.doInput = true
            conn.readTimeout = TIMEOUT_IN_MILLIONS
            conn.connectTimeout = TIMEOUT_IN_MILLIONS
            conn.useCaches = false
            conn.setRequestProperty("Connection", "Keep-Alive")
            conn.setRequestProperty("Charset", "UTF-8")
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            // 设置接收类型否则返回415错误
            //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
            conn.setRequestProperty("accept", "application/json")
            // 往服务器里面发送数据
            if (json != null && !TextUtils.isEmpty(json)) {
                // 使用HttpURLConnection提交JSON数据的时候编码方式为UTF-8
                // 全部中文字符请一定要预先转码为UTF-8，然后在后台server相应的API
                // 中解码为UTF-8，不然就会报错HTTP 400。
                // json = java.net.URLEncoder.encode(json, "utf-8");
                val writebytes = json.toByteArray()
                // 设置文件长度
                conn.setRequestProperty("Content-Length", writebytes.size.toString())
                val outwritestream = conn.outputStream
                outwritestream.write(json.toByteArray())
                outwritestream.flush()
                outwritestream.close()
                Log.d("hlhupload", "doJsonPost: conn" + conn.responseCode)
            }
            if (conn.responseCode == 200) {
                reader = BufferedReader(
                    InputStreamReader(conn.inputStream)
                )
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    result += line
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    interface CallBack {
        fun onRequestComplete(result: String?)
    }
}
