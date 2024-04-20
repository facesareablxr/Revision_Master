package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MatchingGame(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Collects the flashcards state
    val flashcardsState by flashcardViewModel.flashcards.observeAsState(initial = emptyList())

    // Fetch flashcards for the deck
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deckId)
    }

    Scaffold(
        topBar = { SmallTopAppBar(navController = navController, title = "Matching Game") }
    ) {

    }
}

