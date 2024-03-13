package com.lodenou.realestatemanager

import android.os.Build

import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import com.lodenou.realestatemanager.ui.viewmodel.DetailViewModel
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor

import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class DetailViewModelTest {

    private lateinit var viewModel: DetailViewModel
    private val repository: RealEstateRepository = mock()
    private val locationObserver: Observer<Location?> = mock() as Observer<Location?>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = DetailViewModel(repository)
        viewModel.location.observeForever(locationObserver)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun getLatLngFromAddress_success() {
        val address = "Some Address"
        val expectedLocation = Location(35.4, 25.0)
        val geometry = Geometry(location = expectedLocation)
        val result = Results(geometry = geometry)
        val geocodeResult = GeocodeResult(arrayListOf(result), "OK")

        whenever(repository.getLatLngFromAddress(address)).thenReturn(Single.just(geocodeResult).toObservable())

        viewModel.getLatLngFromAddress(address)


        shadowOf(getMainLooper()).idle()

        val locationCaptor = ArgumentCaptor.forClass(Location::class.java)
        verify(locationObserver).onChanged(locationCaptor.capture())
        assertEquals(expectedLocation, locationCaptor.value)

    }

    @After
    fun tearDown() {
        viewModel.location.removeObserver(locationObserver)
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}