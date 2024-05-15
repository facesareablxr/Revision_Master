package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.FlashcardImage
import java.util.Locale

/**
 * Composable function for displaying the top-level of the flashcard viewer.
 * @param navController Navigation controller for navigating between composables.
 * @param deckId Identifier for the deck of flashcards being viewed.
 * @param flashcardViewModel ViewModel for managing flashcard data.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FlashcardViewerTopLevel(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Collects the flashcards state
    val flashcardsState by flashcardViewModel.flashcards.observeAsState(initial = null)

    // Fetch flashcards for the deck
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    Scaffold(
        topBar = { SmallTopAppBar(navController = navController, title = "Flashcard Viewer") },
    ) {
        // Display the flashcards when they are loaded
        flashcardsState?.let { FlashcardViewer(it) }
    }
}

/**
 * Composable function for displaying the flashcards.
 * @param flashcards List of flashcards to display.
 */
@Composable
fun FlashcardViewer(
    flashcards: List<Flashcard>
) {
    // State for tracking current card index and whether front or back of card is showing
    var currentCardIndex by remember { mutableIntStateOf(0) }
    var isFrontShowing by remember { mutableStateOf(true) }

    // State for tracking elapsed time for each flashcard
    val elapsedTimeState = remember { mutableStateOf(0L) }
    val timerScope = rememberCoroutineScope()

    // Start the timer when flashcards are loaded
    LaunchedEffect(Unit) {
        timerScope.launch {
            while (true) {
                delay(1000) // Update every second
                elapsedTimeState.value++
            }
        }
    }

    // Get the current flashcard based on index
    val currentFlashcard = flashcards.getOrNull(currentCardIndex)
    val context = LocalContext.current
    val textToSpeech = remember { TextToSpeech(context) { } }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display flashcard content
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { isFrontShowing = !isFrontShowing },
                contentAlignment = Alignment.Center
            ) {
                currentFlashcard?.let { card ->
                    if (card.imageUri != null) {
                        FlashcardWithImage(
                            front = card.question,
                            back = card.answer,
                            isFrontShowing = isFrontShowing,
                            onFlip = { isFrontShowing = !isFrontShowing },
                            onTextToSpeech = { text ->
                                textToSpeech.language = Locale.UK
                                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            },
                            imageUri = card.imageUri!!
                        )
                    } else {
                        Flashcard(
                            front = card.question,
                            back = card.answer,
                            isFrontShowing = isFrontShowing,
                            onFlip = { isFrontShowing = !isFrontShowing },
                            onTextToSpeech = { text ->
                                textToSpeech.language = Locale.UK
                                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }
                }
            }
        }

        // Navigation buttons for moving between flashcards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentCardIndex > 0) currentCardIndex--
                },
                enabled = currentCardIndex > 0
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }
            IconButton(
                onClick = {
                    if (currentCardIndex < flashcards.lastIndex) currentCardIndex++
                },
                enabled = currentCardIndex < flashcards.lastIndex
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }
    }
}

/**
 * Composable function for displaying a flashcard with an image.
 * @param front Text displayed on the front side of the flashcard.
 * @param back Text displayed on the back side of the flashcard.
 * @param isFrontShowing Flag indicating whether the front or back side of the flashcard is showing.
 * @param onFlip Callback function to flip the flashcard.
 * @param onTextToSpeech Callback function for text-to-speech functionality.
 * @param imageUri URI of the image to display on the flashcard.
 */
@Composable
fun FlashcardWithImage(
    front: String,
    back: String,
    isFrontShowing: Boolean,
    onFlip: () -> Unit,
    onTextToSpeech: (String) -> Unit,
    imageUri: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight()
            .clickable { onFlip() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text-to-speech button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onTextToSpeech(if (isFrontShowing) front else back) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                        contentDescription = stringResource(id = R.string.TTS)
                    )
                }
            }
            // Display image and text content of the flashcard
            FlashcardImage(imagePath = imageUri)
            // Center the text within the card
            AnimatedContent(
                targetState = isFrontShowing,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { showFront ->
                Text(text = if (showFront) front else back)
            }
        }
    }
}

/**
 * Composable function for displaying a text-based flashcard.
 * @param front Text displayed on the front side of the flashcard.
 * @param back Text displayed on the back side of the flashcard.
 * @param isFrontShowing Flag indicating whether the front or back side of the flashcard is showing.
 * @param onFlip Callback function to flip the flashcard.
 * @param onTextToSpeech Callback function for text-to-speech functionality.
 */
@Composable
fun Flashcard(
    front: String,
    back: String,
    isFrontShowing: Boolean,
    onFlip: () -> Unit,
    onTextToSpeech: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight()
            .height(500.dp)
            .clickable { onFlip() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text-to-speech button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onTextToSpeech(if (isFrontShowing) front else back) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                        contentDescription = stringResource(id = R.string.TTS)
                    )
                }
            }
            // Display text content of the flashcard
            AnimatedContent(
                targetState = isFrontShowing,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { showFront ->
                Text(text = if (showFront) front else back)
            }
        }
    }
}
