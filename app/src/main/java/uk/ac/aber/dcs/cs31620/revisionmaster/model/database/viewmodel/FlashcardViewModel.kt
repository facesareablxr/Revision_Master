package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.FlashcardRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard


/**
 * ViewModel class responsible for handling business logic related to flashcards and decks.
 */
class FlashcardViewModel : ViewModel() {
    // Firebase authentication instance to get the current user
    private val currentUser = FirebaseAuth.getInstance().currentUser

    /** DECK VIEW MODEL FUNCTIONS*/

    /**
     * Adds a new deck to the database.
     */
    fun addDeck(
        name: String,
        subject: String,
        isPublic: Boolean,
        description: String,
        owner: String
    ) {
        // Create a Deck object with the provided data
        val deck = Deck(
            name = name,
            subject = subject,
            isPublic = isPublic,
            description = description,
            ownerId = owner
        )
        // Use viewModelScope to launch a coroutine to add the deck asynchronously
        viewModelScope.launch {
            FlashcardRepository.addDeck(deck)
        }
    }

    /**
     * Deletes a deck from the database.
     */
    fun deleteDeck(deckId: String) {
        // Use viewModelScope to launch a coroutine to delete the deck asynchronously
        viewModelScope.launch {
            try {
                FlashcardRepository.deleteDeck(deckId)
            } catch (e: Exception) {
                // Catch any exceptions that occur during deletion and log them
                Log.e("FlashcardViewModel", "Error deleting deck", e)
            }
        }
    }

    /**
     * Updates an existing deck in the database.
     */
    fun updateDeck(
        deckId: String,
        name: String,
        subject: String,
        isPublic: Boolean,
        description: String,
        ownerId: String
    ) {
        // Create an updated Deck object with the provided data
        val updatedDeck = Deck(
            id = deckId,
            name = name,
            subject = subject,
            isPublic = isPublic,
            description = description,
            ownerId = ownerId
        )
        // Use viewModelScope to launch a coroutine to update the deck asynchronously
        viewModelScope.launch {
            FlashcardRepository.updateDeck(updatedDeck)
        }
    }

    /**
     * Flow representing the list of decks.
     */
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks

    /**
     * Fetches decks belonging to the current user.
     */
    fun getUserDecks() {
        // Check if there is a current user
        currentUser?.let { user ->
            // Use viewModelScope to launch a coroutine to fetch user decks asynchronously
            viewModelScope.launch {
                FlashcardRepository.getUserDecks(user.uid).collect { retrievedDecks ->
                    // Update the _decks flow with the retrieved decks
                    _decks.value = retrievedDecks
                }
            }
        }
    }

    /**
     * LiveData representing details of a specific deck.
     */
    private val _deckDetails = MutableLiveData<Deck?>(null)
    val deckDetails: LiveData<Deck?> get() = _deckDetails

    /**
     * Fetches details of a specific deck.
     */
    fun getDeckDetails(deckId: String) {
        // Use viewModelScope to launch a coroutine to fetch deck details asynchronously
        viewModelScope.launch {
            val deck = FlashcardRepository.getDeckDetails(deckId)
            // Update the _deckDetails LiveData with the retrieved deck
            _deckDetails.postValue(deck)
        }
    }

    /**
     * LiveData representing a list of flashcards.
     */
    private val _flashcards = MutableLiveData<List<Flashcard>>(emptyList())
    val flashcards: LiveData<List<Flashcard>> get() = _flashcards

    /**
     * Fetches flashcards for a specific deck.
     */
    fun getFlashcardsForDeck(deckId: String) {
        // Use viewModelScope to launch a coroutine to fetch flashcards asynchronously
        viewModelScope.launch {
            val retrievedFlashcards = FlashcardRepository.getFlashcardsByDeckId(deckId)
            // Update the _flashcards LiveData with the retrieved flashcards
            _flashcards.value = retrievedFlashcards
        }
    }

    /**
     * Updates a flashcard in the database.
     */
    fun updateFlashcard(
        deckId: String,
        flashcardId: String,
        question: String,
        answer: String,
        difficulty: Difficulty
    ) {
        // Create an updated Flashcard object with the provided data
        val updatedFlashcard = Flashcard(
            id = flashcardId,
            question = question,
            answer = answer,
            difficulty = difficulty
        )
        // Use viewModelScope to launch a coroutine to update the flashcard asynchronously
        viewModelScope.launch {
            FlashcardRepository.updateFlashcard(updatedFlashcard,deckId)
        }
    }

    /**
     * Deletes a flashcard from a specific deck.
     */
    fun deleteFlashcard(flashcardId: String, deckId: String) {
        // Use viewModelScope to launch a coroutine to delete the flashcard asynchronously
        viewModelScope.launch {
            FlashcardRepository.deleteFlashcard(flashcardId, deckId)
        }
    }

    /**
     * LiveData representing details of a specific flashcard.
     */
    val flashcardLiveData: MutableLiveData<Flashcard?> = MutableLiveData()

    /**
     * Fetches details of a specific flashcard.
     */
    fun getFlashcardById(deckId: String, flashcardId: String) {
        // Use viewModelScope to launch a coroutine to fetch flashcard details asynchronously
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val flashcard = FlashcardRepository.getFlashcardById(deckId, flashcardId)
                // Update the flashcardLiveData with the retrieved flashcard
                flashcardLiveData.postValue(flashcard)
                // Log the successful retrieval of the flashcard
                Log.d(
                    "FlashcardViewModel",
                    "Retrieved flashcard with ID: $flashcardId from deck ID: $deckId"
                )
            } catch (e: Exception) {
                // Catch any exceptions that occur during retrieval and log them
                Log.e("FlashcardViewModel", "Error retrieving flashcard", e)
            }
        }
    }

    /**
     * Adds a flashcard to a specific deck and updates the deck's average difficulty.
     */
    fun addFlashcardAndUpdateDeck(
        deckId: String,
        question: String,
        answer: String,
        difficulty: Difficulty,
        imageUri: String?
    ) {
        // Create a new Flashcard object with the provided data
        val flashcard = Flashcard(
            question = question,
            answer = answer,
            difficulty = difficulty,
            imageUri = imageUri
        )
        // Use viewModelScope to launch a coroutine to add the flashcard asynchronously
        viewModelScope.launch {
            FlashcardRepository.addFlashcard(deckId, flashcard)
            // Update the average difficulty of the deck
            FlashcardRepository.updateDeckDifficulty(deckId)
        }
    }

    /**
     * Calculates the average mastery level of a list of flashcards.
     */
    fun calculateAverageMastery(flashcards: List<Flashcard>): Float {
        // Calculate the total mastery level of all flashcards
        val totalMastery = flashcards.sumBy { it.mastery.toInt() }
        // Return the average mastery level (if there are no flashcards, return 0)
        return if (flashcards.isEmpty()) 0f else totalMastery / flashcards.size.toFloat()
    }
}
