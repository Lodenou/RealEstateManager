package com.lodenou.realestatemanager.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: RealEstateRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {


    var minprice = mutableDoubleStateOf(0.0)
    var maxprice = mutableDoubleStateOf(0.0)
    var minarea = mutableDoubleStateOf(0.0)
    var maxarea = mutableDoubleStateOf(0.0)
//    var startDate = mutableIntStateOf(0)
//    var endDate = mutableIntStateOf(0)
//    var interests = mutableIntStateOf(0)
}