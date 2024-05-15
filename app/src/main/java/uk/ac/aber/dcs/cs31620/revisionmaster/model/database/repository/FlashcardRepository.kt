package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.TestResult
import java.util.UUID

/**
 * This object handles all flashcard and deck-related interactions with a Firebase Realtime Database.
 * It provides functions to add, retrieve, update, and delete decks and flashcards.
 */
object FlashcardRepository {
    // Firebase Realtime Database initialization
    private val rootNode =
        FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")

    /** ------------------------------------ DECK FUNCTIONS ------------------------------------ **/

    /** INITIALISATION OF VARIABLES **/
    private val decksRef =
        rootNode.reference.child("decks") // Reference to the "decks" node in the database

    /**
     * Adds a deck to the database.
     */
    fun addDeck(deck: Deck) {
        try {
            // Set the value of the deck under its unique ID
            decksRef.child(deck.id).setValue(deck)
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error adding deck: ${deck.id}", e)
        }
    }

    /**
     * Deletes a deck and its associated flashcards from the database.
     */
    suspend fun deleteDeck(deckId: String) {
        try {
            // Remove the deck node from the database
            decksRef.child(deckId).removeValue().await()
            // Remove the flashcards node under the deck
            decksRef.child(deckId).child("flashcards").removeValue().await()
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error deleting deck: $deckId", e)
        }
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
            Log.e(TAG, "Error deleting decks: $e")
        }
    }

    /**
     * Updates a deck's details. This approach is used to avoid deleting flashcards.
     */
    fun updateDeck(
        deckId: String,
        name: String,
        subject: String,
        public: Boolean,
        description: String
    ) {
        try {
            // Update deck details in the database
            decksRef.child(deckId).child("name").setValue(name)
            decksRef.child(deckId).child("subject").setValue(subject)
            decksRef.child(deckId).child("public").setValue(public)
            decksRef.child(deckId).child("description").setValue(description)
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error updating deck: $deckId", e)
        }
    }

    /**
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

            // Emit the list of decks
            emit(decks)
        } catch (e: Exception) {
            // If an exception occurs, emit an empty list
            Log.e(TAG, "Error getting user decks: $e")
            emit(emptyList())
        }
    }

    /**
     * Returns the details of a specific deck, including average difficulty.
     */
    suspend fun getDeckDetails(deckId: String): Deck? {
        try {
            // Retrieve the snapshot of the specified deck
            val snapshot = decksRef.child(deckId).get().await()

            // Convert the snapshot to a Deck object
            val deck = snapshot.getValue(Deck::class.java)

            // If the deck exists, calculate its average difficulty
            deck?.let {
                val flashcards = getFlashcardsByDeckId(deckId)
                it.averageDifficulty = calculateDeckDifficulty(flashcards)
            }

            return deck
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error getting deck details: $e")
            return null
        }
    }

    /**
     * Calculates the average difficulty level of a deck based on its flashcards.
     */
    private fun calculateDeckDifficulty(cards: List<Flashcard>): Difficulty {
        try {
            // Check if there are any flashcards
            if (cards.isEmpty()) {
                Log.d("FlashcardViewModel", "No flashcards found.")
            }

            // Calculate the average weight of flashcards
            val averageWeight = cards.map { it.difficulty.weight }
                .average()
            Log.d("FlashcardViewModel", "Average weight of flashcards: $averageWeight")

            // Find the difficulty level closest to the average weight
            val foundDifficulty = Difficulty.values().find {
                averageWeight - 0.5 <= it.weight && it.weight <= averageWeight + 0.5
            }

            // Return the found difficulty level or default to MEDIUM
            return foundDifficulty ?: run {
                Log.e("FlashcardViewModel", "Error calculating difficulty. Defaulting to MEDIUM.")
                Difficulty.MEDIUM
            }
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error calculating deck difficulty: $e")
            return Difficulty.MEDIUM
        }
    }

    /**
     * Updates the difficulty level of a deck.
     */
    suspend fun updateDeckDifficulty(deckId: String) {
        try {
            // Retrieve updated flashcards for the deck
            val updatedFlashcards = getFlashcardsByDeckId(deckId)

            // Retrieve the deck from the database
            val updatedDeck = decksRef.child(deckId).get().await().getValue(Deck::class.java)

            updatedDeck?.let { deck ->
                // Calculate the new difficulty level
                val newDifficulty = calculateDeckDifficulty(updatedFlashcards)
                deck.averageDifficulty = newDifficulty

                // Update the difficulty under the correct node
                decksRef.child(deck.id).child("averageDifficulty")
                    .setValue(newDifficulty.toString())
            }
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error updating deck difficulty: $e")
        }
    }

    /**
     * Updates the mastery level of a deck.
     */
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
                Log.d(TAG, "No test results available for deck $deckId.")
                return // Exit without updating mastery
            }

            // Calculate mastery as a percentage
            val masteryPercentage = (totalCorrect.toFloat() / totalQuestions) * 100

            // Calculate mastery with eventual progression to 100%
            val mastery = when {
                masteryPercentage >= 90f -> 100f
                masteryPercentage >= 75f -> 75f + (masteryPercentage - 75f) * 0.2f
                masteryPercentage >= 50f -> 50f + (masteryPercentage - 50f) * 0.3f
                else -> masteryPercentage * 0.5f
            }.coerceIn(0f, 100f)

            // Update deck mastery
            decksRef.child(deckId).child("mastery").setValue(mastery.toInt()).await()
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error updating deck mastery: $e")
        }
    }

    /** --------------------------------- FLASHCARD FUNCTIONS --------------------------------- **/

    /**
     * Adds a flashcard to a specific deck.
     */
    fun addFlashcard(deckId: String, flashcard: Flashcard) {
        try {
            // Set the value of the flashcard under its unique ID
            decksRef.child(deckId).child("flashcards").child(flashcard.id).setValue(flashcard)
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error adding flashcard: ${flashcard.id} to deck: $deckId", e)
        }
    }

    /**
     * Retrieves all flashcards belonging to a specific deck.
     */
    suspend fun getFlashcardsByDeckId(deckId: String): List<Flashcard> {
        try {
            val deckReference = decksRef.child(deckId)
            val flashcardsRef = deckReference.child("flashcards")

            // Retrieve the snapshot of flashcards
            val snapshot = flashcardsRef.get().await()

            // Map the snapshot to a list of Flashcard objects
            val flashcards = mutableListOf<Flashcard>()
            snapshot.children.forEach { data ->
                val flashcard = data.getValue(Flashcard::class.java)
                flashcard?.let { flashcards.add(it) }
            }
            return flashcards
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error getting flashcards for deck: $deckId", e)
            return emptyList()
        }
    }

    /**
     * Updates a flashcard in a specific deck.
     */
    fun updateFlashcard(flashcard: Flashcard, deckId: String) {
        try {
            val deckReference = decksRef.child(deckId)
            val flashcardsRef = deckReference.child("flashcards")
            val flashcardRef = flashcardsRef.child(flashcard.id)

            // Update the flashcard in the database
            flashcardRef.setValue(flashcard)
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error updating flashcard: ${flashcard.id} in deck: $deckId", e)
        }
    }

    /**
     * Deletes a flashcard from a specific deck.
     */
    suspend fun deleteFlashcard(flashcardId: String, deckId: String) {
        try {
            // Remove the flashcard from the database
            decksRef.child(deckId).child("flashcards").child(flashcardId).removeValue().await()
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error deleting flashcard: $flashcardId from deck: $deckId", e)
        }
    }

    /**
     * Gets a single flashcard by its ID from the specified deck.
     */
    suspend fun getFlashcardById(deckId: String, flashcardId: String): Flashcard? {
        return try {
            val deckReference = decksRef.child(deckId)
            val flashcardsRef = deckReference.child("flashcards")
            val flashcardRef = flashcardsRef.child(flashcardId)

            // Retrieve the snapshot of the specified flashcard
            val snapshot = flashcardRef.get().await()
            snapshot.getValue(Flashcard::class.java)
        } catch (e: Exception) {
            // Handle exceptions if any
            Log.e(TAG, "Error getting flashcard: $flashcardId from deck: $deckId", e)
            null
        }
    }

    /**
     * Adds a test result to the database and updates the mastery level of the corresponding deck.
     */
    suspend fun addTestResultAndUpdateMastery(
        deckId: String,
        testResult: TestResult
    ) {
        val testId = testResult.testId
        try {
            // Add the test result to the database
            decksRef.child(deckId).child("testResults").child(testId).setValue(testResult).await()

            // Update the mastery level of the deck
            updateDeckMastery(deckId)

        } catch (e: Exception) {
            // Log any errors that occur during the process
            Log.e(TAG, "Error adding test result and updating mastery for deck: $deckId", e)
        }
    }

    /** -------------------------------- TEST RESULT FUNCTIONS -------------------------------- **/

    /**
     * Retrieves all test results for a specific deck.
     */
    fun getAllTestResultsForDeck(deckId: String): Flow<List<TestResult>> = flow {
        try {
            // Log the start of fetching test results for the deck
            Log.d("FlashcardViewModel", "Fetching test results for deck: $deckId")

            // Retrieve test results for the specified deck
            val snapshot = decksRef.child(deckId).child("testResults").get().await()

            // Map the snapshot to a list of TestResult objects
            val testResults = snapshot.children.mapNotNull {
                it.getValue(TestResult::class.java)
            }

            // Log the successful retrieval of test results
            Log.d("FlashcardViewModel", "Test results fetched successfully for deck: $deckId")

            // Emit the list of test results
            emit(testResults)
        } catch (e: Exception) {
            // Log any errors that occur during the process
            Log.e("FlashcardViewModel", "Error fetching test results for deck: $deckId", e)

            // Emit an empty list if an error occurs
            emit(emptyList())
        }
    }

    /**
     * Updates the repetition, difficulty, and next review date of a flashcard in a specific deck.
     */
    fun updateFlashcardRepetition(
        deckId: String,
        flashcardId: String,
        repetition: Int,
        difficulty: Difficulty,
        nextReviewDate: String
    ) {
        try {
            // Get the reference to the flashcard
            val flashcardRef = decksRef.child(deckId).child("flashcards").child(flashcardId)

            // Update the repetition, difficulty, and next review date of the flashcard
            flashcardRef.child("repetition").setValue(repetition)
            flashcardRef.child("difficulty").setValue(difficulty)
            flashcardRef.child("nextReview").setValue(nextReviewDate)
        } catch (e: Exception) {
            // Log any errors that occur during the process
            Log.e(TAG, "Error updating flashcard repetition", e)
        }
    }

    /** ---------------------------------- EXPLORE FUNCTIONS ---------------------------------- **/

    /**
     * Retrieves all public decks from the database.
     */
    suspend fun getPublicDecks(): List<Deck> {
        return try {
            // Retrieve all decks from the database where 'public' is true
            val snapshot = decksRef.get().await()
            if(snapshot.exists()){
                val publicDecks = snapshot.children.mapNotNull { it.getValue(Deck::class.java) }

                Log.d("publicDecks", "Public Decks: $publicDecks")
                publicDecks.filter { it.public }// Return the list of public decks directly
            }
            else{
                Log.d("publicDecks", "No public decks")
                emptyList() // Return an empty list if no public decks exist
            }
        } catch (e: Exception) {
            // Log any errors that occur during the process
            Log.e(TAG, "Error fetching public decks", e)
            emptyList() // Return an empty list in case of an error
        }
    }

    /**
     * Suspended function to add a deck to the user's library.
     * @param deck: The deck to add to the library.
     */
    suspend fun addDeckToLibrary(deck: Deck) {
        // Generate a new ID for the deck
        val newDeckId = generateNewDeckId()
        // Replaces the deck ID, and the mastery of it
        val deckToAdd = deck.copy(id = newDeckId, public = false)
        // Simulate adding the deck to the library
        decksRef.child(newDeckId).setValue(deckToAdd)
        // Retrieve flashcards associated with the original deck
        val flashcards = getFlashcardsByDeckId(deck.id)
        // Add each flashcard to the newly created deck
        for (flashcard in flashcards) {
            addFlashcard(newDeckId, flashcard)
        }
        // Logging a message indicating that the deck and its flashcards have been added to the library
        Log.e("DeckLibrary", "Deck added to library with ID: $newDeckId")
    }

    /**
     * Function to generate a new unique ID for a deck.
     * @return A new unique ID string for the deck.
     */
    private fun generateNewDeckId(): String {
        return UUID.randomUUID().toString()
    }


}


