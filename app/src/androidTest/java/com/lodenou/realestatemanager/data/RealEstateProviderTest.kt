package com.lodenou.realestatemanager.data

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class RealEstateProviderTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun queryRealEstates() {

        val uri = Uri.parse("content://com.lodenou.realestatemanager.data.RealEstateProvider/realEstate")

        val cursor = context.contentResolver.query(uri, null, null, null, null)

        Assert.assertNotNull(cursor)

        val expectedColumns = arrayOf("id", "type", "price", "area", "numberOfRooms", "description", "images", "address", "restaurant", "cinema", "ecole", "commerces", "status", "marketEntryDate", "saleDate", "realEstateAgent")
        expectedColumns.forEach { column ->
            Assert.assertTrue("La colonne $column est manquante",
                (cursor?.getColumnIndex(column) ?: -1) >= 0
            )
        }

        cursor?.close()
    }
}