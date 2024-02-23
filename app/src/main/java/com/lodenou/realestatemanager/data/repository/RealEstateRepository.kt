package com.lodenou.realestatemanager.data.repository

import android.net.Uri
import androidx.annotation.WorkerThread
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lodenou.realestatemanager.Utils.toFirestoreTimestamp
import com.lodenou.realestatemanager.data.RealEstateDao
import com.lodenou.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore) {


    // ROOM
    // Flow of all real estate properties. Unsorted
    val allRealEstates: Flow<List<RealEstate>> = realEstateDao.getAllRealEstates()


    @WorkerThread
    suspend fun insert(realEstate: RealEstate) {
        realEstateDao.insert(realEstate)
    }

    @WorkerThread
    suspend fun update(realEstate: RealEstate) {
        realEstateDao.update(realEstate)
    }

    @WorkerThread
    suspend fun delete(realEstate: RealEstate) {
        realEstateDao.delete(realEstate)
    }

    fun getRealEstateById(id: Int): Flow<RealEstate> {
        return realEstateDao.getRealEstateById(id)
    }


    suspend fun uploadImage(imageUri: Uri): String {
        val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(imageUri).await() // wait end of the upload
        return imageRef.downloadUrl.await().toString() // get & send url
    }


    fun getRealEstatesCollectionReference(): CollectionReference {
        return firestore.collection("RealEstate")
    }


    fun saveRealEstateToFirestore(realEstate: RealEstate, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val firestoreData = hashMapOf(
            "type" to realEstate.type,
            "price" to realEstate.price,
            "area" to realEstate.area,
            "numberOfRooms" to realEstate.numberOfRooms,
            "description" to realEstate.description,
            "images" to realEstate.images?.map { image ->
                hashMapOf(
                    "url" to image.imageUrl,
                    "description" to image.description
                )
            },
            "address" to realEstate.address,
            "pointsOfInterest" to realEstate.pointsOfInterest,
            "status" to realEstate.status,
            "marketEntryDate" to realEstate.marketEntryDate.toFirestoreTimestamp(), // Convert
            "saleDate" to realEstate.saleDate?.toFirestoreTimestamp(), // Convert
            "realEstateAgent" to realEstate.realEstateAgent
        )

        firestore.collection("RealEstate").add(firestoreData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
