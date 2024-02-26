package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

/**
 * Top-level composable for the login screen.
 *
 * @param navController Reference to the navigation controller.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginTopLevel(navController: NavHostController) {
    var user by remember { mutableStateOf(User()) }
    val context = LocalContext.current as Activity

    // Initialize Firebase Auth instance
    val auth = FirebaseAuth.getInstance()

    // String resources for displaying messages
    val fillInAllFields = stringResource(R.string.fillallfields)
    val failureMessage = stringResource(R.string.failedLogIn)

    // Launched effect to check for existing logged-in user, so if for some reason it navigates
    // here while they're logged in, it will pass this screen
    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            goToHome(navController)
        }
    }

    // Flag to control toast visibility
    var showToast by remember { mutableStateOf(false) }

    // Scaffold composable for the overall layout
    Scaffold(
        topBar = {
            SmallTopAppBar(
                navController,
                stringResource(R.string.login), // Set title
            )
        }
    ) {
        // Box composable to stack UI elements
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Login Screen Background Image",
                contentScale = ContentScale.Crop, // Stretch to fill
                modifier = Modifier.fillMaxSize()
            )

            // Login screen content
            LoginScreen(
                user = user,
                updateUser = { user = it }, // Update user state on changes
                forgotPasswordAction = {
                    navController.navigate(Screen.ForgotDetails.route)
                },
                loginAction = {
                    showToast = true // Set flag to show toast after login attempt
                }
            )

            // Conditional toast logic
            if (showToast) {
                Toast.makeText(
                    context,
                    if (user.email.isEmpty() || user.password.isEmpty()) fillInAllFields else failureMessage,
                    Toast.LENGTH_LONG
                ).show()
                showToast = false // Reset flag after showing toast
            }
        }
    }
}

/**
 * Composable function for the login screen layout.
 *
 * @param user Current user information.
 * @param updateUser Function to update user state.
 * @param forgotPasswordAction Function to navigate to forgot password screen.
 * @param loginAction Function to initiate login attempt.
 */
@Composable
fun LoginScreen(
    user: User,
    updateUser: (User) -> Unit = {},
    forgotPasswordAction: () -> Unit = {},
    loginAction: () -> Unit = {}
) {
    // Column for vertical layout with padding
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Input fields and buttons arranged vertically
        EmailBox(user, updateUser)
        Spacer(modifier = Modifier.height(8.dp)) // Spacing between fields
        PasswordBox(user, updateUser)
        Spacer(modifier = Modifier.height(8.dp)) // Spacing between fields
        LoginButton(loginAction)
        ForgotPassword(forgotPasswordAction)
    }
}

/**
 * Composable function for email input field.
 *
 * @param user Current user information.
 * @param updateUser Function to update user state.
 */
@Composable
fun EmailBox(
    user: User,
    updateUser: (User) -> Unit
) {
    OutlinedTextField(
        value = user.email,
        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) }, // Leading email icon
        label = { Text(text = stringResource(R.string.login_email)) }, // Email label
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Email keyboard
        onValueChange = { // Update user state on email change
            updateUser(User(email = it, password = user.password))
        },
        modifier = Modifier.fillMaxWidth() // Full width
    )
}

/**
 * Composable function for password input field.
 *
 * @param user Current user information.
 * @param updateUser Function to update user state.
 */
@Composable
private fun PasswordBox(
    user: User,
    updateUser: (User) -> Unit
) {
    // State variable for password visibility
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = user.password,
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) }, // Leading lock icon
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) { // Toggle visibility button
                Icon(
                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, // Icon based on visibility
                    contentDescription = if (passwordVisible) "Hide password" else "Show password" // Button description
                )
            }
        },
        label = { Text(text = stringResource(R.string.password)) }, // Password label
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Mask password
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Password keyboard
        onValueChange = { // Update user state on password change
            updateUser(User(email = user.email, password = it))
        },
        modifier = Modifier.fillMaxWidth() // Full width
    )
}

/**
 * Composable function for "Forgot password" button.
 *
 * @param forgotPasswordAction Function to trigger forgot password action.
 */
@Composable
private fun ForgotPassword(forgotPasswordAction: () -> Unit) {
    // Full-width button for forgot password
    TextButton(
        onClick = forgotPasswordAction,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.forgot_password))
    }
}

/**
 * Composable function for "Login" button.
 *
 * @param loginAction Function to trigger login action.
 */
@Composable
private fun LoginButton(loginAction: () -> Unit) {
    // Full-width button for login
    Button(
        onClick = loginAction,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.login))
    }
}


