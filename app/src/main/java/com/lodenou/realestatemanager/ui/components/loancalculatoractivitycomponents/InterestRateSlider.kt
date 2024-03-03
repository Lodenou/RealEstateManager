package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import androidx.compose.runtime.Composable
import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel

@Composable
fun InterestRateSlider(viewModel: LoanCalculatorViewModel) {
    SliderWithValueFloat(
        label = "Taux d'intérêt : ${"%.1f".format(viewModel.mortgageRate.doubleValue * 100)}%",
        sliderValue = viewModel.mortgageRate.doubleValue.toFloat() * 100,
        onSliderValueChange = { newValue ->
            viewModel.mortgageRate.doubleValue = newValue.toDouble() / 100
        },
        range = 0f..15f,
    )
}