package com.example.mariaradioarchivum.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mariaradioarchivum.data.dao.RecordingDao
import com.example.mariaradioarchivum.data.model.Recording

@Database(
    entities = [Recording::class],
    version = 1,
    exportSchema = false
)
abstract class RecordingDb: RoomDatabase() {
    abstract val recordingDao: RecordingDao
}