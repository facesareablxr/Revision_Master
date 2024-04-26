package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.processOCR
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.showImagePickerDialog

/**
 * Composable function for adding a flashcard to a deck.
 * @param navController: NavHostController for navigating between composables.
 * @param deckId: String representing the ID of the deck to which the flashcard is added.
 * @param flashcardViewModel: ViewModel for managing flashcard-related data.
 */
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
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data // Store the image URI
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navController,
                title = stringResource(R.string.addFlashcard)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ImageSelectionArea(imageUri) {
                    showImagePickerDialog(context, imagePickerLauncher)
                }

                // Question text field with camera icon
                OutlinedTextFieldWithCamera(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text(stringResource(R.string.question)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                // Answer text field with camera icon
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextFieldWithCamera(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text(stringResource(R.string.answer)) },
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
                            flashcardViewModel.addFlashcardAndUpdateDeck(
                                deckId,
                                question,
                                answer,
                                selectedDifficulty,
                                imageUri?.toString()
                            )
                            showDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.addFlashcard))
                }
            }

            // Dialog to confirm flashcard addition
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.flashcardAdded)) },
                    confirmButton = {
                        TextButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                )
            }
        }
    )
}

/**
 * Composable function for displaying an area for image selection.
 * @param imageUri: The URI of the selected image, null if no image is selected.
 * @param onClick: Callback function to handle click events.
 */
@Composable
fun ImageSelectionArea(
    imageUri: Uri?,
    onClick: () -> Unit
) {
    // Display a box for the image selection area
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(150.dp)
            .clickable(onClick = onClick) // Make the area clickable
            .border(1.dp, Color.Gray), // Add border around the area
        contentAlignment = Alignment.Center // Align content to center
    ) {
        // If no image is selected, display an icon to add a photo
        if (imageUri == null) {
            Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = stringResource(R.string.addImage),
                tint = Color.LightGray,
                modifier = Modifier.size(40.dp)
            )
        } else { // If an image is selected, display the selected image
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = stringResource(R.string.selectedImage),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * Composable function for displaying an outlined text field with a camera button.
 * @param value: The current value of the text field.
 * @param onValueChange: Callback function to handle value changes.
 * @param label: Composable function for the label of the text field.
 * @param modifier: Modifier for customization.
 */
@Composable
fun OutlinedTextFieldWithCamera(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Use ActivityResultContracts.GetContent() to launch image selection
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { // If URI is not null, process the selected image
            processOCR(context, uri.toString()) { recognizedText ->
                onValueChange(recognizedText) // Update text field value with recognized text
            }
        } ?: Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show() // Show toast if image selection fails
    }

    // Display a row containing an outlined text field and a camera button
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Outlined text field for input
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier.weight(1f) // Take up available space
        )
        // IconButton for launching image selection
        IconButton(
            onClick = { launcher.launch("image/*") } // Launch image selection when clicked
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = stringResource(R.string.addImage)
            )
        }
    }
}
