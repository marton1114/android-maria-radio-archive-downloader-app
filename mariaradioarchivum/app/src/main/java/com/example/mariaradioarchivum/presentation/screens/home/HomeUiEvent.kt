package com.example.mariaradioarchivum.presentation.screens.home

import android.content.Context
import com.example.mariaradioarchivum.MainActivity
import com.example.mariaradioarchivum.data.model.Recording

interface HomeUiEvent {
    data object ChangeDeleteButtonVisibilityEvent: HomeUiEvent
    data object ChangeAddRecordingSheetVisibilityEvent: HomeUiEvent
    data object ChangeMediaPlayerSheetVisibilityEvent: HomeUiEvent
    data object ChangeDeleteDialogVisibilityEvent: HomeUiEvent
    data object DecreaseIntervalEvent: HomeUiEvent
    data object IncreaseIntervalEvent: HomeUiEvent
    data object DecreaseDateEvent: HomeUiEvent
    data object IncreaseDateEvent: HomeUiEvent
    data object ChangeDatePickerDialogVisibilityEvent: HomeUiEvent

    data class SetEditedRecordingElementEvent(val recording: Recording): HomeUiEvent
    data class DownloadRecordingEvent(val context: Context): HomeUiEvent
    data class UpdateRecordingNameEvent(val value: String): HomeUiEvent
    data class UpdateRecordingDateEvent(val formattedDate: String, val epochDays: Long): HomeUiEvent
    data object DeleteEditedRecordingEvent: HomeUiEvent
    data class IncrementPlayedRecordingEvent(val context: Context): HomeUiEvent
    data class DecrementPlayedRecordingEvent(val context: Context): HomeUiEvent
    data class UpdatePlayedRecordingEvent(val context: Context, val recording: Recording): HomeUiEvent
    data object PlayPauseRecordingEvent: HomeUiEvent
    data object PauseRecordingEvent: HomeUiEvent
    data object PlayRecordingEvent: HomeUiEvent
    data class UpdateRecordingPosition(val value: Int): HomeUiEvent
    data class JumpForwardMillisecondsInPlayedRecording(val millis: Int): HomeUiEvent
    data class JumpBackMillisecondsInPlayedRecording(val millis: Int): HomeUiEvent
}