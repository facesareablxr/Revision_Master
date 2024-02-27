package uk.ac.aber.dcs.cs31620.revisionmaster.ui.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar

@Composable
fun ChatsScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            MainPageNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.chats)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SuggestionChip(
                    onClick = { /* Handle group chat selection */ },
                    label = { Text("Groups") }
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                SuggestionChip(
                    onClick = { /* Handle DM selection */ },
                    label = { Text("DMs") }
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                SuggestionChip(
                    onClick = { /* Handle discover chat selection */ },
                    label = { Text("Discover") }
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                SuggestionChip(
                    onClick = { /* Handle Q&A chat selection */ },
                    label = { Text("Q&A") }
                )
            }
        }
    }
}