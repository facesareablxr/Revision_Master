package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner

/**
 * Composable function for the screen to add a new flashcard to a deck.
 *
 * @param navController NavController for navigation.
 * @param deckId ID of the deck to which the flashcard will be added.
 * @param flashcardViewModel ViewModel for flashcard operations.
 */
@Composable
fun AddFlashcardScreen(
    navController: NavHostController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel() // Default parameter for ViewModel
) {
    // Mutable state variables for question, answer, dialog visibility, and selected difficulty
    var question by remember { mutableStateOf("") } // Question for the flashcard
    var answer by remember { mutableStateOf("") } // Answer for the flashcard
    var showDialog by remember { mutableStateOf(false) } // Flag to control dialog visibility
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) } // Selected difficulty for the flashcard

    // Scaffold for the screen layout
    Scaffold(
        topBar = {
            // Top app bar with the title "Add Flashcard"
            SmallTopAppBar(
                navController,
                title = "Add Flashcard"
            )
        },
        content = { innerPadding ->
            // Column to arrange content vertically
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Text field for entering the question
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp)) // Spacer for vertical spacing
                // Text field for entering the answer
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                // Spinner for selecting the difficulty
                ButtonSpinner(
                    items = Difficulty.values().map { it.label },
                    label = selectedDifficulty.label,
                    itemClick = { newDifficultyLabel ->
                        selectedDifficulty =
                            Difficulty.values().find { it.label == newDifficultyLabel }!!
                    }
                )

                // Button to add the flashcard
                Button(
                    onClick = {
                        // Check if both question and answer are not empty
                        if (question.isNotEmpty() && answer.isNotEmpty()) {
                            // Call ViewModel function to add the flashcard
                            flashcardViewModel.addFlashcard(
                                deckId,
                                question,
                                answer,
                                selectedDifficulty
                            )
                            showDialog = true // Show dialog after adding the flashcard
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                ) {
                    Text("Add Flashcard")
                }
            }

            // Dialog to confirm flashcard addition
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false }, // Dismiss dialog when requested
                    title = { Text("Flashcard Added!") }, // Dialog title
                    confirmButton = {
                        TextButton(onClick = {
                            navController.popBackStack() // Navigate back after adding the flashcard
                        }) {
                            Text("OK") // OK button text
                        }
                    }
                )
            }
        }
    )
}
