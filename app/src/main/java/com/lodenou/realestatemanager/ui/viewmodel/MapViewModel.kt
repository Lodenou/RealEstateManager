package com.lodenou.realestatemanager.ui.viewmodel

import android.Manifest

import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks

import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.model.RealEstateWithLatLng
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import androidx.lifecycle.Observer
import java.time.LocalDate

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: RealEstateRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> = _userLocation

    private val _realEstatesWithLatLng = MutableLiveData<List<RealEstateWithLatLng>>()
    val realEstatesWithLatLng: LiveData<List<RealEstateWithLatLng>> = _realEstatesWithLatLng

    private val _realEstates = MutableLiveData<List<RealEstate>>()
    val realEstates: LiveData<List<RealEstate>> = _realEstates
    private var realEstatesObserver: Observer<List<RealEstate>>? = null

    init {
        observeLocalRealEstates()
    }


     fun convertAddressesToLatLng(realEstates: List<RealEstate>) {
            val disposable = Observable.fromIterable(realEstates)
                .flatMap { realEstate ->
                    repository.getLatLngFromAddress(realEstate.address ?: "")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { geocodeResult ->
                            val location = geocodeResult.results.firstOrNull()?.geometry?.location
                            val latLng = if (location?.lat != null && location.lng != null) {
                                LatLng(location.lat!!, location.lng!!)
                            } else {
                                LatLng(0.0, 0.0)
                            }
                            RealEstateWithLatLng(realEstate, latLng)

                        }
                        .onErrorReturnItem(RealEstateWithLatLng(realEstate, LatLng(0.0, 0.0)))
                }
                .toList()
                .subscribe({ realEstateWithLatLngList ->
                    _realEstatesWithLatLng.value = realEstateWithLatLngList
                }, { error ->
                })
            compositeDisposable.add(disposable)
    }

private fun observeLocalRealEstates() {
    realEstatesObserver = Observer { realEstatesFromRoom ->
        _realEstates.value = realEstatesFromRoom
        convertAddressesToLatLng(realEstatesFromRoom)
    }
    repository.allRealEstates.asLiveData().observeForever(realEstatesObserver!!)
}
    override fun onCleared() {
        super.onCleared()
        // clear observer forever to avoid memory leaks
        realEstatesObserver?.let { observer ->
            repository.allRealEstates.asLiveData().removeObserver(observer)
        }
        compositeDisposable.clear()
    }


    // location
    fun checkPermissionsAndLocateUser() {
        val permissionsGranted = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).all { permission ->
            ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (permissionsGranted) {
            locateUser()
        } else {
            _userLocation.value = null
        }
    }


    private fun locateUser() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)
        viewModelScope.launch {
            if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        _userLocation.postValue(location)
                    } else {
                        _userLocation.postValue(null)
                    }
                }.addOnFailureListener { exception ->
                    _userLocation.postValue(null)
                }
            } else {
                _userLocation.postValue(null)
            }
        }
    }
}





