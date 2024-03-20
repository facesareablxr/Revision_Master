package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar

/**
 * Top-level composable for the sign-up flow.
 *
 * @param navController Navigation controller to navigate between screens.
 * @param userViewModel ViewModel for user operations.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpTopLevel(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current as Activity
    var user by remember { mutableStateOf(User()) }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordsDoNotMatchMsg = stringResource(R.string.passwordsDoNotMatch)
    val weakPasswordMsg = stringResource(R.string.weakPassword)

    LaunchedEffect(userViewModel) {
        FirebaseAuth.getInstance().currentUser?.let {
            goToHome(navController)
        }
    }

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
            updateUser = { user = it },
            updateConfirmPassword = { confirmPassword = it },
            signupAction = {
                if (user.password != confirmPassword) {
                    showToast(context, passwordsDoNotMatchMsg)
                } else if (!isStrongPassword(user.password!!)) {
                    showToast(context, weakPasswordMsg)
                } else {
                    val auth = FirebaseAuth.getInstance()
                    auth.createUserWithEmailAndPassword(user.email, user.password!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                userViewModel.addUserToDB(user)
                                goToHome(navController)
                            } else {
                                showToast(context, "Signup failed!")
                            }
                        }
                }
            }
        )
    }
}


/**
 * Function to display a toast message.
 */
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            FirstNameField(user = user, updateUser = updateUser)
            Spacer(modifier = Modifier.width(8.dp))
            LastNameField(user = user, updateUser = updateUser)
        }
        Spacer(modifier = Modifier.height(8.dp))
        UsernameField(user = user, updateUser = updateUser)
        Spacer(modifier = Modifier.height(8.dp))
        EmailSignUp(user = user, updateUser = updateUser)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordSignUp(user = user, updateUser = updateUser)
        Spacer(modifier = Modifier.height(8.dp))
        ConfirmPasswordField(
            confirmPassword = confirmPassword,
            updateConfirmPassword = updateConfirmPassword,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SignupButton(signupAction = signupAction)
    }
}

@Composable
private fun FirstNameField(
    user: User,
    updateUser: (User) -> Unit
) {
    OutlinedTextField(
        value = user.firstName,
        label = { Text(text = stringResource(R.string.first_name)) },
        onValueChange = { updatedFirstName ->
            if (isValidName(updatedFirstName)) {
                updateUser(user.copy(firstName = updatedFirstName))
            }
        },
        modifier = Modifier.width(180.dp)
    )
}

@Composable
private fun LastNameField(
    user: User,
    updateUser: (User) -> Unit
) {
    OutlinedTextField(
        value = user.lastName,
        label = { Text(text = stringResource(R.string.last_name)) },
        onValueChange = { updatedLastName ->
            if (isValidName(updatedLastName)) {
                updateUser(user.copy(lastName = updatedLastName))
            }
        },
        modifier = Modifier.width(180.dp)
    )
}


/**
 * Function that checks if the user inputted name is valid
 */
private fun isValidName(name: String): Boolean {
    val regex = "[a-zA-Z]+".toRegex()
    return name.matches(regex)
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
    val usernameState = remember { mutableStateOf(user.username) }

    OutlinedTextField(
        value = usernameState.value,
        label = { Text(text = stringResource(R.string.username)) },
        onValueChange = { newUsername ->
            if (isValidUsername(newUsername)) {
                usernameState.value = newUsername
                updateUser(user.copy(username = newUsername))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


/**
 * Function to check the validity of a username
 */
fun isValidUsername(username: String): Boolean {
    val regex = "^[a-zA-Z0-9_]+$".toRegex()
    return username.matches(regex)
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

    user.password?.let {
        OutlinedTextField(
        value = it,
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