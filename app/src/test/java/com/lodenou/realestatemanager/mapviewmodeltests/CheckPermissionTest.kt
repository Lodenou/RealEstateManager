package com.lodenou.realestatemanager.mapviewmodeltests

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import com.lodenou.realestatemanager.ui.viewmodel.MapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CheckPermissionTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: RealEstateRepository

    private lateinit var viewModel: MapViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = Mockito.mock(RealEstateRepository::class.java)

        Dispatchers.setMain(testDispatcher)


        val fakeImages = listOf(
            ImageWithDescription("imageUri1", "description1"),
            ImageWithDescription("imageUri2", "description2")
        )

        val fakeRealEstateList = listOf(
            RealEstate(
                id = "1",
                type = "Maison",
                price = 250000.0,
                area = 120.0,
                numberOfRooms = 4,
                description = "Belle maison avec jardin",
                images = fakeImages,
                address = "123 Fake Street, Faketown",
                restaurant = true,
                cinema = false,
                ecole = true,
                commerces = true,
                status = "à vendre",
                marketEntryDate = LocalDate.now(),
                saleDate = null,
                realEstateAgent = "Agent Immobilier 1"
            ),
            RealEstate(
                id = "2",
                type = "Appartement",
                price = 350000.0,
                area = 80.0,
                numberOfRooms = 3,
                description = "Appartement spacieux en centre-ville",
                images = fakeImages,
                address = "456 Fake Avenue, Fakecity",
                restaurant = true,
                cinema = true,
                ecole = false,
                commerces = true,
                status = "vendu",
                marketEntryDate = LocalDate.now().minusMonths(2),
                saleDate = LocalDate.now(),
                realEstateAgent = "Agent Immobilier 2"
            )
        )
        val fakeRealEstatesFlow = flowOf(fakeRealEstateList)
        whenever(repository.allRealEstates).thenReturn(fakeRealEstatesFlow)
        viewModel = MapViewModel(repository, context)
    }

    @Test
    fun checkPermissionsAndLocateUser_PermissionsNotGranted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Mockito.mockStatic(ContextCompat::class.java).use { mockedStatic ->
            mockedStatic.`when`<Int> { ContextCompat.checkSelfPermission(eq(context), any()) }
                .thenReturn(PackageManager.PERMISSION_DENIED)

            viewModel.checkPermissionsAndLocateUser()

            assert(viewModel.userLocation.value == null)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}


