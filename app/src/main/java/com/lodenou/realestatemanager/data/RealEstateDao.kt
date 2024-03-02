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
}