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
import androidx.compose.foundation.layout.size
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
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FlashcardViewerTopLevel(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel())
{
    // Collects the flashcards state
    val flashcardsState by flashcardViewModel.flashcards.observeAsState(initial = null)

    // Fetch flashcards for the deck
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    Scaffold(
        topBar = { SmallTopAppBar(navController = navController, title = "Flashcard Viewer") },
    ){
        flashcardsState?.let { FlashcardViewer(it) }

    }
}

@Composable
fun FlashcardViewer(
    flashcards: List<Flashcard>
) {
    var currentCardIndex by remember { mutableStateOf(0) }
    var isFrontShowing by remember { mutableStateOf(true) }

    val currentFlashcard = flashcards.getOrNull(currentCardIndex)
    val context = LocalContext.current
    val textToSpeech = remember { TextToSpeech(context) { } }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { isFrontShowing = !isFrontShowing },
            contentAlignment = Alignment.Center
        ) {
            currentFlashcard?.let { card ->
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


@Composable
fun Flashcard(front: String, back: String, isFrontShowing: Boolean, onFlip: () -> Unit, onTextToSpeech: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(500.dp)
            .clickable { onFlip() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onTextToSpeech(if (isFrontShowing) front else back) }) {
                    Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = "Text to Speech")
                }
            }

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
