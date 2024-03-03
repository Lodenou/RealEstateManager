package com.lodenou.realestatemanager.ui.components.detailactivitycomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.lodenou.realestatemanager.Location

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