package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.ClickableImageBox

/**
 * Composable function for editing a flashcard.
 *
 * @param navController: NavController for navigation within the app.
 * @param flashcardId: ID of the flashcard to be edited.
 * @param deckId: ID of the deck to which the flashcard belongs.
 * @param flashcardViewModel: ViewModel for managing flashcard-related data.
 */
@Composable
fun EditFlashcardScreen(
    navController: NavController,
    flashcardId: String,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Collecting flashcard data from ViewModel
    val flashcard by flashcardViewModel.flashcard.observeAsState()

    // Side effect to fetch flashcard data
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardById(deckId, flashcardId)
    }

    // State to hold the question, answer, selected difficulty, and delete dialog visibility
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<String?>(null) }

    // Side effect to update UI when flashcard data changes
    LaunchedEffect(flashcard) {
        flashcard?.let { flashcard ->
            question = flashcard.question
            answer = flashcard.answer
            selectedDifficulty = flashcard.difficulty
            imageUri = flashcard.imageUri
        }
    }

    // Scaffold for the screen layout
    Scaffold(
        topBar = {
            // Custom top app bar
            SmallTopAppBar(
                navController,
                title = "Edit Flashcard"
            )
        },
        content = { innerPadding ->
            // Column layout to arrange items vertically
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Clickable Image Box for selecting/editing image
                ClickableImageBox(
                    imageUri = imageUri,
                    onImageUploaded = { selectedUri ->
                        imageUri = selectedUri
                    },
                )
                // Delete image button
                if (imageUri != null) {
                    Button(
                        onClick = { imageUri = "" },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Delete Image")
                    }
                }
                // Text field for entering question
                OutlinedTextFieldWithCamera(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text(stringResource(R.string.question)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Text field for entering answer
                OutlinedTextFieldWithCamera(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text(stringResource(R.string.answer)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                // Spinner for selecting difficulty
                ButtonSpinner(
                    items = Difficulty.values().map { it.label },
                    label = selectedDifficulty.label,
                    itemClick = { newDifficultyLabel ->
                        selectedDifficulty = Difficulty.values().find { it.label == newDifficultyLabel }!!
                    }
                )
                // Row layout for buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Button to delete flashcard
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                    // Button to save changes
                    Button(
                        onClick = {
                            // Update flashcard details in database and navigate back
                            flashcardViewModel.updateFlashcard(
                                deckId, flashcardId, question, answer, selectedDifficulty, imageUri.toString()
                            )
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(R.string.saveChanges))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                // AlertDialog for confirming flashcard deletion
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text(stringResource(R.string.deleteCard)) },
                        text = { Text(stringResource(R.string.confirmDelete)) },
                        confirmButton = {
                            // Confirm button
                            Button(onClick = {
                                // Delete flashcard from database and navigate back
                                flashcardViewModel.deleteFlashcard(flashcardId, deckId)
                                navController.popBackStack()
                            }) {
                                Text(stringResource(R.string.confirm))
                            }
                        },
                        dismissButton = {
                            // Cancel button
                            Button(onClick = { showDeleteDialog = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
        }
    )
}
