package com.lodenou.realestatemanager.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import com.lodenou.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lodenou.realestatemanager.ui.viewmodel.LoanCalculatorViewModel

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val viewModel: LoanCalculatorViewModel by viewModels()
        setContent {
            RealEstateManagerTheme {
                Text(text = "TEST")
            }
        }
    }
    
    
}
