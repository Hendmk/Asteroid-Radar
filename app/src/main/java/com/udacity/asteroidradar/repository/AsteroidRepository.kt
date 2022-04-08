package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.PictureOfTheDayEntity
import com.udacity.asteroidradar.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    suspend fun refreshAsteroids(
        startDate: String = getTodayDate(),
        endDate: String = getSeventhDayDate()
    ) {
        withContext(Dispatchers.IO) {
            val response: ResponseBody = AsteroidRadarApi.asteroidService.getAsteroids(
                startDate, endDate,
                Constants.API_KEY
            )
                .await()
            val asteroids: ArrayList<Asteroid> =
                parseAsteroidsJsonResult(JSONObject(response.string()))
            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
        }
    }

    suspend fun getPictureOfTheDay(): PictureOfDay? {
        var pictureOfDay: PictureOfDay
        withContext(Dispatchers.IO) {
            pictureOfDay = AsteroidRadarApi.asteroidService.getPictureOfTheDay().await()
        }
        return pictureOfDay
    }
}