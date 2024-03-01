package com.lodenou.realestatemanager.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lodenou.realestatemanager.Utils
import com.lodenou.realestatemanager.Utils.isInternetAvailable
import com.lodenou.realestatemanager.Utils.networkStatus
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RealEstateViewModel @Inject constructor(
    private val appContext: Context,
    private val repository: RealEstateRepository
) : ViewModel() {

    var imagesWithDescriptions = mutableStateListOf<ImageWithDescription>()

    //    private val _realEstates = MutableLiveData<List<RealEstate>>()
    private val _realEstates = MediatorLiveData<List<RealEstate>>()
    val realEstates: LiveData<List<RealEstate>> = _realEstates

    init {
        observeNetworkStatus()
        observeLocalRealEstates()
    }

    private fun observeLocalRealEstates() {
        // Source Room
        val roomSource = repository.allRealEstates.asLiveData()
        _realEstates.addSource(roomSource) { realEstatesFromRoom ->
            _realEstates.value = realEstatesFromRoom
        }
    }

    private fun setupFirestoreListenerIfConnected() {
        // for update or add
        if (isInternetAvailable(appContext)) {
            val firestoreSource = repository.listenToRealEstatesUpdates().asLiveData()
            _realEstates.addSource(firestoreSource) { realEstatesFromFirestore ->
                _realEstates.value = realEstatesFromFirestore
            }
        }
    }


    val allRealEstates: LiveData<List<RealEstate>> = repository.allRealEstates.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way.
     */
    fun insert(realEstate: RealEstate) = viewModelScope.launch {
        repository.insert(realEstate)
    }

    /**
     * Launching a new coroutine to update the data in a non-blocking way.
     */
    fun update(realEstate: RealEstate) = viewModelScope.launch {
        repository.update(realEstate)
    }

    /**
     * Launching a new coroutine to delete the data in a non-blocking way.
     */
    fun delete(realEstate: RealEstate) = viewModelScope.launch {
        repository.delete(realEstate)
    }

    /**
     * Retrieves a real estate property by its ID.
     */
    fun getRealEstateById(id: String): LiveData<RealEstate> {
        return repository.getRealEstateByIdRoom(id).asLiveData()
    }


    fun addImageWithDescription(imageUri: Uri, description: String) {
        val imageWithDescription = ImageWithDescription(imageUri.toString(), description)
        imagesWithDescriptions.add(imageWithDescription)
    }

    fun removeImageWithDescription(imageWithDescription: ImageWithDescription) {
        imagesWithDescriptions.remove(imageWithDescription)
    }


//    fun saveRealEstateWithImages(realEstate: RealEstate) {
//        viewModelScope.launch {
//            try {
//                val updatedImages = realEstate.images?.mapNotNull { image ->
//                    if (image.imageUrl.startsWith("file://") || image.imageUrl.startsWith("content://")) {
//                        // L'image est stockée localement, nécessite un upload
//                        val newImageUrl = repository.uploadImage(Uri.parse(image.imageUrl))
//                        image.copy(imageUrl = newImageUrl)
//                    } else {
//                        // L'image est déjà sur le Cloud, pas besoin de la re-télécharger
//                        null // ou retourner 'image' si vous voulez la conserver telle quelle
//                    }
//                }?.toList()
//
//                if (updatedImages != null && updatedImages.isNotEmpty()) {
//                    val updatedRealEstate = realEstate.copy(images = updatedImages)
//                    repository.saveRealEstateToFirestore(updatedRealEstate, onSuccess = { /* succès */ }, onFailure = { /* échec */ })
//                }
//            } catch (e: Exception) {
//                Log.e("ViewModel", "Error saving real estate with images", e)
//            }
//        }
//    }


    fun saveRealEstateWithImages(realEstate: RealEstate) = viewModelScope.launch {
        realEstate.images?.forEach { image ->
            // Gérez d'abord le chemin local
            val localUri = image.localUri

            // Initialiser cloudUri à null
            var cloudUri: String? = null

            // Si Internet est disponible, tentez d'uploader l'image et récupérez son URI cloud
            if (isInternetAvailable(appContext)) {
                cloudUri = try {
                    repository.uploadImage(Uri.parse(localUri))
                } catch (e: Exception) {
                    Log.e("ViewModel", "Failed to upload image to cloud", e)
                    null // En cas d'échec, laissez cloudUri à null
                }
            }

            // Mise à jour de l'image avec le cloudUri
            image.cloudUri = cloudUri

            // Maintenant, sauvegardez ou mettez à jour l'objet RealEstate dans Room avec les nouvelles informations d'image
            repository.insert(realEstate)
        }

        // Après avoir géré les images localement et potentiellement les avoir synchronisées avec le cloud,
        // sauvegardez ou mettez à jour l'objet RealEstate dans Firestore si nécessaire
        if (isInternetAvailable(appContext)) {
            repository.saveRealEstateToFirestore(realEstate, onSuccess = {
                Log.d("ViewModel", "RealEstate saved to Firestore successfully")
            }, onFailure = { e ->
                Log.e("ViewModel", "Failed to save RealEstate to Firestore", e)
            })
        }
    }

    // network and sync datas from local & firestore db
    private fun observeNetworkStatus() {

//        synchronizeIfConnected()
        appContext.networkStatus()
            .distinctUntilChanged() // Avoid repetition
            .filter { it } // continue only if internet is on
            .onEach {
                synchronizeImagesIfNeeded()
                synchronizeFirestoreToRoom()
                synchronizeRoomToFirestore()
                setupFirestoreListenerIfConnected()
                setupFirestoreDocumentListener()
            }
            .launchIn(viewModelScope)
    }

    private fun synchronizeFirestoreToRoom() {
        viewModelScope.launch {
            try {
                repository.synchronizeFirestoreToRoom()
                Log.d("ViewModel", "Synchronization from Firestore to Room succeeded")
            } catch (e: Exception) {
                Log.e("ViewModel", "Synchronization from Firestore to Room failed", e)
            }
        }
    }


    private fun synchronizeIfConnected() {
        viewModelScope.launch {
            if (isInternetAvailable(appContext)) {
                synchronizeFirestoreToRoom()
            }
        }
    }

    fun addNewRealEstate(realEstate: RealEstate) = viewModelScope.launch {
        // Si l'internet n'est pas disponible, marquez pour synchronisation vers Firestore
        if (!isInternetAvailable(appContext)) {
            realEstate.needsSyncToFirestore = true
        }

        repository.insert(realEstate) // insert in Room
    }

    private fun synchronizeRoomToFirestore() = viewModelScope.launch {
        repository.synchronizeRoomToFirestore()
    }

    private fun setupFirestoreDocumentListener() {
        repository.setupFirestoreDocumentListener(viewModelScope)
    }

    private fun synchronizeImagesIfNeeded() = viewModelScope.launch {
        repository.getAllRealEstatesSync().collect { realEstatesList ->
            realEstatesList.forEach { realEstate ->
                var updateRequired = false
                val updatedImages = realEstate.images?.map { image ->
                    // Si l'image a déjà un cloudUri, pas besoin de la re-télécharger
                    if (!image.cloudUri.isNullOrEmpty()) {
                        image
                    } else if (isInternetAvailable(appContext)) { // Remplacez isInternetAvailable() par votre fonction de vérification d'Internet
                        // L'image n'a pas de cloudUri et nous avons accès à Internet, essayons d'uploader
                        try {
                            val cloudUri = repository.uploadImage(Uri.parse(image.localUri))
                            updateRequired = true
                            image.copy(cloudUri = cloudUri)
                        } catch (e: Exception) {
                            Log.e("ViewModel", "Failed to upload image ${image.localUri}", e)
                            image // Gardez l'image telle quelle si l'upload échoue
                        }
                    } else {
                        image // Gardez l'image telle quelle si pas d'accès à Internet
                    }
                }

                // Si au moins une image a été mise à jour avec un cloudUri, mettez à jour le RealEstate dans Room
                if (updateRequired) {
                    val updatedRealEstate = realEstate.copy(images = updatedImages)
                    repository.insert(updatedRealEstate)
                }
            }
        }
    }

}
