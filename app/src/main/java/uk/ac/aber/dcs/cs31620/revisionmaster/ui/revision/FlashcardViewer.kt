package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard

@Composable
fun FlashcardScreen(flashcards: List<Flashcard>, deckId: String, flashcardId: String) {
    // Need to add get deck, index of card too

    var currentCardIndex by remember { mutableStateOf(0) }
    var isFrontShowing by remember { mutableStateOf(true) }

    val currentFlashcard = flashcards.getOrNull(currentCardIndex)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        currentFlashcard?.let { card ->
            Flashcard(
                front = card.question,
                back = card.answer,
                isFrontShowing = isFrontShowing,
                onFlip = { isFrontShowing = !isFrontShowing }
            )
        }

        Row(horizontalArrangement = Arrangement.SpaceAround) {
            IconButton(onClick = {
                // Move back if possible
                if (currentCardIndex > 0) currentCardIndex--
            }) {
                Icon(Icons.Filled.ArrowBack, "Previous")
            }

            IconButton(onClick = {
                // Move forward if possible
                if (currentCardIndex < flashcards.lastIndex) currentCardIndex++
            }) {
                Icon(Icons.Filled.ArrowForward, "Next")
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Flashcard(front: String, back: String, isFrontShowing: Boolean, onFlip: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clickable { onFlip() },
    ) {
        AnimatedContent(
            targetState = isFrontShowing,
            transitionSpec = { fadeIn() with fadeOut() }, label = ""
        ) { showFront ->
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (showFront) front else back)
            }
        }
    }
}
