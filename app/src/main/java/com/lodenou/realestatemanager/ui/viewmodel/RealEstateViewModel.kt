package com.lodenou.realestatemanager.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealEstateViewModel @Inject constructor(private val repository: RealEstateRepository) : ViewModel() {


    val allRealEstates: LiveData<List<RealEstate>> = repository.allRealEstates.asLiveData()

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
    fun getRealEstateById(id: Int): LiveData<RealEstate> {
        return repository.getRealEstateById(id).asLiveData()
    }
}
