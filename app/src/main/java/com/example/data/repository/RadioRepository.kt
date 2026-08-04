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
            favicon = "https://graph.facebook.com/TeleticaRadio/picture?width=300&height=300"
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
        RadioStation(
            name = "Bésame 89.9 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CRC_BESAME_AAC.aac",
            country = "CR",
            tags = "Romántica, Baladas, Amor",
            favicon = "https://www.besame.cr/wp-content/uploads/2020/09/cropped-logo-besame-300x300.png"
        ),
        RadioStation(
            name = "Los 40 Costa Rica 104.3 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CRC_LOS40_AAC.aac",
            country = "CR",
            tags = "Pop, Éxitos, Hits, Pop Inglés",
            favicon = "https://los40.com/los40/global/img/logos/los40-logo-redondo.png"
        ),
        RadioStation(
            name = "Radio Columbia 98.7 FM",
            url = "https://columbia.grupocolumbia.co.cr:8000/stream",
            country = "CR",
            tags = "Noticias, Deportes, Debate, Opinión",
            favicon = "https://columbia.co.cr/wp-content/uploads/2021/04/cropped-favicon-columbia-192x192.png"
        ),
        RadioStation(
            name = "Radio Fides 93.1 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/RADIO_FIDES_AAC.aac",
            country = "CR",
            tags = "Católico, Religioso, Oración, Mensajes",
            favicon = "https://www.radiofides.co.cr/wp-content/uploads/2020/09/cropped-logo-fides-new-192x192.jpg"
        ),
        RadioStation(
            name = "Q'Teja 90.7 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CRC_QTEJA_AAC.aac",
            country = "CR",
            tags = "Popular, Grupera, Cumbia, Latina",
            favicon = "https://www.qteja.cr/wp-content/uploads/2020/09/cropped-logo-qteja-2-192x192.png"
        ),
        RadioStation(
            name = "La Caliente 90.7 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CRC_CALIENTE_AAC.aac",
            country = "CR",
            tags = "Cumbia, Tropical, Salsa, Grupera",
            favicon = "https://caliente.cr/wp-content/uploads/2020/09/cropped-logo-caliente-192x192.png"
        ),
        RadioStation(
            name = "Radio Hit 104.7 FM",
            url = "https://columbia.grupocolumbia.co.cr:9050/stream",
            country = "CR",
            tags = "Rock, Pop, Anglo, Alternativo",
            favicon = "https://columbia.co.cr/wp-content/uploads/2021/04/cropped-favicon-hit-192x192.png"
        ),
        RadioStation(
            name = "Radio Centro 96.3 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CRC_CENTRO_AAC.aac",
            country = "CR",
            tags = "Baladas, Romántica, Recuerdos, Oro",
            favicon = "https://www.centro963.cr/wp-content/uploads/2020/09/cropped-logo-centro-192x192.png"
        ),
        RadioStation(
            name = "Zoom Radio 91.9 FM",
            url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CRC_ZOOM_AAC.aac",
            country = "CR",
            tags = "Retro, 80s, 90s, Clásicos, Anglo",
            favicon = "https://zoom919.com/wp-content/uploads/2020/09/cropped-logo-zoom-192x192.png"
        ),
        RadioStation(
            name = "Radio Sinfonola 90.3 FM",
            url = "http://rtvhd.net:9912/stream",
            country = "CR",
            tags = "Recuerdos, Boleros, Clásicos, Pasado",
            favicon = "https://www.sinfonola.co.cr/wp-content/uploads/2019/08/cropped-logo-sinfonola-1-192x192.png"
        ),
        RadioStation(
            name = "Super Estación 89.1 FM",
            url = "http://rtvhd.net:9922/stream",
            country = "CR",
            tags = "Noticias, Música, Deportes, Variedad",
            favicon = "https://superestacion.cr/wp-content/uploads/2020/06/cropped-super-favicon-192x192.png"
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
        ),
        RadioStation(
            name = "Ritmo Romántica 93.1 FM",
            url = "https://mdstrm.com/audio/5fada5850974b6080649727d/icecast.audio",
            country = "PE",
            tags = "Romántica, Baladas, Amor, Bachata",
            favicon = "https://graficos.crp.pe/v2/logos/r-ritmo-romantica.png"
        ),
        RadioStation(
            name = "Radio La Inolvidable 93.7 FM",
            url = "https://mdstrm.com/audio/5fad1e1141ccfb0809b43e5c/icecast.audio",
            country = "PE",
            tags = "Recuerdos, Baladas, Oro, Clásicos",
            favicon = "https://graficos.crp.pe/v2/logos/r-la-inolvidable.png"
        ),
        RadioStation(
            name = "Radio Moda Te Mueve 97.3 FM",
            url = "https://mdstrm.com/audio/5fada51df95e0c0827293570/icecast.audio",
            country = "PE",
            tags = "Reggaeton, Urbano, Trap, Hip-Hop",
            favicon = "https://graficos.crp.pe/v2/logos/r-moda.png"
        ),
        RadioStation(
            name = "Radio Oasis 100.1 FM",
            url = "https://mdstrm.com/audio/5fada5a9cb34b50821d3f58a/icecast.audio",
            country = "PE",
            tags = "Rock, Pop, 80s, 90s, Clásicos",
            favicon = "https://graficos.crp.pe/v2/logos/r-oasis.png"
        ),
        RadioStation(
            name = "Radio Planeta 107.7 FM",
            url = "https://mdstrm.com/audio/5fada5bfe4e09508207a7a51/icecast.audio",
            country = "PE",
            tags = "Pop, Éxitos, Electrónica, Hits Inglés",
            favicon = "https://graficos.crp.pe/v2/logos/r-planeta.png"
        ),
        RadioStation(
            name = "Radio Nueva Q 107.1 FM",
            url = "https://mdstrm.com/audio/5fada5cde4e09508207a7ab1/icecast.audio",
            country = "PE",
            tags = "Cumbia, Sanjuanera, Tropical, Popular",
            favicon = "https://graficos.crp.pe/v2/logos/r-nuevaq.png"
        ),
        RadioStation(
            name = "Radio Felicidad 88.9 FM",
            url = "https://mdstrm.com/audio/5fada52dfc16c006bd63371b/icecast.audio",
            country = "PE",
            tags = "Recuerdos, Baladas, Oro, Clásicos, Criollo",
            favicon = "https://graficos.crp.pe/v2/logos/r-felicidad.png"
        ),
        RadioStation(
            name = "Radio Mar Plus 106.3 FM",
            url = "https://mdstrm.com/audio/5fada53bf95e0c08272935d2/icecast.audio",
            country = "PE",
            tags = "Salsa, Merengue, Tropical, Timba",
            favicon = "https://graficos.crp.pe/v2/logos/r-mar.png"
        ),
        RadioStation(
            name = "Radio Panamericana 101.1 FM",
            url = "https://streaming.grupopanamericana.pe/panamericana",
            country = "PE",
            tags = "Salsa, Tropical, Merengue, Timba, Pop",
            favicon = "https://upload.wikimedia.org/wikipedia/commons/4/4b/Logo_Radio_Panamericana.png"
        ),
        RadioStation(
            name = "Radio Onda Cero 98.1 FM",
            url = "https://streaming.grupopanamericana.pe/ondacero",
            country = "PE",
            tags = "Reggaeton, Urbano, Trap, Pop, Hits",
            favicon = "https://panamericana.pe/static/img/ondacero.png"
        ),
        RadioStation(
            name = "Radio Doble Nueve 99.1 FM",
            url = "https://icecast.doblenueve.com/stream",
            country = "PE",
            tags = "Rock, Alternativo, Indie, Metal",
            favicon = "https://doblenueve.com/wp-content/uploads/2016/12/cropped-logo-99-new-192x192.png"
        ),
        RadioStation(
            name = "Radio Karibeña 94.9 FM",
            url = "http://167.114.118.120:8044/stream",
            country = "PE",
            tags = "Cumbia, Popular, Tropical, Fiesta",
            favicon = "https://radiokaribena.pe/wp-content/uploads/2021/04/cropped-favicon-192x192.png"
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
