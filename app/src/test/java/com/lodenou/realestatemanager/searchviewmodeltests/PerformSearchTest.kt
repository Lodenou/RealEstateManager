package com.lodenou.realestatemanager.searchviewmodeltests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import com.lodenou.realestatemanager.ui.viewmodel.SearchViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.jupiter.api.AfterEach
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.jupiter.api.BeforeEach
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU], application = HiltTestApplication::class)
class PerformSearchTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Inject
    lateinit var repository: RealEstateRepository

    private lateinit var viewModel: SearchViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)

        viewModel = SearchViewModel(repository, ApplicationProvider.getApplicationContext())
    }

    @Test
    fun performSearch_updatesSearchResults() = runBlocking {
        val expectedResults = listOf(
            RealEstate(
                id = "1", type = "Maison", price = 300000.0, area = 120.0, numberOfRooms = 4,
                description = "Belle maison avec jardin et piscine", images = emptyList(),
                address = "123 rue de l'Exemple, 75001 Paris", restaurant = true, cinema = true,
                ecole = true, commerces = true, status = "à vendre",
                marketEntryDate = LocalDate.now().minusDays(10), saleDate = null,
                realEstateAgent = "Agent Immobilier Exemple"
            ),
            // Ajoutez plus d'objets RealEstate si nécessaire
        )

        whenever(repository.allSearchRealEstates(
            minPrice = anyOrNull(),
            maxPrice = anyOrNull(),
            minArea = anyOrNull(),
            maxArea = anyOrNull(),
            restaurant = anyOrNull(),
            cinema = anyOrNull(),
            ecole = anyOrNull(),
            commerces = anyOrNull(),
            startDate = anyOrNull(),
            endDate = anyOrNull(),
            isSold = anyOrNull()
        )).thenReturn(flowOf(expectedResults))

        viewModel.performSearch()


        assertEquals(expectedResults, viewModel.searchResults.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}