package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.database.AppDatabase
import com.example.data.model.RadioStation
import com.example.data.repository.RadioRepository
import com.example.data.service.RadioPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RadioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = RadioRepository(db.favoriteStationDao())

    // ExoPlayer Instance
    private var exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()

    // UI State
    private val _currentStation = MutableStateFlow<RadioStation?>(null)
    val currentStation: StateFlow<RadioStation?> = _currentStation.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _playbackError = MutableStateFlow<String?>(null)
    val playbackError: StateFlow<String?> = _playbackError.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // Country selection ("CR" or "PE")
    private val _selectedCountry = MutableStateFlow("CR")
    val selectedCountry: StateFlow<String> = _selectedCountry.asStateFlow()

    // Search and Dynamic Browser State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<RadioStation>>(emptyList())
    val searchResults: StateFlow<List<RadioStation>> = _searchResults.asStateFlow()

    private val _isLoadingSearch = MutableStateFlow(false)
    val isLoadingSearch: StateFlow<Boolean> = _isLoadingSearch.asStateFlow()

    // Filter genre selection
    private val _selectedGenreFilter = MutableStateFlow("Todos")
    val selectedGenreFilter: StateFlow<String> = _selectedGenreFilter.asStateFlow()

    // Favorites from Database
    val favoriteStations: StateFlow<List<RadioStation>> = repository.allFavoritesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isCurrentStationFavorite = MutableStateFlow(false)
    val isCurrentStationFavorite: StateFlow<Boolean> = _isCurrentStationFavorite.asStateFlow()

    private var searchJob: Job? = null
    private var favoriteCheckJob: Job? = null

    init {
        setupPlayerListener()
        loadCuratedStations()

        RadioPlaybackService.onStopPlayback = {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            }
        }

        // Sync favorite status of currently playing station
        viewModelScope.launch {
            _currentStation.collect { station ->
                favoriteCheckJob?.cancel()
                if (station != null) {
                    favoriteCheckJob = launch {
                        repository.isFavoriteFlow(station.url).collect { fav ->
                            _isCurrentStationFavorite.value = fav
                        }
                    }
                } else {
                    _isCurrentStationFavorite.value = false
                }
            }
        }
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    _currentStation.value?.let { station ->
                        RadioPlaybackService.startService(
                            getApplication(),
                            station.name,
                            "${if (station.country == "CR") "Costa Rica" else "Perú"} - En Vivo"
                        )
                    }
                } else {
                    RadioPlaybackService.stopService(getApplication())
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = playbackState == Player.STATE_BUFFERING
                _playbackError.value = null
            }

            override fun onPlayerError(error: PlaybackException) {
                _isPlaying.value = false
                _isBuffering.value = false
                _playbackError.value = "Error al reproducir la transmisión: ${error.localizedMessage ?: "URL inaccesible"}"
                RadioPlaybackService.stopService(getApplication())
            }
        })
    }

    fun playStation(station: RadioStation) {
        viewModelScope.launch {
            try {
                _playbackError.value = null
                _isBuffering.value = true
                _currentStation.value = station

                exoPlayer.stop()
                exoPlayer.clearMediaItems()

                val mediaItem = MediaItem.fromUri(station.url)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()

                // Explicitly update foreground service to show current station details
                RadioPlaybackService.startService(
                    getApplication(),
                    station.name,
                    "${if (station.country == "CR") "Costa Rica" else "Perú"} - En Vivo"
                )
            } catch (e: Exception) {
                _playbackError.value = "No se pudo iniciar la reproducción: ${e.localizedMessage}"
                _isBuffering.value = false
                RadioPlaybackService.stopService(getApplication())
            }
        }
    }

    fun togglePlayPause() {
        if (_currentStation.value == null) {
            // Play first curated station of current country if none is playing
            val defaultStations = repository.getCuratedStations(_selectedCountry.value)
            if (defaultStations.isNotEmpty()) {
                playStation(defaultStations[0])
            }
            return
        }

        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            _playbackError.value = null
            exoPlayer.play()
        }
    }

    fun setVolume(vol: Float) {
        val boundedVol = vol.coerceIn(0.0f, 1.0f)
        _volume.value = boundedVol
        exoPlayer.volume = boundedVol
    }

    fun selectCountry(country: String) {
        _selectedCountry.value = country
        _selectedGenreFilter.value = "Todos"
        loadCuratedStations()
        // Clear search
        _searchQuery.value = ""
    }

    fun selectGenreFilter(genre: String) {
        _selectedGenreFilter.value = genre
        loadCuratedStations()
    }

    fun loadCuratedStations() {
        val rawList = repository.getCuratedStations(_selectedCountry.value)
        val filtered = if (_selectedGenreFilter.value == "Todos") {
            rawList
        } else {
            rawList.filter { it.tags.contains(_selectedGenreFilter.value, ignoreCase = true) }
        }
        _searchResults.value = filtered
    }

    fun setQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                loadCuratedStations()
            } else {
                delay(400) // Debounce API requests
                _isLoadingSearch.value = true
                val results = repository.searchOnlineStations(_selectedCountry.value, query)
                _searchResults.value = results
                _isLoadingSearch.value = false
            }
        }
    }

    fun toggleFavorite(station: RadioStation) {
        viewModelScope.launch {
            if (favoriteStations.value.any { it.url == station.url }) {
                repository.removeFavorite(station)
            } else {
                repository.addFavorite(station)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        RadioPlaybackService.onStopPlayback = null
        RadioPlaybackService.stopService(getApplication())
        exoPlayer.release()
    }
}
