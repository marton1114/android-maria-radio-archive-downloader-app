package com.example.mariaradioarchivum.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recording_table")
data class Recording(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val date: String = "",
    val path: String = "",
    val position: Int = 0,
    val duration: Int = 0,
)
