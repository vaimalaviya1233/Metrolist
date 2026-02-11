/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.datasource.cache.SimpleCache
import com.metrolist.music.constants.HideExplicitKey
import com.metrolist.music.constants.HideVideoSongsKey
import com.metrolist.music.db.MusicDatabase
import com.metrolist.music.db.entities.Song
import com.metrolist.music.di.DownloadCache
import com.metrolist.music.di.PlayerCache
import com.metrolist.music.extensions.filterExplicit
import com.metrolist.music.extensions.filterVideoSongs
import com.metrolist.music.utils.dataStore
import com.metrolist.music.utils.get
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CachePlaylistViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    @PlayerCache private val playerCache: SimpleCache,
    @DownloadCache private val downloadCache: SimpleCache
) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(0L)

    val cachedSongs: StateFlow<List<Song>> = combine(
        _refreshTrigger,
        context.dataStore.data,
    ) { _, _ ->
        // Reactive update trigger
        Unit
    }.flatMapLatest {
        flow {
            val hideExplicit = context.dataStore.get(HideExplicitKey, false)
            val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false)
            val cachedIds = playerCache.keys.toSet()
            val downloadedIds = downloadCache.keys.toSet()
            val pureCacheIds = cachedIds.subtract(downloadedIds)

            val songs = if (pureCacheIds.isNotEmpty()) {
                database.getSongsByIds(pureCacheIds.toList())
            } else {
                emptyList()
            }

            val completeSongs = songs.filter {
                val contentLength = it.format?.contentLength
                contentLength != null && playerCache.isCached(it.song.id, 0, contentLength)
            }

            if (completeSongs.isNotEmpty()) {
                database.query {
                    completeSongs.forEach {
                        if (it.song.dateDownload == null) {
                            update(it.song.copy(dateDownload = LocalDateTime.now()))
                        }
                    }
                }
            }

            val result = completeSongs
                .filter { it.song.dateDownload != null }
                .sortedByDescending { it.song.dateDownload }
                .filterExplicit(hideExplicit)
                .filterVideoSongs(hideVideoSongs)

            emit(result)
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    fun removeSongFromCache(songId: String) {
        playerCache.removeResource(songId)
        // Trigger refresh after cache removal
        _refreshTrigger.value = System.currentTimeMillis()
    }

    fun refreshCacheList() {
        _refreshTrigger.value = System.currentTimeMillis()
    }
}
