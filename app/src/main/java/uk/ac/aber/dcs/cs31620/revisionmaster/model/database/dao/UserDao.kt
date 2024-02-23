package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.dao

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User

object UserDao {
    private val database = Firebase.database.reference.child("users")

    fun addUser(user: User) {
        database.child(user.userId).setValue(user)
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}