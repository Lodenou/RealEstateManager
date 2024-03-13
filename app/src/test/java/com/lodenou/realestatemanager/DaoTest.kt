package com.lodenou.realestatemanager

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lodenou.realestatemanager.data.RealEstateDao
import com.lodenou.realestatemanager.data.RealEstateRoomDatabase
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var database: RealEstateRoomDatabase
    private lateinit var dao: RealEstateDao

    @Before
    fun setup() {
        // Context of the app under test.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, RealEstateRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.realEstateDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetRealEstate() = runBlocking {
        val realEstate = RealEstate(id = "1",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate)

        val allRealEstates = dao.getAllRealEstates().first()
        assertTrue(allRealEstates.contains(realEstate))
    }

    @Test
    fun getAllRealEstates_returnsAllEntries() = runBlocking {

        val realEstate1  = RealEstate(id = "1",
        type = "Maison",
        price = 250000.0,
        area = 120.0,
        numberOfRooms = 4,
        description = "Une belle maison avec un grand jardin",
        images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
        address = "123 rue Imaginaire, Ville Fictive",
        restaurant = true,
        cinema = false,
        ecole = true,
        commerces = true,
        status = "à vendre",
        marketEntryDate = LocalDate.parse("2022-01-01"),
        saleDate = null,
        realEstateAgent = "Agent Immobilier Exemple")
        val realEstate2 =  RealEstate(id = "2",
        type = "Maison",
        price = 250000.0,
        area = 120.0,
        numberOfRooms = 4,
        description = "Une belle maison avec un grand jardin",
        images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
        address = "123 rue Imaginaire, Ville Fictive",
        restaurant = true,
        cinema = false,
        ecole = true,
        commerces = true,
        status = "à vendre",
        marketEntryDate = LocalDate.parse("2022-01-01"),
        saleDate = null,
        realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate1)
        dao.insert(realEstate2)

        val allRealEstates = dao.getAllRealEstates().first()

        assertEquals(listOf(realEstate1, realEstate2), allRealEstates)
    }

    @Test
    fun getRealEstateById_returnsCorrectRealEstate() = runBlocking {

        val realEstate = RealEstate(id = "1",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate)


        val result = dao.getRealEstateById("1").first()

        assertEquals(realEstate, result)
    }

    @Test
    fun update_realEstate_updatesCorrectly() = runBlocking {
        val realEstate = RealEstate(id = "1",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate)


        val updatedRealEstate = realEstate.copy(price = 500000.0)
        dao.update(updatedRealEstate)


        val result = dao.getRealEstateById("1").first()

        assertEquals(updatedRealEstate, result)
    }
    @Test
    fun delete_realEstate_deletesCorrectly() = runBlocking {

        val realEstate = RealEstate(id = "1",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate)

        dao.delete(realEstate)

        val result = dao.getAllRealEstates().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun deleteRealEstateById_deletesCorrectly() = runBlocking {

        val realEstate = RealEstate(id = "1",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate)


        dao.deleteRealEstateById("1")

        val result = dao.getAllRealEstates().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllRealEstatesSynchronously_returnsCorrectData() = runBlocking {
        val realEstate1  = RealEstate(id = "1",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        val realEstate2 =  RealEstate(id = "2",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate1)
        dao.insert(realEstate2)

        val realEstates = dao.getAllRealEstatesSynchronously()

        assertEquals(2, realEstates.size)
    }
    @Test
    fun getRealEstateForContentProvider_returnsCorrectCursor() = runBlocking {

        val realEstate = RealEstate(id = "1",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate)


        val cursor = dao.getRealEstateForContentProvider()

        assertEquals(1, cursor.count)
        cursor.close()
    }

    @Test
    fun searchRealEstate_returnsFilteredResults() = runBlocking {

        val realEstate1  = RealEstate(id = "1",
            type = "Maison",
            price = 50000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = true,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        val realEstate2 =  RealEstate(id = "2",
            type = "Maison",
            price = 250000.0,
            area = 120.0,
            numberOfRooms = 4,
            description = "Une belle maison avec un grand jardin",
            images = listOf(ImageWithDescription("imageUri1", "Description de l'image 1")),
            address = "123 rue Imaginaire, Ville Fictive",
            restaurant = false,
            cinema = false,
            ecole = true,
            commerces = true,
            status = "à vendre",
            marketEntryDate = LocalDate.parse("2022-01-01"),
            saleDate = null,
            realEstateAgent = "Agent Immobilier Exemple")
        dao.insert(realEstate1)
        dao.insert(realEstate2)

        val minPrice = 50000
        val maxPrice = 150000
        val restaurant = true


        val results = dao.searchRealEstate(minPrice, maxPrice, null, null, restaurant, false, false, false, null, null, null).first()

        assertEquals(1, results.size)
        assertEquals("1", results.first().id)
    }
}