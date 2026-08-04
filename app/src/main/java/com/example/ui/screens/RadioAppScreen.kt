package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.RadioStation
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.MatteSlate
import com.example.ui.theme.TealGlow
import com.example.ui.viewmodel.RadioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioAppScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val currentStation by viewModel.currentStation.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val playbackError by viewModel.playbackError.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isLoadingSearch by viewModel.isLoadingSearch.collectAsStateWithLifecycle()
    val favoriteStations by viewModel.favoriteStations.collectAsStateWithLifecycle()
    val isCurrentStationFavorite by viewModel.isCurrentStationFavorite.collectAsStateWithLifecycle()
    val selectedGenreFilter by viewModel.selectedGenreFilter.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0 = Emisoras, 1 = Buscar, 2 = Favoritos

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = null,
                            tint = AmberGlow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RADIO CR-PE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.White
                        )
                    }
                },
                actions = {
                    // Quick blinking indicator to show the stream is active/listening
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "blinker")
                        val blinkAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 600, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "live_dot"
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPlaying) Color(0xFFD32F2F).copy(alpha = blinkAlpha)
                                    else Color.Gray
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlaying) "VIVO" else "LISTO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isPlaying) Color(0xFFD32F2F) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. DIGITAL TUNING RECEIVER DISPLAY (glowing LCD screen)
            TuningDisplay(
                station = currentStation,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                error = playbackError
            )

            // 2. MAIN INTERACTIVE NAVIGATION TABS
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = AmberGlow,
                indicator = { tabPositions ->
                    if (activeTab < tabPositions.size) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[activeTab])
                                .height(3.dp)
                                .background(AmberGlow, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        )
                    }
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = {
                        activeTab = 0
                        viewModel.loadCuratedStations()
                    },
                    text = { Text("Emisoras", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Language, contentDescription = "Sintonizador") }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Buscar", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Buscar en Web") }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Favoritos", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") }
                )
            }

            // 3. MAIN CONTAINER AREA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> { // SINTONIZADOR LOCAL TAB
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Country Selector Row (Costa Rica vs Peru)
                            CountrySelector(
                                selectedCountry = selectedCountry,
                                onCountrySelected = { viewModel.selectCountry(it) }
                            )

                            // Quick Genre filter
                            GenreFilterChips(
                                selectedGenre = selectedGenreFilter,
                                onGenreSelected = { viewModel.selectGenreFilter(it) }
                            )

                            // List of curated stations
                            StationList(
                                stations = searchResults,
                                currentPlayingUrl = currentStation?.url,
                                isPlaying = isPlaying,
                                favoriteStations = favoriteStations,
                                onStationClick = { viewModel.playStation(it) },
                                onFavoriteToggle = { viewModel.toggleFavorite(it) }
                            )
                        }
                    }

                    1 -> { // GLOBAL DIRECTORY BROWSER TAB
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Country Selector for Search
                            CountrySelector(
                                selectedCountry = selectedCountry,
                                onCountrySelected = { viewModel.selectCountry(it) }
                            )

                            // Search textfield
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = { viewModel.setQuery(it) }
                            )

                            if (isLoadingSearch) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = AmberGlow)
                                }
                            } else if (searchResults.isEmpty() && searchQuery.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No se encontraron emisoras en la red",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                StationList(
                                    stations = searchResults,
                                    currentPlayingUrl = currentStation?.url,
                                    isPlaying = isPlaying,
                                    favoriteStations = favoriteStations,
                                    onStationClick = { viewModel.playStation(it) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(it) }
                                )
                            }
                        }
                    }

                    2 -> { // FAVORITES LOCAL TAB
                        if (favoriteStations.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No tienes emisoras favoritas guardadas",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Haz clic en el ícono de corazón de cualquier emisora para guardarla aquí.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            StationList(
                                stations = favoriteStations,
                                currentPlayingUrl = currentStation?.url,
                                isPlaying = isPlaying,
                                favoriteStations = favoriteStations,
                                onStationClick = { viewModel.playStation(it) },
                                onFavoriteToggle = { viewModel.toggleFavorite(it) }
                            )
                        }
                    }
                }
            }

            // 4. EMBEDDED DOCK PLAYER SYSTEM CONTROLS
            BottomPlayerControls(
                station = currentStation,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                volume = volume,
                isFavorite = isCurrentStationFavorite,
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onVolumeChange = { viewModel.setVolume(it) },
                onFavoriteClick = { currentStation?.let { viewModel.toggleFavorite(it) } }
            )
        }
    }
}

@Composable
fun TuningDisplay(
    station: RadioStation?,
    isPlaying: Boolean,
    isBuffering: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F0F)),
        shape = RoundedCornerShape(12.dp),
        border = borderDialBrush()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Digital Screen Info Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECEPTOR DIGITAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Gray
                )
                Text(
                    text = if (isBuffering) "SINTONIZANDO..." else if (isPlaying) "SINTONIZADO" else "EN ESPERA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isBuffering) AmberGlow else if (isPlaying) TealGlow else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Station details & Frequency Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (station != null) {
                        Text(
                            text = station.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = AmberGlow,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val flag = if (station.country.uppercase() == "CR") "COSTA RICA 🇨🇷" else "PERÚ 🇵🇪"
                            Text(
                                text = flag,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "|  ${station.tags.take(35)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = "SELECCIONE EMISORA",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Presione play en cualquier dial para sintonizar",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Interactive LED Audio visualizer on LCD Screen
                AudioVisualizer(isPlaying = isPlaying)
            }

            // Error Message Display
            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2C1010), RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Red,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun AudioVisualizer(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")

    val barCount = 10
    val animValues = List(barCount) { index ->
        if (isPlaying) {
            infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index * 90) % 280,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )
        } else {
            remember { mutableStateOf(0.15f) }
        }
    }

    Row(
        modifier = modifier.height(38.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        animValues.forEach { animState ->
            val heightFraction = animState.value
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(heightFraction)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(TealGlow, AmberGlow)
                        ),
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    )
            )
        }
    }
}

