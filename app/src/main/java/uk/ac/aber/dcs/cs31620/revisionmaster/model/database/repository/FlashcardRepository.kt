package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.TestResult
import uk.ac.aber.dcs.cs31620.revisionmaster.model.util.Response
import java.util.UUID

/**
 * This object handles all flashcard and deck-related interactions with a Firebase Realtime Database.
 * It provides functions to add, retrieve, update, and delete decks and flashcards.
 */
object FlashcardRepository {
    // Firebase Realtime Database initialization
    private val rootNode =
        FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")
    private val decksRef =
        rootNode.reference.child("decks") // Reference to the "decks" node in the database

    /**
     * Adds a deck to the database.
     */
    suspend fun addDeck(deck: Deck) {
        decksRef.child(deck.id).setValue(deck) // Set the value of the deck under its unique ID
            .addOnSuccessListener {
                Log.d(TAG, "Deck added successfully: ${deck.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding deck: ${deck.id}", e)
            }
    }


    /**b
     * Gets the decks owned by a user using their user ID.
     */
    fun getUserDecks(userId: String): Flow<List<Deck>> = flow {
        try {
            // Retrieve decks where the ownerId matches the provided userId
            val snapshot = decksRef.orderByChild("ownerId").equalTo(userId).get().await()

            // Map the snapshot to a list of Deck objects
            val decks = snapshot.children.mapNotNull {
                it.getValue(Deck::class.java)
            }
            emit(decks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    /**
     * Returns the details of a specific deck, including average difficulty.
     */
    suspend fun getDeckDetails(deckId: String): Deck? {
        val snapshot = decksRef.child(deckId).get().await()
        val deck = snapshot.getValue(Deck::class.java)

        // If theres have a deck, calculate the difficulty
        deck?.let {
            val flashcards = getFlashcardsByDeckId(deckId)
            it.averageDifficulty = calculateDeckDifficulty(flashcards)
        }

        return deck
    }

    /**
     * Retrieves all flashcards belonging to a specific deck.
     */
    suspend fun getFlashcardsByDeckId(deckId: String): List<Flashcard> {
        val deckReference = decksRef.child(deckId)
        val flashcardsRef = deckReference.child("flashcards")

        val snapshot = flashcardsRef.get().await()
        val flashcards = mutableListOf<Flashcard>()
        snapshot.children.forEach { data ->
            val flashcard = data.getValue(Flashcard::class.java)
            flashcard?.let { flashcards.add(it) }
        }
        return flashcards
    }

    /**
     * Calculates the average difficulty level of a deck based on its flashcards.
     */
    private fun calculateDeckDifficulty(cards: List<Flashcard>): Difficulty {
        if (cards.isEmpty()) {
            Log.d("FlashcardViewModel", "No flashcards found.")
        }

        val averageWeight = cards.map { it.difficulty.weight }
            .average() // Calculate the average weight of flashcards
        Log.d("FlashcardViewModel", "Average weight of flashcards: $averageWeight")

        val foundDifficulty = Difficulty.values().find {
            averageWeight - 0.5 <= it.weight && it.weight <= averageWeight + 0.5
        }
        return foundDifficulty ?: run {
            Log.e("FlashcardViewModel", "Error calculating difficulty. Defaulting to MEDIUM.")
            Difficulty.MEDIUM // Default difficulty if calculation fails
        }
    }

    /**
     * Adds a flashcard to a specific deck.
     */
    fun addFlashcard(deckId: String, flashcard: Flashcard) {
        decksRef.child(deckId).child("flashcards").child(flashcard.id).setValue(flashcard)
    }

    suspend fun updateDeckDifficulty(deckId: String) {
        val updatedFlashcards =
            getFlashcardsByDeckId(deckId) // Retrieve updated flashcards for the deck
        val updatedDeck = decksRef.child(deckId).get().await().getValue(Deck::class.java)
        updatedDeck?.let { deck ->
            val newDifficulty = calculateDeckDifficulty(updatedFlashcards)
            deck.averageDifficulty = newDifficulty
            // Update the difficulty under the correct node
            decksRef.child(deck.id).child("averageDifficulty").setValue(newDifficulty.toString())
        }
    }


    /**
     * Deletes a deck and its associated flashcards from the database.
     */
    suspend fun deleteDeck(deckId: String) {
        decksRef.child(deckId).removeValue().await() // Remove the deck node from the database
        decksRef.child(deckId).child("flashcards").removeValue()
            .await() // Remove the flashcards node under the deck
    }

    /**
     * Deletes all decks owned by a user.
     */
    suspend fun deleteDecksByUserId(userId: String) {
        try {
            // Retrieve decks owned by the user
            val snapshot = decksRef.orderByChild("ownerId").equalTo(userId).get().await()

            // Iterate over each deck and delete it
            snapshot.children.forEach { deckSnapshot ->
                val deckId = deckSnapshot.key
                deckId?.let { deleteDeck(it) }
            }
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e("FlashcardRepository", "Error deleting decks: $e")
        }
    }

    /**
     * Updates a deck's details.This was done like this as it would delete the flashcards if it
     * was done another way.
     */
    fun updateDeck(deckId: String,
                   name: String,
                   subject: String,
                   isPublic: Boolean,
                   description: String) {
        decksRef.child(deckId).child("name").setValue(name)
        decksRef.child(deckId).child("subject").setValue(subject)
        decksRef.child(deckId).child("public").setValue(isPublic)
        decksRef.child(deckId).child("description").setValue(description)
    }

    /**
     * Gets a single flashcard by its ID from the specified deck.
     */
    suspend fun getFlashcardById(deckId: String, flashcardId: String): Flashcard? {
        val deckReference = decksRef.child(deckId)
        val flashcardsRef = deckReference.child("flashcards")
        val flashcardRef = flashcardsRef.child(flashcardId)

        val snapshot = flashcardRef.get().await()
        return snapshot.getValue(Flashcard::class.java)
    }

    /**
     * Updates a flashcard in a specific deck.
     */
    fun updateFlashcard(flashcard: Flashcard, deckId: String) {
        val deckReference = decksRef.child(deckId)
        val flashcardsRef = deckReference.child("flashcards")
        val flashcardRef = flashcardsRef.child(flashcard.id)
        flashcardRef.setValue(flashcard)
        if (flashcard.imageUri != null) {
            uploadImage(Uri.parse(flashcard.imageUri))
        }
    }

    /**
     * Deletes a flashcard from a specific deck.
     */
    suspend fun deleteFlashcard(flashcardId: String, deckId: String) {
        decksRef.child(deckId).child("flashcards").child(flashcardId).removeValue()
            .await() // Remove the flashcard from the database
    }

    /**
     * Gets all public decks from the database.
     */
    suspend fun getPublicDecks(): Flow<List<Deck>> = flow {
        try {
            // Retrieve all decks
            val snapshot = decksRef.get().await()

            // Map the snapshot to a list of Deck objects
            val decks = snapshot.children.mapNotNull {
                it.getValue(Deck::class.java)
            }

            // Filter decks to get only public decks
            val publicDecks = decks.filter { it.isPublic }

            emit(publicDecks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }


    // Firebase Storage initialization
    private val firebaseStorage = Firebase.storage

    fun uploadImage(imageUri: Uri) {
        val storageRef = firebaseStorage.reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
    }

    suspend fun addTestResultAndUpdateMastery(
        deckId: String,
        testResult: TestResult
    ): Response<Unit> {
        val testId = testResult.testId
        return try {
            decksRef.child(deckId).child("testResults").child(testId).setValue(testResult).await()
            updateDeckMastery(deckId)
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    private suspend fun updateDeckMastery(deckId: String) {
        try {
            // Retrieve test results for the specified deck
            val testResultsSnapshot = decksRef.child(deckId).child("testResults").get().await()

            var totalCorrect = 0
            var totalQuestions = 0

            // Calculate total correct and total questions
            testResultsSnapshot.children.forEach { testResultSnapshot ->
                val testResult = testResultSnapshot.getValue(TestResult::class.java)
                testResult?.let {
                    totalCorrect += it.correct
                    totalQuestions += it.correct + it.incorrect
                }
            }

            // Ensure there are test results available
            if (totalQuestions == 0) {
                Log.d("FlashcardRepository", "No test results available for deck $deckId.")
                return // Exit without updating mastery
            }

            // Calculate mastery as a percentage
            val masteryPercentage = (totalCorrect.toFloat() / totalQuestions) * 100
            // Calculate mastery with eventual progression to 100%
            val mastery = when {
                masteryPercentage >= 90f -> 100f  // Proficient or Mastery
                masteryPercentage >= 75f -> 75f + (masteryPercentage - 75f) * 0.2f // Gradual progression within Proficient
                masteryPercentage >= 50f -> 50f + (masteryPercentage - 50f) * 0.3f // Gradual progression within Intermediate
                else -> masteryPercentage * 0.5f   // Gradual progression within Novice
            }.coerceIn(0f, 100f) // Ensure mastery stays within 0-100 range


            // Update deck mastery
            decksRef.child(deckId).child("mastery").setValue(mastery.toInt()).await()
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e("FlashcardRepository", "Error updating deck mastery: $e")
        }
    }


    suspend fun getTestResults(deckId: String, testId: String): TestResult? {
        return try {
            val snapshot = decksRef.child(deckId).child("testResults").child(testId).get().await()
            snapshot.getValue(TestResult::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Add a function to get all test results for a deck
    fun getAllTestResultsForDeck(deckId: String): Flow<List<TestResult>> = flow {
        try {
            Log.d("FlashcardViewModel", "Fetching test results for deck: $deckId")

            // Retrieve test results for the specified deck
            val snapshot = decksRef.child(deckId).child("testResults").get().await()

            // Map the snapshot to a list of TestResult objects
            val testResults = snapshot.children.mapNotNull {
                it.getValue(TestResult::class.java)
            }
            Log.d("FlashcardViewModel", "Test results fetched successfully for deck: $deckId")
            emit(testResults)
        } catch (e: Exception) {
            Log.e("FlashcardViewModel", "Error fetching test results for deck: $deckId", e)
            emit(emptyList())
        }
    }

}


