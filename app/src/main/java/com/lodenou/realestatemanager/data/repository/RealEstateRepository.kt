package com.lodenou.realestatemanager.data.repository

import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.model.Status
import java.util.Date

class RealEstateRepository {
    fun getRealEstates(): List<RealEstate> {
        return listOf(
            RealEstate(
                type = "Appartement",
                price = 250000.0,
                area = 70.0,
                numberOfRooms = 3,
                description = "Un bel appartement lumineux et spacieux.",
                images = listOf(ImageWithDescription("url1", "Vue du salon")),
                address = "123 rue Exemple",
                pointsOfInterest = listOf("École", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent A"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),
            RealEstate(
                type = "Maison",
                price = 480000.0,
                area = 150.0,
                numberOfRooms = 5,
                description = "Maison avec jardin dans un quartier calme.",
                images = listOf(ImageWithDescription("url2", "Jardin arrière")),
                address = "456 avenue Exemplaire",
                pointsOfInterest = listOf("Centre commercial", "Parc"),
                status = Status.AVAILABLE,
                marketEntryDate = Date(),
                saleDate = null,
                realEstateAgent = "Agent B"
            ),

        )
    }
}