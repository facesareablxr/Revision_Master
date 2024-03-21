package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Module
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Subject
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.UserRevisionData

object FlashcardRepository {

    private val rootNode =
        FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")
    private val dataReference = rootNode.reference.child("revisionData")

    // Get all user revision data
    suspend fun getUserRevisionData(callback: (UserRevisionData) -> Unit) {
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userRevisionData = snapshot.getValue(UserRevisionData::class.java)
                if (userRevisionData != null) {
                    callback(userRevisionData)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors
                Log.e("Flashcard Repo", "Error getting user revision data: $error")
            }
        })
    }

    // Add user revision data
    suspend fun addUserRevisionData(
        userRevisionData: UserRevisionData,
        callback: (Boolean) -> Unit
    ) {
        dataReference.setValue(userRevisionData)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Add flashcard to a class
    suspend fun addClassFlashcard(
        classId: String,
        flashcard: Flashcard,
        callback: (Boolean) -> Unit
    ) {
        val flashcardsRef = dataReference.child(classId).child("flashcards")
        val key = flashcardsRef.push().key ?: return // Exit if key generation fails

        flashcardsRef.child(key).setValue(flashcard)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Add flashcard to a module
    suspend fun addModuleFlashcard(
        moduleId: String,
        flashcard: Flashcard,
        callback: (Boolean) -> Unit
    ) {
        val flashcardsRef = dataReference.child(moduleId).child("flashcards")
        val key = flashcardsRef.push().key ?: return // Exit if key generation fails

        flashcardsRef.child(key).setValue(flashcard)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Add flashcard to a subject
    suspend fun addSubjectFlashcard(
        subjectId: String,
        flashcard: Flashcard,
        callback: (Boolean) -> Unit
    ) {
        val flashcardsRef = dataReference.child(subjectId).child("flashcards")
        val key = flashcardsRef.push().key ?: return // Exit if key generation fails

        flashcardsRef.child(key).setValue(flashcard)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Get module information
    suspend fun getModuleInformation(moduleId: String, callback: (Module?) -> Unit) {
        dataReference.child(moduleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val module = snapshot.getValue(Module::class.java)
                callback(module)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors
                Log.e("Flashcard Repo", "Error getting module information: $error")
                callback(null)
            }
        })
    }

    // Get subject information
    suspend fun getSubjectInformation(subjectId: String, callback: (Subject?) -> Unit) {
        dataReference.child(subjectId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subject = snapshot.getValue(Subject::class.java)
                callback(subject)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database errors
                Log.e("Flashcard Repo", "Error getting subject information: $error")
                callback(null)
            }
        })
    }

    // Get flashcard information
    suspend fun getFlashcardInformation(
        parentId: String,
        flashcardId: String,
        callback: (Flashcard?) -> Unit
    ) {
        dataReference.child(parentId).child("flashcards").child(flashcardId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val flashcard = snapshot.getValue(Flashcard::class.java)
                    callback(flashcard)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Flashcard Repo", "Error getting flashcard information: $error")
                    callback(null)
                }
            })
    }

    // Update module information
    suspend fun updateModuleInformation(
        moduleId: String,
        updatedModule: Module,
        callback: (Boolean) -> Unit
    ) {
        dataReference.child(moduleId).setValue(updatedModule)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Update subject information
    suspend fun updateSubjectInformation(
        subjectId: String,
        updatedSubject: Subject,
        callback: (Boolean) -> Unit
    ) {
        dataReference.child(subjectId).setValue(updatedSubject)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Update flashcard information
    suspend fun updateFlashcardInformation(
        parentId: String,
        flashcardId: String,
        updatedFlashcard: Flashcard,
        callback: (Boolean) -> Unit
    ) {
        dataReference.child(parentId).child("flashcards").child(flashcardId)
            .setValue(updatedFlashcard)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Delete module
    suspend fun deleteModule(moduleId: String, callback: (Boolean) -> Unit) {
        dataReference.child(moduleId).removeValue()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Delete subject
    suspend fun deleteSubject(subjectId: String, callback: (Boolean) -> Unit) {
        dataReference.child(subjectId).removeValue()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Delete class
    suspend fun deleteClass(classId: String, callback: (Boolean) -> Unit) {
        dataReference.child(classId).removeValue()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Delete flashcard
    suspend fun deleteFlashcard(
        parentId: String,
        flashcardId: String,
        callback: (Boolean) -> Unit
    ) {
        dataReference.child(parentId).child("flashcards").child(flashcardId).removeValue()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Get flashcards by module
    suspend fun getFlashcardsByModule(moduleId: String, callback: (List<Flashcard>) -> Unit) {
        dataReference.child(moduleId).child("flashcards")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val flashcards = mutableListOf<Flashcard>()
                    snapshot.children.forEach { data ->
                        val flashcard = data.getValue(Flashcard::class.java)
                        flashcard?.let { flashcards.add(it) }
                    }
                    callback(flashcards)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Flashcard Repo", "Error getting flashcards by module: $error")
                    callback(emptyList())
                }
            })
    }

    // Get flashcards by class
    suspend fun getFlashcardsByClass(classId: String, callback: (List<Flashcard>) -> Unit) {
        dataReference.child(classId).child("flashcards")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val flashcards = mutableListOf<Flashcard>()
                    snapshot.children.forEach { data ->
                        val flashcard = data.getValue(Flashcard::class.java)
                        flashcard?.let { flashcards.add(it) }
                    }
                    callback(flashcards)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Flashcard Repo", "Error getting flashcards by class: $error")
                    callback(emptyList())
                }
            })
    }

    // Get flashcards by subject
    suspend fun getFlashcardsBySubject(subjectId: String, callback: (List<Flashcard>) -> Unit) {
        dataReference.child(subjectId).child("flashcards")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val flashcards = mutableListOf<Flashcard>()
                    snapshot.children.forEach { data ->
                        val flashcard = data.getValue(Flashcard::class.java)
                        flashcard?.let { flashcards.add(it) }
                    }
                    callback(flashcards)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Flashcard Repo", "Error getting flashcards by subject: $error")
                    callback(emptyList())
                }
            })
    }

    // Get All Flashcards for a User
    suspend fun getAllUserFlashcards(username: String): List<Flashcard> {
        val flashcards = mutableListOf<Flashcard>()

        return try {
            val dataSnapshot = dataReference.get().await()
            val userRevisionData = dataSnapshot.getValue(UserRevisionData::class.java)
            userRevisionData?.flashcards?.let { flashcards.addAll(it) }
            flashcards
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Get User Modules
    suspend fun getUserModules(userId: String, callback: (List<Module>) -> Unit) {
        dataReference.orderByChild("members/$userId").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userModules = mutableListOf<Module>()
                    snapshot.children.forEach { data ->
                        val modulesMap = data.child("modules").children
                        modulesMap.forEach { moduleData ->
                            val module = moduleData.getValue(Module::class.java)
                            module?.let { userModules.add(it) }
                        }
                    }
                    callback(userModules)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Flashcard Repo", "Error getting user modules: $error")
                    callback(emptyList())
                }
            })
    }

    // Get User Subjects
    suspend fun getUserSubjects(userId: String, callback: (List<Subject>) -> Unit) {
        dataReference.orderByChild("members/$userId").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userSubjects = mutableListOf<Subject>()
                    snapshot.children.forEach { data ->
                        val subjectsMap = data.child("subjects").children
                        subjectsMap.forEach { subjectData ->
                            val subject = subjectData.getValue(Subject::class.java)
                            subject?.let { userSubjects.add(it) }
                        }
                    }
                    callback(userSubjects)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Flashcard Repo", "Error getting user subjects: $error")
                    callback(emptyList())
                }
            })
    }

    suspend fun getModulesForSubject(subjectId: String, callback: (List<Module>) -> Unit) {
        dataReference.child(subjectId).child("modules")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val modules = mutableListOf<Module>()
                    snapshot.children.forEach { data ->
                        val module = data.getValue(Module::class.java)
                        module?.let { modules.add(it) }
                    }
                    callback(modules)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors
                    Log.e("Flashcard Repo", "Error getting modules for subject: $error")
                    callback(emptyList())
                }
            })
    }


}