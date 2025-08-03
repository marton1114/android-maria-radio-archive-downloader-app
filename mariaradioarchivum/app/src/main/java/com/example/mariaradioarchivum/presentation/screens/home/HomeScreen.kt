package com.example.mariaradioarchivum.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mariaradioarchivum.presentation.screens.home.components.AddRecordingSheet
import com.example.mariaradioarchivum.presentation.screens.home.components.CustomDatePickerDialog
import com.example.mariaradioarchivum.presentation.screens.home.components.MediaPlayerSheet
import com.example.mariaradioarchivum.presentation.screens.home.components.RecordingElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    val addRecordingSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val mediaPlayerSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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
                .background(MaterialTheme.colorScheme.inverseOnSurface),
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
                verticalArrangement = Arrangement.spacedBy(5.dp),
                contentPadding = PaddingValues(bottom = 136.dp)
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
                            viewModel.onEvent(HomeUiEvent.SetEditedRecordingElementEvent(recording))
                            viewModel.onEvent(HomeUiEvent.ChangeDeleteDialogVisibilityEvent)
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

    if (uiState.isDeleteDialogVisible) {
        AlertDialog(
            title = { Text(text = "Törlés véglegesítése") },
            text = {
                Text(text = "Biztos benne, hogy törölni szeretné a következő felvételt?\n" +
                        "${uiState.recordingToEdit.title}\n" +
                        uiState.recordingToEdit.date
                )
            },
            onDismissRequest = { viewModel.onEvent(HomeUiEvent.ChangeDeleteDialogVisibilityEvent) },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.onEvent(HomeUiEvent.ChangeDeleteDialogVisibilityEvent) }) {
                    Text(text = "Mégsem")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(HomeUiEvent.DeleteEditedRecordingEvent)
                        viewModel.onEvent(HomeUiEvent.ChangeDeleteDialogVisibilityEvent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text(text = "Törlés")
                }
            }
        )
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
        MediaPlayerSheet(
            sheetState = mediaPlayerSheetState,
            recording = uiState.recordingToPlay,
            onDismissRequest = { viewModel.onEvent(HomeUiEvent.ChangeMediaPlayerSheetVisibilityEvent) },
            isPlaying = (uiState.recordingToPlay.id == uiState.idOfPlayingRecording),
            sliderValue = uiState.recordingToPlay.position.toFloat(),
            onSliderValueChange = { value ->
                viewModel.onEvent(HomeUiEvent.UpdateRecordingPosition(value.toInt()))
            },
            onJumpForwardClick = {
                viewModel.onEvent(HomeUiEvent.JumpForwardMillisecondsInPlayedRecording(5000))
            },
            onJumpBackClick = {
                viewModel.onEvent(HomeUiEvent.JumpBackMillisecondsInPlayedRecording(5000))
            },
            onPlayPauseClick = { viewModel.onEvent(HomeUiEvent.PlayPauseRecordingEvent) },
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
        Dialog(onDismissRequest = { }) {
            CircularProgressIndicator()
        }
    }
}
