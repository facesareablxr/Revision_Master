package uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.repository.FlashcardRepository
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.TestResult


/**
 * ViewModel class responsible for handling business logic related to flashcards and decks.
 */
class FlashcardViewModel : ViewModel() {
    // Firebase authentication instance to get the current user
    private val currentUser = FirebaseAuth.getInstance().currentUser

    /** ------------------------------------ DECK FUNCTIONS ------------------------------------ **/

    /** INITIALISATION OF VARIABLES **/
    // Flow representing the list of decks
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks

    // LiveData representing details of a specific deck
    private val _deckDetails = MutableLiveData<Deck?>(null)
    val deckDetails: LiveData<Deck?> = _deckDetails

    /**
     * Adds a new deck to the database.
     */
    fun addDeck(
        name: String,
        subject: String,
        public: Boolean,
        description: String,
        owner: String
    ) {
        try {
            // Create a Deck object with the provided data
            val deck = Deck(
                name = name,
                subject = subject,
                public = public,
                description = description,
                ownerId = owner
            )
            // Use viewModelScope to launch a coroutine to add the deck asynchronously
            viewModelScope.launch {
                FlashcardRepository.addDeck(deck)
            }
        } catch (e: Exception) {
            Log.e("FlashcardViewModel", "Error adding deck", e)
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
        public: Boolean,
        description: String
    ) {
        // Use viewModelScope to launch a coroutine to update the deck asynchronously
        viewModelScope.launch {
            FlashcardRepository.updateDeck(deckId, name, subject, public, description)
        }
    }

    /**
     * Fetches decks belonging to the current user.
     */
    fun getUserDecks() {
        // Check if there is a current user
        currentUser?.let { user ->
            // Use viewModelScope to launch a coroutine to fetch user decks asynchronously
            viewModelScope.launch {
                try {
                    FlashcardRepository.getUserDecks(user.uid).collect { retrievedDecks ->
                        // Update the _decks flow with the retrieved decks
                        _decks.value = retrievedDecks
                    }
                } catch (e: Exception) {
                    Log.e("FlashcardViewModel", "Error fetching user decks", e)
                }
            }
        }
    }

    /**
     * Fetches details of a specific deck.
     */
    fun getDeckDetails(deckId: String) {
        // Use viewModelScope to launch a coroutine to fetch deck details asynchronously
        viewModelScope.launch {
            try {
                val deck = FlashcardRepository.getDeckDetails(deckId)
                // Update the _deckDetails LiveData with the retrieved deck
                _deckDetails.postValue(deck)
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Error fetching deck details", e)
            }
        }
    }

    /** --------------------------------- FLASHCARD FUNCTIONS --------------------------------- **/

    /** INITIALISING VARIABLES **/
    // LiveData representing a list of flashcards
    private val _flashcards = MutableLiveData<List<Flashcard>>(emptyList())
    val flashcards: LiveData<List<Flashcard>> = _flashcards

    // LiveData representing details of a specific flashcard
    private val _flashcard = MutableLiveData<Flashcard?>(null)
    val flashcard: LiveData<Flashcard?> = _flashcard

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
        val flashcard = if (imageUri != null) {
            Flashcard(question = question, answer = answer, difficulty = difficulty, imageUri = imageUri)
        } else {
            Flashcard(question = question, answer = answer,  difficulty = difficulty)
        }
        // Use viewModelScope to launch a coroutine to add the flashcard asynchronously
        viewModelScope.launch {
            try {
                // Add the flashcard and update deck difficulty
                FlashcardRepository.addFlashcard(deckId, flashcard)
                FlashcardRepository.updateDeckDifficulty(deckId)
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Error adding flashcard and updating deck", e)
            }
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
        difficulty: Difficulty,
        imageUri: String?
    ) {
        // Create a new Flashcard object with the provided data
        val updatedFlashcard = if (imageUri != "null") {
            Flashcard(id = flashcardId, question = question, answer = answer, difficulty = difficulty, imageUri = imageUri)
        } else {
            Flashcard(id = flashcardId, question = question, answer = answer,  difficulty = difficulty)
        }
        // Use viewModelScope to launch a coroutine to update the flashcard asynchronously
        viewModelScope.launch {
            FlashcardRepository.updateFlashcard(updatedFlashcard, deckId)
        }
    }

    /**
     * Updates flashcard repetition count and difficulty.
     */
    fun updateFlashcardRepetition(
        deckId: String,
        flashcardId: String,
        repetition: Int,
        difficulty: Difficulty,
        nextReviewDate: String,
    ) {
        viewModelScope.launch {
            try {
                FlashcardRepository.updateFlashcardRepetition(
                    deckId,
                    flashcardId,
                    repetition,
                    difficulty,
                    nextReviewDate
                )
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Error updating flashcard repetition", e)
            }
        }
    }

    /**
     * Deletes a flashcard from a specific deck.
     */
    fun deleteFlashcard(flashcardId: String, deckId: String) {
        // Use viewModelScope to launch a coroutine to delete the flashcard asynchronously
        viewModelScope.launch {
            try {
                FlashcardRepository.deleteFlashcard(flashcardId, deckId)
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Error deleting flashcard", e)
            }
        }
    }

    /**
     * Fetches flashcards for a specific deck.
     */
    fun getFlashcardsForDeck(deckId: String) {
        // Use viewModelScope to launch a coroutine to fetch flashcards asynchronously
        viewModelScope.launch {
            try {
                val retrievedFlashcards = FlashcardRepository.getFlashcardsByDeckId(deckId)
                // Update the _flashcards LiveData with the retrieved flashcards
                _flashcards.postValue(retrievedFlashcards)
            } catch (e: Exception) {
                Log.e("FlashcardViewModel", "Error fetching flashcards for deck", e)
            }
        }
    }

    /**
     * Fetches details of a specific flashcard.
     */
    fun getFlashcardById(deckId: String, flashcardId: String) {
        // Use viewModelScope to launch a coroutine to fetch flashcard details asynchronously
        viewModelScope.launch {
            try {
                val flashcard = FlashcardRepository.getFlashcardById(deckId, flashcardId)
                // Update the _flashcardLiveData with the retrieved flashcard
                _flashcard.postValue(flashcard)
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

    /** -------------------------------- TEST RESULT FUNCTIONS -------------------------------- **/

    /** INITIALISING VARIABLES **/
    // StateFlow to hold the test result
    private val _testResult = MutableStateFlow<TestResult?>(null)
    val testResult: StateFlow<TestResult?> = _testResult

    // LiveData to hold all test results for a deck
    private val _allTestResultsForDeck = MutableLiveData<List<TestResult>>(emptyList())
    val allTestResultsForDeck: LiveData<List<TestResult>> = _allTestResultsForDeck

    /**
     * Adds a test result to the repository and updates the deck's mastery.
     */
    fun addTestResult(
        deckId: String,
        correct: Int,
        incorrect: Int,
        elapsedTime: Long,
        date: String
    ) {
        // Create a TestResult object with the provided data
        val testResult = TestResult(
            deckId = deckId,
            correct = correct,
            incorrect = incorrect,
            elapsedTime = elapsedTime,
            date = date
        )
        // Launch a coroutine in viewModelScope to add the test result and update deck's mastery asynchronously
        viewModelScope.launch {
            try {
                // Add the test result and update the deck's mastery in the repository
                FlashcardRepository.addTestResultAndUpdateMastery(deckId, testResult)
            } catch (e: Exception) {
                // Log any errors that occur during adding the test result and updating deck's mastery
                Log.e("FlashcardViewModel", "Error adding test result and updating mastery", e)
            }
        }
    }

    /**
     * Fetches all test results for a specific deck from the repository.
     */
    fun getAllTestResultsForDeck(deckId: String) {
        // Launch a coroutine in viewModelScope to fetch test results for the deck asynchronously
        viewModelScope.launch {
            try {
                // Collect the test results for the deck from the repository
                FlashcardRepository.getAllTestResultsForDeck(deckId).collect { testResults ->
                    // Update the _allTestResultsForDeck LiveData with the fetched test results
                    _allTestResultsForDeck.value = testResults
                }
            } catch (e: Exception) {
                // Log any errors that occur during fetching of test results for the deck
                Log.e("FlashcardViewModel", "Error fetching test results for deck $deckId", e)
            }
        }
    }

    /** ---------------------------------- EXPLORE FUNCTIONS ---------------------------------- **/


    /** INITIALISING VARIABLES **/
    // Holds the list of public decks
    private val _publicDecks = MutableLiveData<List<Deck>>()
    val publicDecks: LiveData<List<Deck>> = _publicDecks

    /**
     * Retrieves all public decks from the repository and updates the LiveData.
     */
    fun fetchPublicDecks() {
        viewModelScope.launch {
            try {
                // Fetch public decks from the repository
                val fetchedPublicDecks = FlashcardRepository.getPublicDecks()
                _publicDecks.value = fetchedPublicDecks
                Log.d("publicDecks", "Public Decks: $fetchedPublicDecks")
            } catch (e: Exception) {
                // Log any errors that occur during the process
                Log.e(TAG, "Error fetching public decks", e)
            }
        }
    }

    /**
     * Function to add a deck to the user's library.
     * @param deck: The deck to add to the library.
     */
    fun addDeckToLibrary(deck: Deck) {
        viewModelScope.launch {
            // Copying the deck and assigning the current user's ID as the owner
            val copiedDeck = deck.copy(ownerId = currentUser!!.uid, mastery = 0)
            // Adding the copied deck to the user's library via the repository
            FlashcardRepository.addDeckToLibrary(copiedDeck)
            // Logging a message indicating that the deck has been added to the library
            Log.e("DeckLibrary", "Deck added to library: ${copiedDeck.name}")
        }
    }
}


