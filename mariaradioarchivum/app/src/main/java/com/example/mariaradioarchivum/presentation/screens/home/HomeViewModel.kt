package com.example.mariaradioarchivum.presentation.screens.home

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mariaradioarchivum.data.model.Recording
import com.example.mariaradioarchivum.data.repository.DownloaderRepository
import com.example.mariaradioarchivum.data.repository.RecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val recordingsRepository: RecordingRepository,
    private val downloaderRepository: DownloaderRepository
): ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest
    init {
        viewModelScope.launch {
            getRecordings()
        }

        uiState = uiState.copy(
            recordingFormattedDate = LocalDate.ofEpochDay(uiState.recordingDate).format(uiState.dateFormatter),
            recordingFormattedInterval = "${uiState.recordingInterval}:00 - ${(uiState.recordingInterval + 1) % 24}:00"
        )

        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener { focusChange ->
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
                    ) {
                        if (uiState.mediaPlayer?.isPlaying == true) {
                            uiState = uiState.copy(
                                idOfPlayingRecording = -1
                            )
                            if (uiState.mediaPlayer != null) {
                                uiState = uiState.copy(
                                    recordingToPlay = uiState.recordingToPlay.copy(position = uiState.mediaPlayer!!.currentPosition)
                                )
                            }
                            uiState.mediaPlayer?.pause()
                            updateRecording(uiState.recordingToPlay)
                        }
                    }
            }.build()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.ChangeDeleteButtonVisibilityEvent -> {
                uiState = uiState.copy(isDeletingModeOn = !uiState.isDeletingModeOn)
            }
            is HomeUiEvent.ChangeAddRecordingSheetVisibilityEvent -> {
                uiState = uiState.copy(isAddRecordingSheetVisible = !uiState.isAddRecordingSheetVisible)
            }
            is HomeUiEvent.ChangeMediaPlayerSheetVisibilityEvent -> {
                uiState = uiState.copy(isMediaPlayerSheetVisible = !uiState.isMediaPlayerSheetVisible)
            }
            is HomeUiEvent.UpdateRecordingNameEvent -> {
                uiState = uiState.copy(recordingName = event.value)
            }
            is HomeUiEvent.ChangeDatePickerDialogVisibilityEvent -> {
                uiState = uiState.copy(isDatePickerDialogVisible = !uiState.isDatePickerDialogVisible)
            }
            is HomeUiEvent.DecreaseIntervalEvent -> {

                uiState = uiState.copy(
                    recordingInterval = hourBefore(uiState.recordingInterval)
                )
                uiState = uiState.copy(
                    recordingFormattedInterval = "${uiState.recordingInterval}:00 - ${hourAfter(uiState.recordingInterval)}:00"
                )
            }
            is HomeUiEvent.IncreaseIntervalEvent -> {
                uiState = uiState.copy(
                    recordingInterval = hourAfter(uiState.recordingInterval)
                )
                uiState = uiState.copy(
                    recordingFormattedInterval = "${uiState.recordingInterval}:00 - ${hourAfter(uiState.recordingInterval)}:00"
                )
            }
            is HomeUiEvent.DecreaseDateEvent -> {
                uiState = uiState.copy(
                    recordingDate = LocalDate.ofEpochDay(uiState.recordingDate).minusDays(1).toEpochDay()
                )

                uiState = uiState.copy(recordingFormattedDate = LocalDate.ofEpochDay(uiState.recordingDate).format(
                    uiState.dateFormatter))
            }
            is HomeUiEvent.IncreaseDateEvent -> {
                uiState = uiState.copy(
                    recordingDate = LocalDate.ofEpochDay(uiState.recordingDate).plusDays(1).toEpochDay()
                )

                uiState = uiState.copy(recordingFormattedDate = LocalDate.ofEpochDay(uiState.recordingDate).format(
                    uiState.dateFormatter))
            }
            is HomeUiEvent.UpdateRecordingDateEvent -> {
                uiState = uiState.copy(
                    recordingFormattedDate = event.formattedDate,
                    recordingDate = event.epochDays
                )
            }
            is HomeUiEvent.DownloadRecordingEvent -> {
                val uriString = getMariaRadioArchiveUri(
                    uiState.recordingDate,
                    uiState.recordingInterval
                )
                val uri = uriString.toUri()

                uiState = uiState.copy(
                    isLoadingDialogVisible = true
                )

                downloaderRepository.downloadFile(
                    uriString,
                    onComplete = {
                        addRecording(event.context, uri)

                        uiState = uiState.copy(
                            isLoadingDialogVisible = false
                        )
                    }
                )
            }
            is HomeUiEvent.DeleteRecordingEvent -> {
                deleteRecording(event.recording)
            }
            is HomeUiEvent.UpdatePlayedRecordingEvent -> {
                if (uiState.mediaPlayer == null) {
                    uiState = uiState.copy(
                        recordingToPlay = event.recording
                    )

                    uiState = uiState.copy(
                        idOfPlayingRecording = uiState.recordingToPlay.id
                    )

                    uiState = uiState.copy(
                        mediaPlayer = MediaPlayer.create(
                            event.context, uiState.recordings.last {
                                it.id == uiState.idOfPlayingRecording
                            }.path.toUri()
                        )
                    )
                    uiState.mediaPlayer?.seekTo(uiState.recordingToPlay.position)

                } else if (uiState.recordingToPlay.id != event.recording.id) {
                    uiState = uiState.copy(
                        recordingToPlay = event.recording
                    )

                    uiState = uiState.copy(
                        idOfPlayingRecording = uiState.recordingToPlay.id
                    )

                    uiState.mediaPlayer?.reset()

                    uiState = uiState.copy(
                        mediaPlayer = MediaPlayer.create(
                            event.context, uiState.recordings.last {
                                it.id == uiState.idOfPlayingRecording
                            }.path.toUri()
                        )
                    )
                    uiState.mediaPlayer?.seekTo(uiState.recordingToPlay.position)
                }
            }
            is HomeUiEvent.PlayPauseRecordingEvent -> {
                if (uiState.mediaPlayer?.isPlaying == true) {
                    uiState = uiState.copy(
                        idOfPlayingRecording = -1
                    )
                    if (uiState.mediaPlayer != null) {
                        uiState = uiState.copy(
                            recordingToPlay = uiState.recordingToPlay.copy(position = uiState.mediaPlayer!!.currentPosition)
                        )
                    }
                    uiState.mediaPlayer?.pause()
                    updateRecording(uiState.recordingToPlay)

                    audioManager?.abandonAudioFocusRequest(audioFocusRequest);

                } else {
                    val result = audioManager!!.requestAudioFocus(audioFocusRequest)

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        uiState = uiState.copy(
                            idOfPlayingRecording = uiState.recordingToPlay.id
                        )
                        uiState.mediaPlayer?.start()
                    }

                }

            }
            is HomeUiEvent.PlayRecordingEvent -> {
                val result = audioManager!!.requestAudioFocus(audioFocusRequest)
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    uiState = uiState.copy(
                        idOfPlayingRecording = uiState.recordingToPlay.id
                    )
                    uiState.mediaPlayer?.start()
                }
            }
            is HomeUiEvent.UpdateRecordingPosition -> {
                uiState = uiState.copy(
                    recordingToPlay = uiState.recordingToPlay.copy(
                        position = event.value
                    )
                )
            }
        }
    }

    private fun addRecording(context: Context, uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        val file = File(
            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            uri.lastPathSegment.toString()
        )

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, file.path.toUri())
        val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()

        recordingsRepository.addRecordingToRoom(
            Recording(
                title = uiState.recordingName,
                date = "${uiState.recordingFormattedDate} | ${uiState.recordingFormattedInterval}",
                path = file.path,
                position = 0,
                duration = duration!!,
            )
        )
    }

    private fun deleteRecording(recording: Recording) = viewModelScope.launch(Dispatchers.IO) {
        val file = File(recording.path)

        if (file.delete()) {
            recordingsRepository.deleteRecordingFromRoom(recording)
            uiState = uiState.copy(
                isDeletingModeOn = ! uiState.isDeletingModeOn
            )
        }
    }

    private fun updateRecording(recording: Recording) = viewModelScope.launch(Dispatchers.IO) {
        recordingsRepository.updateRecordingFromRoom(
            recording
        )
    }

    private fun hourBefore(hour: Int): Int {
        return if (hour != 0)
            hour - 1
        else
            23
    }

    private fun hourAfter(hour: Int): Int {
        return (hour + 1) % 24
    }

    private suspend fun getRecordings() {
        recordingsRepository.getRecordingsFromRoom().collect {
            uiState = uiState.copy(recordings = it)
        }
    }

    private fun getMariaRadioArchiveUri(epochDays: Long, interval: Int): String {
        val formattedDate = LocalDate.ofEpochDay(epochDays)
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd",
                Locale("hu", "HU")))

        return String.format(Locale.ENGLISH, "http://archivum.mariaradio.ro/Archivum/MRE_%s_%02d-00.mp3", formattedDate, interval)
    }

}