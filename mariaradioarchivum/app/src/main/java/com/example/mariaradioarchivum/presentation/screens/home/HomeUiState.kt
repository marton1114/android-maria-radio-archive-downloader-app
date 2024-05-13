package com.example.mariaradioarchivum.presentation.screens.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue

data class HomeUiState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val isDeletingModeOn: Boolean = false,
    val isAddRecordingSheetVisible: Boolean = false,
    val isMediaPlayerSheetVisible: Boolean = false,
)