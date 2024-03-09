package com.lodenou.realestatemanager.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: RealEstateRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {


    var minPrice = mutableIntStateOf(0)
    var maxPrice = mutableIntStateOf(0)
    var minArea = mutableIntStateOf(0)
    var maxArea = mutableIntStateOf(0)
//    var startDate = mutableIntStateOf(0)
//    var endDate = mutableIntStateOf(0)
//    var interests = mutableIntStateOf(0)

    private val _searchResults = MutableStateFlow<List<RealEstate>>(emptyList())
    val searchResults: StateFlow<List<RealEstate>> = _searchResults.asStateFlow()

    fun performSearch() {
        viewModelScope.launch {
            repository.allSearchRealEstates(
                minPrice = minPrice.intValue.takeIf { it > 0 },
                maxPrice = maxPrice.intValue.takeIf { it > 0 },
                minArea = minArea.intValue.takeIf { it > 0 },
                maxArea = maxArea.intValue.takeIf { it > 0 }
            ).collect { results ->
                _searchResults.value = results
            }
        }
    }
}