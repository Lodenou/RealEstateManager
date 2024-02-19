package com.lodenou.realestatemanager.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "real_estate_table")
data class RealEstate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String?,
    val price: Double?,
    val area: Double?,
    val numberOfRooms: Int?,
    val description: String?,
    val images: List<ImageWithDescription>?,
    val address: String?,
    val pointsOfInterest: List<String>?,
    val status: Status?,
    val marketEntryDate: LocalDate,
    val saleDate: LocalDate?, // Nullable for unsold
    val realEstateAgent: String?
)



