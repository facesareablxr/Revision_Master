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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar

/**
 *
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForgotPassScreenTopLevel(
    navController: NavHostController
) {
    val email = rememberSaveable { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current as Activity

    val fillEmailFieldsMsg = stringResource(R.string.fillEmail)
    val resetLinkSentMsg = stringResource(R.string.resetLinkSent)
    val unableToSendResetEmailMsg = stringResource(R.string.unableToSendResetEmail)

    Scaffold(
        topBar = {
            SmallTopAppBar(navController, stringResource(R.string.forgot_password)
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ForgotPasswordContent(
                        email = email.value,
                        updateEmail = { email.value = it },
                        getPasswordAction = {
                            if (email.value.isEmpty()) {
                                Toast.makeText(context, fillEmailFieldsMsg, Toast.LENGTH_LONG).show()
                            } else {
                                auth.sendPasswordResetEmail(email.value)
                                    .addOnCompleteListener(context) { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, resetLinkSentMsg, Toast.LENGTH_LONG)
                                                .show()
                                        } else {
                                            Toast.makeText(context, unableToSendResetEmailMsg, Toast.LENGTH_LONG)
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
 *
 */
@Composable
private fun ForgotPasswordContent(
    email: String,
    updateEmail: (String) -> Unit,
    getPasswordAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            label = { Text(stringResource(R.string.login_email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = updateEmail,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = getPasswordAction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.get_password))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}