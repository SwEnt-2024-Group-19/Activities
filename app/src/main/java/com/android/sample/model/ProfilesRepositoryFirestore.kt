package com.android.sample.model
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProfilesRepositoryFirestore (private val db: FirebaseFirestore): ProfilesRepository {

    override fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("profiles").document(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null ) {
                    val user = documentToUser(document)  // Convert document to User
                    onSuccess(user)
                } else {
                    onSuccess(null)  // No document found, return null
                }
            } else {
                task.exception?.let { e ->
                    Log.e("UserProfileRepository", "Error getting user document", e)
                    onFailure(e)
                } }
        }
        }


    private fun documentToUser(document: DocumentSnapshot): User? {
        return try {
            val id = document.id
            val name = document.getString("name") ?: return null
            val surname = document.getString("surname") ?: return null
            val photo = document.getString("photo") ?: return null
            Log.e("not an error", "id is $id" )
            Log.e("not an error", "name is $name" )
            Log.e("not an error", "surname is $surname" )
            Log.e("not an error", "photo url is $photo" )

            val interests = (document.get("interests") as? List<*>)?.filterIsInstance<String>() ?: return null
            Log.e("not an error", "interests are $interests" )
            val activities = (document.get("activities") as? List<*>)?.filterIsInstance<String>() ?: return null
          //  Log.e("not an error", "activities are "+ activities.toString())

            //val activities = document.get("activities") as? List<*>
            //val interests = document.get("interests") as? List<*>

            User(
                id = id,
                name = name,
                surname = surname,
                interests = interests,
                activities = activities,
                photo = photo)
        } catch (e: Exception) {
            Log.e("TodosRepositoryFirestore", "Error converting document to ToDo", e)
            null
        }
    }
}
