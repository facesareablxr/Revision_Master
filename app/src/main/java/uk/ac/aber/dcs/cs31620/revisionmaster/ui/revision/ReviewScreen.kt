package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.ConfirmationAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.SpacedRepetition

/**
 * Composable function for displaying the review screen for flashcards.
 * @param navController Navigation controller for navigating between composables.
 * @param deckId Identifier for the deck of flashcards being reviewed.
 * @param flashcardViewModel ViewModel for managing flashcard data.
 */
@Composable
fun ReviewScreen(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Initialize state variables
    val spacedRepetition = remember { SpacedRepetition() }
    val startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var elapsedSeconds by remember { mutableStateOf(0L) }
    var currentIndex by remember { mutableStateOf(0) }
    var correctMatches by remember { mutableStateOf(0) }
    var incorrectMatches by remember { mutableStateOf(0) }
    var isTestComplete by remember { mutableStateOf(false) }
    var isFrontShowing by remember { mutableStateOf(true) }

    // Fetch flashcards for the deck
    val flashcards by flashcardViewModel.flashcards.observeAsState(initial = null)
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    // Scaffold for the review screen
    Scaffold(
        topBar = {
            ConfirmationAppBar(
                navController = navController,
                title = "Flashcard Self Test",
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                // Filter flashcards due for review
                val dueFlashcards = flashcards?.filter { spacedRepetition.isDueForReview(it) }
                if (!dueFlashcards.isNullOrEmpty()) {
                    // Display flashcards for review
                    TestInProgress(
                        currentIndex = currentIndex,
                        flashcards = dueFlashcards,
                        correctMatches = correctMatches,
                        incorrectMatches = incorrectMatches,
                        isFrontShowing = isFrontShowing,
                        onCorrectMatch = {
                            correctMatches++
                            if (currentIndex + 1 < dueFlashcards.size) {
                                currentIndex++
                                isFrontShowing = true // Reset to front-facing
                            } else {
                                isTestComplete = true
                            }
                        },
                        onIncorrectMatch = {
                            incorrectMatches++
                            if (currentIndex + 1 < dueFlashcards.size) {
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
                } else {
                    // Display message when no flashcards are due for review
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        stringResource(R.string.noReview)
                    }
                }
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
