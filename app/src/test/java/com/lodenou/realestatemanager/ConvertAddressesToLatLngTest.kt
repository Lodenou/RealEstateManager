package com.lodenou.realestatemanager

import android.content.Context
import android.os.Build
import android.os.Looper.getMainLooper
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.maps.model.LatLng
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.model.RealEstateWithLatLng
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import com.lodenou.realestatemanager.ui.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.testing.HiltTestApplication
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU], application = HiltTestApplication::class)
class ConvertAddressesToLatLngTest {


    private lateinit var viewModel: MapViewModel
    private val repository: RealEstateRepository = mock()
    private val realEstatesWithLatLngObserver: Observer<List<RealEstateWithLatLng>> = mock()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
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

        viewModel = MapViewModel(repository, ApplicationProvider.getApplicationContext())
        viewModel.realEstatesWithLatLng.observeForever(realEstatesWithLatLngObserver)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

        @Test
        fun convertAddressesToLatLng_success() {
            val realEstates = listOf(
                RealEstate(address = "Some Address 1"),
                RealEstate(address = "Some Address 2")
            )

            whenever(repository.getLatLngFromAddress(anyString())).thenAnswer { invocation ->
                val geocodeResult = when (invocation.arguments[0] as String) {
                    "Some Address 1" -> GeocodeResult(
                        results = arrayListOf(
                            Results(
                                addressComponents = arrayListOf(),
                                geometry = Geometry(
                                    location = Location(35.4, 25.0)
                                )
                            )
                        ),
                        status = "OK"
                    )
                    "Some Address 2" -> GeocodeResult(
                        results = arrayListOf(
                            Results(
                                addressComponents = arrayListOf(),
                                geometry = Geometry(
                                    location = Location(45.0, 15.0)
                                )
                            )
                        ),
                        status = "OK"
                    )
                    else -> GeocodeResult(
                        results = arrayListOf(
                            Results(
                                addressComponents = arrayListOf(),
                                geometry = Geometry(
                                    location = Location(0.0, 0.0)
                                )
                            )
                        ),
                        status = "OK"
                    )
                }
                Single.just(geocodeResult).toObservable()
            }

            viewModel.convertAddressesToLatLng(realEstates)
            shadowOf(getMainLooper()).idle()

            // verifiy if the onchanged is called
            verify(realEstatesWithLatLngObserver, times(1)).onChanged(any())
        }
        @After
        fun tearDown() {
            viewModel.realEstatesWithLatLng.removeObserver(realEstatesWithLatLngObserver)
            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()
        }
    }