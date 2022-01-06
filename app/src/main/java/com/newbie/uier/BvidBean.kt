package com.newbie.uier

data class BvidBean(
    val bvid : String,
    val aid : String,
    val tname : String,
    val pic : String,
    val title : String,
    val pubdate : String,
    val ctime : String,
    val desc : String,
    val duration : String,
    val pages : List<CidBean>
)
