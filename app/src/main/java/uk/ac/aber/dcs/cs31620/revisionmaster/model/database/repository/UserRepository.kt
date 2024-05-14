package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

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
            uploadImage(Uri.parse(user.profilePictureUrl))
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    // Firebase Storage initialization
    private val firebaseStorage = Firebase.storage

    private fun uploadImage(imageUri: Uri) {
        val storageRef = firebaseStorage.reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
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
    suspend fun updateUser(user: User, imageUri: Uri): Response<Unit> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = usersReference.child(userId)
            try {
                uploadImage(imageUri)
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
        val followingUsernames = mutableListOf<String>()
        return try {
            val followingSnapshot = usersReference.child(currentUser.uid).child("following").get().await()
            if (followingSnapshot.exists()) {
                // Iterating through each child node to retrieve the usernames
                followingSnapshot.children.forEach { followingData ->
                    val username = followingData.key
                    username?.let { followingUsernames.add(it) }
                }
                // Log the list of usernames
                Log.d("FollowingList", "Following: $followingUsernames")
            }
            followingUsernames
        } catch (e: Exception) {
            Log.e("FollowingList", "Error getting following list: ${e.message}")
            emptyList()
        }
    }

    /**
     * Gets the user's followers list by their usernames.
     */
    suspend fun getFollowers(): List<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return emptyList()

        return try {
            val followersSnapshot = usersReference.child(currentUser.uid).child("followers").get().await()
            if (followersSnapshot.exists()) {
                // Directly get usernames from the snapshot
                val followersUsernames = followersSnapshot.children
                    .mapNotNull { it.getValue(String::class.java) }
                    .toList()
                Log.d("FollowersList", "Followers: $followersUsernames")
                followersUsernames // Return the list if it exists
            } else {
                emptyList() // Return an empty list if the user has no followers
            }
        } catch (e: Exception) {
            Log.e("FollowersList", "Error getting followers list: ${e.message}")
            emptyList()
        }
    }


    /**
     * Helper function to fetch username by user ID.
     */
    private suspend fun getUsernameById(userId: String): String? {
        return try {
            val userSnapshot = usersReference.child(userId).get().await()
            userSnapshot.child("username").getValue(String::class.java)
        } catch (e: Exception) {
            Log.e("UsernameById", "Error getting username by ID: ${e.message}")
            null
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

    suspend fun addSchedule(userId: String, schedule: Schedule) {
        usersReference.child(userId).child("schedules").child(schedule.id).setValue(schedule)

    }

    suspend fun updateSchedule(
        userId: String,
        scheduleId: String,
        schedule: Schedule
    ): Response<Unit> {
        val userScheduleReference =
            usersReference.child(userId).child("schedules").child(scheduleId)
        return try {
            userScheduleReference.setValue(schedule).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun getSchedules(userId: String): List<Schedule> {
        val snapshot = usersReference.child(userId).child("schedules").get().await()
        val schedules = mutableListOf<Schedule>()
        snapshot.children.forEach { data ->
            val session = data.getValue(Schedule::class.java)
            session?.let { schedules.add(it) }
        }
        return schedules
    }

    /**
     * Returns the details of a specific schedule, including average difficulty.
     */
    suspend fun getScheduleDetails(userId: String, scheduleId: String): Schedule? {
        val snapshot =
            usersReference.child(userId).child("schedules").child(scheduleId).get().await()
        return snapshot.getValue(Schedule::class.java)
    }


    suspend fun deleteSchedule(userId: String, scheduleId: String): Response<Unit> {
        val userScheduleReference =
            usersReference.child(userId).child("schedules").child(scheduleId)
        return try {
            userScheduleReference.removeValue().await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun deleteSchedulesContainingDay(userId: String, day: String): Response<Unit> {
        return try {
            val userSchedulesReference = usersReference.child(userId).child("schedules")
            val userSchedulesSnapshot = userSchedulesReference.get().await()
            val schedulesToDelete = mutableListOf<String>()

            userSchedulesSnapshot.children.forEach { scheduleSnapshot ->
                val schedule = scheduleSnapshot.getValue(Schedule::class.java)
                if (schedule != null && schedule.dayOfWeek.contains(day)) {
                    schedulesToDelete.add(schedule.id)
                }
            }

            schedulesToDelete.forEach { scheduleId ->
                userSchedulesReference.child(scheduleId).removeValue().await()
            }

            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    /**
     * Deletes a deck and its associated flashcards from the database.
     */
    suspend fun deleteUser(userId: String) {
        usersReference.child(userId).removeValue().await()
        FlashcardRepository.deleteDecksByUserId(userId)
    }

    // Function to follow a user
    suspend fun followUser(userId: String, usernameToFollow: String) {
        val userIdToFollow = getUserIdByUsername(usernameToFollow)
        val user = getUserById(userId)
        // Add the user to the following list
        usersReference.child(userId).child("following").child(usernameToFollow).setValue(true)
        // Add the current user to the followers list of the followed user
        usersReference.child(userIdToFollow).child("followers").child(user!!.username).setValue(true)
    }

    // Function to unfollow a user
    suspend fun unfollowUser(userId: String, usernameToUnfollow: String) {
        val userIdToUnfollow = getUserIdByUsername(usernameToUnfollow)
        val user = getUserById(userId)
        // Remove the user from the following list
        usersReference.child(userId).child("following").child(usernameToUnfollow).removeValue()
        // Remove the current user from the followers list of the unfollowed user
        usersReference.child(userIdToUnfollow).child("followers").child(user!!.username).removeValue()
    }

    // Function to get the user ID by username
    private suspend fun getUserIdByUsername(username: String): String {
        val snapshot = usersReference.orderByChild("username").equalTo(username).get().await()
        return snapshot.children.firstOrNull()?.key
            ?: throw Exception("User with username $username not found")
    }
}
