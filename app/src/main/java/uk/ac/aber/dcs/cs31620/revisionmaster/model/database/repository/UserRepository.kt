package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.Response
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User

object UserRepository {
    // Get a reference to the Firebase database root node
    private val rootNode = FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")

    // Get a reference to the "users" branch within the database
    private val usersReference = rootNode.getReference("users")

    /**
     * Attempts to add a new user to the Firebase database.
     *
     * @param user The user object to be added.
     * @return A Response object indicating success or failure, along with relevant data:
     *         - Success(user): If the user is added successfully, returns the added user object.
     *         - Failure(Exception): If an error occurs, returns the exception.
     */
    suspend fun addUser(user: User): Response<User> {
        val username = user.username
        val userRef = usersReference.child(username) // Get a reference to the specific user's node

        // Check if a user with the same username already exists:
        val existingUser = userRef.get().await().getValue(User::class.java)
        return if (existingUser != null) {
            // If a user with the same username exists, fail the addition and return an error message
            Response.Failure(Exception("Username already exists"))
        } else {
            // If no existing user is found, attempt to add the new user:
            try {
                userRef.setValue(user).await() // Set the user's data in the database
                Response.Success(user) // Return a success response with the added user
            } catch (e: Exception) {
                // If an error occurs during addition, return a failure response with the exception
                Response.Failure(e)
            }
        }
    }
}