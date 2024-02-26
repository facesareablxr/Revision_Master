package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

/**
 * Top-level composable for the sign-up flow.
 *
 * @param navController Navigation controller to navigate between screens.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpTopLevel(
    navController: NavHostController
) {
    // State variables for user information and confirm password
    var user by remember { mutableStateOf(User()) }
    var confirmPassword by remember { mutableStateOf("") }

    // Firebase authentication instance and current activity context
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current as Activity

    // String resources for error messages
    val passwordsDoNotMatchMsg = stringResource(R.string.passwordsDoNotMatch)
    val registrationFailedMsg = stringResource(R.string.registrationFailed)
    val weakPasswordMsg = stringResource(R.string.weakPassword)

    // Check if user is already signed in and navigate to home if so
    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            goToHome(navController)
        }
    }

    // Scaffold with top bar and sign-up screen content
    Scaffold(
        modifier = Modifier.padding(8.dp),
        topBar = {
            SmallTopAppBar(
                navController = navController,
                title = stringResource(R.string.signup)
            )
        }
    ) {
        SignupScreen(
            user = user,
            confirmPassword = confirmPassword,
            updateUser = { updatedUser -> user = updatedUser },
            updateConfirmPassword = { updatedPassword -> confirmPassword = updatedPassword },
            signupAction = {
                // Check password match and strength before registering
                if (user.password != confirmPassword) {
                    Toast.makeText(context, passwordsDoNotMatchMsg, Toast.LENGTH_LONG).show()
                } else if (!isStrongPassword(user.password)) {
                    Toast.makeText(context, weakPasswordMsg, Toast.LENGTH_LONG).show()
                } else {
                    // Attempt user registration and handle success/failure
                    auth.createUserWithEmailAndPassword(user.email, user.password)
                        .addOnCompleteListener(context) { task ->
                            if (task.isSuccessful) {
                                // Sign in the new user and navigate to home
                                auth.signInWithEmailAndPassword(user.email, user.password)
                                    .addOnCompleteListener(context) { signInTask ->
                                        if (signInTask.isSuccessful) {
                                            goToHome(navController)
                                        } else {
                                            // Handle sign-in failure after successful creation
                                            Toast.makeText(context, "Sign-in failed after registration.", Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }
                            } else {
                                // Handle registration failure
                                Toast.makeText(context, registrationFailedMsg, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
            }
        )
    }
}

/**
 * Composable for the sign-up screen content.
 *
 * @param user User information object.
 * @param confirmPassword Confirmed password entered by the user.
 * @param updateUser Function to update the user object state.
 * @param updateConfirmPassword Function to update the confirmed password state.
 * @param signupAction Function to handle the sign-up button click.
 */
@Composable
fun SignupScreen(
    user: User,
    confirmPassword: String,
    updateUser: (User) -> Unit,
    updateConfirmPassword: (String) -> Unit,
    signupAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        UsernameField(user = user, updateUser = updateUser)
        Spacer(modifier = Modifier.height(8.dp))
        EmailSignUp(user = user, updateUser = updateUser)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordSignUp(user = user, updateUser = updateUser)
        Spacer(modifier = Modifier.height(8.dp))
        ConfirmPasswordField(
            confirmPassword = confirmPassword,
            updateConfirmPassword = updateConfirmPassword
        )
        Spacer(modifier = Modifier.height(8.dp))
        SignupButton(signupAction = signupAction)
    }
}

/**
 * Composable function that renders a username text field.
 *
 * @param user The user object containing the username.
 * @param updateUser Function to update the user object with a new username.
 */
@Composable
private fun UsernameField(
    user: User,
    updateUser: (User) -> Unit
) {
    OutlinedTextField(
        value = user.username,
        label = { Text(text = stringResource(R.string.username)) },
        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
        onValueChange = { updatedUser -> updateUser(user.copy(username = updatedUser)) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Composable function that renders an email text field for signup.
 *
 * @param user The user object containing the email.
 * @param updateUser Function to update the user object with a new email.
 */
@Composable
private fun EmailSignUp(
    user: User,
    updateUser: (User) -> Unit
) {
    OutlinedTextField(
        value = user.email,
        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
        label = { Text(text = stringResource(R.string.login_email)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        onValueChange = { updatedEmail -> updateUser(user.copy(email = updatedEmail)) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Composable function that renders a password text field with toggle visibility.
 *
 * @param user The user object containing the password.
 * @param updateUser Function to update the user object with a new password.
 */
@Composable
private fun PasswordSignUp(
    user: User,
    updateUser: (User) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = user.password,
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        label = { Text(text = stringResource(R.string.password)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = { updatedPassword -> updateUser(user.copy(password = updatedPassword)) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Composable function that renders a confirm password text field with toggle visibility.
 *
 * @param confirmPassword The current value of the confirm password field.
 * @param updateConfirmPassword Function to update the confirm password value.
 */
@Composable
private fun ConfirmPasswordField(
    confirmPassword: String,
    updateConfirmPassword: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = confirmPassword,
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        label = { Text(text = stringResource(R.string.confirm_password)) }, // Updated Label
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = updateConfirmPassword, // Update function passed directly
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Composable function that renders a signup button.
 *
 * @param signupAction Function to be called when the button is clicked.
 */
@Composable
private fun SignupButton(
    signupAction: () -> Unit
) {
    Button(
        onClick = signupAction,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.signup))
    }
}

/**
 * Checks if the provided password meets minimum strength requirements.
 *
 * A strong password should:
 * - Be at least 8 characters long
 * - Contain at least one uppercase letter
 * - Contain at least one lowercase letter
 * - Contain at least one number
 * - Contain at least one special character (not alphanumeric or whitespace)
 *
 * @param password The password to be evaluated.
 * @return True if the password meets all requirements, False otherwise.
 */
fun isStrongPassword(password: String): Boolean {
    val minLength = 8

    // Check for all required characters:
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasNumber = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetter() && !it.isDigit() } // Exclude digits

    return password.length >= minLength &&
            hasUpperCase &&
            hasLowerCase &&
            hasNumber &&
            hasSpecialChar
}