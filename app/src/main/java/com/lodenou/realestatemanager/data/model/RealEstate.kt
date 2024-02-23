package com.lodenou.realestatemanager.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "real_estate_table")
data class RealEstate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String? = null,
    val price: Double? = null,
    val area: Double? = null,
    val numberOfRooms: Int? = null,
    val description: String? = null,
    // Pour les images, assurez-vous que la classe ImageWithDescription a également un constructeur sans arguments
    // Firestore ne peut pas stocker directement des listes d'objets personnalisés, vous devrez gérer la sérialisation/désérialisation manuellement
    val images: List<ImageWithDescription>? = null,
    val address: String? = null,
    val pointsOfInterest: String? = null,
    val status: String? = null,
    var marketEntryDate: LocalDate = LocalDate.now(),
    var saleDate: LocalDate? = null,
    val realEstateAgent: String? = null
)



