package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar


/**
 * Composable function for the Edit Deck screen.
 * @param navController: NavController for navigation within the app.
 * @param deckId: ID of the deck to be edited.
 * @param flashcardViewModel: ViewModel for managing flashcard-related data.
 */
@Composable
fun EditDeckScreen(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // List of subjects
    val subjects = stringArrayResource(R.array.subjects).toList()
    // Mutable state variables for deck details
    var deckName by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Subject")}
    var isPublic by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    // Observe deck details from ViewModel
    val deckState by flashcardViewModel.deckDetails.observeAsState()

    // Fetch deck details when screen launches
    LaunchedEffect(Unit) {
        flashcardViewModel.getDeckDetails(deckId)
    }

    // Update state variables when deck details change
    LaunchedEffect(deckState) {
        deckState?.let { deck ->
            deckName = deck.name
            selectedSubject = deck.subject
            isPublic = deck.isPublic
            description = deck.description
        }
    }

    // Scaffold for the screen layout
    Scaffold(
        topBar = {
            // Custom top app bar
            SmallTopAppBar(
                title = stringResource(R.string.editDeck),
                navController = navController
            )
        }
    ) { innerPadding ->
        // Column for arranging UI components vertically
        Column(modifier = Modifier.padding(innerPadding)) {
            // Deck name entry box
            DeckNameEnterBox(
                deckName = deckName,
                onDeckNameChange = { newValue -> deckName = newValue }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Description box
            DescriptionBox(
                description = description,
                onDescriptionChange = { newValue -> description = newValue }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Subject spinner
            SubjectSpinner(
                subjects = subjects,
                selectedSubject = selectedSubject,
                onSubjectChange = { newValue -> selectedSubject = newValue }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Public switch
            PublicSwitch(
                isPublic = isPublic,
                onPublicChange = { newValue -> isPublic = newValue }
            )
            // Button to save changes
            Button(
                onClick = {
                    // Update deck details in the database and navigate back
                    flashcardViewModel.updateDeck(
                        deckId,
                        deckName,
                        selectedSubject,
                        isPublic,
                        description
                    )
                    navController.navigateUp()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(stringResource(R.string.saveChanges))
            }
        }
    }
}
