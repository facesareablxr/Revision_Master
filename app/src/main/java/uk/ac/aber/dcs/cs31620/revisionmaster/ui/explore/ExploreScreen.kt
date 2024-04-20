package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
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
fun ExploreScreen(navController: NavController, exploreViewModel: ExploreViewModel = viewModel()) {
    val searchQuery = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            NonMainTopAppBar(
                navController = navController,
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
            SearchBar(
                onSearchInputChanged = { searchQuery.value = it }
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

            // Display search results
            val searchResults = exploreViewModel.searchResults.collectAsState()
            searchResults.value.forEach { result ->
                when (result) {
                    is User -> {
                        // Display user information
                        Text(text = "User: ${result.username}")
                    }
                    is Deck -> {
                        // Display deck information
                        Text(text = "Deck: ${result.name}")
                    }
                }
            }
        }
    }

    // Trigger search whenever searchQuery changes
    LaunchedEffect(searchQuery.value) {
        exploreViewModel.search(searchQuery.value)
    }
}

@Composable
fun SearchBar(onSearchInputChanged: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                onSearchInputChanged(it)
            },
            label = { Text(text = "Search") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier.weight(1f)
        )
    }
}