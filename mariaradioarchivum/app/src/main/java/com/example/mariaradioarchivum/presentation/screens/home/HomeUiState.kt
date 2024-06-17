package com.example.mariaradioarchivum.presentation.screens.home

import android.media.MediaPlayer
import com.example.mariaradioarchivum.data.model.Recording
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

data class HomeUiState(
    val isDeletingModeOn: Boolean = false,
    val isAddRecordingSheetVisible: Boolean = false,
    val isMediaPlayerSheetVisible: Boolean = false,
    val isDatePickerDialogVisible: Boolean = false,

    val idOfPlayingRecording: Int = -1,
    val recordingToPlay: Recording = Recording(),

    val recordingName: String = "",
    val recordingDate: Long = LocalDate.now().toEpochDay(),
    val recordingFormattedDate: String = "",
    val recordingInterval: Int = LocalDateTime.now().minusHours(1).hour,
    val recordingFormattedInterval: String = "",

    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy. MMMM dd.",
        Locale("hu", "HU")),


    val recordings: List<Recording> = emptyList(),

    val mediaPlayer: MediaPlayer? = null
)