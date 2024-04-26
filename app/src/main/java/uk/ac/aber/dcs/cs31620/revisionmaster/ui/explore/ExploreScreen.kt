package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.ExploreViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar

@Composable
fun ExploreScreen(
    navController: NavController,
    exploreViewModel: ExploreViewModel = viewModel()
){
    var searchQuery by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            NonMainTopAppBar(
                title = stringResource(R.string.explore)
            )
        },
        bottomBar = {
            MainPageNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SuggestionChip(
                    onClick = { /* Handle groups selection */ },
                    label = { Text("Groups") }
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                SuggestionChip(
                    onClick = { /* Handle people selection */ },
                    label = { Text("People") }
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                SuggestionChip(
                    onClick = { /* Handle open sets selection */ },
                    label = { Text("Open Sets") }
                )
            }

            val searchResults by exploreViewModel.searchResults.collectAsState(initial = emptyList())

            LaunchedEffect(Unit) {
                exploreViewModel.search(searchQuery)
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(searchResults) { result ->
                    when (result) {
                        is User -> {
                            UserCard(user = result)
                        }

                        is Deck -> {
                            DeckCard(deck = result)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "User: ${user.username}", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun DeckCard(deck: Deck) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Deck: ${deck.name}", style = MaterialTheme.typography.headlineMedium)

        }
    }
}