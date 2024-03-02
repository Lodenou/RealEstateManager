package com.lodenou.realestatemanager.data.model


import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "real_estate_table")
data class RealEstate(
    @PrimaryKey var id: String = "",
    val type: String? = null,
    val price: Double? = null,
    val area: Double? = null,
    val numberOfRooms: Int? = null,
    val description: String? = null,
    val images: List<ImageWithDescription>? = null,
    val address: String? = null,
    val pointsOfInterest: List<String>? = null,
    val status: String? = null,
    var marketEntryDate: LocalDate = LocalDate.now(),
    var saleDate: LocalDate? = null,
    val realEstateAgent: String? = null,
    )



