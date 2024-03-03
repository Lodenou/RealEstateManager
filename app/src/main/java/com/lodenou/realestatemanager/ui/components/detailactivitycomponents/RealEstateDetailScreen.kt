package com.lodenou.realestatemanager.ui.components.detailactivitycomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.BuildConfig
import com.lodenou.realestatemanager.ui.viewmodel.DetailViewModel

@Composable
fun RealEstateDetailScreen(realEstateId: String, viewModel: DetailViewModel) {
    LaunchedEffect(realEstateId) {
        viewModel.getRealEstateFromRoomById(realEstateId)
    }

    val realEstate = viewModel.realEstate.observeAsState().value
    val scrollState = rememberScrollState()
    val apiKey = BuildConfig.API_KEY
    val location = viewModel.location.observeAsState().value

    if (realEstate != null) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .verticalScroll(scrollState)
        ) {
            PhotoCarousel(images = realEstate.images)
            Spacer(modifier = Modifier.height(5.dp))
            DescriptionPart(descriptionText = realEstate.description)
            Spacer(modifier = Modifier.height(5.dp))
            DetailsSection(
                surface = realEstate.area,
                numberOfRooms = realEstate.numberOfRooms,
                location = realEstate.address
            )

            // Map part
            realEstate.address?.let { address ->
                // Call each time realestate is call with correct address
                LaunchedEffect(address) {
                    viewModel.getLatLngFromAddress(address)
                }
            }
            // Display map
            DisplayStaticMap(location = location, apiKey = apiKey)
        }


    } else {
        Text(text = "Chargement des détails du bien immobilier...")
    }
}