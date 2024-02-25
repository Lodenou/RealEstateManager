package com.lodenou.realestatemanager.data.repository

import android.net.Uri
import androidx.annotation.WorkerThread
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lodenou.realestatemanager.BuildConfig
import com.lodenou.realestatemanager.GeocodeResult
import com.lodenou.realestatemanager.Utils.toFirestoreTimestamp
import io.reactivex.rxjava3.core.Observable
import com.lodenou.realestatemanager.data.RealEstateDao
import com.lodenou.realestatemanager.data.RealestateApi
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

    fun getRealEstateByIdRoom(id: String): Flow<RealEstate> {
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


    fun saveRealEstateToFirestore(realEstate: RealEstate, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
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
            "marketEntryDate" to realEstate.marketEntryDate.toFirestoreTimestamp(),
            "saleDate" to realEstate.saleDate?.toFirestoreTimestamp(),
            "realEstateAgent" to realEstate.realEstateAgent
        )

        val newDocumentRef = firestore.collection("RealEstate").document() // Crée une référence de document avec un ID unique
        firestoreData["id"] = newDocumentRef.id // Optionnel: Ajouter l'ID au document pour le conserver dans Firestore

        newDocumentRef.set(firestoreData)
            .addOnSuccessListener {
                onSuccess(newDocumentRef.id) // Passez l'ID du document généré à onSuccess
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun getRealEstateByIdFirestore(id: String): Task<DocumentSnapshot> {
        return firestore.collection("RealEstate").document(id).get()
    }

    // Map api

    fun getLatLngFromAddress(address: String): Observable<GeocodeResult> {
        return RealestateApi.retrofitService.getLatLngFromAddress(address, BuildConfig.API_KEY)
    }
}
