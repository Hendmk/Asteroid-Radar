package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.getSeventhDayDate
import com.udacity.asteroidradar.api.getTodayDate
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _navigateToDetailFragment = MutableLiveData<Asteroid>()

    val navigateToDetailFragment: LiveData<Asteroid>
        get() = _navigateToDetailFragment

    private var _asteroidsList = MutableLiveData<List<Asteroid>>()

    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroidsList

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    init {
        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroids()
                refreshPictureOfDay()
                showAllWeekAsteroids()
            } catch (e: Exception) {
                println("*Exception refreshAsteroids*_ ${e.message}")
            }
        }
    }

    private suspend fun refreshPictureOfDay() {
        _pictureOfDay.value = asteroidRepository.getPictureOfTheDay()
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToDetailFragment.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToDetailFragment.value = null
    }

    fun showAllWeekAsteroids() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroidsByDate(
                getTodayDate(),
                getSeventhDayDate()
            )
                .collect {
                    _asteroidsList.value = it
                }
        }
    }

    fun showTodayAsteroids() {
        viewModelScope.launch {
            database.asteroidDao.getTodayAsteroids(getTodayDate())
                .collect {
                    _asteroidsList.postValue(it)
                }
        }
    }

    fun showSavedAsteroids() {
        viewModelScope.launch {
            database.asteroidDao.getAllAsteroids()
                .collect {
                    _asteroidsList.postValue(it)
                }
        }
    }
}