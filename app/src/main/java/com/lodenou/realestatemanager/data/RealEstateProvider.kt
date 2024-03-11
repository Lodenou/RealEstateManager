package com.lodenou.realestatemanager.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import kotlinx.coroutines.flow.forEach

class RealEstateProvider : ContentProvider() {

    private var realEstateDao: RealEstateDao? = null

    override fun onCreate(): Boolean {
        realEstateDao = RealEstateRoomDatabase.getDatabase(context!!).realEstateDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        // Définition des colonnes du MatrixCursor basé sur le modèle RealEstate
        val cursor = MatrixCursor(arrayOf("id", "type", "price", "area", "numberOfRooms", "description", "images", "address", "restaurant", "cinema", "ecole", "commerces", "status", "marketEntryDate", "saleDate", "realEstateAgent"))

        // Simulation de récupération des données de manière synchronisée.
        // Vous devez adapter cette partie pour qu'elle corresponde à votre implémentation de récupération des données.
        val realEstates = realEstateDao?.getAllRealEstatesSynchronously()

        realEstates?.forEach { realEstate ->
            // Formatage ou récupération des données nécessaires à partir de realEstate
            val imagesString = realEstate.images?.let { images ->
                // Ici, adaptez la conversion selon vos besoins, par exemple en convertissant la liste en JSON.
                convertImagesListToString(images)
            } ?: ""

            // Ajout des données de realEstate dans le cursor
            cursor.addRow(arrayOf(
                realEstate.id,
                realEstate.type,
                realEstate.price,
                realEstate.area,
                realEstate.numberOfRooms,
                realEstate.description,
                imagesString, // Les images sont converties en une String. Adaptez selon votre implémentation.
                realEstate.address,
                realEstate.restaurant,
                realEstate.cinema,
                realEstate.ecole,
                realEstate.commerces,
                realEstate.status,
                realEstate.marketEntryDate.toString(), // LocalDate converti en String
                realEstate.saleDate?.toString(), // Nullable LocalDate converti en String si non-null
                realEstate.realEstateAgent
            ))
        }

        return cursor
    }

    override fun getType(p0: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    private fun convertImagesListToString(images: List<ImageWithDescription>): String {
        // Implémentation de l'exemple : convertir chaque image en JSON ou concaténer les URIs des images, etc.
        return images.joinToString(separator = ";") { it.description }
    }
}

