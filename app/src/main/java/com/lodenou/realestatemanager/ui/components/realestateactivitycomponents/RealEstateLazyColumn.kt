package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.lodenou.realestatemanager.R
import com.lodenou.realestatemanager.ui.activity.DetailActivity
import com.lodenou.realestatemanager.data.model.RealEstate
import java.io.File
import java.io.FileOutputStream


@Composable
fun RealEstateListScreen(realEstates: List<RealEstate>?) {
    if (realEstates.isNullOrEmpty()) {
        Text(text = "Aucun bien à afficher", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn {
            items(realEstates) { realEstate ->
                RealEstateItem(realEstate)
            }
        }
    }
}

@Composable
fun RealEstateItem(realEstate: RealEstate) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .padding(16.dp)
        .clickable { // Intent pour lancer DetailActivity
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("realEstateId", realEstate.id)
            }
            context.startActivity(intent)
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (realEstate.images?.isNotEmpty() == true) {
                val imageUrl = realEstate.images.first().imageUri
                // Displaying the image with Coil
                Image(
                    painter = rememberImagePainter(
                        data = imageUrl,
                        builder = {
                            crossfade(true)
                            error(R.drawable.ic_launcher_foreground) // error image
                            placeholder(R.drawable.ic_launcher_background) // loading image
                            listener(onError = { request, throwable ->
                                Log.e("ImageLoadError", "Failed to load image", throwable)
                            })
                        }
                    ),
                    contentDescription = "Real Estate Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                // Using Icon to display an icon when no image is available
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "No image",
                    modifier = Modifier.size(100.dp) // Ensure the icon size matches the images for uniform layout
                )
            }
            Spacer(modifier = Modifier.width(8.dp)) // Space between the image/icon and text

            // Real estate details to the right of the image
            Column {
                Text(text = "Type: ${realEstate.type}")
                Text(text = "Price: ${realEstate.price}")
                Text(text = "Address: ${realEstate.address}")
            }
        }
    }
}


