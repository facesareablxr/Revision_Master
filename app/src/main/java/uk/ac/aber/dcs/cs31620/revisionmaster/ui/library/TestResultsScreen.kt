package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.TestResult
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import kotlin.math.roundToInt

@Composable
fun TestResultsScreen(
    deckId: String,
    navController: NavController,
    flashcardViewModel: FlashcardViewModel = viewModel(),
) {
    // Collecting the test results from ViewModel
    val testResults by flashcardViewModel.allTestResultsForDeck.observeAsState()

    LaunchedEffect(Unit) {
        flashcardViewModel.getAllTestResultsForDeck(deckId)
    }

    // Scaffold representing the overall layout structure
    Scaffold(
        topBar = {
            SmallTopAppBar(
                navController,
                title = stringResource(R.string.testResults)
            )
        },
        content = { innerPadding ->
            // Scrollable column layout for displaying test results
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                item {
                    testResults?.let { results ->
                        if (results.isNotEmpty()) {
                            // Display each test result as a card
                            results.forEach { result ->
                                TestResultCard(result = result)
                            }
                        } else {
                            // Display a message if there are no test results
                            Text(
                                text = stringResource(R.string.noResults),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

/**
 * Composable function to display a test result card with a leading image.
 *
 * @param result The test result data to display.
 */
@Composable
fun TestResultCard(result: TestResult) {
    // Calculate accuracy percentage
    val totalMatches = result.correct + result.incorrect
    var accuracy by remember { mutableIntStateOf(0) }
    accuracy =
        if (totalMatches > 0) ((result.correct.toFloat() / totalMatches.toFloat()) * 100).roundToInt() else 0

    // Determine image based on accuracy
    val imageResourceId = when {
        accuracy >= 75 -> R.drawable.excellent
        accuracy >= 50 -> R.drawable.awesome
        accuracy >= 25 -> R.drawable.greatjob
        else -> R.drawable.goodjob
    }

    // Card to display test result information
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Leading image based on accuracy
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                // Display the day of the test
                Text(
                    text = "Day of test: ${result.date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Accuracy: ${accuracy}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                val time = result.elapsedTime
                val minutes = time / 60
                val seconds = time % 60
                Text(
                    text = "Time: $minutes min $seconds sec",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
