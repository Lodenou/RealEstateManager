package com.lodenou.realestatemanager.ui.viewmodel

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class LoanCalculatorViewModel(): ViewModel() {
        var amount = mutableStateOf("")
        var mortgageRate = mutableDoubleStateOf(0.0)
        var term = mutableIntStateOf(0)

        var calculationResult = mutableStateOf<Double?>(null)
}