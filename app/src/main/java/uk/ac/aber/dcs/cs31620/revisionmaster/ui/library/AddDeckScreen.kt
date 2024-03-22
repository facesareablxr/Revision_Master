package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddDeckScreen(
    navController: NavController,
) {
    val subjects = stringArrayResource(id = R.array.subjects).toList()
    val flashcardViewModel: FlashcardViewModel = viewModel()
    val userViewModel: UserViewModel= viewModel()
    var deckName by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("") } // Track selected subject
    var isPublic by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    val user by userViewModel.user.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        userViewModel.getUserData()
    }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = stringResource(R.string.createDeck),
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Use innerPadding from Scaffold for correct layout
        ) {
            DeckNameEnterBox(
                deckName = deckName,
                onDeckNameChange = { newValue -> deckName = newValue }
            )
            Spacer(modifier = Modifier.height(8.dp))
            DescriptionBox(
                description = description,
                onDescriptionChange = { newValue -> description = newValue }
            )
            Spacer(modifier = Modifier.height(8.dp))
            SubjectSpinner(
                subjects = subjects,
                selectedSubject = selectedSubject,
                onSubjectChange = { newValue -> selectedSubject = newValue }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PublicSwitch(
                isPublic = isPublic,
                onPublicChange = { newValue -> isPublic = newValue }
            )
            CreateDeckButton(
                flashcardViewModel,
                deckName,
                selectedSubject,
                isPublic,
                description,
                user,
                navController
            )
        }
    }
}

@Composable
private fun PublicSwitch(isPublic: Boolean, onPublicChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Public Deck"
        )
        Switch(
            checked = isPublic,
            onCheckedChange = onPublicChange,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                checkedThumbColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun CreateDeckButton(
    flashcardViewModel: FlashcardViewModel,
    deckName: String,
    selectedSubject: String,
    isPublic: Boolean,
    description: String,
    user: User?,
    navController: NavController
) {
    Button(
        onClick = {
            // Call function in ViewModel to add deck with provided details
            flashcardViewModel.addDeck(
                deckName,
                selectedSubject,
                isPublic,
                description,
                user!!.username
            )
            // Navigate back after adding deck
            navController.popBackStack()
        },
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Create Deck")
    }
}

@Composable
private fun DescriptionBox(description: String, onDescriptionChange: (String) -> Unit) {
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text("Description (Optional)") },
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        maxLines = 3
    )
}

@Composable
private fun SubjectSpinner(
    subjects: List<String>,
    selectedSubject: String,
    onSubjectChange: (String) -> Unit
) {
    ButtonSpinner(
        items = subjects,
        label = selectedSubject,
        itemClick = onSubjectChange
    )
}

@Composable
private fun DeckNameEnterBox(deckName: String, onDeckNameChange: (String) -> Unit) {
    OutlinedTextField(
        value = deckName,
        onValueChange = onDeckNameChange,
        label = { Text("Deck Name") },
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    )
}