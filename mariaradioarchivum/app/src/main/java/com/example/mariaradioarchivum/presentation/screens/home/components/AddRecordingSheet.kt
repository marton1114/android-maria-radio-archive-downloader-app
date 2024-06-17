package com.example.mariaradioarchivum.presentation.screens.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mariaradioarchivum.data.model.Recording

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordingSheet(
    sheetState: SheetState,
    recordingNameValue: String,
    onRecordingValueChange: (value: String) -> Unit,
    onDismissRequest: () -> Unit,

    recordingDate: String,
    onPreviousDayClick: () -> Unit,
    onDateClick: () -> Unit,
    onNextDayClick: () -> Unit,

    recordingInterval: String,
    onPreviousIntervalClick: () -> Unit,
    onIntervalClick: () -> Unit,
    onNextIntervalClick: () -> Unit,

    onDownloadClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        shape = RectangleShape,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismissRequest) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
                Text(text = "Felvétel letöltése", style = MaterialTheme.typography.titleLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledIconButton(
                        onClick = onDownloadClick,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row {
                            Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
                        }
                    }
                }
            }
            Text(text = "Mentett felvétel címe", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline)
            OutlinedTextField(value = recordingNameValue, onValueChange = { onRecordingValueChange(it) }, modifier = Modifier.fillMaxWidth())
            Text(text = "Dátum", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline)
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)
                ),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPreviousDayClick) {
                    Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                        contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                Text(text = recordingDate, modifier = Modifier
                    .clickable { onDateClick() }
                    .weight(1F)
                    .padding(5.dp),
                    textAlign = TextAlign.Center)
                IconButton(onClick = onNextDayClick) {
                    Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                        contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
            }
            Text(text = "Időintervallum", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline)
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)
                ),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPreviousIntervalClick) {
                    Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                        contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                Text(text = recordingInterval, modifier = Modifier
                    .clickable { onIntervalClick() }
                    .weight(1F)
                    .padding(5.dp),
                    textAlign = TextAlign.Center)
                IconButton(onClick = onNextIntervalClick) {
                    Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                        contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}