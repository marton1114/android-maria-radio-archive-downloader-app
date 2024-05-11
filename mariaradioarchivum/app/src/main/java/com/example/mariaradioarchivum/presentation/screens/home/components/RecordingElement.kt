package com.example.mariaradioarchivum.presentation.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RecordingElement(
    title: String,
    date: String,
    isPlaying: Boolean = false,
    isDeleteVisible: Boolean = false,
    onTrashClicked: () -> Unit
) {
    var playButton = Icons.Default.PlayArrow
    var textColor = MaterialTheme.colorScheme.onSurfaceVariant
    var iconColor = MaterialTheme.colorScheme.primary

    if (isPlaying) {
        playButton = Icons.Default.Pause
        textColor = MaterialTheme.colorScheme.tertiary
        iconColor = MaterialTheme.colorScheme.tertiary
    }

    Card {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier
                .weight(1F)
                .size(60.dp)) {
                Icon(imageVector = Icons.Default.Radio, contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(5F),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, color = textColor,
                    fontWeight = FontWeight.Bold)
                Text(text = date, style = MaterialTheme.typography.titleSmall,
                    fontFamily = FontFamily.Monospace, color = textColor)
            }
            Row(
                modifier = Modifier.weight(2F),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onTrashClicked, modifier = Modifier.size(60.dp)) {
                    Icon(playButton, tint = iconColor, contentDescription = null)
                }
                AnimatedVisibility(
                    visible = isDeleteVisible,
                    enter = slideInHorizontally { 100 },
                    exit = slideOutHorizontally { 100 }
                ) {
                    IconButton(onClick = onTrashClicked, modifier = Modifier.size(60.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null,
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}