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
import com.lodenou.realestatemanager.activity.DetailActivity
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
                // Sélectionnez l'URI appropriée en fonction de la disponibilité d'Internet
                val imageUrl = realEstate.images.first().imageUri


                //TODO FIND WAY TO GET PERMISSION FOR IMAGE STORAGE
//                val imageUri = copyImageToAppStorageFromUriString(imageUrl, context)
                // Displaying the image with Coil
                Image(
                    painter = rememberImagePainter(
                        data = imageUrl,
                        builder = {
                            crossfade(true)
                            error(R.drawable.ic_launcher_foreground) // Utilisez votre image d'erreur
                            placeholder(R.drawable.ic_launcher_background) // Utilisez votre image de chargement
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


fun copyImageToAppStorage(imageUri: Uri, context: Context): Uri? {
    // Ici, insérez votre logique existante pour copier l'image dans le stockage de l'application
    // et retourner le nouvel Uri de l'image copiée.
    // Exemple simplifié (assurez-vous d'implémenter la gestion d'erreur appropriée) :
    val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
    val newFile = File(context.filesDir, "images/${System.currentTimeMillis()}.jpg").apply {
        parentFile?.mkdirs() // Assurez-vous que le dossier existe
    }
    val outputStream = FileOutputStream(newFile)

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return Uri.fromFile(newFile)
}

fun copyImageToAppStorageFromUriString(imageUriString: String, context: Context): Uri? {
    // Convertir la chaîne en Uri
    val imageUri = Uri.parse(imageUriString)
    return copyImageToAppStorage(imageUri, context)
}