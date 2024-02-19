package com.lodenou.realestatemanager.data.repository

import androidx.annotation.WorkerThread
import com.lodenou.realestatemanager.data.RealEstateDao
import com.lodenou.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealEstateRepository @Inject constructor(private val realEstateDao: RealEstateDao) {

    // ROOM
    // Flow of all real estate properties. Unsorted
    val allRealEstates: Flow<List<RealEstate>> = realEstateDao.getAllRealEstates()


    @WorkerThread
    suspend fun insert(realEstate: RealEstate) {
        realEstateDao.insert(realEstate)
    }

    @WorkerThread
    suspend fun update(realEstate: RealEstate) {
        realEstateDao.update(realEstate)
    }

    @WorkerThread
    suspend fun delete(realEstate: RealEstate) {
        realEstateDao.delete(realEstate)
    }

    fun getRealEstateById(id: Int): Flow<RealEstate> {
        return realEstateDao.getRealEstateById(id)
    }
}