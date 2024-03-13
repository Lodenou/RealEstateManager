package com.lodenou.realestatemanager.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.lodenou.realestatemanager.data.model.RealEstate

class RealEstateProvider : ContentProvider() {

    companion object {
        val AUTHORITY = "com.lodenou.realestatemanager.data.RealEstateProvider"
        val TABLE_NAME: String = RealEstate::class.java.simpleName
    }


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?
    ): Cursor {
        if (context != null) {
            val cursor: Cursor = RealEstateRoomDatabase.getDatabase(context!!).realEstateDao()
                .getRealEstateForContentProvider()
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.property/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String?>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        return 0
    }

}

