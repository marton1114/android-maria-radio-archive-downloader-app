package com.example.mariaradioarchivum.data.repository

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import java.io.File

class DownloaderRepositoryImpl(
    private val context: Context
): DownloaderRepository {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, onComplete: () -> Unit): Long {
        val uri = url.toUri()
        val request = DownloadManager.Request(uri)
            .setMimeType("audio/mpeg")
//            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(uri.lastPathSegment)
//            .addRequestHeader("Authorization", "Bearer <token>")
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, uri.lastPathSegment)
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        val downloadId = downloadManager.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if(id != -1L) {
                    println("Download with ID $id finished!")

                    val query = DownloadManager.Query()
                    id?.let { query.setFilterById(it) }
                    val cursor: Cursor? = downloadManager?.query(query)

                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            if (columnIndex >= 0) {
                                val status = cursor.getInt(columnIndex)
                                if (status == DownloadManager.STATUS_FAILED) {
                                    Toast.makeText(
                                        context,
                                        "Erről az időpontról nincs letölthető felvétel!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }

                if (id == downloadId) {
                    onComplete()
                    context?.unregisterReceiver(this)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

        return downloadId
    }

    override fun isDownloadComplete(downloadId: Long): Boolean {
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager?.query(query) ?: return false
        if (!cursor.moveToFirst()) {
            cursor.close()
            return false
        }
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status = cursor.getInt(columnIndex)
        cursor.close()
        return status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED
    }
}