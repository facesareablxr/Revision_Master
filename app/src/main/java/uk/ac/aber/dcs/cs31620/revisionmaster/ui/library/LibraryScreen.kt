package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Subject
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar

@Composable
fun LibraryScreen(
    navController: NavHostController,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val viewModel: UserViewModel = viewModel()
    val user by viewModel.user.collectAsState(initial = null)
    val subjectsState = remember { mutableStateOf<List<Subject>>(emptyList()) }
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.getUserData()
        user?.let {
            flashcardViewModel.getUserSubjects(it.username) { subjects ->
                subjectsState.value = subjects
            }
        }
    }

    val tabs = listOf("Study Sets", "Favourites")
    val selectedTab = remember { mutableStateOf(tabs[0]) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            NonMainTopAppBar(navController, stringResource(R.string.library))
        },
        bottomBar = { MainPageNavigationBar(navController) },
        floatingActionButton = {
            // Handle add action based on selected tab (optional)
            if (selectedTab.value == "Study Sets") {
                FloatingActionButton(
                    onClick = { /* Handle creating new study set */ }
                ){
                    Icon(imageVector = Icons.Default.Add, stringResource(id = R.string.addData))
                }
            }
        },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column {
            TabRow(
                selectedTabIndex = tabs.indexOf(selectedTab.value),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab.value == tab,
                        onClick = { selectedTab.value = tab },
                        text = { Text(text = tab) }
                    )
                }
            }
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab.value) {
                    "Study Sets" -> {
                        if (subjectsState.value.isNotEmpty()) {
                            SubjectList(subjects = subjectsState.value, navController)
                        } else {
                            NoDataMessage()
                        }
                    }

                    "Favourites" -> {
                        // Implement displaying favourite study sets here
                    }
                }
            }
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
fun SubjectList(subjects: List<Subject>, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        subjects.forEach { subject ->
            SubjectItem(subject = subject, navController = navController)
        }
    }
}

@Composable
fun SubjectItem(subject: Subject, navController: NavHostController) {
    // Implement displaying individual subject details here
    // You can use Card or Surface composables for styling
    // Add click listener to navigate to the study set details screen
}
