package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
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
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

/**
 *
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginTopLevel(navController: NavHostController) {
    val user = remember { mutableStateOf(User()) }
    val context = LocalContext.current as Activity
    val auth = FirebaseAuth.getInstance()

    val fillInAllFields = stringResource(R.string.fillallfields)
    val failureMessage = stringResource(R.string.failedLogIn)

    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            goToHome(navController)
        }
    }

    var showToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navController,
                stringResource(R.string.login)
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LoginScreen(
                user = user.value,
                onUserChange = { newUser -> user.value = newUser },
                onForgotPassword = { navController.navigate(Screen.ForgotDetails.route) },
                onLogin = {
                    if (user.value.email.isEmpty()  || user.value.password!!.isEmpty()) {
                        showToast = true
                    } else {
                        user.value.email.let { it1 ->
                            user.value.password.let { it2 ->
                                if (it2 != null) {
                                    auth.signInWithEmailAndPassword(it1, it2)
                                        .addOnCompleteListener(context) { task ->
                                            if (task.isSuccessful) {
                                                // Login successful
                                                Toast.makeText(context, "Successfully logged in", Toast.LENGTH_LONG).show()
                                                goToHome(navController)
                                            } else {
                                                // Login failed
                                                Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show()
                                                Log.d("FB-AUTH", "Login failed. Cause: ${task.exception?.cause}")
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            )

            if (showToast) {
                Toast.makeText(context, fillInAllFields, Toast.LENGTH_LONG).show()
                showToast = false
            }
        }
    }
}

/**
 * Composable function for the login screen layout.
 *
 * @param user Current user information.
 * @param onUserChange Function to update user state.
 * @param onForgotPassword Function to navigate to forgot password screen.
 * @param onLogin Function to initiate login attempt.
 */
@Composable
fun LoginScreen(
    user: User,
    onUserChange: (User) -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        EmailBox(user, onUserChange)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordBox(user, onUserChange)
        Spacer(modifier = Modifier.height(8.dp))
        LoginButton(onLogin)
        ForgotPassword(onForgotPassword)
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

    user.password?.let {
        OutlinedTextField(
        value = it,
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


