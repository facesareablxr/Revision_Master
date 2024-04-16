package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner

@Composable
fun EditFlashcardScreen(
    navController: NavController,
    flashcardId: String,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Collecting data from ViewModel
    val flashcard by flashcardViewModel.flashcardLiveData.observeAsState()

    // Side effect to fetch flashcard data
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardById(deckId, flashcardId)
    }

    // State to hold the question, answer, and selected difficulty
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Side effect to update UI when flashcard data changes
    LaunchedEffect(flashcard) {
        flashcard?.let { flashcard ->
            question = flashcard.question
            answer = flashcard.answer
            selectedDifficulty = flashcard.difficulty
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navController,
                title = "Edit Flashcard"
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                ButtonSpinner(
                    items = Difficulty.values().map { it.label },
                    label = selectedDifficulty.label,
                    itemClick = { newDifficultyLabel ->
                        selectedDifficulty =
                            Difficulty.values().find { it.label == newDifficultyLabel }!!
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                    Button(
                        onClick = {
                            flashcardViewModel.updateFlashcard(
                                deckId, flashcardId, question, answer, selectedDifficulty
                            )
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(R.string.saveChanges))
                    }
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete Flashcard?") },
                        text = { Text("This action cannot be undone.") },
                        confirmButton = {
                            Button(onClick = {
                                flashcardViewModel.deleteFlashcard(flashcardId, deckId)
                                navController.popBackStack()
                            }) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    )
}