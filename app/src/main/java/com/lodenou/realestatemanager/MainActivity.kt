package com.lodenou.realestatemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lodenou.realestatemanager.ui.viewmodel.MainActivityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainActivityViewModel by viewModels()
        setContent {
            RealEstateManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val realEstates by viewModel.realEstates.observeAsState(initial = emptyList())
                    RealEstateList(realEstates)
                }
            }
        }
    }
}

@Composable
fun RealEstateList(realEstates: List<RealEstate>) {
    LazyColumn {
        items(realEstates) { realEstate ->
            RealEstateItem(realEstate)
        }
    }
}
@Composable
fun RealEstateItem(realEstate: RealEstate) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Type: ${realEstate.type}")
        Text(text = "Prix: ${realEstate.price}")
        Text(text = "Image: ${realEstate.images?.get(0)}")
        Text(text = "Adresse: ${realEstate.address}")
    }
}



