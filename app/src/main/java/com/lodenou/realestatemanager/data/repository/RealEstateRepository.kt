package com.lodenou.realestatemanager.data.repository

import androidx.annotation.WorkerThread
import com.lodenou.realestatemanager.BuildConfig
import com.lodenou.realestatemanager.GeocodeResult
import io.reactivex.rxjava3.core.Observable
import com.lodenou.realestatemanager.data.RealEstateDao
import com.lodenou.realestatemanager.data.RealestateApi
import com.lodenou.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao,
   ) {


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

    fun getRealEstateByIdRoom(id: String): Flow<RealEstate> {
        return realEstateDao.getRealEstateById(id)
    }

    suspend fun deleteRealEstateById(id: String) {
        realEstateDao.deleteRealEstateById(id)
    }

    // Map api

    fun getLatLngFromAddress(address: String): Observable<GeocodeResult> {
        return RealestateApi.retrofitService.getLatLngFromAddress(address, BuildConfig.API_KEY)
    }
}
