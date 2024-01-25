package com.lodenou.realestatemanager.data.model

import java.util.Date

data class RealEstate(
    val type: String?,
    val price: Double?,
    val area: Double?,
    val numberOfRooms: Int?,
    val description: String?,
    val images: List<ImageWithDescription>?,
    val address: String?,
    val pointsOfInterest: List<String>?,
    val status: Status?,
    val marketEntryDate: Date,
    val saleDate: Date?, // Nullable for unsold
    val realEstateAgent: String?
)

