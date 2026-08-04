package com.example.data.repository

import com.example.data.api.RetrofitClient
import com.example.data.database.FavoriteStation
import com.example.data.database.FavoriteStationDao
import com.example.data.model.RadioStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RadioRepository(private val favoriteStationDao: FavoriteStationDao) {

    // Curated high-quality working fallback stations
    private val curatedStations = listOf(
        // === COSTA RICA ===
        RadioStation(
            name = "Teletica Radio 91.5 FM",
            url = "https://g2qd375ol7an-hls-live.5centscdn.com/Radio/eae835e83c0494a376229f254f7d3392.sdp/chunks.m3u8",
            country = "CR",
            tags = "Información, Noticias, Deportes, Variedad",
            favicon = "https://cdn-radiotime-logos.tunein.com/s276433q.png"
        ),
        RadioStation(
            name = "Radio Musical 97.5 FM",
            url = "https://live.turadio.stream:7005/stream?type=http&nocache=596",
            country = "CR",
            tags = "Baladas, Romántica, Pop, Clásicos",
            favicon = "https://radiomusical.com/wp-content/uploads/2020/11/logo-musical.png"
        ),
        RadioStation(
            name = "Urbano 106 FM",
            url = "https://usa18.fastcast4u.com/proxy/rmoohhrw?mp=/1",
            country = "CR",
            tags = "Urbano, Reggaeton, Hip-Hop, Dancehall",
            favicon = "https://i0.wp.com/www.urbano106.com/wp-content/uploads/2018/10/LOGO-PARA-WEBS.png"
        ),
        RadioStation(
            name = "IQ Radio 93.9 FM",
            url = "http://rtvhd.net:9942/;stream/1",
            country = "CR",
            tags = "Alternativo, Rock, Pop, Éxitos",
            favicon = "https://cdn-radiotime-logos.tunein.com/s50287q.png"
        ),
        RadioStation(
            name = "Radio Omega 105.1 FM",
            url = "http://rtvhd.net:9932/stream",
            country = "CR",
            tags = "Música Latina, Humor, Variado, Éxitos",
            favicon = "https://www.omega.fm/wp-content/uploads/2020/09/cropped-LOGO-OMEGA-FONDO-AZUL.png"
        ),
        RadioStation(
            name = "103 hit 103.1 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CRC_103_1AAC.aac",
            country = "CR",
            tags = "Pop, Éxitos, Hits, Electrónica",
            favicon = "https://mmo.aiircdn.com/943/63d062c743beb.png"
        ),
        RadioStation(
            name = "Radio Mil Recuerdos Stereo",
            url = "https://sechin.grupocentroserver.com/radio/8060/radio.mp3",
            country = "CR",
            tags = "Recuerdos, Baladas, Oro, Clásicos",
            favicon = "https://cdn-radiotime-logos.tunein.com/s243685q.png"
        ),
        RadioStation(
            name = "Radio María CR 100.7 FM",
            url = "http://dreamsiteradiocp2.com:8044/stream",
            country = "CR",
            tags = "Católico, Religioso, Mensajes, Oración",
            favicon = "https://cdn-radiotime-logos.tunein.com/s13309q.png"
        ),

        // === PERU ===
        RadioStation(
            name = "Radio Oxígeno 102.1 FM",
            url = "https://mdstrm.com/audio/5fab0687bcd6c2389ee9480c/live.m3u8",
            country = "PE",
            tags = "Rock, Pop, 80s, 90s, Clásicos",
            favicon = "https://graph.facebook.com/RadioOxigenoFM/picture?width=200&height=200"
        ),
        RadioStation(
            name = "Radio RPP Noticias",
            url = "https://mdstrm.com/audio/5fab3416b5f9ef165cfab6e9/icecast.audio",
            country = "PE",
            tags = "Noticias, Deportes, Perú, Información",
            favicon = "https://mds.rpp-noticias.io/static/img/favicons/apple-touch-icon-120x120.png"
        ),
        RadioStation(
            name = "Studio 92 (92.5 FM Lima)",
            url = "https://mdstrm.com/audio/5fada553978fe1080e3ac5ea/icecast.audio",
            country = "PE",
            tags = "Pop, Juvenil, Electrónica, K-pop, Hits",
            favicon = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Logostudio92.png/225px-Logostudio92.png"
        ),
        RadioStation(
            name = "Radio Corazón (94.3 FM)",
            url = "https://mdstrm.com/audio/5fada514fc16c006bd63370f/icecast.audio",
            country = "PE",
            tags = "Romántica, Baladas, Amor, Español",
            favicon = "https://eaudioplayer.radio-grpp.io/static/dist/img/favicons/apple-touch-icon.png?v=2016063020297"
        ),
        RadioStation(
            name = "Radio La Mega 96.7 FM",
            url = "https://mdstrm.com/audio/5fada56fe4e09508207a7951/icecast.audio",
            country = "PE",
            tags = "Cumbia, Salsa, Tropical, Folklore",
            favicon = "https://eaudioplayer.radio-grpp.io/static/dist/img/favicons/apple-touch-icon.png"
        ),
        RadioStation(
            name = "Top Latino",
            url = "http://online.radiodifusion.net:8020/stream",
            country = "PE",
            tags = "Pop, Latino, Éxitos, Top 40, Música",
            favicon = "https://cdn-radiotime-logos.tunein.com/s114389q.png"
        ),
        RadioStation(
            name = "Radio Huancayo 104.1 FM",
            url = "https://cloud9.ldwebstudios.net:7000/;",
            country = "PE",
            tags = "Cumbia, Folclore, Reggaeton, Sierra",
            favicon = "https://cdn-radiotime-logos.tunein.com/s27598q.png"
        ),
        RadioStation(
            name = "PBO Radio 91.9 FM",
            url = "http://n06.radiojar.com/2fse67zuv8hvv?1669054401=&rj-tok=AAABhJtuNOUAIi7Le3h5PA-2VA&rj-ttl=5",
            country = "PE",
            tags = "Noticias, Política, Debate, Opinión",
            favicon = "https://yt3.googleusercontent.com/ytc/AIdro_moHkYj7-6YxW9I7Y3U0Z6I_k1I-X0_77S3GZfB=s900-c-k-c0x00ffffff-no-rj"
        )
    )

    fun getCuratedStations(country: String): List<RadioStation> {
        return curatedStations.filter { it.country.equals(country, ignoreCase = true) }
    }

    // Dynamic search via Radio Browser API
    suspend fun searchOnlineStations(countryCode: String, query: String?): List<RadioStation> {
        return try {
            val apiStations = RetrofitClient.apiService.searchStations(
                countryCode = countryCode.uppercase(),
                name = if (query.isNullOrBlank()) null else query,
                limit = 40
            )
            apiStations.mapNotNull { api ->
                val resolvedUrl = api.urlResolved
                if (resolvedUrl.isNullOrBlank()) return@mapNotNull null

                RadioStation(
                    name = api.name ?: "Radio Desconocida",
                    url = resolvedUrl,
                    country = api.countryCode ?: countryCode,
                    tags = api.tags?.replace(",", ", ") ?: "Música, Noticias",
                    favicon = api.favicon ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to local curated matching search query if offline or API error
            getCuratedStations(countryCode).filter {
                query.isNullOrBlank() || it.name.contains(query, ignoreCase = true) || it.tags.contains(query, ignoreCase = true)
            }
        }
    }

    // Favorites persistence via Room DB
    val allFavoritesFlow: Flow<List<RadioStation>> = favoriteStationDao.getAllFavorites().map { entities ->
        entities.map { entity ->
            RadioStation(
                name = entity.name,
                url = entity.url,
                country = entity.country,
                tags = entity.genre,
                favicon = entity.favicon
            )
        }
    }

    fun isFavoriteFlow(url: String): Flow<Boolean> {
        return favoriteStationDao.isFavorite(url)
    }

    suspend fun addFavorite(station: RadioStation) {
        favoriteStationDao.insertFavorite(
            FavoriteStation(
                url = station.url,
                name = station.name,
                country = station.country,
                genre = station.tags,
                favicon = station.favicon
            )
        )
    }

    suspend fun removeFavorite(station: RadioStation) {
        favoriteStationDao.deleteFavorite(
            FavoriteStation(
                url = station.url,
                name = station.name,
                country = station.country,
                genre = station.tags,
                favicon = station.favicon
            )
        )
    }
}
