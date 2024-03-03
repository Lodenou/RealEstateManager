package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel

@Composable
fun LoanAmountInput(viewModel: LoanCalculatorViewModel, context: Context) {
    TextField(
        value = viewModel.amount.value,
        onValueChange = { newValue ->
            handleAmountInput(newValue, viewModel, context)
        },
        label = { Text("Montant du prêt") },
        placeholder = { Text("Entrez le montant") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun handleAmountInput(
    newValue: String,
    viewModel: LoanCalculatorViewModel,
    context: Context
) {
    val newValueInt = newValue.toIntOrNull()
    when {
        newValue.isEmpty() -> viewModel.amount.value = ""
        newValueInt == null -> Toast.makeText(
            context,
            "Veuillez rentrer un montant valide",
            Toast.LENGTH_LONG
        ).show()

        newValueInt > 1500000 -> viewModel.amount.value = "1500000"
        else -> viewModel.amount.value = newValue
    }
}