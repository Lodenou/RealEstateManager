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

    private val _realEstates = MediatorLiveData<List<RealEstate>>()
    val realEstates: LiveData<List<RealEstate>> = _realEstates

    init {
        observeLocalRealEstates()

        _realEstatesWithLatLng.value = listOf(
            RealEstateWithLatLng(
                RealEstate(
                    id = "1",
                    type = "Maison",
                    price = 300000.0,
                    area = 120.0,
                    numberOfRooms = 5,
                    description = "Une belle maison avec vue sur mer",
                    images = listOf(), // Assurez-vous d'avoir une liste d'images ou laissez-la vide si votre classe ImageWithDescription est bien définie ailleurs.
                    address = "123 Main St, Ville, Pays",
                    pointsOfInterest = listOf("École", "Parc", "Supermarché"),
                    status = "À vendre",
                    marketEntryDate = LocalDate.now(),
                    saleDate = null,
                    realEstateAgent = "Agent Immobilier"
                ),
                LatLng(48.8134, 2.5521)
            )
        )
    }


    private fun convertAddressesToLatLng(realEstates: List<RealEstate>) {
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
                                LatLng(0.0, 0.0) // Fournir une valeur par défaut si la lat ou lng est null
                            }
                            RealEstateWithLatLng(realEstate, latLng)
                        }
                        .onErrorReturnItem(RealEstateWithLatLng(realEstate, LatLng(0.0, 0.0))) // Gérer l'erreur
                }
                .toList()
                .subscribe({ realEstateWithLatLngList ->
                    _realEstatesWithLatLng.value = realEstateWithLatLngList
                }, { error ->
                    Log.e("MapViewModel", "Error converting addresses", error)
                })
            compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear() // Clean sub when vm is destroyed
    }

    private fun observeLocalRealEstates() {
        // Source Room
        val roomSource = repository.allRealEstates.asLiveData()
        _realEstates.addSource(roomSource) { realEstatesFromRoom ->
            _realEstates.value = realEstatesFromRoom
            convertAddressesToLatLng(realEstatesFromRoom)
        }
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
            // Gérer le cas où les permissions ne sont pas accordées
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
                        Log.d("MapViewModel", "Location fetched: Lat=${location.latitude}, Lon=${location.longitude}")
                        _userLocation.postValue(location)
                    } else {
                        Log.d("MapViewModel", "Location fetched is null")
                        _userLocation.postValue(null)
                    }
                }.addOnFailureListener { exception ->
                    Log.e("MapViewModel", "Exception while fetching location", exception)
                    _userLocation.postValue(null)
                }
            } else {
                Log.d("MapViewModel", "Location permissions not granted")
                _userLocation.postValue(null)
            }
        }
    }
}





