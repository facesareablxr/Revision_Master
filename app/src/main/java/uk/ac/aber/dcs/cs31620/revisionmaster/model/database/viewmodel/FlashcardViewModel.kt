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
 *
 */
class FlashcardViewModel : ViewModel() {
    private val currentUser = FirebaseAuth.getInstance().currentUser

    /** DECK VIEW MODEL FUNCTIONS*/

    /**
     * Adds a deck into the database
     */
    fun addDeck(
        name: String,
        subject: String,
        isPublic: Boolean,
        description: String,
        owner: String
    ) {
        val deck = Deck(
            name = name,
            subject = subject,
            isPublic = isPublic,
            description = description,
            ownerId = owner
        )
        viewModelScope.launch {
            FlashcardRepository.addDeck(deck)
        }
    }

    /**
     * Deletes deck from database
     */
    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            try {
                FlashcardRepository.deleteDeck(deckId)
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Error deleting deck", e)
            }
        }
    }

    /**
     * Updates deck in database
     */
    fun updateDeck(
        deckId: String,
        name: String,
        subject: String,
        isPublic: Boolean,
        description: String,
        ownerId: String
    ) {
        val updatedDeck = Deck(
            id = deckId,
            name = name,
            subject = subject,
            isPublic = isPublic,
            description = description,
            ownerId = ownerId
        )
        viewModelScope.launch {
            FlashcardRepository.updateDeck(updatedDeck)
        }
    }

    /**
     *
     */
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks

    fun getUserDecks() {
        currentUser?.let { user ->
            viewModelScope.launch {
                FlashcardRepository.getUserDecks(user.uid).collect { retrievedDecks ->
                    _decks.value = retrievedDecks
                }
            }
        }
    }

    /**
     *
     */
    private val _deckDetails = MutableLiveData<Deck?>(null)
    val deckDetails: LiveData<Deck?> get() = _deckDetails

    fun getDeckDetails(deckId: String) {
        viewModelScope.launch {
            val deck = FlashcardRepository.getDeckDetails(deckId)
            _deckDetails.postValue(deck)
        }
    }

    /**
     *
     */
    private val _deckWithFlashcards = MutableLiveData<Deck?>(null)
    val deckWithFlashcards: LiveData<Deck?> = _deckWithFlashcards

    fun getDeckWithFlashcards(deckId: String) {
        viewModelScope.launch {
            val deck = FlashcardRepository.getDeckWithFlashcards(deckId)
            _deckWithFlashcards.postValue(deck)
        }
    }

    /**
     *
     */
    private val _flashcards = MutableLiveData<List<Flashcard>>(emptyList())
    val flashcards: LiveData<List<Flashcard>> get() = _flashcards

    fun getFlashcardsForDeck(deckId: String) {
        viewModelScope.launch {
            val retrievedFlashcards = FlashcardRepository.getFlashcardsByDeckId(deckId)
            _flashcards.value = retrievedFlashcards
        }
    }

    /**
     *
     */
    fun addFlashcard(
        deckId: String,
        question: String,
        answer: String,
        difficulty: Difficulty,
        imageUri: String?
    ) {
        val flashcard = Flashcard(
            question = question,
            answer = answer,
            difficulty = difficulty,
            imageUri = imageUri
        )
        viewModelScope.launch {
            FlashcardRepository.addFlashcard(deckId, flashcard)
        }
    }

    fun updateFlashcard(
        deckId: String,
        flashcardId: String,
        question: String,
        answer: String,
        difficulty: Difficulty
    ) {
        val updatedFlashcard = Flashcard(
            id = flashcardId,
            question = question,
            answer = answer,
            difficulty = difficulty
        )
        viewModelScope.launch {
            FlashcardRepository.updateFlashcard(updatedFlashcard,deckId)
        }
    }

    fun deleteFlashcard(flashcardId: String, deckId: String) {
        viewModelScope.launch {
            FlashcardRepository.deleteFlashcard(flashcardId, deckId)
        }
    }

    val flashcardLiveData: MutableLiveData<Flashcard?> = MutableLiveData()
    fun getFlashcardById(deckId: String, flashcardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try { // Add a try-catch for potential errors
                val flashcard = FlashcardRepository.getFlashcardById(deckId, flashcardId)
                flashcardLiveData.postValue(flashcard)
                Log.d(
                    "FlashcardViewModel",
                    "Retrieved flashcard with ID: $flashcardId from deck ID: $deckId"
                )
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Error retrieving flashcard", e)
            }
        }
    }

    /**
     * FLASHCARD FUNCTIONS
     */
    fun randomizeFlashcards(flashcards: List<Flashcard>): List<Flashcard> {
        return flashcards.shuffled()
    }

    fun getNextFlashcard(
        flashcards: List<Flashcard>,
        currentIndex: Int
    ): Flashcard? {
        return if (currentIndex < flashcards.size) {
            flashcards[currentIndex]
        } else {
            null
        }
    }

}
