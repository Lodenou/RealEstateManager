package com.lodenou.realestatemanager.ui.components.detailactivitycomponents

import android.widget.Space
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.BuildConfig
import com.lodenou.realestatemanager.R
import com.lodenou.realestatemanager.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealEstateDetailScreen(realEstateId: String, viewModel: DetailViewModel, onBackButtonClick: ()-> Unit) {
    LaunchedEffect(realEstateId) {
        viewModel.getRealEstateFromRoomById(realEstateId)
    }

    val realEstate = viewModel.getRealEstateFromRoomById(realEstateId).observeAsState().value
    val scrollState = rememberScrollState()
    val apiKey = BuildConfig.API_KEY
    val location = viewModel.location.observeAsState().value

    if (realEstate != null) {

        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Real Estate Manager",
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackButtonClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.app_name),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }

        )
        { paddingValues ->

        Column(
            modifier = Modifier
                .padding(15.dp)
                .padding(paddingValues)
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
            Info(realEstate = realEstate)
            Spacer(modifier = Modifier.height(5.dp))

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
    }

    } else {
        Text(text = "Chargement des détails du bien immobilier...")
    }
}