package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    label: String,
    defaultText: String
) {
    val open = remember { mutableStateOf(false) }

    if (open.value) {
        val initialDate =
            value ?: LocalDate.now()
        CalendarDialog(
            state = rememberUseCaseState(
                visible = true,
                onCloseRequest = { open.value = false }),
            config = CalendarConfig(
                yearSelection = true,
                style = CalendarStyle.MONTH,
            ),
            selection = CalendarSelection.Date(
                selectedDate = initialDate
            ) { newDate ->
                onValueChange(newDate)
            },
        )
    }

    val displayText = value?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: defaultText
    OutlinedTextField(
        modifier = Modifier.clickable { open.value = true },
        enabled = false,
        value = displayText,
        onValueChange = {},
        label = { Text(label) },
        shape = RoundedCornerShape(30.dp),
        trailingIcon = {
            if (value != null) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.clickable { onValueChange(null) }
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}