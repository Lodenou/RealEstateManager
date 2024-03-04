package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.lodenou.realestatemanager.ui.viewmodel.RealEstateViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImagePickerWithDescription(viewModel: RealEstateViewModel) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var imageDescription by remember { mutableStateOf("") }
    var showSourceDialog by remember { mutableStateOf(false) }

    // Création d'une Uri pour stocker l'image prise par la caméra
    val photoUri = remember {
        mutableStateOf<Uri?>(null)
    }

    // Préparer le launcher pour prendre une photo
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri.value?.let { uri ->
                    imageUri = uri
                    showDescriptionDialog = true
                }
            }
        }

    // Préparer le launcher pour sélectionner une image
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {

                Log.d("ImagePicker", "URI Received: $uri")

                try {

                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                    Log.d("ImagePicker", "Persistable read permission taken successfully for $uri")
                } catch (e: Exception) {
                    Log.e("ImagePicker", "Failed to take persistable read permissions", e)
                }

                imageUri = it
                showDescriptionDialog = true
            }
        }

    // Demander la permission d'accès à la caméra
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Créer une Uri temporaire pour l'image
                photoUri.value = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    createImageFile(context)
                )
                takePictureLauncher.launch(photoUri.value)
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Sélectionner la source de l'image
    fun showImageSourceDialog() {
        showSourceDialog = true
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Sélectionner la source de l'image") },
            text = {
                Column {
                    CustomButton(
                        text = "Prendre une photo",
                        onClick = {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            showSourceDialog = false
                        }
                    )
                    CustomButton(
                        text = "Choisir depuis la galerie",
                        onClick = {
                            pickImageLauncher.launch(arrayOf("image/*"))
                            showSourceDialog = false
                        }
                    )
                }
            },
            confirmButton = {}
        )
    }

    CustomButton(
        text = "choisir une image",
        onClick = { showImageSourceDialog() }
    )

    // Dialog pour entrer la description de l'image
    if (showDescriptionDialog) {
        DescriptionDialog(imageUri, imageDescription) { desc ->
            imageDescription = desc
            if (imageUri != null && imageDescription.isNotEmpty()) {
                viewModel.addImageWithDescription(imageUri!!, imageDescription)
                imageUri = null
                imageDescription = ""
            }
            showDescriptionDialog = false
        }
    }
    DisplaySelectedImages(viewModel)
}

@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
    ) {
        Text(text, color = Color.Black)
    }
}

@Composable
fun DescriptionDialog(
    imageUri: Uri?,
    initialDescription: String,
    onConfirm: (String) -> Unit
) {
    var description by rememberSaveable { mutableStateOf(initialDescription) }

    if (imageUri != null) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text("Description de l'image") },
            text = {
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Entrez une description") }
                )
            },
            confirmButton = {
                Button(onClick = { onConfirm(description) }) {
                    Text("Confirmer")
                }
            }
        )
    }
}

@Composable
fun DisplaySelectedImages(viewModel: RealEstateViewModel) {
    val context = LocalContext.current
    Column {
        viewModel.imagesWithDescriptions.forEach { imageWithDescription ->

            imageWithDescription.imageUri


            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(imageWithDescription.imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(imageWithDescription.description)
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { viewModel.removeImageWithDescription(imageWithDescription) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {

    }
}
