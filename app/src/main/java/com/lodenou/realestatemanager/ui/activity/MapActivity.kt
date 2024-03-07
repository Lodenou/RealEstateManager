package com.lodenou.realestatemanager.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.ui.viewmodel.MapViewModel

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapActivity : ComponentActivity(), OnMapReadyCallback {

    private val mapViewModel: MapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
        setContent {
            RealEstateMap(mapViewModel = mapViewModel)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }


    private fun requestLocationPermissions() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                mapViewModel.checkPermissionsAndLocateUser()
            } else {
                finish()
            }
        }
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

@Composable
fun RealEstateMap(mapViewModel: MapViewModel) {
    val context = LocalContext.current
    // Observing RealEstate list from ViewModel
    val userLocation by mapViewModel.userLocation.observeAsState()
    val realEstatesWithLatLng by mapViewModel.realEstatesWithLatLng.observeAsState(initial = emptyList())

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 20f) // Valeur initiale
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), 16f
                )
            )
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        // Loop through real estates to place markers
        realEstatesWithLatLng.forEach { realEstate ->
            Log.d(
                "MarkerData",
                "Real estate: ${realEstate.realEstate.status}, LatLng: ${realEstate.latLng}"
            )
            Marker(
                state = MarkerState(position = realEstate.latLng),
                title = realEstate.realEstate.status, // Assurez-vous que RealEstate a un champ `name`
                snippet = "Marker for ${realEstate.realEstate.status}",
                onClick = {
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("realEstateId", realEstate.realEstate.id)
                    }
                    context.startActivity(intent)
                    true
                }
            )
        }
    }
}











