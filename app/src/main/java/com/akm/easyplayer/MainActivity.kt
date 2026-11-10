package com.akm.easyplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val tracks = mutableStateListOf<Track>()

    private val audioPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                loadTracks()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EasyPlayerUI(
                tracks = tracks,
                onPlay = { track -> playTrack(track) }
            )
        }

        checkAudioPermission()
    }

    private fun checkAudioPermission() {
        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (checkSelfPermission(permission) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            loadTracks()
        } else {
            audioPermissionLauncher.launch(permission)
        }
    }

    private fun loadTracks() {
        tracks.clear()
        tracks.addAll(AudioRepository(this).getAllTracks())
    }

    private fun playTrack(track: Track) {
        val intent = Intent(this, PlaybackService::class.java).apply {
            action = "PLAY"
            putExtra("track_uri", track.uri.toString())
            putExtra("track_title", track.title)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}

@Composable
fun EasyPlayerUI(
    tracks: List<Track>,
    onPlay: (Track) -> Unit
) {
    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE9EDF2),
            Color(0xFFDCE3EA),
            Color(0xFFF7F8FA)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(18.dp)
    ) {
        Text(
            text = "Easy Player",
            fontSize = 30.sp,
            color = Color(0xFF202A35)
        )

        Text(
            text = "Your Music Library",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.75f)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "MY LIBRARY",
                    color = Color(0xFF526477),
                    fontSize = 13.sp
                )

                Text(
                    text = "${tracks.size} Songs",
                    color = Color(0xFF202A35),
                    fontSize = 25.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "All Songs",
            fontSize = 21.sp,
            color = Color(0xFF202A35)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No music found\nAllow audio permission or add music",
                    color = Color.DarkGray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tracks, key = { it.id }) { track ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.8f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = track.title,
                                    color = Color(0xFF202A35),
                                    fontSize = 16.sp
                                )

                                Text(
                                    text = track.artist,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = { onPlay(track) }
                            ) {
                                Text("Play")
                            }
                        }
                    }
                }
            }
        }
    }
}
