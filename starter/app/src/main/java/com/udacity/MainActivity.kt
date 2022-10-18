package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            Log.d("Main", "hey, that tickles!")
            when(download_options.checkedRadioButtonId) {
                R.id.download_glide_button ->
                    download(getString(R.string.download_glide_url))
                R.id.download_udacity_button ->
                    download(getString(R.string.download_udacity_url))
                R.id.download_retrofit_button ->
                    download(getString(R.string.download_retrofit_url))
                else ->
                    Toast.makeText(
                        this,
                        R.string.download_no_selection_message,
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            when (intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
                downloadID -> {
                    val cursor = downloadManager.query(
                        DownloadManager.Query().setFilterById(downloadID)
                    )
                    if (cursor.moveToFirst()) {
                        val statusCol = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        when(cursor.getInt(statusCol)) {
                            DownloadManager.STATUS_PENDING -> {
                                Log.w("Main", "waiting to download...")
                            }
                            DownloadManager.STATUS_RUNNING -> {
                                // this state never broadcasts
                                val colTotalBytes = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                                val totalBytes = cursor.getLong(colTotalBytes)
                                val colGotBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                                val gotBytes = cursor.getLong(colGotBytes)
                                val progress = (gotBytes * 100 / totalBytes)
                                Log.d("Main", "download progress ... $progress")
                            }
                            DownloadManager.STATUS_FAILED -> {
                                Log.w("Main", "download failed")
                                custom_button.setIsLoading(false)
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                Log.d("Main", "download success!")
                                custom_button.setProgress(0)
                                custom_button.setIsLoading(false)
                            }
                        }
                        cursor.close()
                    }
                }
                else -> Log.w("Main", "Got some other download ID: ${intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)}")
            }
        }
    }

    private fun download(url: String) {
        Log.d("Main", "go download $url")
        custom_button.setIsLoading(true)
        custom_button.setProgress(30)
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }

    companion object {
        private const val CHANNEL_ID = "loadAppChannelId"
    }
}
