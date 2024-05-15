package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar

/**
 * Composable function for the top-level screen of the "Forgot Password" feature.
 *
 * @param navController: Navigation controller for managing navigation within the app.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForgotPassScreenTopLevel(
    navController: NavController
) {
    // State for managing the entered email address
    val email = rememberSaveable { mutableStateOf("") }
    // Firebase authentication instance
    val auth = FirebaseAuth.getInstance()
    // Activity context
    val context = LocalContext.current as Activity

    // String resources for messages
    val fillEmailFieldsMsg = stringResource(R.string.fillEmail)
    val resetLinkSentMsg = stringResource(R.string.resetLinkSent)
    val unableToSendResetEmailMsg = stringResource(R.string.unableToSendResetEmail)

    // Scaffold for the screen layout
    Scaffold(
        topBar = {
            // Top app bar for navigation
            SmallTopAppBar(navController, stringResource(R.string.forgot_password))
        },
        content = {
            // Main content of the screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Container for the content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Forgot password content
                    ForgotPasswordContent(
                        email = email.value,
                        updateEmail = { email.value = it },
                        getPasswordAction = {
                            // Action to retrieve password
                            if (email.value.isEmpty()) {
                                // Show toast if email field is empty
                                Toast.makeText(context, fillEmailFieldsMsg, Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                // Send password reset email
                                auth.sendPasswordResetEmail(email.value)
                                    .addOnCompleteListener(context) { task ->
                                        // Show appropriate toast based on success or failure
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                resetLinkSentMsg,
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                unableToSendResetEmailMsg,
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }
                                    }
                            }
                        }
                    )
                }
            }
        }
    )
}

/**
 * Composable function for the content of the "Forgot Password" screen.
 *
 * @param email: Entered email address.
 * @param updateEmail: Function to update the email address.
 * @param getPasswordAction: Function to perform when attempting to retrieve password.
 */
@Composable
private fun ForgotPasswordContent(
    email: String,
    updateEmail: (String) -> Unit,
    getPasswordAction: () -> Unit
) {
    // Column layout for content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input field for email
        OutlinedTextField(
            value = email,
            label = { Text(stringResource(R.string.login_email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = updateEmail,
            modifier = Modifier.fillMaxWidth()
        )
        // Spacer for layout
        Spacer(modifier = Modifier.height(16.dp))
        // Button to trigger password retrieval
        Button(
            onClick = getPasswordAction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.get_password))
        }
        // Spacer for layout
        Spacer(modifier = Modifier.height(16.dp))
    }
}
