package com.lodenou.realestatemanager.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealEstateViewModel @Inject constructor(
    private val repository: RealEstateRepository
) : ViewModel() {

    var imagesWithDescriptions = mutableStateListOf<ImageWithDescription>()
    private var realEstatesObserver: Observer<List<RealEstate>>? = null


    private val _realEstates = MutableLiveData<List<RealEstate>>()
    val realEstates: LiveData<List<RealEstate>> = _realEstates

    init {
        observeLocalRealEstates()
    }

    private fun observeLocalRealEstates() {
        realEstatesObserver = Observer { realEstatesFromRoom ->
            _realEstates.value = realEstatesFromRoom
        }
        repository.allRealEstates.asLiveData().observeForever(realEstatesObserver!!)
    }

    override fun onCleared() {
        super.onCleared()
        // remove observer
        realEstatesObserver?.let { observer ->
            repository.allRealEstates.asLiveData().removeObserver(observer)
        }
    }
    /**
     * Launching a new coroutine to insert the data in a non-blocking way.
     */
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

    /**
     * Retrieves a real estate property by its ID.
     */
    fun getRealEstateById(id: String): LiveData<RealEstate> {
        return repository.getRealEstateByIdRoom(id).asLiveData()
    }


    fun addImageWithDescription(imageUri: Uri, description: String) {
        val imageWithDescription = ImageWithDescription(imageUri.toString(), description)
        imagesWithDescriptions.add(imageWithDescription)
    }

    fun removeImageWithDescription(imageWithDescription: ImageWithDescription) {
        imagesWithDescriptions.remove(imageWithDescription)
    }
}
