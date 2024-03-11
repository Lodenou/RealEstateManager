package com.lodenou.realestatemanager.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: RealEstateRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    var searchPerformed = mutableStateOf(false)

    var minPrice = mutableIntStateOf(0)
    var maxPrice = mutableIntStateOf(0)
    var minArea = mutableIntStateOf(0)
    var maxArea = mutableIntStateOf(0)

    var restaurant = mutableStateOf(false)
    var cinema = mutableStateOf(false)
    var ecole = mutableStateOf(false)
    var commerces = mutableStateOf(false)

    var startDate = mutableStateOf(LocalDate.now())
    var endDate = mutableStateOf(LocalDate.now())

    var isSold = mutableStateOf<Boolean?>(null)

    private val _searchResults = MutableStateFlow<List<RealEstate>>(emptyList())
    val searchResults: StateFlow<List<RealEstate>> = _searchResults.asStateFlow()

    fun performSearch() {
        searchPerformed.value = true
        viewModelScope.launch {
            repository.allSearchRealEstates(
                minPrice = minPrice.intValue.takeIf { it > 0 },
                maxPrice = maxPrice.intValue.takeIf { it > 0 },
                minArea = minArea.intValue.takeIf { it > 0 },
                maxArea = maxArea.intValue.takeIf { it > 0 },
                restaurant = restaurant.value,
                cinema = cinema.value,
                ecole = ecole.value,
                commerces = commerces.value,
                startDate = startDate.value,
                endDate = endDate.value,
                isSold = isSold.value
            ).collect { results ->
                _searchResults.value = results
            }
        }
    }

    fun resetSearch() {
        searchPerformed.value = false
        minPrice.value = 0
        maxPrice.value = 0
        minArea.value = 0
        maxArea.value = 0
        restaurant.value = false
        cinema.value = false
        ecole.value = false
        commerces.value = false
        startDate.value = LocalDate.now()
        endDate.value = LocalDate.now()
        isSold.value = null
        _searchResults.value = emptyList()
    }
}