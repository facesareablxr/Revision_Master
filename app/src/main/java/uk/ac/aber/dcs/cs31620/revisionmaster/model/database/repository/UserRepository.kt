package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User

object UserRepository {
    private val rootNode = FirebaseDatabase.getInstance()
    private val reference = rootNode.getReference("users")

    suspend fun addUserToDB(
        user: User,
        position: Int,
        listener: ValueEventListener? = null
    ) = try {
        listener?.let {
            reference.removeEventListener(it)
            reference.addValueEventListener(it)
        }
        reference.child(position.toString()).setValue(user).await()
        Result.success(user)

    } catch (e: Exception){
        Result.failure(e)
    }
}
