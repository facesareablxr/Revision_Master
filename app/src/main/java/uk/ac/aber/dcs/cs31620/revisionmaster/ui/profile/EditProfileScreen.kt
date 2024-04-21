package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

//noinspection UsingMaterialAndMaterial3Libraries
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.showImagePickerDialog
import java.io.File


@Composable
fun EditProfileTopLevel( navController: NavController) {
    EditProfileScreen(navController)
}

@Composable
fun EditProfileScreen(
    navController: NavController
) {
    val viewModel: UserViewModel = viewModel()
    val user by viewModel.user.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    if (user == null) {
        // Show loading indicator while data is being fetched
        LoadingIndicator()
    } else {
        EditProfileContent(user = user, navController = navController)
    }

}

@Composable
fun EditProfileContent(
    user: User?,
    navController: NavController
) {
    if (user == null) {
        return
    }

    val viewModel: UserViewModel = viewModel()

    val firstName = remember { mutableStateOf(user.firstName) }
    val lastName = remember { mutableStateOf(user.lastName) }
    val profilePictureUri = remember { mutableStateOf(user.profilePictureUrl) }

    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var areFieldsValid by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val universities = remember(context) {
        context.resources.getStringArray(R.array.universities).toList()
    }

    // State variables for ButtonSpinner
    var selectedUniversity by remember {
        mutableStateOf(user.institution ?: "") // Check for existing institution
    }

    Scaffold(
        topBar = {
            // Custom app bar allowing the user to navigate back
            SmallTopAppBar(navController, title = stringResource(id = R.string.edit_profile))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (firstName.value.isNotEmpty() && lastName.value.isNotEmpty()) {
                        viewModel.updateProfile(
                            firstName = firstName.value,
                            lastName = lastName.value,
                            institution = selectedUniversity,
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
                    .fillMaxSize()
                    .clickable { keyboardController?.hide() },
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddNewImage(
                    imagePath = profilePictureUri.value,
                    modifier = Modifier.padding(16.dp),
                    updateImagePath = { profilePictureUri.value = it}
                )
                Spacer(modifier = Modifier.padding(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // First name, last name fields
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


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddNewImage(
    imagePath: String?,
    modifier: Modifier,
    updateImagePath: (String) -> Unit = {}
) {
    val photoFile: File? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val resultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                when {
                    data.hasExtra(MediaStore.EXTRA_OUTPUT) -> { // Camera intent
                        updateImagePath(photoFile!!.absolutePath)
                    }
                    data.data != null -> { // Gallery intent
                        updateImagePath(data.data!!.toString())
                    }
                    else -> { }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = { showImagePickerDialog(context, resultLauncher) }) {
            Icon(Icons.Default.AddAPhoto, contentDescription = stringResource(R.string.addImage))
        }
        GlideImage(
            model = if (!imagePath.isNullOrEmpty()) Uri.parse(imagePath) else R.drawable.profile_image_placeholder,
            contentDescription = stringResource(R.string.profilePicture),
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}