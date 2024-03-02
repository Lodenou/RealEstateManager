package com.lodenou.realestatemanager.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.lodenou.realestatemanager.BuildConfig
import com.lodenou.realestatemanager.Location
import com.lodenou.realestatemanager.Utils
import com.lodenou.realestatemanager.Utils.isInternetAvailable
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lodenou.realestatemanager.ui.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val realEstateId = intent.getStringExtra("realEstateId") ?: return

        setContent {
            RealEstateManagerTheme {
                RealEstateDetailScreen(realEstateId, viewModel)
            }
        }
    }
}

@Composable
fun RealEstateDetailScreen(realEstateId: String, viewModel: DetailViewModel) {
    // Utilisez LaunchedEffect pour charger les données une fois
    val context = LocalContext.current
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

@Composable
fun PhotoCarousel(images: List<ImageWithDescription>?) {
    val context = LocalContext.current
    Column {
        Text(
            text = "Photos",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        LazyRow(modifier = Modifier.padding(horizontal = 8.dp)) {
            if (images != null) {
                items(images.size) { index ->
                    val image = images[index]
                    // Choisissez l'URI à utiliser: cloudUri si disponible et internet est disponible, sinon localUri
                    val imageUri = image.imageUri


                    Card(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .width(200.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box {
                            Image(
                                painter = rememberImagePainter(data = imageUri),
                                contentDescription = image.description,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                            // Bandeau de description avec transparence
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.5f),
                                                Color.Black.copy(alpha = 0.5f)
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                                    .padding(horizontal = 18.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = image.description,
                                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DescriptionPart(descriptionText: String?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.padding(0.dp, 8.dp))
        if (descriptionText != null) {
            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}


@Composable
fun InfoItemWithIcon(icon: @Composable () -> Unit, infoText: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Text(
            text = infoText,
            modifier = Modifier.padding(start = 8.dp),
            color = Color.White,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun DetailsSection(surface: Double?, numberOfRooms: Int?, location: String?) {
    Column(modifier = Modifier.padding(8.dp)) {
        InfoItemWithIcon(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Surface", tint = Color.White) },
            infoText = "Surface : ${surface}m²"
        )
        Spacer(modifier = Modifier.height(4.dp))
        InfoItemWithIcon(
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Chambres",
                    tint = Color.White
                )
            },
            infoText = "Nombre de chambres : $numberOfRooms"
        )
        Spacer(modifier = Modifier.height(4.dp))
        InfoItemWithIcon(
            icon = {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Localisation",
                    tint = Color.White
                )
            },
            infoText = "Localisation : $location"
        )
    }
}


@Composable
fun DisplayStaticMap(location: Location?, apiKey: String) {
    location?.let {
        val url = "https://maps.googleapis.com/maps/api/staticmap?center=${it.lat},${it.lng}&zoom=15&size=600x300&markers=color:red%7Clabel:S%7C${it.lat},${it.lng}&key=$apiKey"

        Image(
            painter = rememberImagePainter(url),
            contentDescription = "Static Map",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Vous pouvez ajuster la taille selon vos besoins
        )
    }
}

