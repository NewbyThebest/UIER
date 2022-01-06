package com.newbie.uier

import tv.danmaku.ijk.media.player.IMediaPlayer

abstract class VideoPlayerListener : IMediaPlayer.OnBufferingUpdateListener,
    IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener,
    IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener,
    IMediaPlayer.OnSeekCompleteListener