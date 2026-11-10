package com.akm.easyplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder

class PlaybackService : Service() {

    private var player: MediaPlayer? = null

    companion object {
        private const val CHANNEL_ID = "easy_player_channel"
        private const val NOTIFICATION_ID = 101
        private const val ACTION_PLAY = "PLAY"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == ACTION_PLAY) {
            val uriString = intent.getStringExtra("track_uri")
            val title = intent.getStringExtra("track_title") ?: "Easy Player"

            if (uriString != null) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(title)
                )
                playAudio(Uri.parse(uriString))
            }
        }

        return START_NOT_STICKY
    }

    private fun playAudio(uri: Uri) {
        try {
            player?.release()

            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )

                setDataSource(this@PlaybackService, uri)

                setOnPreparedListener {
                    it.start()
                }

                setOnCompletionListener {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }

                setOnErrorListener { _, _, _ ->
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    true
                }

                prepareAsync()
            }

        } catch (e: Exception) {
            player?.release()
            player = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun createNotification(title: String): Notification {
        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }

        return builder
            .setContentTitle(title)
            .setContentText("Playing music • Easy Player")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Easy Player Music",
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
