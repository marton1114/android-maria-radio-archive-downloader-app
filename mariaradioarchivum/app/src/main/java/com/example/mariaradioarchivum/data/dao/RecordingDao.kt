package com.example.mariaradioarchivum.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mariaradioarchivum.data.model.Recording
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recording_table ORDER BY id DESC")
    fun getRecordings(): Flow<List<Recording>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecording(recording: Recording)

    @Delete
    fun deleteRecording(recording: Recording)

    @Update
    fun updateRecording(recording: Recording)
}