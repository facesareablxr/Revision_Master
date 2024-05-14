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
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.ConfirmationAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import java.util.Locale

@Composable
fun FlashcardSelfTestScreen(
    navController: NavHostController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var elapsedSeconds by remember { mutableStateOf(0L) }

    var currentIndex by remember { mutableStateOf(0) }
    var correctMatches by remember { mutableStateOf(0) }
    var incorrectMatches by remember { mutableStateOf(0) }
    var isTestComplete by remember { mutableStateOf(false) }
    var isFrontShowing by remember { mutableStateOf(true) }

    val flashcards by flashcardViewModel.flashcards.observeAsState(initial = null)

    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    Scaffold(
        topBar = {
            ConfirmationAppBar(
                navController = navController,
                title = "Flashcard Self Test",
            )
        },
        content = {
                innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)){
                TestInProgress(
                    currentIndex = currentIndex,
                    flashcards = flashcards,
                    correctMatches = correctMatches,
                    incorrectMatches = incorrectMatches,
                    isFrontShowing = isFrontShowing,
                    onCorrectMatch = {
                        correctMatches++
                        if (currentIndex + 1 < flashcards!!.size) {
                            currentIndex++
                            isFrontShowing = true // Reset to front-facing
                        } else {
                            isTestComplete = true
                        }
                    },
                    onIncorrectMatch = {
                        incorrectMatches++
                        if (currentIndex + 1 < flashcards!!.size) {
                            currentIndex++
                            isFrontShowing = true // Reset to front-facing
                        } else {
                            isTestComplete = true
                        }
                    },
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

@Composable
fun TestInProgress(
    currentIndex: Int,
    flashcards: List<Flashcard>?,
    correctMatches: Int,
    incorrectMatches: Int,
    isFrontShowing: Boolean,
    onCorrectMatch: () -> Unit,
    onIncorrectMatch: () -> Unit,
    onFlip: () -> Unit
) {
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
                ButtonBar(
                    onCorrectMatch = onCorrectMatch,
                    onIncorrectMatch = onIncorrectMatch
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Correct Matches: $correctMatches")
            Text("Incorrect Matches: $incorrectMatches")
        }
    }
}

@Composable
fun ButtonBar(
    onCorrectMatch: () -> Unit,
    onIncorrectMatch: () -> Unit
) {
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
