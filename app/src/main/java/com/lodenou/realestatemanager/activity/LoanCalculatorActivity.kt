package com.lodenou.realestatemanager.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel
import kotlin.math.pow

class LoanCalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: LoanCalculatorViewModel by viewModels()
        setContent {
            RealEstateManagerTheme {
                LoanCalculator(viewModel)
            }
        }
    }
}

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

@Composable
fun LoanTitleWithBackButton() {
    val activity = LocalContext.current as Activity
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),

        ) {
        IconButton(
            onClick = { activity.finish() },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Retour",
            )

        }
        Text(
            text = "Simulateur de prêt",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 52.dp),
            textAlign = TextAlign.Start,
        )
    }
}

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


@OptIn(ExperimentalComposeUiApi::class)
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

@Composable
fun DisplayLoanCalculationResult(viewModel: LoanCalculatorViewModel) {
    DisplayResult(result = viewModel.calculationResult.value?.toFloat())
}


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

@Composable
fun DisplayResult(result: Float?) {
    Column {
        if (result != null) {
            Spacer(modifier = Modifier.height(26.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            )
            {
                Text(
                    text = "" + result + "€ de mensualités à payer", textAlign = TextAlign.Center,
                    modifier = Modifier.width(150.dp)
                )
            }

        }
    }
}