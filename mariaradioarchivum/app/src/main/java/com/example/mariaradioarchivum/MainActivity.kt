package com.example.mariaradioarchivum

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mariaradioarchivum.presentation.screens.home.HomeScreen
import com.example.mariaradioarchivum.ui.theme.MáriaRádióArchívumTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val channel = NotificationChannel(
                "MEDIA_PLAYBACK_CHANNEL",
                "Media Playback", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            MáriaRádióArchívumTheme {
                HomeScreen()
            }
        }
    }
}