package com.example.mariaradioarchivum.presentation.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RecordingElement() {
    Card(onClick = { /*TODO*/ }) {
        Row {
            Icon(imageVector = Icons.Default.Star, contentDescription = null )
        }
    }
}

@Preview
@Composable
fun RecordingElementPreview() {
    RecordingElement()
}