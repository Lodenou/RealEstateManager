package com.lodenou.realestatemanager.data

import android.database.Cursor
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


    // Content provider purpose
    @Query("SELECT * FROM real_estate_table")
    fun getAllRealEstatesSynchronously(): List<RealEstate>

    @Query("SELECT * FROM real_estate_table")
    fun getRealEstateForContentProvider(): Cursor

    @Query("""
    SELECT * FROM real_estate_table 
    WHERE (:minPrice IS NULL OR price >= :minPrice)
    AND (:maxPrice IS NULL OR price <= :maxPrice)
    AND (:minArea IS NULL OR area >= :minArea)
    AND (:maxArea IS NULL OR area <= :maxArea)
    AND (:restaurant = 0 OR restaurant = :restaurant)
    AND (:cinema = 0 OR cinema = :cinema)
    AND (:ecole = 0 OR ecole = :ecole)
    AND (:commerces = 0 OR commerces = :commerces)
   AND (:startDate IS NULL OR  :endDate IS NULL OR marketEntryDate BETWEEN :startDate AND :endDate)
   AND (:isSold IS NULL OR (saleDate IS NOT NULL AND :isSold = 1) OR (saleDate IS NULL AND :isSold = 0))
""")
    fun searchRealEstate(
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        restaurant: Boolean = false,
        cinema: Boolean = false,
        ecole: Boolean = false,
        commerces: Boolean = false,
        startDate: LocalDate?,
        endDate: LocalDate?,
        isSold: Boolean?
    ): Flow<List<RealEstate>>
}
