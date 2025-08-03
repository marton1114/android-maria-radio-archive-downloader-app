package com.example.mariaradioarchivum

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.mariaradioarchivum.data.model.Recording
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlaybackService : Service() {

    private val binder = LocalBinder()

    private var mediaPlayer: MediaPlayer? = null
    private var currentRecording: Recording? = null

    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest
    private var hasAudioFocus = false

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    data class PlaybackState(
        val recording: Recording? = null,
        val isPlaying: Boolean = false,
        val position: Int = 0
    )

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_LOSS -> pausePlayback()
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pausePlayback()
                    AudioManager.AUDIOFOCUS_GAIN -> resumeIfAppropriate()
                }
            }
            .build()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(isPlaying = false))
    }

    override fun onBind(intent: Intent): IBinder = binder

    fun setRecording(recording: Recording?, context: Context) {
        recording?.let { safeRecording ->
            if (currentRecording?.id != safeRecording.id) {
                releasePlayer()
                currentRecording = safeRecording
                mediaPlayer = MediaPlayer.create(context, safeRecording.path.toUri()).apply {
                    isLooping = false
                    setOnCompletionListener {
                        // lejátszás vége
                        updateState(isPlaying = false, position = currentRecording?.duration ?: 0)
                        abandonFocus()
                    }
                }
                mediaPlayer?.seekTo(safeRecording.position)
                updateState(
                    recording = safeRecording,
                    isPlaying = false,
                    position = safeRecording.position
                )
            }
        } ?: run {
            currentRecording = null
            releasePlayer()
            updateState(
                recording = null,
                isPlaying = false,
                position = null
            )
            refreshNotification(isPlaying = false)
        }
    }

    fun playPause() {
        if (mediaPlayer?.isPlaying == true) {
            pausePlayback()
        } else {
            play()
        }
    }

    fun play() {
        if (!requestFocus()) return
        mediaPlayer?.let { mp ->
            mp.start()
            updateState(isPlaying = true)
            refreshNotification(isPlaying = true)
            startPositionUpdater()
        }
    }

    fun pausePlayback() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            updateState(isPlaying = false)
            refreshNotification(isPlaying = false)
            abandonFocus()
        }
    }

    fun updatePosition(pos: Int) {
        mediaPlayer?.seekTo(pos)
        currentRecording = currentRecording?.copy(position = pos)
        updateState(position = pos)
    }

    fun jumpForward(ms: Int) {
        mediaPlayer?.let { mp ->
            val newPos = (mp.currentPosition + ms).coerceAtMost(currentRecording?.duration ?: mp.duration)
            mp.seekTo(newPos)
            currentRecording = currentRecording?.copy(position = newPos)
            updateState(position = newPos)
        }
    }

    fun jumpBack(ms: Int) {
        mediaPlayer?.let { mp ->
            val newPos = (mp.currentPosition - ms).coerceAtLeast(0)
            mp.seekTo(newPos)
            currentRecording = currentRecording?.copy(position = newPos)
            updateState(position = newPos)
        }
    }

    // --- belső segédfüggvények ---
    private fun updateState(
        recording: Recording? = this.currentRecording,
        isPlaying: Boolean? = null,
        position: Int? = null
    ) {
        val prev = _playbackState.value
        _playbackState.value = PlaybackState(
            recording = recording ?: prev.recording,
            isPlaying = isPlaying ?: prev.isPlaying,
            position = position ?: mediaPlayer?.currentPosition ?: prev.position
        )
    }

    private fun requestFocus(): Boolean {
        if (hasAudioFocus) return true
        val result = audioManager.requestAudioFocus(audioFocusRequest)

        hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        return hasAudioFocus
    }

    private fun abandonFocus() {
        if (!hasAudioFocus) return
        audioManager.abandonAudioFocusRequest(audioFocusRequest)

        hasAudioFocus = false
    }

    private fun resumeIfAppropriate() {
        if (mediaPlayer != null && !_playbackState.value.isPlaying) {
            play()
        }
    }

    private var positionUpdaterJob: Job? = null
    private fun startPositionUpdater() {
        positionUpdaterJob?.cancel()
        positionUpdaterJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                updateState(position = mediaPlayer!!.currentPosition)
                delay(500L)
            }
        }
    }

    private fun releasePlayer() {
        positionUpdaterJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        releasePlayer()
        abandonFocus()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Audio lejátszás értesítés" }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val playPauseAction = if (isPlaying) {
            val pauseIntent = Intent(this, AudioPlaybackService::class.java).apply {
                action = ACTION_PAUSE
            }
            val pausePending = PendingIntent.getService(
                this, 0, pauseIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            NotificationCompat.Action.Builder(
                R.drawable.ic_pause,
                "Szünet",
                pausePending
            ).build()
        } else {
            val playIntent = Intent(this, AudioPlaybackService::class.java).apply {
                action = ACTION_PLAY
            }
            val playPending = PendingIntent.getService(
                this, 0, playIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            NotificationCompat.Action.Builder(
                R.drawable.ic_play,
                "Lejátszás",
                playPending
            ).build()
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentRecording?.title ?: "Nincs kiválasztva felvétel")
            .setContentText(currentRecording?.date ?: "")
            .addAction(playPauseAction)
            .setSmallIcon(R.drawable.ic_music)
            .setStyle(mediaStyle)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .setCustomBigContentView(null)
            .build()
    }

    private fun refreshNotification(isPlaying: Boolean) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(isPlaying))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> play()
            ACTION_PAUSE -> pausePlayback()
            ACTION_STOP -> {
                pausePlayback()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    companion object {
        const val CHANNEL_ID = "audio_playback_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
    }
}