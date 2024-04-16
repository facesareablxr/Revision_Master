package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard

/**
 * This object handles all flashcard and deck-related interactions with a Firebase Realtime Database.
 * It provides functions to add, retrieve, update, and delete decks and flashcards.
 */
object FlashcardRepository {
    // Firebase Realtime Database initialization
    private val rootNode =
        FirebaseDatabase.getInstance("https://revision-master-91910-default-rtdb.europe-west1.firebasedatabase.app")
    private val decksRef = rootNode.reference.child("decks") // Reference to the "decks" node in the database

    /**
     * Adds a deck to the database.
     */
    fun addDeck(deck: Deck) {
        decksRef.child(deck.id).setValue(deck) // Set the value of the deck under its unique ID
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
            emit(decks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    /**
     * Returns the details of a specific deck.
     */
    suspend fun getDeckDetails(deckId: String): Deck? {
        val snapshot = decksRef.child(deckId).get().await() // Retrieve deck details from the database
        return snapshot.getValue(Deck::class.java)
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
            val flashcard = data.getValue(Flashcard::class.java) // Convert each child snapshot to a Flashcard object
            flashcard?.let { flashcards.add(it) }
        }
        return flashcards
    }

    /**
     * Retrieves a Deck object along with its associated flashcards.
     */
    suspend fun getDeckWithFlashcards(deckId: String): Deck? {
        val deckRef = decksRef.child(deckId)
        val deckSnapshot = deckRef.get().await()
        if (!deckSnapshot.exists()) {
            return null
        }

        val deck = deckSnapshot.getValue(Deck::class.java) ?: return null // Convert the snapshot to a Deck object

        val flashcardsRef = deckRef.child("flashcards")
        val flashcardsSnapshot = flashcardsRef.get().await() // Retrieve all flashcards under the deck
        val flashcards = mutableListOf<Flashcard>()
        flashcardsSnapshot.children.forEach { data ->
            val flashcard = data.getValue(Flashcard::class.java)
            flashcard?.let { flashcards.add(it) } // Add the flashcard to the list if it's not null
        }
        deck.cards = flashcards // Assign the list of flashcards to the deck
        deck.averageDifficulty = calculateDeckDifficulty(flashcards) // Calculate and update the deck's average difficulty
        return deck
    }

    /**
     * Calculates the average difficulty level of a deck based on its flashcards.
     */
    private fun calculateDeckDifficulty(cards: List<Flashcard>): Difficulty {
        if (cards.isEmpty()) {
            Log.d("FlashcardViewModel", "No flashcards found. Defaulting to MEDIUM difficulty.")
            return Difficulty.MEDIUM // Default difficulty if no flashcards are found
        }

        val averageWeight = cards.map { it.difficulty.weight }.average() // Calculate the average weight of flashcards
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

    /**
     * Deletes a deck and its associated flashcards from the database.
     */
    suspend fun deleteDeck(deckId: String) {
        decksRef.child(deckId).removeValue().await() // Remove the deck node from the database
        decksRef.child(deckId).child("flashcards").removeValue().await() // Remove the flashcards node under the deck
    }

    /**
     * Updates a deck's details.
     */
    fun updateDeck(deck: Deck) {
        decksRef.child(deck.id).setValue(deck) // Update the deck details in the database
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
    }

    /**
     * Deletes a flashcard from a specific deck.
     */
    suspend fun deleteFlashcard(flashcardId: String, deckId: String) {
        decksRef.child(deckId).child("flashcards").child(flashcardId).removeValue().await() // Remove the flashcard from the database
    }
}
