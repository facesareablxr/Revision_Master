package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar

@Composable
fun EditProfileTopLevel( navController: NavController) {
    EditProfileScreen(navController)
}

@Composable
fun EditProfileScreen(
    navController: NavController,
) {
    val viewModel: UserViewModel = viewModel()
    val user by viewModel.user.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    if (user == null) {
        // Show loading indicator while data is being fetched
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Inflate the layout with user data
        val firstName = mutableStateOf(user!!.firstName)
        val lastName = mutableStateOf(user!!.lastName)
        val institution = mutableStateOf(user!!.institution ?: "")
        val profilePictureUri = mutableStateOf(user!!.profilePictureUrl)

        val coroutineScope = rememberCoroutineScope()
        val keyboardController = LocalSoftwareKeyboardController.current
        var areFieldsValid by remember { mutableStateOf(true) }

        val context = LocalContext.current
        val universities = remember(context) {
            context.resources.getStringArray(R.array.universities).toList()
        }

        var showDialog by remember { mutableStateOf(false) }
        var selectedUniversityIndex by remember { mutableStateOf(-1) }

        // Scaffold for the screen
        Scaffold(
            topBar = {
                // Custom app bar allowing the user to navigate back
                SmallTopAppBar(navController, title = stringResource(id = R.string.edit_profile))
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (firstName.value.isNotEmpty() && lastName.value.isNotEmpty() && institution.value.isNotEmpty()) {
                            viewModel.updateProfile(
                                firstName = firstName.value,
                                lastName = lastName.value,
                                institution = institution.value,
                                profilePictureUrl = profilePictureUri.value
                            )
                            navController.navigateUp()
                        } else {
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
                ) {
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
                        .fillMaxSize()
                        .clickable { keyboardController?.hide() },
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile picture (moved to the top)
                    ProfilePicture(
                        value = profilePictureUri.value,
                        defaultValue = "path/to/default/profile/image.jpg", // Replace with your default image path
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 16.dp),
                        onValueChange = { profilePictureUri.value = it }
                    )
                    Spacer(modifier = Modifier.padding(8.dp)) // Add spacing between profile picture and text fields

                    // Text fields remain within a LazyColumn for scrolling
                    LazyColumn(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // First name, last name, institution fields
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
                        // Institution input box
                        item {
                            OutlinedTextField(
                                value = institution.value,
                                onValueChange = { /* Do nothing */ },
                                label = { Text("Institution") },
                                trailingIcon = {
                                    IconButton(onClick = { showDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Select institution"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false
                            )
                            if (showDialog) {
                                UniversitySelectionDialog(
                                    universities = universities,
                                    selectedUniversityIndex = selectedUniversityIndex,
                                    onUniversitySelected = { index ->
                                        selectedUniversityIndex = index
                                        institution.value = universities[index]
                                        showDialog = false
                                    },
                                    onDismissRequest = { showDialog = false }
                                )
                            }
                        }
                    }
                    SnackbarHost(
                        hostState = remember { SnackbarHostState() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        )
    }
}

@Composable
fun UniversitySelectionDialog(
    universities: List<String>,
    selectedUniversityIndex: Int,
    onUniversitySelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Select Institution") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                )
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(universities.filter { it.contains(searchQuery, ignoreCase = true) }) { index, university ->
                        UniversityItem(
                            university = university,
                            isSelected = index == selectedUniversityIndex,
                            onClick = { onUniversitySelected(index) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            // Optional confirmation button if needed
        }
    )
}

@Composable
fun UniversityItem(
    university: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = university,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
fun ProfilePicture(
    value: String?,
    defaultValue: String = "res/drawable/profile_image_placeholder.xml", // Replace with your default image path
    modifier: Modifier,
    onValueChange: (String) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(CircleShape)  // Clip image to circular shape
            .background(Color.LightGray)
            .clickable { openDialog = true }
    ) {
        // Show profile picture if available
        if (value.isNullOrEmpty().not()) {
            Image(
                painter = rememberAsyncImagePainter(value!!),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Show default image if no profile picture is set
            Image(
                painter = rememberAsyncImagePainter(defaultValue),  // Use rememberImagePainter for static image
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (openDialog) {
            ImageSelectionDialog(
                onDismissRequest = { openDialog = false },
                onImageSelected = { imagePath ->
                    onValueChange(imagePath)
                    openDialog = false
                }
            )
        }
    }
}

@Composable
fun ImageSelectionDialog(
    onDismissRequest: () -> Unit,
    onImageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Choose Image") },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        // Launch Camera Intent
                        // Handle image capture and path retrieval
                        onImageSelected("path/to/captured/image.jpg")
                    }
                ) {
                    Text("Camera")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        // Launch Gallery Intent
                        // Handle image selection and path retrieval
                        onImageSelected("path/to/selected/image.jpg")
                    }
                ) {
                    Text("Gallery")
                }
            }
        }
    )
}

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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}