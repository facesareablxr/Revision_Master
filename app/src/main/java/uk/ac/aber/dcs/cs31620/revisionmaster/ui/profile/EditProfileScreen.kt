package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.ProfileImageSelector

/**
 * Composable function for displaying the edit profile screen.
 *
 * @param navController: NavController to handle navigation within the app.
 */
@Composable
fun EditProfileScreen(
    navController: NavController
) {
    // ViewModel to manage user data
    val viewModel: UserViewModel = viewModel()
    // Collecting user data from the ViewModel
    val user by viewModel.user.collectAsState(initial = null)

    // Fetch user data when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    // If user data is not yet available, show loading indicator, else display edit profile content
    if (user == null) {
        LoadingIndicator() // Show loading indicator while data is being fetched
    } else {
        EditProfileContent(user = user, navController = navController) // Display edit profile content
    }
}

/**
 * Composable function for displaying the content of the edit profile screen.
 *
 * @param user: User object containing user data.
 * @param navController: NavController to handle navigation within the app.
 */
@Composable
fun EditProfileContent(
    user: User?,
    navController: NavController
) {
    // If user data is null, return
    if (user == null) {
        return
    }

    // ViewModel to manage user data
    val viewModel: UserViewModel = viewModel()

    // Mutable state variables for user data
    val firstName = remember { mutableStateOf(user.firstName) }
    val lastName = remember { mutableStateOf(user.lastName) }
    var imageUri by remember { mutableStateOf(user.profilePictureUrl) }

    // Coroutine scope for managing coroutines
    val coroutineScope = rememberCoroutineScope()
    // State variable to track whether input fields are valid
    var areFieldsValid by remember { mutableStateOf(true) }

    // Context for accessing resources
    val context = LocalContext.current
    // List of universities retrieved from resources
    val universities = remember(context) {
        context.resources.getStringArray(R.array.universities).toList()
    }

    // State variable for selected university in ButtonSpinner
    var selectedUniversity by remember {
        mutableStateOf(user.institution ?: "") // Check for existing institution
    }

    // Scaffold for layout structure
    Scaffold(
        topBar = {
            // Custom app bar allowing the user to navigate back
            SmallTopAppBar(navController, title = stringResource(id = R.string.editProfile))
        },
        floatingActionButton = {
            // Floating action button for saving changes
            FloatingActionButton(
                onClick = {
                    if (firstName.value.isNotEmpty() && lastName.value.isNotEmpty()) {
                        viewModel.updateProfile(
                            firstName = firstName.value,
                            lastName = lastName.value,
                            institution = selectedUniversity,
                            profilePictureUrl = imageUri.toString()
                        )
                        navController.navigateUp() // Navigate back
                    } else {
                        // If required fields are not filled, show snackbar
                        areFieldsValid = false
                        coroutineScope.launch {
                            SnackbarHostState().showSnackbar(
                                message = "Please fill in all required fields",
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.save_changes)
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Clickable Image Box for selecting/editing image
                ProfileImageSelector(
                    imageUrl = imageUri,
                    onImageSelected = { selectedUri ->
                        imageUri = selectedUri
                    }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // First name input box
                    item {
                        TextInputField(
                            value = firstName.value,
                            label = stringResource(id = R.string.first_name),
                            onValueChange = { firstName.value = it }
                        )
                    }
                    // Last name input box
                    item {
                        TextInputField(
                            value = lastName.value,
                            label = stringResource(id = R.string.last_name),
                            onValueChange = { lastName.value = it }
                        )
                    }
                    // Institution input using ButtonSpinner
                    item {
                        ButtonSpinner(
                            items = universities,
                            label = selectedUniversity,
                            itemClick = { selectedUniversity = it },

                            )
                    }
                }

            }
        }
    )
}

/**
 * Composable function for displaying a text input field.
 *
 * @param value: Current value of the input field.
 * @param label: Label for the input field.
 * @param onValueChange: Callback function for value change.
 */
@Composable
fun TextInputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            autoCorrect = true
        )
    )
}

/**
 * Composable function for displaying a loading indicator.
 */
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

