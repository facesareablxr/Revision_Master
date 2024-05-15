package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar

/**
 * Composable function for displaying a preview screen of a deck of flashcards.
 * @param navController: NavController for navigation within the app.
 * @param deckId: Identifier for the deck.
 * @param flashcardViewModel: ViewModel for managing flashcard data.
 */
@Composable
fun DeckPreviewScreen(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Observing the state of deck details and flashcards using ViewModel
    val deckState by flashcardViewModel.deckDetails.observeAsState()
    val flashcardsState by flashcardViewModel.flashcards.observeAsState()

    // Fetching deck details and flashcards when the composable is launched
    LaunchedEffect(Unit) {
        flashcardViewModel.getDeckDetails(deckId)
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    // Scaffold composable to create a screen scaffold with top bar, content, and floating action button
    Scaffold(
        topBar = {
            // Displaying a small top app bar with deck name
            deckState?.let { deck ->
                SmallTopAppBar(navController = navController, title = deck.name)
            }
        },
        content = { innerPadding ->
            // Column composable for arranging content vertically
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Displaying flashcards content
                MaterialsContent(flashcardsState)
            }
        },
        floatingActionButton = {
            // Floating action button to download the deck
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.Download, stringResource(R.string.download))},
                text = { Text(text =stringResource(R.string.downloadDeck))},
                onClick = {
                    flashcardViewModel.addDeckToLibrary(deckState!!) // Add deck to library
                    navController.navigateUp() // Navigate back
                },
            )
        }
    )
}

/**
 * Composable function for displaying the content of flashcards.
 * @param flashcardsState: List of flashcards.
 */
@Composable
fun MaterialsContent(
    flashcardsState: List<Flashcard>?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Checking if flashcard list is empty or not
            flashcardsState?.let { flashcardList ->
                if (flashcardList.isEmpty()) {
                    // Displaying a message if flashcard list is empty
                    Text(
                        stringResource(R.string.noCards),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // LazyColumn for lazy loading of flashcard items
                    LazyColumn(verticalArrangement = Arrangement.Top) {
                        items(flashcardList) { flashcard ->
                            FlashcardItem(flashcard) // Displaying individual flashcard item
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable function for displaying an individual flashcard item.
 * @param flashcard: The flashcard object to display.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FlashcardItem(flashcard: Flashcard) {
    // Card composable to display flashcard content in a card
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Displaying flashcard image if available
            flashcard.imageUri?.let {
                GlideImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterVertically),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                // Displaying flashcard question
                Text(
                    text = flashcard.question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Displaying flashcard answer
                Text(text = flashcard.answer, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                // Displaying flashcard difficulty level as a chip
                Chip(
                    text = flashcard.difficulty.toString().lowercase()
                        .replaceFirstChar { it.uppercase() })
            }
        }
    }
}

/**
 * Composable function for displaying a chip-like element.
 *
 * @param text: Text to display within the chip.
 */
@Composable
fun Chip(text: String) {
    // Surface composable to display text in a chip-like style
    Surface(shape = RoundedCornerShape(16.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}
