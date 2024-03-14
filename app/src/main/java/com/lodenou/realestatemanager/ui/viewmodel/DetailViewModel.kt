package com.lodenou.realestatemanager.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lodenou.realestatemanager.Location
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RealEstateRepository
) : ViewModel() {


    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val compositeDisposable = CompositeDisposable()

    var imagesWithDescriptionsDetail = mutableStateListOf<ImageWithDescription>()
    /**
     * Retrieves a real estate property by its ID.
     */
    fun getRealEstateFromRoomById(id: String): LiveData<RealEstate> {
        return repository.getRealEstateByIdRoom(id).asLiveData()
    }

    private val _realEstate = MutableLiveData<RealEstate?>()
    val realEstate: LiveData<RealEstate?> = _realEstate
    
    fun getLatLngFromAddress(address: String) {
        compositeDisposable.add(
            repository.getLatLngFromAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { geocodeResult ->

                    geocodeResult.results.firstOrNull()?.geometry?.location ?: Location(35.4, 25.0)
                }
                .subscribe({ location ->
                    _location.value = location
                }, { error ->
                    Log.e("DetailViewModel", "Error fetching location", error)
                    _location.value = null
                })
        )
    }
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear() // Clean composite when vm is destroyed
    }

    // update realestate

    fun insert(realEstate: RealEstate) = viewModelScope.launch {
        repository.insert(realEstate)
    }

    /**
     * Launching a new coroutine to update the data in a non-blocking way.
     */
    fun update(realEstate: RealEstate) = viewModelScope.launch {
        repository.update(realEstate)
    }

    /**
     * Launching a new coroutine to delete the data in a non-blocking way.
     */
    fun delete(realEstate: RealEstate) = viewModelScope.launch {
        repository.delete(realEstate)
    }

    fun addImageWithDescription(imageUri: Uri, description: String) {
        val imageWithDescription = ImageWithDescription(imageUri.toString(), description)
        imagesWithDescriptionsDetail.add(imageWithDescription)
    }

    fun removeImageWithDescription(imageWithDescription: ImageWithDescription) {
        imagesWithDescriptionsDetail.remove(imageWithDescription)
    }
}