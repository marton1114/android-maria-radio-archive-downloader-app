package com.example.mariaradioarchivum.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mariaradioarchivum.presentation.screens.home.components.AddRecordingSheet
import com.example.mariaradioarchivum.presentation.screens.home.components.MediaPlayerSheet
import com.example.mariaradioarchivum.presentation.screens.home.components.RecordingElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    val addRecordingSheetState = rememberModalBottomSheetState()
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(title = {
                Text(text = "Letöltött hangfelvételek", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
            }, colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ))
            
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                RecordingElement("Teszt cím 1", "2024.01.12. 14:00", true, uiState.isDeletingModeOn, {}) {
                    viewModel.onEvent(HomeUiEvent.ChangeMediaPlayerSheetVisibilityEvent)
                }
                RecordingElement("Teszt cím 124", "2024.01.12. 14:00", false, uiState.isDeletingModeOn, {}) {}
                RecordingElement("Teszt cím 41", "2024.01.12. 14:00", false, uiState.isDeletingModeOn, {}) {}
            }
        }
    }

    if (uiState.isAddRecordingSheetVisible) {
        AddRecordingSheet(
            sheetState = addRecordingSheetState,
            onDownloadClick = {},
            onDismissRequest = { viewModel.onEvent(HomeUiEvent.ChangeAddRecordingSheetVisibilityEvent) }
        )
    }
    if (uiState.isMediaPlayerSheetVisible) {
        MediaPlayerSheet(
            title = "Teszt cím",
            date = "2024.01.14. 14:00",
            isPlaying = false,
            sheetState = mediaPlayerSheetState,
            onDismissRequest = { viewModel.onEvent(HomeUiEvent.ChangeMediaPlayerSheetVisibilityEvent) }
        )
    }
}
