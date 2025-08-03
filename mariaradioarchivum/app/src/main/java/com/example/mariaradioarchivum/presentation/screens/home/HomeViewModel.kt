package com.example.mariaradioarchivum.presentation.screens.home

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mariaradioarchivum.PlaybackController
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
    private val downloaderRepository: DownloaderRepository,
    private val playbackController: PlaybackController
): ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        viewModelScope.launch {
            getRecordings()
        }

        uiState = uiState.copy(
            recordingFormattedDate = LocalDate.ofEpochDay(uiState.recordingDate).format(uiState.dateFormatter),
            recordingFormattedInterval = "${uiState.recordingInterval}:00 - ${(uiState.recordingInterval + 1) % 24}:00"
        )
        viewModelScope.launch {
            playbackController.playbackState.collect { state ->
                uiState = uiState.copy(
                    idOfPlayingRecording = if (state.isPlaying) state.recordingId else -1,
                    recordingToPlay = uiState.recordings.find { it.id == state.recordingId }
                        ?.copy(position = state.position) ?: uiState.recordingToPlay,
                )
            }
        }
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

                uiState = uiState.copy(
                    recordingFormattedDate = LocalDate.ofEpochDay(uiState.recordingDate).format(
                    uiState.dateFormatter)
                )
            }
            is HomeUiEvent.UpdateRecordingDateEvent -> {
                uiState = uiState.copy(
                    recordingFormattedDate = event.formattedDate,
                    recordingDate = event.epochDays
                )
            }
            is HomeUiEvent.DownloadRecordingEvent -> {
                uiState = uiState.copy(isLoadingDialogVisible = true)

                val uriString = getMariaRadioArchiveUri(
                    uiState.recordingDate,
                    uiState.recordingInterval
                )

                downloaderRepository.downloadFile(
                    uriString,
                    onComplete = {
                        addRecording(event.context, uriString.toUri())

                        uiState = uiState.copy(
                            isLoadingDialogVisible = false
                        )
                    }
                )
            }
            is HomeUiEvent.DeleteEditedRecordingEvent -> {
                onEvent(HomeUiEvent.PauseRecordingEvent)
                deleteRecording(uiState.recordingToEdit)
                if (uiState.recordingToPlay.id == uiState.recordingToEdit.id) {
                    playbackController.setRecording(null, context)
                }
            }
            is HomeUiEvent.ChangeDeleteDialogVisibilityEvent -> {
                uiState = uiState.copy(isDeleteDialogVisible = ! uiState.isDeleteDialogVisible)
            }
            is HomeUiEvent.UpdatePlayedRecordingEvent -> {
                playbackController.setRecording(event.recording, event.context)
            }
            is HomeUiEvent.PlayPauseRecordingEvent -> {
                playbackController.playPause()
            }
            is HomeUiEvent.PauseRecordingEvent -> {
                playbackController.pause()
            }
            is HomeUiEvent.PlayRecordingEvent -> {
                playbackController.play()
            }
            is HomeUiEvent.UpdateRecordingPosition -> {
                playbackController.updatePosition(event.value)
            }
            is HomeUiEvent.SetEditedRecordingElementEvent -> {
                uiState = uiState.copy(recordingToEdit = event.recording)
            }
            is HomeUiEvent.JumpForwardMillisecondsInPlayedRecording -> {
                playbackController.jumpForward(event.millis)
            }
            is HomeUiEvent.JumpBackMillisecondsInPlayedRecording -> {
                playbackController.jumpBack(event.millis)
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