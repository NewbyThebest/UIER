package com.newbie.uier

import android.content.Context
import tv.danmaku.ijk.media.player.IjkMediaPlayer

import android.view.SurfaceHolder

import android.view.Gravity

import android.view.SurfaceView

import android.text.TextUtils
import android.util.AttributeSet
import android.view.View

import androidx.annotation.AttrRes

import android.widget.FrameLayout
import android.widget.MediaController
import androidx.annotation.Nullable
import java.io.IOException


class PlayerView : FrameLayout, MediaController.MediaPlayerControl {
    /**
     * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
     */
    private var mMediaPlayer: IjkMediaPlayer? = null
    private var mMediaController: IMediaController? = null
    /**
     * 视频文件地址
     */
    private var mPath = ""
    private var surfaceView: SurfaceView? = null
    private var listener: VideoPlayerListener? = null
    private var mContext: Context? = null

    private var mAid = ""

    constructor(context: Context) : super(context) {
        initVideoView(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        initVideoView(context)
    }

    constructor(
        context: Context,
        @Nullable attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initVideoView(context)
    }

    private fun initVideoView(context: Context) {
        mContext = context

        //获取焦点，不知道有没有必要~。~
        isFocusable = true
    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     *
     * @param path the path of the video.
     */
    fun setVideoPath(path: String, aid: String) {
        mAid = aid
        if (TextUtils.equals("", mPath)) {
            //如果是第一次播放视频，那就创建一个新的surfaceView
            mPath = path
            createSurfaceView()
        } else {
            //否则就直接load
            mPath = path
            load()
        }
    }

    /**
     * 新建一个surfaceview
     */
    private fun createSurfaceView() {
        //生成一个新的surface view
        surfaceView = SurfaceView(mContext)
        surfaceView!!.holder.addCallback(LmnSurfaceCallback())
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER
        )
        surfaceView!!.layoutParams = layoutParams
        this.addView(surfaceView)
    }

    /**
     * surfaceView的监听器
     */
    private inner class LmnSurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {}
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            //surfaceview创建成功后，加载视频
            load()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {}
    }

    /**
     * 加载视频
     */
    private fun load() {
        //每次都要重新创建IMediaPlayer
        createPlayer()
        try {
            val map = mutableMapOf<String,String>()
            map["Referer"] =
                "https://www.bilibili.com/video/av" + mAid + "/"
            map["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0"
            mMediaPlayer!!.setDataSource(mPath, map)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //给mediaPlayer设置视图
        mMediaPlayer!!.setDisplay(surfaceView!!.holder)
        mMediaPlayer!!.prepareAsync()
    }

    /**
     * 创建一个新的player
     */
    private fun createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.setDisplay(null)
            mMediaPlayer!!.release()
        }
        val ijkMediaPlayer = IjkMediaPlayer()
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG)

//        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
        mMediaPlayer = ijkMediaPlayer
        attachMediaController();
        if (listener != null) {
            mMediaPlayer!!.setOnPreparedListener(listener)
            mMediaPlayer!!.setOnInfoListener(listener)
            mMediaPlayer!!.setOnSeekCompleteListener(listener)
            mMediaPlayer!!.setOnBufferingUpdateListener(listener)
            mMediaPlayer!!.setOnErrorListener(listener)
        }
    }

    fun setMediaController(controller: IMediaController) {
        mMediaController?.hide()
        mMediaController = controller
        attachMediaController()
    }

    private fun attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController!!.setMediaPlayer(this)
            val anchorView: View = if (this.parent is View) this.parent as View else this
            mMediaController!!.setAnchorView(anchorView)
//            mMediaController!!.setEnabled(isInPlaybackState())
        }
    }

    fun setListener(listener: VideoPlayerListener?) {
        this.listener = listener
        if (mMediaPlayer != null) {
            mMediaPlayer!!.setOnPreparedListener(listener)
        }
    }

    /**
     * -------======--------- 下面封装了一下控制视频的方法
     */
    override fun start() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.start()
        }
    }

    fun release() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    override fun pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()
        }
    }

    override fun getDuration(): Int {
        TODO("Not yet implemented")
    }

    override fun getCurrentPosition(): Int {
        TODO("Not yet implemented")
    }

    override fun seekTo(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBufferPercentage(): Int {
        TODO("Not yet implemented")
    }

    override fun canPause(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSeekBackward(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSeekForward(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAudioSessionId(): Int {
        TODO("Not yet implemented")
    }

    fun stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
        }
    }

    fun reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
        }
    }

    val duration: Long
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.duration
        } else {
            0
        }
    val currentPosition: Long
        get() {
            return if (mMediaPlayer != null) {
                mMediaPlayer!!.currentPosition
            } else {
                0
            }
        }

    fun seekTo(l: Long) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.seekTo(l)
        }
    }
}