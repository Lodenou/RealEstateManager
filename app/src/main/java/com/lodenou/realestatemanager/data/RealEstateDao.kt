package com.lodenou.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lodenou.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface RealEstateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(realEstate: RealEstate): Long

    @Query("SELECT * FROM real_estate_table")
    fun getAllRealEstates(): Flow<List<RealEstate>>

    @Query("SELECT * FROM real_estate_table")
    fun getAllRealEstatesSync(): Flow<List<RealEstate>>

    @Query("SELECT * FROM real_estate_table WHERE id = :id")
    fun getRealEstateById(id: String): Flow<RealEstate>

    @Update
    suspend fun update(realEstate: RealEstate)

    @Delete
    suspend fun delete(realEstate: RealEstate)

    @Query("DELETE FROM real_estate_table WHERE id = :id")
    suspend fun deleteRealEstateById(id: String)

//    @Query("""
//        SELECT * FROM real_estate_table
//        WHERE price BETWEEN :minPrice AND :maxPrice
//        AND area BETWEEN :minArea AND :maxArea
//        AND marketEntryDate BETWEEN :startDate AND :endDate
//        AND (:interests IS NULL OR pointsOfInterest LIKE :interests)
//    """)
//    fun searchRealEstate(
//        minPrice: Double?,
//        maxPrice: Double?,
//        minArea: Double?,
//        maxArea: Double?,
//        startDate: LocalDate,
//        endDate: LocalDate,
//        interests: String? // Utilisez un format pour les points d'intérêt qui permet la recherche LIKE, ou transformez-le en conditions multiples si nécessaire
//    ): Flow<List<RealEstate>> // Utilisez LiveData ou Flow selon vos besoins

    @Query("""
        SELECT * FROM real_estate_table 
        WHERE price BETWEEN :minPrice AND :maxPrice
        AND area BETWEEN :minArea AND :maxArea

    """)
    fun searchRealEstate(
        minPrice: Double?,
        maxPrice: Double?,
        minArea: Double?,
        maxArea: Double?,

    ): Flow<List<RealEstate>>
}
