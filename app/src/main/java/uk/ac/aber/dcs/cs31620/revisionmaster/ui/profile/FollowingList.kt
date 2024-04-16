package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel

@Composable
fun FollowingScreen(userViewModel: UserViewModel = viewModel()) {
    val searchTextState = remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(key1 = userViewModel) {
        userViewModel.getUserData()
        userViewModel.user.value?.let {
            userViewModel.getFollowingList()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchTextState.value,
            onValueChange = { searchTextState.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        FollowingList(userViewModel.followingList.value)
    }
}

@Composable
fun FollowingList(followingList: List<String>) {
    LazyColumn {
        items(followingList) { following ->
            Text(
                text = following,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}