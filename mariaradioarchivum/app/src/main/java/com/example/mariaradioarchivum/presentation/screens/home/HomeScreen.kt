package com.example.mariaradioarchivum.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

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
                    onClick = { /*TODO*/ })
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
                Text(
                    text = "Letöltött hangfelvételek",
                    style = MaterialTheme.typography.titleLarge
                )
            }, colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ))
            
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                RecordingElement("Kisdengeleg mise", "2024.01.12. 14:00", true, uiState.isDeletingModeOn, {})
                RecordingElement("Mezőpetri mise", "2024.01.12. 14:00", false, uiState.isDeletingModeOn, {})
                RecordingElement("Nagykároly mise", "2024.01.12. 14:00", false, uiState.isDeletingModeOn, {})
            }
        }
    }
}
