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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SummaryScreen(
    correctMatches: Int,
    incorrectMatches: Int,
    deckId: String,
    navController: NavHostController,
) {
    val totalMatches = correctMatches + incorrectMatches
    val accuracy = if (totalMatches > 0) (correctMatches.toFloat() / totalMatches.toFloat()) * 100 else 0
    val masteryProgress = "Your mastery progress: $accuracy%"

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "Summary",
                navController = navController
            )
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

            GlideImage(model = (R.drawable.giphy), contentDescription = "Congrats")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Correct Matches: $correctMatches")

            Text("Incorrect Matches: $incorrectMatches")

            Spacer(modifier = Modifier.height(16.dp))
            Text(masteryProgress)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate("deck_details_screen/$deckId") }) {
                Text("Return to Deck Details")
            }
        }
    }
}
