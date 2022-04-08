package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.model.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()


interface AsteroidRadarApiService {

    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key")apiKey:String = API_KEY): Deferred<PictureOfDay>

    @GET("neo/rest/v1/feed")
    fun getAsteroids(@Query("start_date") startDate:String,
                             @Query("end_date") endDate:String,
                             @Query("api_key") apiKey: String = API_KEY): Deferred<ResponseBody>

}

object AsteroidRadarApi { val asteroidService = retrofit.create(AsteroidRadarApiService::class.java) }
