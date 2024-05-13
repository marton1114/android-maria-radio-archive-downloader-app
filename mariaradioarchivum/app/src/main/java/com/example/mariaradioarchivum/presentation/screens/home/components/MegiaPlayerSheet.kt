package com.example.mariaradioarchivum.presentation.screens.home.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Forward5
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay5
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerSheet(
    title: String,
    date: String,
    isPlaying: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        shape = RectangleShape,
        sheetState = sheetState,
        modifier = Modifier.padding(0.dp, 12.dp),
        containerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                )
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Card() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = title, style = MaterialTheme.typography.titleLarge)
                        Text(text = date, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            Card(modifier = Modifier.weight(1F)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Rounded.LibraryMusic, contentDescription = null,
                        modifier = Modifier.fillMaxSize())
                }
            }
            Card() {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(value = 0.5F, onValueChange = {})
                    Row(
                        modifier = Modifier.padding(6.dp)
                    ) {
                        Text(text = "00:00", modifier = Modifier.weight(1F), textAlign = TextAlign.Start)
                        Text(text = "59:59", modifier = Modifier.weight(1F), textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilledIconButton(
                            onClick = { /*TODO*/ },
                            colors = IconButtonDefaults.filledIconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) { Icon(imageVector = Icons.Rounded.Replay5, contentDescription = null) }
                        FilledIconButton(onClick = { /*TODO*/ },
                            modifier = Modifier.size(54.dp),
                            colors = IconButtonDefaults.filledIconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) { Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = null) }
                        FilledIconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(68.dp)) {
                            Icon(imageVector = Icons.Rounded.Pause, contentDescription = null)
//                            Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                        }
                        FilledIconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.size(54.dp),
                            colors = IconButtonDefaults.filledIconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) { Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = null) }
                        FilledIconButton(
                            onClick = { /*TODO*/ },
                            colors = IconButtonDefaults.filledIconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) { Icon(imageVector = Icons.Rounded.Forward5, contentDescription = null) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
            Spacer(modifier = Modifier.height(42.dp))
        }
    }
}