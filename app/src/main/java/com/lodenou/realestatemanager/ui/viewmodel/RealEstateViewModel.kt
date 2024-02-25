package com.lodenou.realestatemanager.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class RealEstateViewModel @Inject constructor(private val repository: RealEstateRepository) : ViewModel() {

    var imagesWithDescriptions = mutableStateListOf<ImageWithDescription>()


    val realEstatesFromFirestore = MutableLiveData<List<RealEstate>>()

    init {
        listenToRealEstatesUpdates()
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


    fun saveRealEstateWithImages(realEstate: RealEstate) {
        viewModelScope.launch {
            try {

                val updatedImages = realEstate.images?.map { image ->
                    val imageUrl = repository.uploadImage(Uri.parse(image.imageUrl))
                    image.copy(imageUrl = imageUrl) // UPDATE IMAGE URL
                }

                // Créez une nouvelle instance de RealEstate avec les URLs des images mises à jour
                val updatedRealEstate = realEstate.copy(images = updatedImages)

                // Sauvegardez l'objet RealEstate mis à jour dans Firestore
                repository.saveRealEstateToFirestore(updatedRealEstate, onSuccess = {
                    // succeed
                }, onFailure = {

                })
            } catch (_: Exception) {

            }
        }
    }



    @Suppress("UNCHECKED_CAST")
    private fun listenToRealEstatesUpdates() {
        repository.getRealEstatesCollectionReference().addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("RealEstateViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }

            val realEstateList = ArrayList<RealEstate>()
            snapshots?.documents?.forEach { document ->
                // Manual treatment to avoid convert pb
                val id = document.id
                val type = document.getString("type")
                val price = document.getDouble("price")
                val area = document.getDouble("area")
                val numberOfRooms = document.getLong("numberOfRooms")?.toInt()
                val description = document.getString("description")
                val images = document["images"] as? List<Map<String, String>>
                val address = document.getString("address")
                val pointsOfInterest = document.getString("pointsOfInterest")
                val status = document.getString("status")
                val marketEntryDate = document.getTimestamp("marketEntryDate")?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                val saleDate = document.getTimestamp("saleDate")?.toDate()?.toInstant()?.atZone(
                    ZoneId.systemDefault())?.toLocalDate()
                val realEstateAgent = document.getString("realEstateAgent")

                // Conversion of image list
                val convertedImages = images?.map { map ->
                    ImageWithDescription(
                        imageUrl = map["url"] ?: "",
                        description = map["description"] ?: ""
                    )
                } ?: listOf()

                val realEstate = RealEstate(
                    id = id,
                    type = type ?: "",
                    price = price ?: 0.0,
                    area = area ?: 0.0,
                    numberOfRooms = numberOfRooms ?: 0,
                    description = description ?: "",
                    images = convertedImages,
                    address = address ?: "",
                    pointsOfInterest = pointsOfInterest ?: "",
                    status = status ?: "",
                    marketEntryDate = marketEntryDate ?: LocalDate.now(),
                    saleDate = saleDate,
                    realEstateAgent = realEstateAgent ?: ""
                )
                realEstateList.add(realEstate)
            }
            realEstatesFromFirestore.postValue(realEstateList)
        }
    }
}
