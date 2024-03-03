package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import androidx.compose.runtime.Composable
import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel

@Composable
fun LoanTermSlider(viewModel: LoanCalculatorViewModel) {
    SliderWithValueInt(
        label = "Durée du prêt (en années): ${viewModel.term.intValue}",
        sliderValue = viewModel.term.intValue.toFloat(),
        onSliderValueChange = { newValue ->
            viewModel.term.intValue = newValue.toInt()
        },
        range = 0f..25f
    )
}