package com.lodenou.realestatemanager.data

import android.content.Context
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RealEstateProviderTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        // Obtient le contexte de l'instrumentation
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun queryRealEstates() {
        // Construit l'URI pour accéder au ContentProvider
        val uri = Uri.parse("content://com.lodenou.realestatemanager.data.RealEstateProvider/realEstate")

        // Exécute la requête via le ContentResolver
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        assertNotNull("Le curseur est null", cursor)

        // Vérifie que le curseur contient au moins une entrée
        assertTrue("Le curseur est vide", (cursor?.count ?: 0) > 0)

        // Vérifie la présence des colonnes attendues
        val expectedColumns = arrayOf("id", "type", "price", "area", "numberOfRooms", "description", "images", "address", "restaurant", "cinema", "ecole", "commerces", "status", "marketEntryDate", "saleDate", "realEstateAgent")
        expectedColumns.forEach { column ->
            assertTrue("La colonne $column est manquante",
                (cursor?.getColumnIndex(column) ?: -1) >= 0
            )
        }

        // Ferme le curseur
        cursor?.close()
    }
}