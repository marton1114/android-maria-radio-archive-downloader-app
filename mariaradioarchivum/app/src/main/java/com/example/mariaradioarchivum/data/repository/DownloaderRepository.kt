package com.example.mariaradioarchivum.data.repository

interface DownloaderRepository {
    fun downloadFile(url: String, onComplete: () -> Unit): Long
    fun isDownloadComplete(downloadId: Long): Boolean
}