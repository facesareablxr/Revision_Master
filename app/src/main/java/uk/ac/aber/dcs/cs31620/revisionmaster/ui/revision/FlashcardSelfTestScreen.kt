package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.ConfirmationAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.SpacedRepetition
import java.util.Locale

/**
 * Composable to display the flashcard self-test screen.
 * @param navController The navController used for navigation within the app
 * @param deckId The ID of the deck for which the flashcards are being tested
 * @param flashcardViewModel The ViewModel responsible for managing flashcard data
 */
@Composable
fun FlashcardSelfTestScreen(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Define various state variables needed for the self-test screen
    val spacedRepetition = remember { SpacedRepetition() }
    val startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var elapsedSeconds by remember { mutableStateOf(0L) }
    var currentIndex by remember { mutableStateOf(0) }
    var correctMatches by remember { mutableStateOf(0) }
    var incorrectMatches by remember { mutableStateOf(0) }
    var isTestComplete by remember { mutableStateOf(false) }
    var isFrontShowing by remember { mutableStateOf(true) }

    // Observe the flashcards from the ViewModel
    val flashcards by flashcardViewModel.flashcards.observeAsState(initial = null)

    // Fetch flashcards when the composable is launched
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    // Scaffold for the layout structure
    Scaffold(
        topBar = {
            ConfirmationAppBar(
                navController = navController,
                title = "Flashcard Self Test",
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                // Display the test in progress
                TestInProgress(
                    currentIndex = currentIndex,
                    flashcards = flashcards,
                    correctMatches = correctMatches,
                    incorrectMatches = incorrectMatches,
                    isFrontShowing = isFrontShowing,
                    onCorrectMatch = {
                        // Handle correct match
                        correctMatches++
                        if (currentIndex + 1 < flashcards!!.size) {
                            currentIndex++
                            isFrontShowing = true // Reset to front-facing
                        } else {
                            isTestComplete = true
                        }
                    },
                    onIncorrectMatch = {
                        // Handle incorrect match
                        incorrectMatches++
                        if (currentIndex + 1 < flashcards!!.size) {
                            currentIndex++
                            isFrontShowing = true // Reset to front-facing
                        } else {
                            isTestComplete = true
                        }
                    },
                    spacedRepetition = spacedRepetition,
                    flashcardViewModel = flashcardViewModel,
                    deckId = deckId,
                    onFlip = { isFrontShowing = !isFrontShowing }
                )
            }
        }
    )

    // Navigate to summary screen when the test is complete
    if (isTestComplete) {
        val endTime = System.currentTimeMillis()
        elapsedSeconds = (endTime - startTime) / 1000
        navController.navigate(
            "${Screen.Summary.route}/$correctMatches/$incorrectMatches/$elapsedSeconds/$deckId"
        )
    }
}

/**
 * Composable to display the ongoing test.
 * @param currentIndex The index of the current flashcard being displayed
 * @param flashcards The list of flashcards to be tested
 * @param correctMatches The number of correctly matched flashcards
 * @param incorrectMatches The number of incorrectly matched flashcards
 * @param isFrontShowing Indicates whether the front side of the flashcard is currently showing
 * @param onCorrectMatch Callback function for handling correct match
 * @param onIncorrectMatch Callback function for handling incorrect match
 * @param spacedRepetition Object responsible for spaced repetition logic
 * @param flashcardViewModel The ViewModel responsible for managing flashcard data
 * @param deckId The ID of the deck for which the flashcards are being tested
 * @param onFlip Callback function for flipping the flashcard
 */
@Composable
fun TestInProgress(
    currentIndex: Int,
    flashcards: List<Flashcard>?,
    correctMatches: Int,
    incorrectMatches: Int,
    isFrontShowing: Boolean,
    onCorrectMatch: () -> Unit,
    onIncorrectMatch: () -> Unit,
    spacedRepetition: SpacedRepetition,
    flashcardViewModel: FlashcardViewModel,
    deckId: String,
    onFlip: () -> Unit
) {
    // Context for text-to-speech
    val context = LocalContext.current
    val textToSpeech = remember { TextToSpeech(context) { } }
    val currentFlashcard = flashcards?.getOrNull(currentIndex)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentFlashcard != null) {
            Box(
                modifier = Modifier
                    .size(300.dp, 200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onFlip() }
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Display flashcard content
                if (currentFlashcard.imageUri != null) {
                    FlashcardWithImage(
                        front = currentFlashcard.question,
                        back = currentFlashcard.answer,
                        isFrontShowing = isFrontShowing,
                        onFlip = onFlip,
                        onTextToSpeech = { text ->
                            textToSpeech.language = Locale.UK
                            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        },
                        imageUri = currentFlashcard.imageUri.toString()
                    )
                } else {
                    Flashcard(
                        front = currentFlashcard.question,
                        back = currentFlashcard.answer,
                        isFrontShowing = isFrontShowing,
                        onFlip = onFlip,
                        onTextToSpeech = { text ->
                            textToSpeech.language = Locale.UK
                            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (!isFrontShowing) {
                // Display button bar for correct/incorrect actions
                ButtonBar(
                    onCorrectMatch = {
                        val updatedCard = spacedRepetition.calculateNextReview(currentFlashcard, Difficulty.EASY)
                        updatedCard.nextReview?.let {
                            flashcardViewModel.updateFlashcardRepetition(deckId, updatedCard.id, updatedCard.repetition, updatedCard.difficulty,
                                it
                            )
                        } // Notify the ViewModel
                        onCorrectMatch()
                    },
                    onIncorrectMatch = {
                        val updatedCard = spacedRepetition.calculateNextReview(currentFlashcard, Difficulty.HARD)
                        updatedCard.nextReview?.let {
                            flashcardViewModel.updateFlashcardRepetition(deckId, updatedCard.id, updatedCard.repetition, updatedCard.difficulty,
                                it
                            )
                        }
                        onIncorrectMatch()
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Display match counts
            Text("Correct Matches: $correctMatches")
            Text("Incorrect Matches: $incorrectMatches")
        }
    }
}

/**
 * Composable for the buttons to self-mark.
 * @param onCorrectMatch Callback function for correct match action
 * @param onIncorrectMatch Callback function for incorrect match action
 */
@Composable
fun ButtonBar(
    onCorrectMatch: () -> Unit,
    onIncorrectMatch: () -> Unit
) {
    // Row containing buttons for correct and incorrect actions
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = {
            onCorrectMatch()
        }) {
            Text("Correct")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = {
            onIncorrectMatch()
        }) {
            Text("Incorrect")
        }
    }
}

