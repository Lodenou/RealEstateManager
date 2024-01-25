package com.lodenou.realestatemanager.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository

class MainActivityViewModel : ViewModel() {
    private val repository = RealEstateRepository()
    val realEstates = MutableLiveData<List<RealEstate>>(repository.getRealEstates())

    init {
        realEstates.value = repository.getRealEstates()
    }
}
