package com.android.sample.model.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor() : ViewModel() {

    fun uploadActivityImages(
        activityId: String,
        bitmaps: List<Bitmap>,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val uploadedImageUrls = mutableListOf<String>()
        var uploadCount = 0

        bitmaps.forEach { bitmap ->
            val timestamp = System.currentTimeMillis()
            val activityImageRef = storageRef.child("activities/$activityId/image_$timestamp.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val data = baos.toByteArray()

            activityImageRef.putBytes(data)
                .addOnSuccessListener {
                    activityImageRef.downloadUrl.addOnSuccessListener { uri ->
                        uploadedImageUrls.add(uri.toString())
                        uploadCount++

                        if (uploadCount == bitmaps.size) {
                            val activityDocRef =
                                FirebaseFirestore.getInstance().collection("activities").document(activityId)

                            activityDocRef.update("images", FieldValue.arrayUnion(*uploadedImageUrls.toTypedArray()))
                                .addOnSuccessListener { onSuccess(uploadedImageUrls) }
                                .addOnFailureListener { e -> onFailure(e) }
                        }
                    }.addOnFailureListener { e -> onFailure(e) }
                }.addOnFailureListener { e -> onFailure(e) }
        }
    }

    fun updateActivityImages(
        activityId: String,
        existingImageUrls: List<String>,
        bitmaps: List<Bitmap>,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val newImageUrls = mutableListOf<String>()
        var uploadCount = bitmaps.size

        bitmaps.forEach { bitmap ->
            val timestamp = System.currentTimeMillis()
            val activityImageRef = storageRef.child("activities/$activityId/image_$timestamp.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val data = baos.toByteArray()

            activityImageRef.putBytes(data)
                .addOnSuccessListener {
                    activityImageRef.downloadUrl.addOnSuccessListener { uri ->
                        newImageUrls.add(uri.toString())
                        uploadCount--

                        if (uploadCount == 0) {
                            val finalUrls = (existingImageUrls + newImageUrls).distinct()
                            finalizeUpdate(activityId, finalUrls, onSuccess, onFailure)
                        }
                    }
                }
                .addOnFailureListener { onFailure(it) }
        }
    }

    private fun finalizeUpdate(
        activityId: String,
        imageUrls: List<String>,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val activityDocRef = FirebaseFirestore.getInstance().collection("activities").document(activityId)
        activityDocRef.update("images", imageUrls)
            .addOnSuccessListener { onSuccess(imageUrls) }
            .addOnFailureListener { onFailure(it) }
    }

    fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun fetchProfileImageUrl(
        userId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val profilePicRef = storageRef.child("users/$userId/profile_picture.jpg")

        profilePicRef.downloadUrl
            .addOnSuccessListener { uri -> onSuccess(uri.toString()) }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun fetchActivityImageUrls(
        activityId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("activities").document(activityId).get()
            .addOnSuccessListener { document ->
                val imageUrls = document["images"] as? List<String> ?: emptyList()
                onSuccess(imageUrls)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun fetchActivityImagesAsBitmaps(
        activityId: String,
        onSuccess: (List<Bitmap>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fetchActivityImageUrls(activityId, { urls ->
            fetchBitmapsFromUrls(urls, onSuccess, onFailure)
        }, onFailure)
    }

    private fun fetchBitmapsFromUrls(
        urls: List<String>,
        onSuccess: (List<Bitmap>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val bitmaps = mutableListOf<Bitmap>()
        var successCount = 0

        if (urls.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        urls.forEach { url ->
            val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
            imageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmaps.add(bitmap)
                    successCount++

                    if (successCount == urls.size) {
                        onSuccess(bitmaps)
                    }
                }
                .addOnFailureListener { exception -> onFailure(exception) }
        }
    }
}
