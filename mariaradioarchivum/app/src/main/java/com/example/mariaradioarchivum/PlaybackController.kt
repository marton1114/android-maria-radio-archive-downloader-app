package com.example.mariaradioarchivum

import android.content.Context
import com.example.mariaradioarchivum.data.model.Recording
import kotlinx.coroutines.flow.StateFlow

interface PlaybackController {
    val playbackState: StateFlow<PlaybackState>

    data class PlaybackState(
        val recordingId: Int = -1,
        val isPlaying: Boolean = false,
        val position: Int = 0
    )

    fun setRecording(recording: Recording?, context: Context)
    fun playPause()
    fun play()
    fun pause()
    fun updatePosition(pos: Int)
    fun jumpForward(ms: Int)
    fun jumpBack(ms: Int)
    fun release()
}