package com.example.mariaradioarchivum.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mariaradioarchivum.presentation.screens.home.components.AddRecordingSheet
import com.example.mariaradioarchivum.presentation.screens.home.components.CustomDatePickerDialog
import com.example.mariaradioarchivum.presentation.screens.home.components.MediaPlayerSheet
import com.example.mariaradioarchivum.presentation.screens.home.components.RecordingElement
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    val addRecordingSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val mediaPlayerSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val context = LocalContext.current
    var deleteButtonColor = MaterialTheme.colorScheme.errorContainer
    if (uiState.isDeletingModeOn) {
        deleteButtonColor = MaterialTheme.colorScheme.error
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(text = "Törlés") },
                    icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null) },
                    containerColor = deleteButtonColor,
                    onClick = {
                        viewModel.onEvent(HomeUiEvent.ChangeDeleteButtonVisibilityEvent)
                    }
                )
                ExtendedFloatingActionButton(
                    text = { Text(text = "Hozzáadás") },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                    onClick = { viewModel.onEvent(HomeUiEvent.ChangeAddRecordingSheetVisibilityEvent) })
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    MaterialTheme.colorScheme.inverseOnSurface
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            MaterialTheme.colorScheme.primaryContainer,
//                            MaterialTheme.colorScheme.tertiaryContainer
//                        )
//                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(title = {
                Text(text = "Letöltött hangfelvételek", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
            }, colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ))
            
            LazyColumn(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(uiState.recordings) { recording ->
                    RecordingElement(
                        title = recording.title,
                        date = recording.date,
                        isPlaying = (recording.id == uiState.idOfPlayingRecording),
                        isDeleteVisible = uiState.isDeletingModeOn,
                        onPlayClick = {
                            viewModel.onEvent(HomeUiEvent.UpdatePlayedRecordingEvent(context, recording))
                            viewModel.onEvent(HomeUiEvent.PlayPauseRecordingEvent)
                        },
                        onTrashClicked = {
                            viewModel.onEvent(HomeUiEvent.DeleteRecordingEvent(context, recording))
                        }
                    ) {
                        viewModel.onEvent(HomeUiEvent.UpdatePlayedRecordingEvent(context, recording))
                        viewModel.onEvent(HomeUiEvent.ChangeMediaPlayerSheetVisibilityEvent)
                        viewModel.onEvent(HomeUiEvent.PlayRecordingEvent)
                    }
                }
            }
        }
    }

    if (uiState.isAddRecordingSheetVisible) {
        AddRecordingSheet(
            sheetState = addRecordingSheetState,
            recordingNameValue = uiState.recordingName,
            onRecordingValueChange = { viewModel.onEvent(HomeUiEvent.UpdateRecordingNameEvent(it)) },
            onDismissRequest = { viewModel.onEvent(HomeUiEvent.ChangeAddRecordingSheetVisibilityEvent) },
            recordingDate = uiState.recordingFormattedDate,
            onPreviousDayClick = { viewModel.onEvent(HomeUiEvent.DecreaseDateEvent) },
            onDateClick = { viewModel.onEvent(HomeUiEvent.ChangeDatePickerDialogVisibilityEvent) },
            onNextDayClick = { viewModel.onEvent(HomeUiEvent.IncreaseDateEvent) },
            recordingInterval = uiState.recordingFormattedInterval,
            onPreviousIntervalClick = { viewModel.onEvent(HomeUiEvent.DecreaseIntervalEvent) },
            onIntervalClick = {},
            onNextIntervalClick = { viewModel.onEvent(HomeUiEvent.IncreaseIntervalEvent) },
            onDownloadClick = {
                viewModel.onEvent(HomeUiEvent.DownloadRecordingEvent(context))
                viewModel.onEvent(HomeUiEvent.ChangeAddRecordingSheetVisibilityEvent)
            }
        )
    }

    if (uiState.isMediaPlayerSheetVisible) {
        // currently the best way to handle slider with mediaplayer
        LaunchedEffect(key1 = Unit) {
            while (uiState.isMediaPlayerSheetVisible) {
                viewModel.onEvent(HomeUiEvent.UpdateRecordingPosition(uiState.mediaPlayer?.currentPosition ?: 0))
                delay(1000)
            }
        }

        MediaPlayerSheet(
            sheetState = mediaPlayerSheetState,
            recording = uiState.recordingToPlay,
            onDismissRequest = { viewModel.onEvent(HomeUiEvent.ChangeMediaPlayerSheetVisibilityEvent) },
            isPlaying = (uiState.recordingToPlay.id == uiState.idOfPlayingRecording),
            sliderValue = uiState.recordingToPlay.position.toFloat(),
            onSliderValueChange = { value ->
                uiState.mediaPlayer?.seekTo(value.toInt())
                viewModel.onEvent(HomeUiEvent.UpdateRecordingPosition(value.toInt()))
            },
            onJumpForwardClick = {
                val currentPosition = uiState.mediaPlayer?.currentPosition ?: 0
                val duration = uiState.recordingToPlay.duration

                if (currentPosition + 5000 <= duration)
                    uiState.mediaPlayer?.seekTo(currentPosition + 5000)
                else
                    uiState.mediaPlayer?.seekTo(duration)

            },
            onJumpBackClick = {
                val currentPosition: Int = uiState.mediaPlayer?.currentPosition ?: 0
                if (currentPosition - 5000 >= 0)
                    uiState.mediaPlayer?.seekTo(currentPosition - 5000)
                else
                    uiState.mediaPlayer?.seekTo(0)
            },
            onPlayPauseClick = {
                viewModel.onEvent(HomeUiEvent.PlayPauseRecordingEvent)
            },
            onSkipBackwardClick = { HomeUiEvent.IncrementPlayedRecordingEvent(context) },
            onSkipForwardClick = { HomeUiEvent.DecrementPlayedRecordingEvent(context) },
        )

    }
    if (uiState.isDatePickerDialogVisible) {
        CustomDatePickerDialog(
            onDateSelected = { formattedDate, epochDays ->
                viewModel.onEvent(HomeUiEvent.UpdateRecordingDateEvent(
                    formattedDate,
                    epochDays
                ))
            },
            localDateFormatter = uiState.dateFormatter,
            onDismiss = { viewModel.onEvent(HomeUiEvent.ChangeDatePickerDialogVisibilityEvent) },
        )
    }

    if (uiState.isLoadingDialogVisible) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            CircularProgressIndicator()
        }
    }
}
