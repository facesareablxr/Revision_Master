package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.processOCR

@Composable
fun AddFlashcardScreen(
    navController: NavHostController,
    deckId: String,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    val imageUri by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navController,
                title = "Add Flashcard"
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                // Question text field with camera icon
                OutlinedTextFieldWithCamera(
                    value =  question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                // Answer text field with camera icon
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextFieldWithCamera(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier
                        .fillMaxWidth()
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
                        if (question.isNotEmpty() && answer.isNotEmpty()) {
                            flashcardViewModel.addFlashcard(
                                deckId,
                                question,
                                answer,
                                selectedDifficulty,
                                imageUri
                            )
                            showDialog = true
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
                    onDismissRequest = { showDialog = false },
                    title = { Text("Flashcard Added!") },
                    confirmButton = {
                        TextButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun OutlinedTextFieldWithCamera(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            processOCR(context, uri.toString()) { recognizedText ->
                onValueChange(recognizedText)
            }
        } ?: Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { launcher.launch("image/*") }
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Add Image"
            )
        }
    }
}