package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SliderWithValueFloat(
    label: String,
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    Column {
        Text(text = label)
        Slider(
            value = sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = range,
        )
    }
}

@Composable
fun SliderWithValueInt(
    label: String,
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    Column {
        Text(text = label)
        Slider(
            value = sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = range,
            steps = (range.endInclusive.toInt() - range.start.toInt()) - 1
        )
    }
}