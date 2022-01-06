package com.newbie.uier

import android.view.View
import android.widget.MediaController

interface IMediaController {
    fun hide()
    fun isShowing(): Boolean

    fun setAnchorView(view: View?)
    fun setEnabled(enabled: Boolean)
    fun setMediaPlayer(player: MediaController.MediaPlayerControl?)
    fun show(timeout: Int)
    fun show()

    //----------
    // Extends
    //----------
    fun showOnce(view: View?)
}