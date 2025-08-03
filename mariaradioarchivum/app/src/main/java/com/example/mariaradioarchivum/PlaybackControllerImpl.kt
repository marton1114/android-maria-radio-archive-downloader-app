package com.example.mariaradioarchivum

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.example.mariaradioarchivum.data.model.Recording
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaybackControllerImpl(
    private val context: Context
) : PlaybackController {

    private var service: AudioPlaybackService? = null
    private var bound = false

    private val _playbackState = MutableStateFlow(PlaybackController.PlaybackState())
    override val playbackState: StateFlow<PlaybackController.PlaybackState> = _playbackState.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val local = binder as? AudioPlaybackService.LocalBinder
            service = local?.getService()
            bound = true
            observeService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            service = null
        }
    }

    init {
        Intent(context, AudioPlaybackService::class.java).also { intent ->
            ContextCompat.startForegroundService(context, intent)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private var job: Job? = null
    private fun observeService() {
        job?.cancel()
        service?.let { svc ->
            job = CoroutineScope(Dispatchers.Main).launch {
                svc.playbackState.collect { state ->
                    _playbackState.value = PlaybackController.PlaybackState(
                        recordingId = state.recording?.id ?: -1,
                        isPlaying = state.isPlaying,
                        position = state.position
                    )
                }
            }
        }
    }

    override fun setRecording(recording: Recording?, context: Context) {
        service?.setRecording(recording, context)
    }

    override fun playPause() {
        service?.playPause()
    }

    override fun play() {
        service?.play()
    }

    override fun pause() {
        service?.pausePlayback()
    }

    override fun updatePosition(pos: Int) {
        service?.updatePosition(pos)
    }

    override fun jumpForward(ms: Int) {
        service?.jumpForward(ms)
    }

    override fun jumpBack(ms: Int) {
        service?.jumpBack(ms)
    }

    override fun release() {
        job?.cancel()
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }
}
