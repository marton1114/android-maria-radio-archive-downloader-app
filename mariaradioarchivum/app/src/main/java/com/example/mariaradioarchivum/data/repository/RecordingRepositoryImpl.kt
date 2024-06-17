package com.example.mariaradioarchivum.data.repository

import com.example.mariaradioarchivum.data.dao.RecordingDao
import com.example.mariaradioarchivum.data.model.Recording
import kotlinx.coroutines.flow.Flow

class RecordingRepositoryImpl(
    private val recordingDao: RecordingDao
): RecordingRepository {
    override fun getRecordingsFromRoom(): Flow<List<Recording>> = recordingDao.getRecordings()

    override suspend fun addRecordingToRoom(recording: Recording) =
        recordingDao.addRecording(recording)

    override suspend fun deleteRecordingFromRoom(recording: Recording) =
        recordingDao.deleteRecording(recording)

    override suspend fun updateRecordingFromRoom(recording: Recording) =
        recordingDao.updateRecording(recording)

}