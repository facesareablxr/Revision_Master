package uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SummaryScreen(
    correctMatches: Int,
    incorrectMatches: Int,
    elapsedSeconds: Long,
    deckId: String,
    navController: NavController,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val totalMatches = correctMatches + incorrectMatches
    var accuracy by remember { mutableIntStateOf(0) }
    accuracy = if (totalMatches > 0) ((correctMatches.toFloat() / totalMatches.toFloat()) * 100).roundToInt() else 0
    val masteryProgress = "Your accuracy: $accuracy%"

    val imageResourceId = when {
        accuracy >= 75 -> R.drawable.excellent
        accuracy >= 50 -> R.drawable.awesome
        accuracy >= 25 -> R.drawable.greatjob
        else -> R.drawable.goodjob
    }

    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Summary") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Test Summary", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            GlideImage(
                model = imageResourceId,
                contentDescription = "Accuracy Image"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Correct Matches: $correctMatches")

            Text("Incorrect Matches: $incorrectMatches")

            Spacer(modifier = Modifier.height(16.dp))
            Text(masteryProgress)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Date: $currentDate")

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                flashcardViewModel.addTestResult(deckId, correctMatches, incorrectMatches, elapsedSeconds, currentDate)
                navController.popBackStack()
                navController.navigate(Screen.DeckDetails.route + "/${deckId}")
            }) {
                Text("Return to Deck")
            }
        }
    }
}