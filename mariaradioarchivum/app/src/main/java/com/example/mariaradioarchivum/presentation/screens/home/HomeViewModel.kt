package com.example.mariaradioarchivum.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

): ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

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
        }
    }
}