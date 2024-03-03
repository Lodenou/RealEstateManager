package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel
import kotlin.math.pow

@Composable
fun ValidateButton(viewModel: LoanCalculatorViewModel, context: Context) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Button(
        onClick = {
            validateAndCalculateLoan(viewModel, context)
            keyboardController?.hide()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Valider",
            color = Color.Black
        )
    }
}

private fun validateAndCalculateLoan(viewModel: LoanCalculatorViewModel, context: Context) {
    val amount = viewModel.amount.value.toDoubleOrNull() ?: 0.0
    val rate = viewModel.mortgageRate.doubleValue
    val termYears = viewModel.term.intValue


    if (amount > 0 && rate > 0 && termYears > 0) {
        val monthlyPayment = calculateMonthlyPayment(amount, rate, termYears)
        viewModel.calculationResult.value = monthlyPayment


    } else {
        Toast.makeText(context, "Veuillez entrer des valeurs valides", Toast.LENGTH_LONG).show()
    }
}

fun calculateMonthlyPayment(amount: Double, annualRate: Double, termYears: Int): Double {
    val monthlyInterestRate = annualRate / 12
    val loanTermMonths = termYears * 12

    return if (monthlyInterestRate > 0) {

        val monthlyPayment =
            amount * (monthlyInterestRate * (1 + monthlyInterestRate).pow(loanTermMonths)) /
                    ((1 + monthlyInterestRate).pow(loanTermMonths) - 1)
        monthlyPayment

    } else {
        amount / loanTermMonths
    }
}