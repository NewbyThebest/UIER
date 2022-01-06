package com.newbie.uier

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.MediaController
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBar


class AndroidMediaController : MediaController, IMediaController {
    private var mActionBar: ActionBar? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, useFastForward: Boolean) : super(context, useFastForward) {
        initView(context)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    private fun initView(context: Context) {}
    fun setSupportActionBar(@Nullable actionBar: ActionBar) {
        mActionBar = actionBar
        if (isShowing()) {
            actionBar.show()
        } else {
            actionBar.hide()
        }
    }

    override fun show() {
        super.show()
        mActionBar?.show()
    }

    override fun hide() {
        super.hide()
        mActionBar?.hide()
        for (view in mShowOnceArray) view.setVisibility(View.GONE)
        mShowOnceArray.clear()
    }

    override fun isShowing(): Boolean {
        return super.isShowing()
    }

    //----------
    // Extends
    //----------
    private val mShowOnceArray: ArrayList<View> = ArrayList<View>()
    override fun showOnce(view: View?) {
        if (view != null) {
            mShowOnceArray.add(view)
        }
        view?.setVisibility(View.VISIBLE)
        show()
    }
}