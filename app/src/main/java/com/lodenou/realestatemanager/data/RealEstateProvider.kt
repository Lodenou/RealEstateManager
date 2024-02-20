package com.lodenou.realestatemanager.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.lifecycle.asLiveData

//class RealEstateProvider : ContentProvider() {
//
//    private var realEstateDao: RealEstateDao? = null
//
//    override fun onCreate(): Boolean {
//        realEstateDao = AppDatabase.getDatabase(context!!, null).realEstateDao()
//        return true
//    }
//
//    // Exemple d'implémentation de query() utilisant le DAO
//    override fun query(
//        uri: Uri,
//        projection: Array<String>?,
//        selection: String?,
//        selectionArgs: Array<String>?,
//        sortOrder: String?
//    ): Cursor? {
//        // Exemple : Accéder à tous les biens immobiliers
//        val cursor = MatrixCursor(arrayOf("id", "word", "type", "price", "area", "numberOfRooms",
//            "description", "images", "address", "pointsOfInterest", "status", "marketEntryDate", "saleDate", "realEstateAgent"))
//        val realEstates = realEstateDao?.getAllRealEstates()?.asLiveData()?.value
//        realEstates?.forEach { realEstate ->
//            // Ajouter vos données à cursor ici
//        }
//        return cursor
//    }
//
//    override fun getType(p0: Uri): String? {
//        TODO("Not yet implemented")
//    }
//
//    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
//        TODO("Not yet implemented")
//    }
//
//    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
//        TODO("Not yet implemented")
//    }
//
//    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
//        TODO("Not yet implemented")
//    }
//}