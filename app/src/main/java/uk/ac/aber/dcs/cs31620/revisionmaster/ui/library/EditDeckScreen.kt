package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar


/**
 *
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditDeckScreen(
    navController: NavController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val subjects = stringArrayResource(R.array.subjects).toList()
    var deckName by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Subject") }
    var isPublic by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    val deckState by flashcardViewModel.deckDetails.observeAsState()

    LaunchedEffect(Unit) {
        flashcardViewModel.getDeckDetails(deckId)
    }

    LaunchedEffect(deckState) {
        deckState?.let { deck ->
            deckName = deck.name
            selectedSubject = deck.subject
            isPublic = deck.isPublic
            description = deck.description
        }
    }

    // For pushing ownerID, as I found that it removed it from the DB otherwise.
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val uid = currentUser?.uid


    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = stringResource(R.string.editDeck),
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            /* This uses the deck name entry box from the AddDeckScreen, allowing the user to edit
            the name of the deck */
            DeckNameEnterBox(
                deckName = deckName,
                onDeckNameChange = { newValue -> deckName = newValue }
            )
            Spacer(modifier = Modifier.height(8.dp))
            /* This uses the description box from the AddDeckScreen, allowing the user to change the
            description of the deck */
            DescriptionBox(
                description = description,
                onDescriptionChange = { newValue -> description = newValue }
            )
            Spacer(modifier = Modifier.height(8.dp))
            /* This uses the subject spinner from the AddDeckScreen, allowing the user to change
            what subject the deck is for */
            SubjectSpinner(
                subjects = subjects,
                selectedSubject = selectedSubject,
                onSubjectChange = { newValue -> selectedSubject = newValue }
            )
            Spacer(modifier = Modifier.height(16.dp))
            /* This toggles the boolean expression for it being a public deck */
            PublicSwitch(
                isPublic = isPublic,
                onPublicChange = { newValue -> isPublic = newValue }
            )
            /* When pressed, this button will update the deck details in the database, and return to
            the previous screen */
            Button(
                onClick = {
                    // Calls the view model to update the deck with the ID, and all other information
                    flashcardViewModel.updateDeck(
                        deckId,
                        deckName,
                        selectedSubject,
                        isPublic,
                        description,
                        uid.toString()
                    )
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.saveChanges))
            }
        }
    }
}
