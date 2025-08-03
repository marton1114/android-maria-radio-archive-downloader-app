package com.example.mariaradioarchivum.di

import android.content.Context
import androidx.room.Room
import com.example.mariaradioarchivum.PlaybackController
import com.example.mariaradioarchivum.PlaybackControllerImpl
import com.example.mariaradioarchivum.data.dao.RecordingDao
import com.example.mariaradioarchivum.data.network.RecordingDb
import com.example.mariaradioarchivum.data.repository.DownloaderRepository
import com.example.mariaradioarchivum.data.repository.DownloaderRepositoryImpl
import com.example.mariaradioarchivum.data.repository.RecordingRepository
import com.example.mariaradioarchivum.data.repository.RecordingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideRecordingDb(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecordingDb::class.java,
        "recording_table"
    ).build()

    @Provides
    fun provideRecordingDao(recordingDb: RecordingDb) = recordingDb.recordingDao

    @Provides
    fun provideBookRepository(
        recordingDao: RecordingDao
    ): RecordingRepository = RecordingRepositoryImpl(
        recordingDao = recordingDao
    )
    @Provides
    fun provideDownloaderRepository(
        @ApplicationContext context: Context
    ): DownloaderRepository = DownloaderRepositoryImpl(
        context = context
    )

    @Provides
    fun providePlaybackController(
        @ApplicationContext context: Context,
    ): PlaybackController = PlaybackControllerImpl(
        context = context,
    )
}
