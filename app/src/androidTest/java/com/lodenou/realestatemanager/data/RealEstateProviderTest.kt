package com.lodenou.realestatemanager.data

import android.content.Context
import android.net.Uri
import androidx.room.Room

import androidx.test.platform.app.InstrumentationRegistry
import com.lodenou.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate


@RunWith(JUnit4::class)
class RealEstateProviderTest {

    private lateinit var context: Context
    private lateinit var db: RealEstateRoomDatabase
    private lateinit var realEstateDao: RealEstateDao
    private val testRealEstate = RealEstate(
        id = "1",
        type = "Maison",
        price = 300000.0,
        area = 120.0,
        numberOfRooms = 5,
        description = "Une belle maison",
        images = listOf(),
        address = "123 rue Exemple",
        restaurant = true,
        cinema = false,
        ecole = true,
        commerces = true,
        status = "à vendre",
        marketEntryDate = LocalDate.now(),
        saleDate = null,
        realEstateAgent = "Agent Exemple"
    )

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, RealEstateRoomDatabase::class.java).allowMainThreadQueries().build()
        realEstateDao = db.realEstateDao()

        // Insérer une entrée RealEstate dans la base de données
        runBlocking {
            realEstateDao.insert(testRealEstate)
        }
    }

    @Test
    fun queryRealEstates() {
        val uri = Uri.parse("content://com.lodenou.realestatemanager.data.RealEstateProvider/real_estate_table")
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        // Vérifie que le curseur n'est pas null et contient au moins une entrée
        assertNotNull("Le curseur est null", cursor)
        assertTrue("Le curseur est vide", (cursor?.count ?: 0) > 0)

        // Vérifie la présence des colonnes attendues
        val expectedColumns = arrayOf("id", "type", "price", "area", "numberOfRooms", "description",
            "images", "address", "restaurant", "cinema", "ecole", "commerces", "status", "marketEntryDate",
            "saleDate", "realEstateAgent")

        expectedColumns.forEach { column ->
            assertTrue("La colonne $column est manquante",
                (cursor?.getColumnIndex(column) ?: -1) >= 0
            )
        }

        cursor?.close()
    }


        @After
        fun tearDown() {
            // Supprimer l'entrée RealEstate de la base de données après le test
            runBlocking {
                realEstateDao.delete(testRealEstate)
            }

            db.close()
        }
}