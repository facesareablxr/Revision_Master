package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Follows
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
/**
 * This object handles all user-related interactions with a Firebase Realtime Database.
 */
object UserRepository {
    /* Firebase initialization */
    private val rootNode =
        FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")
    private val usersReference = rootNode.getReference("users")

    /** ------------------------------------ USER FUNCTIONS ------------------------------------ **/

    /**
     * Adds a user to the database.
     */
    suspend fun addUser(user: User, uid: String): User? {
        try {
            // Checks for existing username and returns null if a duplicate is found
            val existingUser = usersReference.orderByChild("username").equalTo(user.username).get().await().children.firstOrNull()?.getValue(User::class.java)
            if (existingUser != null) {
                throw Exception("Username already exists")
            }
            usersReference.child(uid).setValue(user).await()
            return user
        } catch (e: Exception) {
            Log.e(TAG, "Error adding user: ${e.message}")
            return null
        }
    }

    /**
     * Gets a user from the database using their ID.
     */
    suspend fun getUserById(userId: String): User? {
        return try {
            // Gets the data snapshot using the ID
            val snapshot = usersReference.child(userId).get().await()
            // Checks if the snapshot exists, returns null if not
            if (snapshot.exists()) {
                snapshot.getValue(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by ID: ${e.message}")
            null
        }
    }

    /**
     * Updates the user.
     * Uses the UID of the currently authenticated user to update their data.
     */
    suspend fun updateUser(user: User, imageUri: String) {
        try {
            val currentUser =
                FirebaseAuth.getInstance().currentUser ?: throw Exception("User not signed in")
            val userId = currentUser.uid
            val userRef = usersReference.child(userId)
            userRef.setValue(user).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: ${e.message}")
        }
    }

    /**
     * Deletes a user and all details associated with them from the database.
     */
    suspend fun deleteUser(userId: String) {
        usersReference.child(userId).removeValue().await()
        FlashcardRepository.deleteDecksByUserId(userId)
    }

    /** ----------------------------------- STREAK FUNCTIONS ---------------------------------- **/

    /**
     * Updates the user's streak and data in the database.
     */
    suspend fun updateUserStreakAndData(userId: String, updatedUserData: User) {
        try {
            usersReference.child(userId).setValue(updatedUserData).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user streak and data: ${e.message}")
        }
    }

    /** ---------------------------------- SCHEDULE FUNCTIONS ---------------------------------- **/

    /**
     * Adds a schedule for a user to the database.
     */
    suspend fun addSchedule(userId: String, schedule: Schedule) {
        try {
            usersReference.child(userId).child("schedules").child(schedule.id).setValue(schedule)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error adding schedule: ${e.message}")
        }
    }

    /**
     * Updates a schedule for a user in the database.
     */
    suspend fun updateSchedule(
        userId: String,
        scheduleId: String,
        schedule: Schedule
    ) {
        try {
            val userScheduleReference =
                usersReference.child(userId).child("schedules").child(scheduleId)
            userScheduleReference.setValue(schedule).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating schedule: ${e.message}")
        }
    }

    /**
     * Retrieves all schedules for a user from the database.
     */
    suspend fun getSchedules(userId: String): List<Schedule> {
        return try {
            val snapshot = usersReference.child(userId).child("schedules").get().await()
            val schedules = mutableListOf<Schedule>()
            snapshot.children.forEach { data ->
                val session = data.getValue(Schedule::class.java)
                session?.let { schedules.add(it) }
            }
            schedules
        } catch (e: Exception) {
            Log.e(TAG, "Error getting schedules: ${e.message}")
            emptyList()
        }
    }

    /**
     * Retrieves the details of a specific schedule for a user from the database.
     */
    suspend fun getScheduleDetails(userId: String, scheduleId: String): Schedule? {
        return try {
            val snapshot =
                usersReference.child(userId).child("schedules").child(scheduleId).get().await()
            snapshot.getValue(Schedule::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting schedule details: ${e.message}")
            null
        }
    }

    /**
     * Deletes a schedule for a user from the database.
     */
    suspend fun deleteSchedule(userId: String, scheduleId: String) {
        try {
            val userScheduleReference =
                usersReference.child(userId).child("schedules").child(scheduleId)
            userScheduleReference.removeValue().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting schedule: ${e.message}")
        }
    }

    /**
     * Deletes all schedules containing a specific day for a user from the database.
     */
    suspend fun deleteSchedulesContainingDay(userId: String, day: String) {
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting schedules containing day: ${e.message}")
        }
    }


    /** ---------------------------------- EXPLORE FUNCTIONS ---------------------------------- **/

    /**
     * Retrieves the user and their followers/following lists from the database.
     */
    suspend fun getUserFollows(userId: String): Follows? {
        return try {
            val userSnapshot = usersReference.child(userId).get().await()
            if (userSnapshot.exists()) {
                val user = userSnapshot.getValue(User::class.java)
                val followersSnapshot = usersReference.child(userId).child("followers").get().await()
                val followingSnapshot = usersReference.child(userId).child("following").get().await()

                val followers = followersSnapshot.children.mapNotNull {
                    val followerId = it.key
                    Log.d(TAG, "Follower: $followerId")
                    followerId
                }
                val following = followingSnapshot.children.mapNotNull {
                    val followingId = it.key
                    Log.d(TAG, "Following: $followingId")
                    followingId
                }

                Follows(user, followers, following)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user follows: ${e.message}")
            null
        }
    }


    /**
     * Retrieves all users from the database.
     */
    suspend fun getAllUsers(): Flow<List<User>> = flow {
        try {
            // Log a message indicating that we're retrieving users
            Log.d(TAG, "Fetching all users from the database...")
            val snapshot = usersReference.get().await()
            val users = snapshot.children.mapNotNull {
                it.getValue(User::class.java)
            }
            // Log the number of users fetched
            Log.d(TAG, "Fetched ${users.size} users from the database")
            emit(users)
        } catch (e: Exception) {
            // Log the error if an exception occurs
            Log.e(TAG, "Error fetching users from the database", e)
            emit(emptyList())
        }
    }

    /**
     * Follows a user by adding them to the current user's following list and vice versa.
     */
    suspend fun followUser(userId: String, usernameToFollow: String) {
        try {
            val userIdToFollow = getUserIdByUsername(usernameToFollow)
            val user = getUserById(userId)
            // Add the user to the following list
            usersReference.child(userId).child("following").child(usernameToFollow).setValue(true)
            // Add the current user to the followers list of the followed user
            usersReference.child(userIdToFollow).child("followers").child(user!!.username)
                .setValue(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error following user: ${e.message}")
        }
    }

    /**
     * Unfollows a user by removing them from the current user's following list and vice versa.
     */
    suspend fun unfollowUser(userId: String, usernameToUnfollow: String) {
        try {
            val userIdToUnfollow = getUserIdByUsername(usernameToUnfollow)
            val user = getUserById(userId)
            // Remove the user from the following list
            usersReference.child(userId).child("following").child(usernameToUnfollow).removeValue()
            // Remove the current user from the followers list of the unfollowed user
            usersReference.child(userIdToUnfollow).child("followers").child(user!!.username)
                .removeValue()
        } catch (e: Exception) {
            Log.e(TAG, "Error unfollowing user: ${e.message}")
        }
    }

    /**
     * Retrieves the user ID by username from the database.
     */
    suspend fun getUserIdByUsername(username: String): String {
        return try {
            val snapshot = usersReference.orderByChild("username").equalTo(username).get().await()
            snapshot.children.firstOrNull()?.key
                ?: throw Exception("User with username $username not found")
        } catch (e: Exception) {
            throw Exception("Error getting user ID by username: ${e.message}")
        }
    }

    /**
     * Retrieves the user ID and user by username from the database.
     */
    suspend fun getUserIdAndUserByUsername(username: String): Pair<String?, User?> {
        return try {
            val snapshot = usersReference.orderByChild("username").equalTo(username).get().await()
            val userSnapshot = snapshot.children.firstOrNull()
            val userId = userSnapshot?.key
            val user = userSnapshot?.getValue(User::class.java)
            Pair(userId, user)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by username: ${e.message}")
            Pair(null, null)
        }
    }

}
