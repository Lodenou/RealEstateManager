package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel

@Composable
fun LoanCalculator(viewModel: LoanCalculatorViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        LoanTitleWithBackButton()
        Spacer(modifier = Modifier.height(16.dp))
        LoanAmountInput(viewModel, context)
        Spacer(modifier = Modifier.height(16.dp))
        InterestRateSlider(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        LoanTermSlider(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        ValidateButton(viewModel, context)
        DisplayLoanCalculationResult(viewModel)
    }
}