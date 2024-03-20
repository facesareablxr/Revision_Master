package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Flashcard
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Module
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Subject
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.UserClasses
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.LibraryTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

@Composable
fun LibraryTopLevel() {

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LibraryScreen(
    navController: NavHostController,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Create a ViewModel instance to manage the user data
    val viewModel: UserViewModel = viewModel()
    // Observe the user data using a state variable
    val user by viewModel.user.collectAsState(initial = null)
    val subjectsState = remember { mutableStateOf<List<Subject>>(emptyList()) }
    val listState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.getUserData()
        user?.let {
            flashcardViewModel.getUserSubjects(it.username) { subjects ->
                subjectsState.value = subjects
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            LibraryTopAppBar(navController)
        },
        bottomBar = { MainPageNavigationBar(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.AddDeck.route) },
                icon = { Icon(Icons.Filled.Add, "Localized Description") },
                text = { Text(text = "Add Deck") },
            )
        },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.End
    ) { _ ->
        Column {
            // Search and filter section
            SearchAndFilterBar(
                onSearchInputChanged = { /* Handle search queries */ },
                onFilterSelected = { /* Handle filter selection */ }
            )

            // Display subjects or empty message
            if (subjectsState.value.isNotEmpty()) {
                SubjectView(subjects = subjectsState.value)
            } else {
                NoDataMessage()
            }
        }
    }
}

@Composable
fun SearchAndFilterBar(
    onSearchInputChanged: (String) -> Unit,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = onSearchInputChanged,
            label = { Text(text = "Search") },
            modifier = Modifier.weight(1f)
        )
        Surface(
            modifier = Modifier
                .padding(8.dp, top = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .clickable { /* Show filter options */ },
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
            Icon(Icons.Filled.FilterList, "Filter", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun NoDataMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.addData),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SubjectView(subjects: List<Subject>) {
    LazyColumn {
        items(subjects) { subject ->
            SubjectCard(subject = subject)
        }
    }
}

@Composable
fun SubjectCard(subject: Subject) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { /* Navigate to Modules View */ },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = subject.subjectName, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun ModuleView(modules: List<Module>) {
    LazyColumn {
        items(modules) { module ->
            ModuleCard(module = module)
        }
    }
}

@Composable
fun ModuleCard(module: Module) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { /* Navigate to Classes View */ },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = module.moduleName, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun ClassView(classes: List<UserClasses>) {
    LazyColumn {
        items(classes) { userClass ->
            ClassCard(userClass = userClass)
        }
    }
}

@Composable
fun ClassCard(userClass: UserClasses) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { /* Navigate to Flashcards View */ },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = userClass.className, style = MaterialTheme.typography.headlineMedium)
            // Add any additional details about the class here
        }
    }
}

@Composable
fun FlashcardView(flashcards: List<Flashcard>) {
    // Implement flashcards display
    // Flashcards could be shown individually in a grid format
}