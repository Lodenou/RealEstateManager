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
//       SELECT * FROM real_estate_table
//    WHERE (:minPrice IS NULL OR price >= :minPrice)
//    AND (:maxPrice IS NULL OR price <= :maxPrice)
//    AND (:minArea IS NULL OR area >= :minArea)
//    AND (:maxArea IS NULL OR area <= :maxArea)
//
//    """)
//    fun searchRealEstate(
//        minPrice: Int?,
//        maxPrice: Int?,
//        minArea: Int?,
//        maxArea: Int?,
//
//    ): Flow<List<RealEstate>>

    @Query("""
    SELECT * FROM real_estate_table 
    WHERE (:minPrice IS NULL OR price >= :minPrice)
    AND (:maxPrice IS NULL OR price <= :maxPrice)
    AND (:minArea IS NULL OR area >= :minArea)
    AND (:maxArea IS NULL OR area <= :maxArea)
    AND (:restaurant IS NULL OR restaurant = :restaurant)
    AND (:cinema IS NULL OR cinema = :cinema)
    AND (:ecole IS NULL OR ecole = :ecole)
    AND (:commerces IS NULL OR commerces = :commerces)
    AND (:startDate IS NULL OR  :endDate IS NULL OR marketEntryDate BETWEEN :startDate AND :endDate)
""")
    fun searchRealEstate(
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        restaurant: Boolean? = null,
        cinema: Boolean? = null,
        ecole: Boolean? = null,
        commerces: Boolean? = null,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Flow<List<RealEstate>>
}
