package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response
import java.util.Calendar
import java.util.Date

object UserRepository {
    private val rootNode = FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")
    private val usersReference = rootNode.getReference("users")
    private val auth = FirebaseAuth.getInstance()

    suspend fun addUser(user: User): Response<User> {
        val username = user.username
        val userRef = usersReference.child(username)
        // Check for existing user before setting value
        val existingUser = userRef.get().await().getValue(User::class.java)
        return if (existingUser != null) {
            Response.Failure(Exception("Username already exists"))
        } else {
            try {
                userRef.setValue(user).await()
                Response.Success(user)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        usersReference.orderByChild("email")
            .equalTo(email)

        val snapshot = usersReference.get().await()
        return snapshot.children.firstOrNull()?.getValue(User::class.java)
    }

    suspend fun updateUser(user: User) {
        val username = user.username
        val userRef = usersReference.child(username)
        try {
            userRef.setValue(user).await()
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun getFollowingList(username: String): List<String> {
        val followingList = mutableListOf<String>()
        return try {
            val querySnapshot = usersReference.orderByChild("username").equalTo(username).get().await()

            if (querySnapshot.exists()) {
                val userSnapshot = querySnapshot.children.first()
                val following = userSnapshot.child("following").getValue(object : GenericTypeIndicator<List<String>>() {})
                followingList.addAll(following.orEmpty())
            }
            followingList
        } catch (e: Exception) {
            // Error occurred during the operation
            emptyList()
        }
    }

    suspend fun getFollowers(username: String): List<String> {
        val followingList = mutableListOf<String>()
        return try {
            val querySnapshot = usersReference.orderByChild("username").equalTo(username).get().await()

            if (querySnapshot.exists()) {
                val userSnapshot = querySnapshot.children.first()
                val following = userSnapshot.child("following").getValue(object : GenericTypeIndicator<List<String>>() {})
                followingList.addAll(following.orEmpty())
            }
            followingList
        } catch (e: Exception) {
            // Error occurred during the operation
            emptyList()
        }
    }

    suspend fun updateUserStreak(userId: String) {
        val userRef = usersReference.child(userId)
        try {
            val today = Date() // Get today's date

            val updatedUser = userRef.get().await().getValue(User::class.java)?.apply {
                val lastLogin = lastLoginDate // Assuming there's a lastLoginDate property in User

                // Check if user logged in yesterday to maintain the streak
                val loggedInYesterday = isYesterday(lastLogin, today)
                if (loggedInYesterday) {
                    currentStreak += 1 // Increase streak if logged in yesterday
                } else {
                    currentStreak = 0 // Reset streak if not logged in yesterday
                }
                lastLoginDate = today // Update last login date
            }
            updatedUser?.let { userRef.setValue(it).await() }
        } catch (e: Exception) {
            // Handle error updating streak (e.g., log the error)
        }
    }

    // Helper function to check if date is yesterday
    private fun isYesterday(date: Date?, today: Date): Boolean {
        if (date == null) {
            return false
        }
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, -1) // Subtract one day
        return calendar.time.date == date.date &&
                calendar.time.month == date.month &&
                calendar.time.year == date.year
    }
}

