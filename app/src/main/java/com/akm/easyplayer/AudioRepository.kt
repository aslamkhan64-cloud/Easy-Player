package com.akm.easyplayer

import android.content.Context
import android.provider.MediaStore
import android.os.Build

class AudioRepository(private val context: Context) {

    fun getAllTracks(): List<Track> {
        val tracks = mutableListOf<Track>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.Audio.Media.RELATIVE_PATH
            else
                MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val folderColumn = 5

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = android.content.ContentUris.withAppendedId(collection, id)

                val folder = cursor.getString(folderColumn)
                    ?.trimEnd('/')
                    ?.substringAfterLast('/')
                    ?: "Unknown"

                tracks.add(
                    Track(
                        id = id,
                        title = cursor.getString(titleColumn) ?: "Unknown",
                        artist = cursor.getString(artistColumn) ?: "Unknown Artist",
                        album = cursor.getString(albumColumn) ?: "Unknown Album",
                        duration = cursor.getLong(durationColumn),
                        uri = uri,
                        folder = folder
                    )
                )
            }
        }

        return tracks
    }
}
