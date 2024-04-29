package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
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
        topBar = { SmallTopAppBar(navController = navController, title = "Matching Game") },
    ) {
        MatchingGameContent(flashcardsState)
    }
}

@Composable
fun MatchingGameContent(
    flashcards: List<Flashcard>
) {
    val questions = mutableListOf<String>()
    val answers = mutableListOf<String>()

    flashcards.forEach { flashcard ->
        questions.add(flashcard.question)
        answers.add(flashcard.answer)
    }

    var groupedQuestions = questions.chunked(4)
    groupedQuestions = groupedQuestions.shuffled()
    var groupedAnswers = answers.chunked(4)
    groupedAnswers = groupedAnswers.shuffled()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedQuestions.zip(groupedAnswers).forEach { (questionRow, answerRow) ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            ) {
                questionRow.forEach { question ->
                    MatchingGameCard(text = question)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            ) {
                answerRow.forEach { answer ->
                    MatchingGameCard(text = answer)
                }
            }
        }
    }
}



@Composable
fun MatchingGameCard(
    text: String
) {
    Card(
        modifier = Modifier
            .size(120.dp, 120.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

