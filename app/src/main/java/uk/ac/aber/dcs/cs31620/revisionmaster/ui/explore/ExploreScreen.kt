package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar


@Composable
fun ExploreScreen(navController: NavController) {
    Scaffold(
        topBar = { NonMainTopAppBar(navController = navController, title = stringResource(R.string.explore)) },
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
                onSearchInputChanged = { /* Handle search input changes */ }
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

        }
    }
}

@Composable
fun SearchBar(
    onSearchInputChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = onSearchInputChanged,
            label = { androidx.compose.material.Text(text = "Search") },
            modifier = Modifier.weight(1f)
        )
    }
}