package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * This object handles all user-related interactions with a Firebase Realtime Database.
 */
object UserRepository {
    /* Firebase initialization */
    private val rootNode =
        FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")
    private val usersReference = rootNode.getReference("users")

    /**
     * Adds a user to the database.
     */
    suspend fun addUser(user: User): Response<User> {
        // Gets the current signed-in user
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return Response.Failure(
            Exception("User not signed in")
        )
        // Checks for existing username and returns an error if a duplicate is found
        val existingUser = usersReference.orderByChild("username").equalTo(user.username).get()
            .await().children.firstOrNull()?.getValue(
                User::class.java
            )
        if (existingUser != null) {
            return Response.Failure(Exception("Username already exists"))
        }
        // Stores the user using their unique ID in the "users" node
        return try {
            usersReference.child(currentUser.uid).setValue(user).await()
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    /**
     * Gets a user from the database using their ID.
     */
    suspend fun getUserById(userId: String): User? {
        // Gets the data snapshot using the ID
        return try {
            val snapshot = usersReference.child(userId).get().await()
            // Checks if the snapshot exists, returns null if not
            if (snapshot.exists()) {
                snapshot.getValue(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Updates the user.
     * Uses the UID of the currently authenticated user to update their data.
     */
    suspend fun updateUser(user: User): Response<Unit> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = usersReference.child(userId)
            try {
                userRef.setValue(user).await()
                Response.Success(Unit)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        } else {
            Response.Failure(Exception("User not signed in"))
        }
    }

    /**
     * Gets the current user's following list.
     */
    suspend fun getFollowingList(): List<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return emptyList()

        val followingList = mutableListOf<String>()
        return try {
            val userSnapshot = usersReference.child(currentUser.uid).get().await()
            if (userSnapshot.exists()) {
                val following = userSnapshot.child("following")
                    .getValue(object : GenericTypeIndicator<List<String>>() {})
                followingList.addAll(following.orEmpty())
            }
            followingList
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Gets the user's followers list by their user ID.
     */
    suspend fun getFollowers(): List<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return emptyList()
        val followersList = mutableListOf<String>()
        return try {
            val userSnapshot = usersReference.child(currentUser.uid).get().await()
            if (userSnapshot.exists()) {
                val following = userSnapshot.child("following")
                    .getValue(object : GenericTypeIndicator<List<String>>() {})
                followersList.addAll(following.orEmpty())
            }
            followersList
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Update the user but only their streak.
     */
    suspend fun updateUserStreakAndData(userId: String, updatedUserData: User): Response<Unit> {
        return try {
            usersReference.child(userId).setValue(updatedUserData).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    /**
     * Gets the user's profile image, using their profile URL. This currently does not work.
     */
    suspend fun downloadUserProfileImage(user: User): Bitmap? {
        val imageUrl = user.profilePictureUrl
        return try {
            val url = URL(imageUrl)
            val connection = withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpURLConnection
            connection.doInput = true
            withContext(Dispatchers.IO) {
                connection.connect()
            }
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets all users from the database.
     */
    suspend fun getAllUsers(): Flow<List<User>> = flow {
        try {
            val snapshot = usersReference.get().await()
            val users = snapshot.children.mapNotNull {
                it.getValue(User::class.java)
            }
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

}
