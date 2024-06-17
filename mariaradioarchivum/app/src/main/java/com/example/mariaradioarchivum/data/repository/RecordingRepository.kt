package com.example.mariaradioarchivum.data.repository

import com.example.mariaradioarchivum.data.model.Recording
import kotlinx.coroutines.flow.Flow

interface RecordingRepository {
    fun getRecordingsFromRoom(): Flow<List<Recording>>

    suspend fun addRecordingToRoom(recording: Recording)

    suspend fun deleteRecordingFromRoom(recording: Recording)

    suspend fun updateRecordingFromRoom(recording: Recording)
}