@Composable
fun CountrySelector(
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Costa Rica
        CountryButton(
            text = "COSTA RICA 🇨🇷",
            isSelected = selectedCountry == "CR",
            onClick = { onCountrySelected("CR") },
            modifier = Modifier.weight(1f)
        )

        // Peru
        CountryButton(
            text = "PERÚ 🇵🇪",
            isSelected = selectedCountry == "PE",
            onClick = { onCountrySelected("PE") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CountryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AmberGlow.copy(alpha = 0.15f) else Color(0xFF1E1E1E))
            .border(
                width = 1.5.dp,
                color = if (isSelected) AmberGlow else Color(0xFF2E2E2E),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            ),
            color = if (isSelected) AmberGlow else Color.LightGray
        )
    }
}

@Composable
fun GenreFilterChips(
    selectedGenre: String,
    onGenreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val genres = listOf("Todos", "Noticias", "Rock", "Pop", "Romántica", "Urbano", "Cumbia", "Católico")

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(genres) { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onGenreSelected(genre) },
                label = { Text(genre, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AmberGlow,
                    selectedLabelColor = Color.Black,
                    containerColor = MatteSlate,
                    labelColor = Color.LightGray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedGenre == genre,
                    borderColor = Color(0xFF2E2E2E),
                    selectedBorderColor = AmberGlow,
                    borderWidth = 1.dp
                )
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Buscar emisora por nombre o género...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmberGlow) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.Gray)
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MatteSlate,
            unfocusedContainerColor = MatteSlate,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.LightGray,
            focusedIndicatorColor = AmberGlow,
            unfocusedIndicatorColor = Color(0xFF2E2E2E),
            cursorColor = AmberGlow
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun StationList(
    stations: List<RadioStation>,
    currentPlayingUrl: String?,
    isPlaying: Boolean,
    favoriteStations: List<RadioStation>,
    onStationClick: (RadioStation) -> Unit,
    onFavoriteToggle: (RadioStation) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("station_list"),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(stations, key = { it.url }) { station ->
            val isCurrent = station.url == currentPlayingUrl
            val isFav = favoriteStations.any { it.url == station.url }

            StationItemRow(
                station = station,
                isCurrent = isCurrent,
                isPlaying = isPlaying && isCurrent,
                isFavorite = isFav,
                onPlayClick = { onStationClick(station) },
                onFavoriteToggle = { onFavoriteToggle(station) }
            )
        }
    }
}

@Composable
fun StationItemRow(
    station: RadioStation,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlayClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF1F1B12) else MatteSlate
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isCurrent) AmberGlow else Color(0xFF262626)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Station Logo with fallbacks
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F0F0F)),
                contentAlignment = Alignment.Center
            ) {
                if (station.favicon.isNotEmpty()) {
                    AsyncImage(
                        model = station.favicon,
                        contentDescription = "Logo de ${station.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = android.R.drawable.ic_media_play)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = null,
                        tint = if (isCurrent) AmberGlow else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // If currently playing, show small play/visualizer overlay
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Reproduciendo",
                            tint = TealGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Station Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) AmberGlow else Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = station.tags,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Play / Listen action icon
            IconButton(
                onClick = onPlayClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isCurrent) AmberGlow else Color(0xFF2B2B2B),
                    contentColor = if (isCurrent) Color.Black else Color.White
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Sintonizar"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Favorites quick button
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("favorite_button")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

@Composable
fun BottomPlayerControls(
    station: RadioStation?,
    isPlaying: Boolean,
    isBuffering: Boolean,
    volume: Float,
    isFavorite: Boolean,
    onPlayPauseClick: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, Color(0xFF262626))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Curated active metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sintonized info
                Column(modifier = Modifier.weight(1f)) {
                    if (station != null) {
                        Text(
                            text = "ESTÁS ESCUCHANDO:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.Gray
                        )
                        Text(
                            text = station.name,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "RADIO CR-PE RECEPTOR",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.LightGray
                        )
                        Text(
                            text = "Ninguna emisora activa",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                if (station != null) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.testTag("player_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else Color.LightGray
                        )
                    }
                }

                // Play / Pause Circle Action Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AmberGlow)
                        .clickable(onClick = onPlayPauseClick)
                        .testTag("play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Reproducir / Pausar",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Volume adjustment slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val speakerIcon = when {
                    volume == 0.0f -> Icons.Default.VolumeMute
                    volume < 0.5f -> Icons.Default.VolumeDown
                    else -> Icons.Default.VolumeUp
                }

                Icon(
                    imageVector = speakerIcon,
                    contentDescription = "Volumen",
                    tint = AmberGlow,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberGlow,
                        activeTrackColor = AmberGlow,
                        inactiveTrackColor = Color(0xFF2E2E2E)
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${(volume * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.LightGray
                )
            }
        }
    }
}

// Utility gradient border builder for custom receiver appearance
@Composable
fun borderDialBrush(): BorderStroke {
    return BorderStroke(
        width = 1.5.dp,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF333333), Color(0xFF1E1E1E), Color(0xFF444444)),
        )
    )
}
