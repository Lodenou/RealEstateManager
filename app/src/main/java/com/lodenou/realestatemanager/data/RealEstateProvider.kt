package com.lodenou.realestatemanager.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import kotlinx.coroutines.flow.forEach

class RealEstateProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.lodenou.realestatemanager.data.RealEstateProvider"
        const val REAL_ESTATE_TABLE = 100
        const val REAL_ESTATE_ID = 101

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "real_estate_table", REAL_ESTATE_TABLE)
            addURI(AUTHORITY, "real_estate_table/#", REAL_ESTATE_ID)
        }
    }

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
        val cursor = MatrixCursor(arrayOf("id", "type", "price", "area", "numberOfRooms", "description",
            "images", "address", "restaurant", "cinema", "ecole", "commerces", "status", "marketEntryDate",
            "saleDate", "realEstateAgent"))

        when (uriMatcher.match(uri)) {
            REAL_ESTATE_TABLE -> {
                val realEstates = realEstateDao?.getAllRealEstatesSynchronously()
                realEstates?.forEach { realEstate ->
                    val imagesString = convertImagesListToString(realEstate.images ?: listOf())

                    cursor.addRow(arrayOf(
                        realEstate.id,
                        realEstate.type,
                        realEstate.price?.toString() ?: "",
                        realEstate.area?.toString() ?: "",
                        realEstate.numberOfRooms?.toString() ?: "",
                        realEstate.description ?: "",
                        imagesString, // Assume que vous avez une fonction pour convertir les images en String
                        realEstate.address ?: "",
                        realEstate.restaurant.toString(),
                        realEstate.cinema.toString(),
                        realEstate.ecole.toString(),
                        realEstate.commerces.toString(),
                        realEstate.status ?: "",
                        realEstate.marketEntryDate.toString(),
                        realEstate.saleDate?.toString() ?: "",
                        realEstate.realEstateAgent ?: ""
                    ))
                }
            }
            REAL_ESTATE_ID -> {
                // Gérer la requête pour un ID spécifique si nécessaire
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        return cursor
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    // Implémentez les autres méthodes (getType, insert, delete, update) selon vos besoins...

    private fun convertImagesListToString(images: List<ImageWithDescription>): String {
        return images.joinToString(separator = ";") { it.description }
    }
}

