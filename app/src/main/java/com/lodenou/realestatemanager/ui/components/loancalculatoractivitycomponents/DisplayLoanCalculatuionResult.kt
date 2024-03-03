package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import androidx.compose.runtime.Composable
import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel

@Composable
fun DisplayLoanCalculationResult(viewModel: LoanCalculatorViewModel) {
    DisplayResult(result = viewModel.calculationResult.value?.toFloat())
}