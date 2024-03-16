package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response

object UserRepository {
    private val rootNode = FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app") // Replace with your actual Database URL
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


}

