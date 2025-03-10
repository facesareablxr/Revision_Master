package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner

/**
 * Composable function for the screen to add a new deck.
 *
 * Here the user will be able to create a deck with their chosen name, a description which can be
 * used to explain what the deck is for and a subject, as although a university student only really
 * do one subject - some do dual degrees and it allows for the filtering/searching in future.
 *
 * @param navController NavController for navigation.
 */
@Composable
fun AddDeckScreen(
    navController: NavController
) {
    // Declaration of variables for the deck creation
    val subjects = stringArrayResource(R.array.subjects).toList()
    val flashcardViewModel: FlashcardViewModel = viewModel()
    var deckName by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Subject") }
    var public by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    // Access Firebase authentication
    val auth = Firebase.auth
    // Get the currently logged-in user and their UID
    val currentUser = auth.currentUser
    val uid = currentUser?.uid

    // Scaffold for the screen layout
    Scaffold(
        topBar = {
            // Top app bar with the title "Create Revision Deck"
            SmallTopAppBar(
                title = stringResource(R.string.createDeck),
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Text field for entering the deck name
            DeckNameEnterBox(
                deckName = deckName,
                onDeckNameChange = { newValue -> deckName = newValue }
            )

            // Text field for entering the deck description
            DescriptionBox(
                description = description,
                onDescriptionChange = { newValue -> description = newValue }
            )

            // Spacer to ensure all boxes are evenly spaced
            Spacer(modifier = Modifier.padding(4.dp))

            // Spinner for selecting the subject of the deck
            SubjectSpinner(
                subjects = subjects,
                selectedSubject = selectedSubject,
                onSubjectChange = { newValue -> selectedSubject = newValue }
            )


            // Switch for toggling deck visibility
            PublicSwitch(
                public = public,
                onPublicChange = { newValue -> public = newValue }
            )

            // Button to create the deck
            CreateDeckButton(
                flashcardViewModel,
                deckName,
                selectedSubject,
                public,
                description,
                uid!!,
                navController
            )
        }
    }
}

/**
 * Composable function for the switch to toggle deck visibility.
 * @param public Current visibility status.
 * @param onPublicChange Function to handle visibility change.
 */
@Composable
fun PublicSwitch(
    public: Boolean,
    onPublicChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Text indicating deck visibility
        Text(
            text = stringResource(R.string.publicDeck)
        )
        // Switch for toggling visibility
        Switch(
            checked = public,
            onCheckedChange = onPublicChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
 * Composable function for the button to create a new deck.
 * @param flashcardViewModel ViewModel for flashcard operations.
 * @param deckName Name of the deck.
 * @param selectedSubject Selected subject for the deck.
 * @param public Visibility status of the deck.
 * @param description Description of the deck.
 * @param uid User ID of the deck creator.
 * @param navController NavController for navigation.
 */
@Composable
private fun CreateDeckButton(
    flashcardViewModel: FlashcardViewModel,
    deckName: String,
    selectedSubject: String,
    public: Boolean,
    description: String,
    uid: String,
    navController: NavController
) {
    // Button to create the deck
    Button(
        onClick = {
            // Call ViewModel function to add the deck
            flashcardViewModel.addDeck(
                deckName,
                selectedSubject,
                public,
                description,
                uid
            )
            // Navigate back after adding the deck
            navController.popBackStack()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.createButton))
    }
}

/**
 * Composable function for the text field to enter deck description.
 * @param description Current description text.
 * @param onDescriptionChange Function to handle description text change.
 */
@Composable
fun DescriptionBox(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    // Text field for entering deck description
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text(stringResource(R.string.optional)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            autoCorrect = true
        )
    )
}

/**
 * Composable function for the spinner to select deck subject.
 * @param subjects List of available subjects.
 * @param selectedSubject Currently selected subject.
 * @param onSubjectChange Function to handle subject selection change.
 */
@Composable
fun SubjectSpinner(
    subjects: List<String>,
    selectedSubject: String,
    onSubjectChange: (String) -> Unit
) {
    // Button-based spinner for selecting deck subject
    ButtonSpinner(
        items = subjects,
        label = selectedSubject,
        itemClick = onSubjectChange
    )
}

/**
 * Composable function for the text field to enter deck name.
 * @param deckName Current deck name.
 * @param onDeckNameChange Function to handle deck name change.
 */
@Composable
fun DeckNameEnterBox(
    deckName: String,
    onDeckNameChange: (String) -> Unit
) {
    // Text field for entering deck name
    OutlinedTextField(
        value = deckName,
        onValueChange = onDeckNameChange,
        label = { Text(stringResource(R.string.deckName)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
