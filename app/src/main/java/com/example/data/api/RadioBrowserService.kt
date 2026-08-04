package com.example.data.api

import com.squareup.moshi.Json
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class ApiRadioStation(
    @Json(name = "name") val name: String?,
    @Json(name = "url_resolved") val urlResolved: String?,
    @Json(name = "countrycode") val countryCode: String?,
    @Json(name = "tags") val tags: String?,
    @Json(name = "favicon") val favicon: String?
)

interface RadioBrowserApi {
    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("countrycode") countryCode: String,
        @Query("name") name: String? = null,
        @Query("limit") limit: Int = 40,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true
    ): List<ApiRadioStation>
}

object RetrofitClient {
    private const val BASE_URL = "https://de1.api.radio-browser.info/"

    val apiService: RadioBrowserApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(RadioBrowserApi::class.java)
    }
}
