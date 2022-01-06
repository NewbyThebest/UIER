package com.newbie.uier

data class VideoBean(
    val durl : List<urlBean>
){
    data class urlBean(
        val length : String,
        val size : String,
        val url : String,
    )
}
